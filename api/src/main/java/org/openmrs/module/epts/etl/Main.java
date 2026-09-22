package org.openmrs.module.epts.etl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openmrs.api.context.Context;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.controller.DynamicProcessStarter;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class Main implements Runnable {

	public static final String STARTUP_FILE_GLOBAL_PROPERTY =
			"epts.etl.startup.file";

	public static final String ENABLED_GLOBAL_PROPERTY =
			"epts.etl.enabled";

	private static final EtlLogger LOG =
			EtlLogger.getLogger(Main.class);

	private static final AtomicBoolean OPENMRS_START_TRIGGERED =
			new AtomicBoolean(false);

	/*
	 * The actual ProcessStarter created when ETL is started
	 * through OpenMRS/Tomcat.
	 *
	 * OMOD uses this same instance to monitor/control the
	 * running ETL process.
	 */
	private static volatile ProcessStarter openMrsProcessStarter;

	public static void main(String[] synConfigFiles)
			throws IOException, DBException {

		int i = 0;

		for (EtlConfiguration etlConfig : loadSyncConfig(synConfigFiles)) {

			ProcessStarter p;

			if (!etlConfig.isManualStart()) {

				i++;

				if (i > 1) {
					throw new ForbiddenOperationException(
							"Currently not supported multiple Configuration files");
				}

				p = etlConfig.isDynamic()
						? new DynamicProcessStarter(etlConfig)
						: new ProcessStarter(etlConfig);

				p.run();
			}
		}
	}

	/**
	 * Starts the ETL from the OpenMRS/Tomcat lifecycle.
	 *
	 * This method does not call main(), because main() is the
	 * traditional command-line entry point and executes the
	 * ProcessStarter synchronously.
	 */
	public static void startFromOpenMRS()
			throws IOException, DBException {

		/*
		 * Check whether automatic ETL startup is enabled.
		 */
		String enabledValue =
				Context.getAdministrationService()
						.getGlobalProperty(ENABLED_GLOBAL_PROPERTY);

		if (!Boolean.parseBoolean(enabledValue)) {

			LOG.info(
					"EPTS ETL is disabled. Skipping automatic startup.");

			return;
		}

		/*
		 * Prevent duplicate startup from the OpenMRS lifecycle.
		 */
		if (!OPENMRS_START_TRIGGERED.compareAndSet(false, true)) {

			LOG.info(
					"EPTS ETL startup was already triggered.");

			return;
		}

		try {

			/*
			 * Read the startup configuration.
			 */
			String startupFile =
					Context.getAdministrationService()
							.getGlobalProperty(
									STARTUP_FILE_GLOBAL_PROPERTY);

			if (startupFile == null
					|| startupFile.trim().isEmpty()) {

				throw new IllegalStateException(
						"The OpenMRS global property '"
								+ STARTUP_FILE_GLOBAL_PROPERTY
								+ "' must contain the ETL startup file path");
			}

			/*
			 * Keep using the existing configuration loading mechanism.
			 */
			List<EtlConfiguration> configurations =
					loadSyncConfig(
							new String[] {
									startupFile.trim()
							});

			if (configurations.isEmpty()) {

				throw new IllegalStateException(
						"No ETL configuration could be loaded from: "
								+ startupFile);
			}

			if (configurations.size() > 1) {

				throw new IllegalStateException(
						"Currently not supported multiple Configuration files");
			}

			EtlConfiguration configuration =
					configurations.get(0);

			/*
			 * Preserve the existing manual-start behavior.
			 */
			if (configuration.isManualStart()) {

				LOG.info(
						"ETL configuration is marked for manual start. "
								+ "Skipping automatic OpenMRS startup.");

				return;
			}

			/*
			 * Create exactly the same type of ProcessStarter
			 * used by the traditional launcher.
			 */
			ProcessStarter processStarter =
					configuration.isDynamic()
							? new DynamicProcessStarter(configuration)
							: new ProcessStarter(configuration);

			/*
			 * IMPORTANT:
			 *
			 * Keep the exact instance used by the running ETL.
			 * The OMOD will access this same object later.
			 */
			openMrsProcessStarter = processStarter;

			/*
			 * ProcessStarter.run() contains its own lifecycle and
			 * waits until the ETL is finalized.
			 *
			 * Therefore it must execute outside the OpenMRS startup
			 * thread.
			 */
			Thread etlThread = new Thread(
					processStarter,
					"epts-etl-starter");

			etlThread.setDaemon(true);
			etlThread.start();

			LOG.info(
					"EPTS ETL startup requested successfully from OpenMRS.");

		} catch (Exception e) {

			openMrsProcessStarter = null;
			OPENMRS_START_TRIGGERED.set(false);

			LOG.err(
					"Failed to start EPTS ETL from OpenMRS.",
					e);

			if (e instanceof IOException) {
				throw (IOException) e;
			}

			if (e instanceof DBException) {
				throw (DBException) e;
			}

			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}

			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the actual ProcessStarter used by the ETL
	 * started through OpenMRS.
	 */
	public static ProcessStarter getOpenMrsProcessStarter() {
		return openMrsProcessStarter;
	}

	/**
	 * Indicates whether the ETL was initialized through OpenMRS.
	 */
	public static boolean isOpenMrsProcessStarted() {
		return openMrsProcessStarter != null;
	}

	public static List<EtlConfiguration> loadSyncConfig(File[] syncConfigFiles)
			throws ForbiddenOperationException, IOException {

		String[] pathToFiles = new String[syncConfigFiles.length];

		for (int i = 0; i < syncConfigFiles.length; i++) {
			pathToFiles[i] =
					syncConfigFiles[i].getAbsolutePath();
		}

		return loadSyncConfig(pathToFiles);
	}

	public static List<EtlConfiguration> loadSyncConfig(
			String[] synConfigFiles)
			throws ForbiddenOperationException {

		List<EtlConfiguration> syncConfigs =
				new ArrayList<EtlConfiguration>(
						synConfigFiles.length);

		for (String confFile : synConfigFiles) {

			File file = new File(confFile);

			if (file.isDirectory()) {

				File[] files = file.listFiles();

				String[] paths = new String[files.length];

				for (int i = 0; i < files.length; i++) {
					paths[i] =
							files[i].getAbsolutePath();
				}

				syncConfigs.addAll(
						loadSyncConfig(paths));

			} else {

				EtlConfiguration conf;

				try {

					LOG.warn("LOADING CONF FILE " + file);

					conf =
							EtlConfiguration.loadFromFile(file);

					conf.validate();

					if (!conf.existsOnArray(syncConfigs)) {

						LOG.warn(
								"USING ETL CONFIGURATION FILE "
										+ conf.getRelatedConfFile()
										.getAbsolutePath());

						syncConfigs.add(conf);

					} else {

						throw new ForbiddenOperationException(
								"The configuration ["
										+ conf.getConfigFileName()
										+ "] exists in more than one files");
					}

				} catch (IOException e) {

					throw new RuntimeException(e);
				}
			}
		}

		return syncConfigs;
	}

	public static void runSync(EtlConfiguration configuration)
			throws DBException {

		ProcessController controller =
				new ProcessController(null, configuration);

		ThreadPoolService.getInstance()
				.createNewThreadPoolExecutor(
						controller.getControllerId())
				.execute(controller);
	}

	@Override
	public void run() {
	}
}