
package org.openmrs.module.epts.etl.controller;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * Responsible for starting, restarting and controlling the lifecycle
 * of an ETL process.
 *
 * <p>
 * ProcessStarter does not execute the ETL itself. It creates and manages
 * a {@link ProcessController}, which is responsible for the actual ETL
 * execution.
 * </p>
 *
 * <p>
 * The class supports two execution modes:
 * </p>
 *
 * <ul>
 *     <li>
 *         {@link #run()} - traditional synchronous execution used by
 *         the original ETL launcher.
 *     </li>
 *     <li>
 *         {@link #startProcess()} - asynchronous execution used when
 *         the ETL is started from OpenMRS.
 *     </li>
 * </ul>
 *
 * <p>
 * The asynchronous mode is important for OpenMRS because the OpenMRS
 * startup thread must not remain blocked while the ETL is running.
 * </p>
 */
public class ProcessStarter implements ControllerStarter {

	/*
	 * Shared utility instance used by the ETL.
	 *
	 * Kept for compatibility with the existing ETL code.
	 */
	public static CommonUtilities utilities =
			CommonUtilities.getInstance();

	/*
	 * Indicates whether this ProcessStarter has already been initialized.
	 *
	 * volatile is used because the value can be accessed by different
	 * threads during asynchronous execution.
	 */
	private volatile boolean initialized;

	/*
	 * Indicates whether the current ETL execution has finished.
	 */
	private volatile boolean finalized;

	/*
	 * Configuration used to create the ProcessController.
	 *
	 * The same configuration is reused when the ETL is restarted.
	 */
	private final EtlConfiguration etlConfig;

	/*
	 * Controller responsible for the actual ETL execution.
	 *
	 * volatile guarantees that other threads, such as the OpenMRS
	 * monitor/controller thread, can see the current controller.
	 */
	protected volatile ProcessController currentController;

	/*
	 * Logger used by the ETL starter.
	 */
	private final EtlLogger logger;

	/*
	 * Lock used during initialization to avoid creating more than
	 * one ProcessController concurrently.
	 */
	private final Object LOCK = new Object();

	/**
	 * Creates a ProcessStarter using the ETL configuration.
	 *
	 * @param etlConfig ETL configuration
	 */
	public ProcessStarter(EtlConfiguration etlConfig) {
		this.etlConfig = etlConfig;
		this.logger = EtlLogger.getLogger(ProcessStarter.class);
	}

	/**
	 * Creates a ProcessStarter using an existing SLF4J logger.
	 *
	 * @param etlConfig ETL configuration
	 * @param logger existing logger
	 */
	public ProcessStarter(EtlConfiguration etlConfig, Logger logger) {
		this.etlConfig = etlConfig;
		this.logger = new EtlLogger(logger);
	}

	/**
	 * Returns the logger used by this starter.
	 *
	 * @return ETL logger
	 */
	public EtlLogger getLogger() {
		return logger;
	}

	/**
	 * Returns the ETL configuration associated with this starter.
	 *
	 * @return ETL configuration
	 */
	public EtlConfiguration getEtlConfig() {
		return etlConfig;
	}

	/**
	 * Returns the controller currently managed by this starter.
	 *
	 * <p>
	 * This method is also important for the OpenMRS monitor because
	 * it allows the module to inspect the actual running ETL controller.
	 * </p>
	 *
	 * @return current ProcessController
	 */
	public ProcessController getCurrentController() {
		return currentController;
	}

	/**
	 * Returns whether this starter has already been initialized.
	 *
	 * @return true if initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Returns whether the current ETL execution has been finalized.
	 *
	 * @return true if finalized
	 */
	public boolean isFinalized() {
		return finalized;
	}

	/**
	 * Starts the ETL process asynchronously.
	 *
	 * <p>
	 * This is the preferred method when the ETL is started from OpenMRS.
	 * It creates a separate daemon thread and returns immediately.
	 * </p>
	 *
	 * <p>
	 * This prevents OpenMRS from being blocked while the ETL is executing.
	 * </p>
	 *
	 * @return the ProcessController used by the ETL
	 * @throws ForbiddenOperationException if initialization fails
	 * @throws DBException if the process cannot access its database
	 */
	public synchronized ProcessController startProcess()
			throws ForbiddenOperationException, DBException {

		/*
		 * Make sure the ProcessController exists before starting.
		 */
		init();

		ProcessController controller = this.currentController;

		if (controller == null) {
			throw new IllegalStateException(
					"ProcessController was not initialized.");
		}

		/*
		 * If the ETL is already running, do not create another execution.
		 */
		if (controller.isRunning()) {

			logger.info(
					"ETL process is already running: "
							+ controller.getControllerId());

			return controller;
		}

		/*
		 * A finished or stopped controller cannot be reused.
		 * Create a new controller using the same ETL configuration.
		 */
		if (controller.isFinished() || controller.isStopped()) {

			logger.info(
					"Current controller is no longer active. "
							+ "Creating a new controller.");

			controller = createController();
			this.currentController = controller;
		}

		/*
		 * The new execution is not finalized yet.
		 */
		this.finalized = false;

		/*
		 * Keep the controller reference used by this particular
		 * execution. This is important because currentController
		 * can later change during a restart or child configuration.
		 */
		final ProcessController controllerToStart = controller;

		/*
		 * Start the ETL in a separate daemon thread.
		 *
		 * The caller returns immediately instead of waiting for
		 * the ETL to finish.
		 */
		Thread starterThread = new Thread(
				() -> runController(controllerToStart),
				"ETL-STARTER-" + controllerToStart.getControllerId());

		starterThread.setDaemon(true);
		starterThread.start();

		logger.info(
				"ETL process execution requested: "
						+ controllerToStart.getControllerId());

		return controllerToStart;
	}

	/**
	 * Initializes the ProcessStarter.
	 *
	 * <p>
	 * Initialization only creates the ProcessController.
	 * It does not start the ETL.
	 * </p>
	 *
	 * <p>
	 * The double-check locking pattern prevents multiple threads
	 * from creating different controllers at the same time.
	 * </p>
	 *
	 * @throws ForbiddenOperationException if initialization fails
	 * @throws DBException if the process cannot access its database
	 */
	public void init()
			throws ForbiddenOperationException, DBException {

		/*
		 * Fast path: already initialized.
		 */
		if (this.initialized) {
			return;
		}

		synchronized (LOCK) {

			/*
			 * Check again after acquiring the lock.
			 */
			if (this.initialized) {
				return;
			}

			logger.debug("Initializing the ProcessStarter...");

			logger.debug(
					"Initializing ProcessController using "
							+ this.etlConfig.getConfigFilePath());

			/*
			 * ProcessController represents the actual ETL execution.
			 */
			this.currentController =
					new ProcessController(this, this.etlConfig);

			logger.debug("ProcessController Initialized");

			this.initialized = true;

			logger.debug("Starter Initialization Finished");
		}
	}

	/**
	 * Traditional synchronous ETL execution.
	 *
	 * <p>
	 * This method is kept for compatibility with the existing ETL
	 * launcher and ControllerStarter contract.
	 * </p>
	 *
	 * <p>
	 * Unlike {@link #startProcess()}, this method waits until the
	 * current ETL execution finishes.
	 * </p>
	 */
	@Override
	public void run() {

		try {

			/*
			 * When DEBUG logging is enabled, preserve the existing
			 * startup delay used by the ETL.
			 */
			applyStartupDebugDelayIfConfigured();

			/*
			 * Make sure the controller exists.
			 */
			init();

			ProcessController controller = this.currentController;

			if (controller == null) {
				throw new IllegalStateException(
						"ProcessController was not initialized.");
			}

			/*
			 * A disabled configuration must not execute.
			 */
			if (controller.getRelatedEtlConf().isDisabled()) {

				logger.info(
						"Operation "
								+ controller.getControllerId()
								+ " is disabled. Skipping...");

				controller.markAsFinished();

				handleControllerFinalization(controller);

				return;
			}

			/*
			 * Start the actual ETL controller.
			 */
			this.finalized = false;

			startController(controller);

			/*
			 * Traditional execution waits here until the ETL finishes.
			 */
			waitUntilFinalized(controller);

			/*
			 * Write the final execution status to the log.
			 */
			logFinalStatus(controller);

		} catch (Exception e) {

			logger.err("ProcessStarter failed", e);

			/*
			 * If an unexpected error occurs, request the ETL to stop.
			 */
			if (this.currentController != null) {
				this.currentController.requestStop();
			}

			throw new RuntimeException(e);
		}
	}

	/**
	 * Executes one controller asynchronously.
	 *
	 * <p>
	 * This method is called by {@link #startProcess()}.
	 * </p>
	 *
	 * <p>
	 * It contains the same execution lifecycle as {@link #run()},
	 * but it runs inside the ETL starter thread.
	 * </p>
	 *
	 * @param controller controller to execute
	 */
	private void runController(ProcessController controller) {

		try {

			applyStartupDebugDelayIfConfigured();

			/*
			 * A disabled configuration is skipped without starting
			 * the actual ETL.
			 */
			if (controller.getRelatedEtlConf().isDisabled()) {

				logger.info(
						"Operation "
								+ controller.getControllerId()
								+ " is disabled. Skipping...");

				controller.markAsFinished();

				handleControllerFinalization(controller);

				return;
			}

			/*
			 * Submit the controller to the ETL thread pool.
			 */
			startController(controller);

			/*
			 * Wait inside this background thread.
			 *
			 * The OpenMRS thread is not blocked.
			 */
			waitUntilFinalized(controller);

			logFinalStatus(controller);

		} catch (Exception e) {

			logger.err(
					"ETL asynchronous execution failed for controller "
							+ controller.getControllerId(),
					e);

			/*
			 * Make a best effort to stop the controller after
			 * an unexpected execution error.
			 */
			controller.requestStop();
		}
	}

	/**
	 * Creates a new ProcessController using the current ETL configuration.
	 *
	 * <p>
	 * A new controller is required when an old controller has already
	 * finished or has been stopped.
	 * </p>
	 *
	 * @return new ProcessController
	 * @throws DBException if controller initialization requires database
	 *                     access and that access fails
	 */
	protected ProcessController createController()
			throws DBException {

		return new ProcessController(this, this.etlConfig);
	}

	/**
	 * Handles the finalization of a ProcessController.
	 *
	 * <p>
	 * If the current configuration has a child configuration, the child
	 * ETL is loaded and executed automatically.
	 * </p>
	 *
	 * <p>
	 * Otherwise the current ETL execution is marked as finalized.
	 * </p>
	 *
	 * @param controllerToFinalize controller that reached its final state
	 */
	@Override
	public void handleControllerFinalization(
			Controller controllerToFinalize) {

		/*
		 * Stop threads created by the controller.
		 */
		controllerToFinalize.killSelfCreatedThreads();

		ProcessController controller =
				(ProcessController) controllerToFinalize;

		/*
		 * Process only controllers that finished normally.
		 */
		if (controllerToFinalize.isFinished()) {

			/*
			 * Check whether the current ETL configuration points
			 * to another configuration file.
			 */
			if (controller.getRelatedEtlConf().getChildConfigFilePath() != null) {

				try {

					logger.warn(
							"Loading next child conf file "
									+ controller.getRelatedEtlConf()
									.getChildConfigFilePath());

					/*
					 * Load the child configuration.
					 */
					EtlConfiguration childConfig =
							EtlConfiguration.loadFromFile(
									new File(
											controller.getRelatedEtlConf()
													.getChildConfigFilePath()));

					/*
					 * Create a controller for the child configuration.
					 */
					ProcessController child =
							new ProcessController(this, childConfig);

					this.currentController = child;

					/*
					 * Skip disabled child configurations.
					 */
					if (this.currentController.isDisabled()) {

						logger.info(
								"Operation "
										+ this.currentController
										.getControllerId()
										+ " is marked as disabled... "
										+ "skipping...");

						this.currentController.markAsFinished();

						this.handleControllerFinalization(
								this.currentController);

					} else {

						/*
						 * Execute the child configuration using
						 * the normal ETL thread pool.
						 */
						ExecutorService executor =
								ThreadPoolService.getInstance()
										.createNewThreadPoolExecutor(
												this.currentController
														.getControllerId());

						executor.execute(this.currentController);

						/*
						 * Terminate resources belonging to the
						 * previous controller.
						 */
						if (!controllerToFinalize.isDisabled()) {

							ThreadPoolService.getInstance()
									.terminateTread(
											logger,
											controllerToFinalize
													.getControllerId(),
											controllerToFinalize);
						}
					}

				} catch (DBException e) {

					throw new RuntimeException(e);

				} catch (IOException e) {

					throw new RuntimeException(e);

				} finally {

					/*
					 * Allow the previous controller to perform
					 * its own finalization logic.
					 */
					controller.handleFinalization();
				}

			} else {

				/*
				 * There is no child configuration.
				 * This is the end of the ETL chain.
				 */
				controller.handleFinalization();

				this.finalized = true;
			}

		} else if (controllerToFinalize.isStopped()) {

			logger.warn(
					"THE APPLICATION IS STOPPING DUE STOP REQUESTED!");

			controller.handleFinalization();

			this.finalized = true;
		}
	}

	/**
	 * Applies the existing startup delay when DEBUG logging is enabled.
	 *
	 * <p>
	 * This behavior is preserved from the original implementation.
	 * </p>
	 */
	public void applyStartupDebugDelayIfConfigured() {

		if (EtlLogger.determineLogLevel().equals(Level.DEBUG)) {
			TimeCountDown.sleep(10);
		}
	}

	/**
	 * Submits a ProcessController to the ETL thread pool.
	 *
	 * <p>
	 * ProcessController contains the actual ETL execution logic.
	 * </p>
	 *
	 * @param controller controller to execute
	 */
	private void startController(ProcessController controller) {

		ExecutorService executor =
				ThreadPoolService.getInstance()
						.createNewThreadPoolExecutor(
								controller.getControllerId());

		executor.execute(controller);
	}

	/**
	 * Waits for the current controller to reach a final state.
	 *
	 * <p>
	 * This method is only called from the ETL starter/background thread,
	 * never directly from the OpenMRS startup thread.
	 * </p>
	 *
	 * @param controller controller being monitored
	 */
	private void waitUntilFinalized(ProcessController controller) {

		while (!finalized
				&& !controller.isFinished()
				&& !controller.isStopped()) {

			/*
			 * Check the controller state periodically.
			 *
			 * The previous implementation waited 60 seconds, which
			 * made lifecycle detection unnecessarily slow.
			 */
			TimeCountDown.sleep(2);

			logger.debug(
					"ETL process is still running: "
							+ controller.getControllerId());
		}
	}

	/**
	 * Logs the final status of an ETL execution.
	 *
	 * @param controller completed controller
	 */
	private void logFinalStatus(ProcessController controller) {

		if (controller.isFinished()) {

			logger.warn("ALL JOBS ARE FINISHED");

		} else if (controller.isStopped()) {

			logger.warn("ALL JOBS ARE STOPPED");
		}
	}

	/**
	 * Restarts the current ETL process.
	 *
	 * <p>
	 * If an ETL process is running, a stop request is sent first.
	 * The method waits for the old controller to stop and then creates
	 * a completely new ProcessController using the same configuration.
	 * </p>
	 *
	 * <p>
	 * The new execution is started asynchronously, so the caller
	 * does not have to wait for the ETL to finish.
	 * </p>
	 *
	 * @return the new ProcessController
	 * @throws ForbiddenOperationException if initialization fails
	 * @throws DBException if the process cannot access its database
	 */
	public synchronized ProcessController restartProcess()
			throws ForbiddenOperationException, DBException {

		/*
		 * Make sure the starter has a controller.
		 */
		init();

		ProcessController oldController = this.currentController;

		if (oldController != null) {

			/*
			 * Only request a stop when the old controller is still active.
			 */
			if (oldController.isRunning()
					|| (!oldController.isStopped()
					&& !oldController.isFinished())) {

				logger.info(
						"Requesting stop of current ETL process before restart: "
								+ oldController.getControllerId());

				oldController.requestStop();

				/*
				 * Wait up to 60 seconds for the old controller to stop.
				 */
				int attempts = 0;
				final int maxAttempts = 60;

				while (!oldController.isStopped()
						&& !oldController.isFinished()
						&& attempts < maxAttempts) {

					TimeCountDown.sleep(1);
					attempts++;
				}

				/*
				 * Do not start a new ETL if the previous one could not
				 * be stopped safely.
				 */
				if (!oldController.isStopped()
						&& !oldController.isFinished()) {

					throw new IllegalStateException(
							"Unable to restart ETL process because the "
									+ "current process "
									+ oldController.getControllerId()
									+ " did not stop within the expected time.");
				}
			}
		}

		logger.info(
				"Creating a new ProcessController for ETL restart.");

		/*
		 * Create a completely new controller using the same configuration.
		 */
		ProcessController newController = createController();

		/*
		 * Replace the current controller reference.
		 *
		 * This is the controller that the monitor will see after restart.
		 */
		this.currentController = newController;

		this.finalized = false;

		logger.info(
				"ETL process restarted with controller: "
						+ newController.getControllerId());

		final ProcessController controllerToStart = newController;

		/*
		 * Start the new execution asynchronously.
		 */
		Thread starterThread = new Thread(
				() -> runController(controllerToStart),
				"ETL-STARTER-" + controllerToStart.getControllerId());

		starterThread.setDaemon(true);
		starterThread.start();

		logger.info(
				"ETL restart execution requested: "
						+ controllerToStart.getControllerId());

		return controllerToStart;
	}
}
