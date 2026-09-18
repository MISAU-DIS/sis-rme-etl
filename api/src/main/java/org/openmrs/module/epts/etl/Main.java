
package org.openmrs.module.epts.etl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openmrs.api.context.Context;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.controller.DynamicProcessStarter;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Main entry point for the EPTS ETL application.
 *
 * <p>
 * The class supports two startup modes:
 * </p>
 *
 * <ul>
 *     <li>
 *         Traditional command-line startup through {@link #main(String[])}.
 *     </li>
 *     <li>
 *         OpenMRS startup through {@link #startFromOpenMRS()}.
 *     </li>
 * </ul>
 *
 * <p>
 * When started from OpenMRS, the actual {@link ProcessStarter} instance is
 * kept by this class so that the OMOD layer can access the same ETL process.
 * This prevents the OMOD from creating a second independent ETL process.
 * </p>
 */
public class Main {

	public static final String STARTUP_FILE_GLOBAL_PROPERTY =
			"epts.etl.startup.file";

	public static final String ENABLED_GLOBAL_PROPERTY =
			"epts.etl.enabled";

	/**
	 * Prevents OpenMRS startup from triggering the ETL more than once.
	 */
	private static final AtomicBoolean OPENMRS_START_TRIGGERED =
			new AtomicBoolean(false);

	/**
	 * The ProcessStarter created for the ETL started from OpenMRS.
	 *
	 * <p>
	 * This is the actual starter used by the running ETL process.
	 * OMOD services must use this instance instead of creating another
	 * ProcessStarter.
	 * </p>
	 */
	private static volatile ProcessStarter openMrsProcessStarter;

	/**
	 * Traditional command-line entry point.
	 *
	 * <p>
	 * This behavior is kept compatible with the original ETL launcher.
	 * </p>
	 *
	 * @param args startup arguments
	 */
	public static void main(String[] args) {

		EtlLogger logger = EtlLogger.getLogger(Main.class);

		try {

			EtlConfiguration[] configurations = loadSyncConfig(args);

			if (configurations == null || configurations.length == 0) {
				logger.warn("No ETL configuration was found.");
				return;
			}

			if (configurations.length > 1) {
				logger.warn(
						"Multiple ETL configurations were found. "
								+ "Only one configuration is expected.");
			}

			EtlConfiguration configuration = configurations[0];

			if (configuration.isManualStart()) {
				logger.info(
						"ETL configuration is marked for manual start. "
								+ "Skipping automatic execution.");
				return;
			}

			ProcessStarter processStarter;

			if (configuration.isDynamic()) {

				logger.info(
						"Starting ETL using DynamicProcessStarter.");

				processStarter =
						new DynamicProcessStarter(configuration);

			} else {

				logger.info(
						"Starting ETL using ProcessStarter.");

				processStarter =
						new ProcessStarter(configuration);
			}

			/*
			 * Preserve the traditional synchronous command-line behavior.
			 */
			processStarter.run();

		} catch (Exception e) {

			logger.err(
					"Failed to start EPTS ETL.",
					e);

			throw new RuntimeException(e);
		}
	}

	/**
	 * Starts the ETL from inside OpenMRS.
	 *
	 * <p>
	 * Unlike the traditional {@link #main(String[])} method, this method
	 * starts the ETL asynchronously so that the OpenMRS startup thread is
	 * not blocked while the ETL is running.
	 * </p>
	 *
	 * <p>
	 * The created {@link ProcessStarter} is stored in
	 * {@link #openMrsProcessStarter}. This allows the OMOD layer to access
	 * the same running ETL process.
	 * </p>
	 *
	 * @throws IOException if the startup configuration cannot be loaded
	 * @throws DBException if ETL database initialization fails
	 */
	public static void startFromOpenMRS()
			throws IOException, DBException {

		EtlLogger logger = EtlLogger.getLogger(Main.class);

		/*
		 * Check whether ETL is enabled.
		 */
		String enabledValue =
				Context.getAdministrationService()
						.getGlobalProperty(ENABLED_GLOBAL_PROPERTY);

		boolean enabled =
				Boolean.parseBoolean(enabledValue);

		if (!enabled) {

			logger.info(
					"EPTS ETL is disabled. "
							+ "Skipping automatic startup.");

			return;
		}

		/*
		 * Prevent multiple OpenMRS startup calls from creating
		 * multiple ETL processes.
		 */
		if (!OPENMRS_START_TRIGGERED.compareAndSet(false, true)) {

			logger.info(
					"EPTS ETL startup was already triggered.");

			return;
		}

		try {

			/*
			 * Read the startup configuration from OpenMRS Global Properties.
			 */
			String startupFile =
					Context.getAdministrationService()
							.getGlobalProperty(
									STARTUP_FILE_GLOBAL_PROPERTY);

			if (startupFile == null
					|| startupFile.trim().isEmpty()) {

				throw new IllegalStateException(
						"Global Property "
								+ STARTUP_FILE_GLOBAL_PROPERTY
								+ " is not configured.");
			}

			logger.info(
					"Starting EPTS ETL from OpenMRS using configuration: "
							+ startupFile);

			/*
			 * Load the ETL configuration directly.
			 *
			 * We intentionally do not call main(), because main() performs
			 * traditional synchronous execution.
			 */
			EtlConfiguration[] configurations =
					loadSyncConfig(
							new String[]{
									startupFile.trim()
							});

			if (configurations == null
					|| configurations.length == 0) {

				throw new IllegalStateException(
						"No ETL configuration could be loaded from: "
								+ startupFile);
			}

			if (configurations.length > 1) {

				throw new IllegalStateException(
						"Multiple ETL configurations were loaded from: "
								+ startupFile);
			}

			EtlConfiguration configuration =
					configurations[0];

			/*
			 * Respect manual-start configurations.
			 */
			if (configuration.isManualStart()) {

				logger.info(
						"ETL configuration is marked for manual start. "
								+ "Automatic OpenMRS startup will be skipped.");

				return;
			}

			ProcessStarter processStarter;

			/*
			 * Create the same type of starter used by the traditional
			 * launcher.
			 */
			if (configuration.isDynamic()) {

				logger.info(
						"Creating DynamicProcessStarter for OpenMRS startup.");

				processStarter =
						new DynamicProcessStarter(configuration);

			} else {

				logger.info(
						"Creating ProcessStarter for OpenMRS startup.");

				processStarter =
						new ProcessStarter(configuration);
			}

			/*
			 * Keep the exact starter instance used by the running ETL.
			 *
			 * This is the instance that EtlProcessService will later expose
			 * to the OMOD monitor/controller layer.
			 */
			openMrsProcessStarter = processStarter;

			/*
			 * IMPORTANT:
			 *
			 * startProcess() is asynchronous.
			 *
			 * It initializes the ProcessController, creates the ETL starter
			 * thread and returns immediately.
			 */
			processStarter.startProcess();

			logger.info(
					"EPTS ETL startup requested successfully from OpenMRS.");

		} catch (ForbiddenOperationException e) {

			/*
			 * The OpenMRS activator should not be allowed to fail because
			 * the ETL startup failed.
			 */
			openMrsProcessStarter = null;

			logger.err(
					"Failed to initialize EPTS ETL from OpenMRS.",
					e);

			throw new RuntimeException(e);

		} catch (DBException e) {

			openMrsProcessStarter = null;

			logger.err(
					"Database error while starting EPTS ETL from OpenMRS.",
					e);

			throw e;

		} catch (IOException e) {

			openMrsProcessStarter = null;

			logger.err(
					"Failed to load EPTS ETL startup configuration.",
					e);

			throw e;

		} catch (RuntimeException e) {

			openMrsProcessStarter = null;

			logger.err(
					"Unexpected error while starting EPTS ETL from OpenMRS.",
					e);

			throw e;

		} catch (Exception e) {

			openMrsProcessStarter = null;

			logger.err(
					"Unexpected error while starting EPTS ETL from OpenMRS.",
					e);

			throw new RuntimeException(e);

		} finally {

			/*
			 * If startup failed, allow another OpenMRS startup attempt.
			 *
			 * If startup succeeded, the flag remains true.
			 */
			if (openMrsProcessStarter == null) {
				OPENMRS_START_TRIGGERED.set(false);
			}
		}
	}

	/**
	 * Returns the ProcessStarter used by the ETL started from OpenMRS.
	 *
	 * @return the actual OpenMRS ETL ProcessStarter, or null if it has not
	 * been started
	 */
	public static ProcessStarter getOpenMrsProcessStarter() {
		return openMrsProcessStarter;
	}

	/**
	 * Indicates whether an ETL ProcessStarter was created for OpenMRS.
	 *
	 * @return true when the OpenMRS ETL starter exists
	 */
	public static boolean isOpenMrsProcessStarted() {
		return openMrsProcessStarter != null;
	}

	/**
	 * Loads ETL configurations from command-line arguments.
	 *
	 * @param args configuration file arguments
	 * @return loaded configurations
	 * @throws IOException if configuration loading fails
	 */
	public static EtlConfiguration[] loadSyncConfig(
			String[] args) throws IOException {

		if (args == null || args.length == 0) {
			return new EtlConfiguration[0];
		}

		EtlConfiguration[] configurations =
				new EtlConfiguration[args.length];

		for (int i = 0; i < args.length; i++) {

			configurations[i] =
					EtlConfiguration.loadFromFile(
							new File(args[i]));
		}

		return configurations;
	}

	/**
	 * Loads ETL configurations from files.
	 *
	 * @param files configuration files
	 * @return loaded configurations
	 * @throws IOException if configuration loading fails
	 */
	public static EtlConfiguration[] loadSyncConfig(
			File[] files) throws IOException {

		if (files == null || files.length == 0) {
			return new EtlConfiguration[0];
		}

		EtlConfiguration[] configurations =
				new EtlConfiguration[files.length];

		for (int i = 0; i < files.length; i++) {

			configurations[i] =
					EtlConfiguration.loadFromFile(files[i]);
		}

		return configurations;
	}

	/**
	 * Compatibility method retained for callers that expect a run method
	 * on Main.
	 *
	 * <p>
	 * The actual startup is performed by {@link #main(String[])} or
	 * {@link #startFromOpenMRS()}.
	 * </p>
	 */
	public static void run() {
		// Compatibility method intentionally left empty.
	}
}
