package org.openmrs.module.epts.etl;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openmrs.api.context.Context;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.controller.DynamicProcessStarter;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.controller.ProcessStarter;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class Main implements Runnable {

	public static final String STARTUP_FILE_GLOBAL_PROPERTY = "eptsetl.startupFile";

	public static final String ENABLED_GLOBAL_PROPERTY = "eptsetl.enabled";

	public static final String STARTUP_READINESS_TIMEOUT_SECONDS_GLOBAL_PROPERTY = "eptsetl.openmrsReadinessTimeoutSeconds";

	public static final String STARTUP_READINESS_POLL_SECONDS_GLOBAL_PROPERTY = "eptsetl.openmrsReadinessPollSeconds";

	public static final String STARTUP_STABILIZATION_SECONDS_GLOBAL_PROPERTY = "eptsetl.openmrsStartupStabilizationSeconds";

	public static final String OPENMRS_BASE_URL_GLOBAL_PROPERTY = "eptsetl.openmrsBaseUrl";

	private static final long DEFAULT_OPENMRS_READINESS_TIMEOUT_SECONDS = 600L;

	private static final long DEFAULT_OPENMRS_READINESS_POLL_SECONDS = 5L;

	private static final long DEFAULT_OPENMRS_STARTUP_STABILIZATION_SECONDS = 30L;

	public static final EtlLogger LOG = EtlLogger.getLogger(Main.class);

	private static final AtomicBoolean OPENMRS_START_TRIGGERED = new AtomicBoolean(false);

	/*
	 * The actual ProcessStarter created when ETL is started through OpenMRS/Tomcat.
	 *
	 * OMOD uses this same instance to monitor/control the running ETL process.
	 */
	private static volatile ProcessStarter openMrsProcessStarter;

	/* Cached during the privileged module-startup callback for read-only UI use. */
	private static volatile boolean openMrsEtlEnabled;

	public static void main(String[] synConfigFiles) throws IOException, DBException {
		ProcessStarter starter = performeInitialization(synConfigFiles);

		if (starter == null) {
			LOG.info("No automatic ETL configurations were found. Nothing to start.");
			return;
		}
		starter.run();
	}

	private static ProcessStarter performeInitialization(String[] synConfigFiles)
			throws ForbiddenOperationException, DBException {
		List<EtlConfiguration> dynamics = new ArrayList<>();
		List<EtlConfiguration> statics = new ArrayList<>();

		for (EtlConfiguration etlConfig : loadSyncConfig(synConfigFiles)) {

			if (!etlConfig.isManualStart()) {

				if (etlConfig.isDynamic()) {
					dynamics.add(etlConfig);
				} else {
					statics.add(etlConfig);
				}

			}
		}

		ProcessStarter starter = null;

		if (!statics.isEmpty() && !dynamics.isEmpty()) {
			throw new ForbiddenOperationException(
					"Mixing of static and dynamic configuration is currently not allowed!");
		}

		if (!statics.isEmpty()) {
			starter = ProcessStarter.init(statics, LOG);
		} else if (!dynamics.isEmpty()) {
			starter = DynamicProcessStarter.init(dynamics, LOG);
		}

		return starter;
	}

	/**
	 * Starts the ETL from the OpenMRS/Tomcat lifecycle.
	 *
	 * This method does not call main(), because main() is the traditional
	 * command-line entry point and executes the ProcessStarter synchronously.
	 */
	public static void startFromOpenMRS() throws IOException, DBException {

		/*
		 * Check whether automatic ETL startup is enabled.
		 */
		String enabledValue = Context.getAdministrationService().getGlobalProperty(ENABLED_GLOBAL_PROPERTY);
		openMrsEtlEnabled = Boolean.parseBoolean(enabledValue);

		/*
		 * Capture the module properties while the activator still has its privileged
		 * OpenMRS context. Background ETL threads can then read this snapshot without
		 * requiring Get Global Properties merely to discover their credentials.
		 */
		EtlDataConfiguration.loadOpenMrsGlobalProperties();

		if (!openMrsEtlEnabled) {

			LOG.info("EPTS ETL is disabled. Skipping automatic startup.");

			return;
		}

		/*
		 * Prevent duplicate startup from the OpenMRS lifecycle.
		 */
		if (!OPENMRS_START_TRIGGERED.compareAndSet(false, true)) {

			LOG.info("EPTS ETL startup was already triggered.");

			return;
		}

		try {
			long readinessTimeoutSeconds = getNonNegativeGlobalProperty(
					STARTUP_READINESS_TIMEOUT_SECONDS_GLOBAL_PROPERTY, DEFAULT_OPENMRS_READINESS_TIMEOUT_SECONDS);
			long readinessPollSeconds = getPositiveGlobalProperty(STARTUP_READINESS_POLL_SECONDS_GLOBAL_PROPERTY,
					DEFAULT_OPENMRS_READINESS_POLL_SECONDS);
			long startupStabilizationSeconds = getNonNegativeGlobalProperty(
					STARTUP_STABILIZATION_SECONDS_GLOBAL_PROPERTY, DEFAULT_OPENMRS_STARTUP_STABILIZATION_SECONDS);
			String openMrsBaseUrl = Context.getAdministrationService().getGlobalProperty(OPENMRS_BASE_URL_GLOBAL_PROPERTY);

			if (openMrsBaseUrl == null || openMrsBaseUrl.trim().isEmpty())
				throw new IllegalStateException("The OpenMRS global property '" + OPENMRS_BASE_URL_GLOBAL_PROPERTY
						+ "' must contain the OpenMRS base URL");

			/*
			 * Read the startup configuration.
			 */
			String startupFile = Context.getAdministrationService().getGlobalProperty(STARTUP_FILE_GLOBAL_PROPERTY);

			if (startupFile == null || startupFile.trim().isEmpty()) {

				throw new IllegalStateException("The OpenMRS global property '" + STARTUP_FILE_GLOBAL_PROPERTY
						+ "' must contain the ETL startup file path");
			}

			String[] startupFiles = startupFile.split(",");

			/*
			 * Configuration loading and ProcessStarter initialization must also happen in
			 * this worker. Both may access the database and are intentionally postponed
			 * until OpenMRS is serving HTTP requests normally.
			 *
			 * ProcessStarter.run() contains its own lifecycle and waits until the ETL is
			 * finalized, so none of this work may execute in the OpenMRS startup thread.
			 */
			Thread etlThread = new Thread(
					() -> startWhenOpenMrsIsReady(startupFiles, readinessTimeoutSeconds, readinessPollSeconds,
							startupStabilizationSeconds, openMrsBaseUrl.trim()),
					"epts-etl-starter");

			etlThread.setDaemon(true);
			etlThread.start();

			LOG.info("EPTS ETL startup requested successfully. Waiting up to " + readinessTimeoutSeconds
					+ " second(s) for OpenMRS readiness, checking every " + readinessPollSeconds
					+ " second(s), with a final stabilization period of " + startupStabilizationSeconds + " second(s).");

		} catch (Exception e) {

			openMrsProcessStarter = null;
			OPENMRS_START_TRIGGERED.set(false);

			LOG.err("Failed to start EPTS ETL from OpenMRS.", e);

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

	private static long getNonNegativeGlobalProperty(String propertyName, long defaultValue) {
		String configuredValue = Context.getAdministrationService().getGlobalProperty(propertyName);

		if (configuredValue == null || configuredValue.trim().isEmpty())
			return defaultValue;

		try {
			long value = Long.parseLong(configuredValue.trim());
			if (value < 0)
				throw new NumberFormatException("negative value");
			return value;
		} catch (NumberFormatException exception) {
			throw new IllegalStateException("The OpenMRS global property '" + propertyName
					+ "' must be a non-negative integer, but was: " + configuredValue, exception);
		}
	}

	private static long getPositiveGlobalProperty(String propertyName, long defaultValue) {
		long value = getNonNegativeGlobalProperty(propertyName, defaultValue);
		if (value == 0)
			throw new IllegalStateException(
					"The OpenMRS global property '" + propertyName + "' must be greater than zero");
		return value;
	}

	private static void startWhenOpenMrsIsReady(String[] startupFiles, long timeoutSeconds, long pollSeconds,
			long stabilizationSeconds, String openMrsBaseUrl) {

		try {
			long timeoutNanos = Math.multiplyExact(timeoutSeconds, 1_000_000_000L);
			long stabilizationNanos = Math.multiplyExact(stabilizationSeconds, 1_000_000_000L);
			long deadline = System.nanoTime() + timeoutNanos;
			long readySince = -1L;

			while (true) {
				long now = System.nanoTime();
				int connectionTimeoutMillis = (int) Math.min(Integer.MAX_VALUE,
						Math.multiplyExact(pollSeconds, 1000L));

				if (isOpenMrsReady(openMrsBaseUrl, connectionTimeoutMillis)) {
					if (readySince < 0) {
						readySince = now;
						LOG.info("OpenMRS context and HTTP connector are available. Waiting for startup stabilization.");
					}

					if (now - readySince >= stabilizationNanos)
						break;
				} else {
					readySince = -1L;
				}

				if (timeoutSeconds == 0 || System.nanoTime() - deadline >= 0)
					throw new IllegalStateException(
							"OpenMRS did not become ready within " + timeoutSeconds + " second(s)");

				Thread.sleep(Math.multiplyExact(pollSeconds, 1000L));
			}

			LOG.info("OpenMRS startup is complete and stable. Initializing EPTS ETL.");
			ProcessStarter processStarter = performeInitialization(startupFiles);

			if (processStarter == null) {
				LOG.info("No automatic ETL configurations were found. OpenMRS startup will not launch an ETL process.");
				OPENMRS_START_TRIGGERED.set(false);
				return;
			}

			/* Expose the starter only after OpenMRS readiness and ETL initialization. */
			openMrsProcessStarter = processStarter;
			LOG.info("EPTS ETL initialization completed. Starting ETL processing.");
			processStarter.run();
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			openMrsProcessStarter = null;
			OPENMRS_START_TRIGGERED.set(false);
			LOG.warn("EPTS ETL startup was cancelled while waiting for OpenMRS readiness.");
		} catch (ArithmeticException exception) {
			openMrsProcessStarter = null;
			OPENMRS_START_TRIGGERED.set(false);
			LOG.err("The configured EPTS ETL readiness timeout or polling interval is too large.", exception);
		} catch (Exception exception) {
			openMrsProcessStarter = null;
			OPENMRS_START_TRIGGERED.set(false);
			LOG.err("Failed to start EPTS ETL while waiting for OpenMRS readiness.", exception);
		}
	}

	private static boolean isOpenMrsReady(String openMrsBaseUrl, int connectionTimeoutMillis) {
		if (Context.isRefreshingContext())
			return false;

		boolean sessionOpenedHere = false;

		try {

			if (!Context.isSessionOpen()) {
				Context.openSession();
				sessionOpenedHere = true;
			}

			/*
			 * Do not invoke privileged API methods here. Opening a Hibernate session
			 * successfully after the Spring context refresh is enough to confirm that the
			 * OpenMRS service infrastructure is available to this worker thread.
			 */
			return Context.getAdministrationService() != null
					&& isOpenMrsHttpConnectorAvailable(openMrsBaseUrl, connectionTimeoutMillis);
		} catch (RuntimeException exception) {
			LOG.warn("OpenMRS is not ready yet: " + exception.getMessage());
			return false;
		} finally {
			if (sessionOpenedHere)
				Context.closeSession();
		}
	}

	private static boolean isOpenMrsHttpConnectorAvailable(String openMrsBaseUrl, int connectionTimeoutMillis) {
		HttpURLConnection connection = null;

		try {
			String sessionUrl = openMrsBaseUrl.endsWith("/") ? openMrsBaseUrl + "ws/rest/v1/session"
					: openMrsBaseUrl + "/ws/rest/v1/session";
			int requestTimeoutMillis = Math.min(connectionTimeoutMillis, 2000);

			connection = (HttpURLConnection) new URL(sessionUrl).openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(requestTimeoutMillis);
			connection.setReadTimeout(requestTimeoutMillis);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Accept", "application/json");

			int responseCode = connection.getResponseCode();
			return responseCode == HttpURLConnection.HTTP_OK
					|| responseCode == HttpURLConnection.HTTP_UNAUTHORIZED
					|| responseCode == HttpURLConnection.HTTP_FORBIDDEN;
		} catch (IOException | IllegalArgumentException exception) {
			LOG.warn("OpenMRS HTTP application is not ready yet at " + openMrsBaseUrl + ": " + exception.getMessage());
			return false;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	/**
	 * Returns the actual ProcessStarter used by the ETL started through OpenMRS.
	 */
	public static ProcessStarter getOpenMrsProcessStarter() {
		return openMrsProcessStarter;
	}

	/** Restarts a completed OpenMRS-managed starter with fresh controllers. */
	public static synchronized boolean restartFromOpenMRS() throws DBException {
		ProcessStarter current = openMrsProcessStarter;
		if (current == null || !current.isFinalized())
			return false;

		ProcessStarter replacement = current.recreate();
		openMrsProcessStarter = replacement;

		Thread etlThread = new Thread(replacement, "epts-etl-starter-restart");
		etlThread.setDaemon(true);
		etlThread.start();
		return true;
	}

	/**
	 * Indicates whether the ETL was initialized through OpenMRS.
	 */
	public static boolean isOpenMrsProcessStarted() {
		return openMrsProcessStarter != null;
	}

	/**
	 * Returns the enabled flag captured during OpenMRS module startup without
	 * requiring the caller to have the Get Global Properties privilege.
	 */
	public static boolean isOpenMrsEtlEnabled() {
		return openMrsEtlEnabled;
	}

	public static List<EtlConfiguration> loadSyncConfig(File[] syncConfigFiles)
			throws ForbiddenOperationException, IOException {

		String[] pathToFiles = new String[syncConfigFiles.length];

		for (int i = 0; i < syncConfigFiles.length; i++) {
			pathToFiles[i] = syncConfigFiles[i].getAbsolutePath();
		}

		return loadSyncConfig(pathToFiles);
	}

	public static List<EtlConfiguration> loadSyncConfig(String[] synConfigFiles) throws ForbiddenOperationException {

		List<EtlConfiguration> syncConfigs = new ArrayList<EtlConfiguration>(synConfigFiles.length);

		for (String confFile : synConfigFiles) {

			File file = new File(confFile);

			if (file.isDirectory()) {

				File[] files = file.listFiles();

				String[] paths = new String[files.length];

				for (int i = 0; i < files.length; i++) {
					paths[i] = files[i].getAbsolutePath();
				}

				syncConfigs.addAll(loadSyncConfig(paths));

			} else {

				EtlConfiguration conf;

				try {

					LOG.warn("LOADING CONF FILE " + file);

					conf = EtlConfiguration.loadFromFile(file);

					conf.validate();

					if (!conf.existsOnArray(syncConfigs)) {

						LOG.warn("USING ETL CONFIGURATION FILE " + conf.getRelatedConfFile().getAbsolutePath());

						syncConfigs.add(conf);

					} else {

						throw new ForbiddenOperationException(
								"The configuration [" + conf.getConfigFileName() + "] exists in more than one files");
					}

				} catch (IOException e) {

					throw new RuntimeException(e);
				}
			}
		}

		return syncConfigs;
	}

	public static void runSync(EtlConfiguration configuration) throws DBException {

		ProcessController controller = new ProcessController(null, configuration);

		ThreadPoolService.getInstance().createNewThreadPoolExecutor(controller.getControllerId()).execute(controller);
	}

	@Override
	public void run() {
	}
}
