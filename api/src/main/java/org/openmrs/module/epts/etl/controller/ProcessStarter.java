package org.openmrs.module.epts.etl.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.slf4j.event.Level;

public class ProcessStarter implements ControllerStarter {

	public static CommonUtilities utilities = CommonUtilities.getInstance();

	private volatile boolean stopRequested;

	protected List<ProcessController> currentControllers;

	protected List<EtlConfiguration> configs;

	private final EtlLogger logger;

	protected ProcessStarter(List<EtlConfiguration> configs, EtlLogger logger) {
		this.configs = configs;

		this.currentControllers = new ArrayList<>();

		if (logger == null) {

			this.logger = EtlLogger.getLogger(ProcessStarter.class);
		} else {
			this.logger = logger;
		}
	}

	public List<ProcessController> getCurrentControllers() {
		return currentControllers;
	}

	public ProcessController getCurrentController(EtlConfiguration config) {

		for (ProcessController controller : currentControllers) {

			if (controller.getRelatedEtlConf().equals(config)) {
				return controller;
			}
		}

		return null;
	}

	public EtlConfiguration getEtlConfig(ProcessController controller) {
		return controller.getRelatedEtlConf();
	}

	public EtlLogger getLogger() {
		return logger;
	}

	/**
	 * Initialize this starter. The initialization include the initialization of
	 * related controllers. Not that initialization doesn't start any process
	 * 
	 * @throws ForbiddenOperationException
	 * @throws IOException
	 * @throws DBException
	 */
	public static ProcessStarter init(List<EtlConfiguration> configs, EtlLogger logger)
			throws ForbiddenOperationException, DBException {
		ProcessStarter starter = new ProcessStarter(configs, logger);

		logger.debug("Initializing the ProcessStarter...");

		for (EtlConfiguration config : configs) {
			logger.debug("Initializing ProcessController for conmfiguration {} ", config.getConfigFilePath());

			starter.currentControllers.add(new ProcessController(starter, config));

			logger.debug("ProcessController Initialized");
		}

		logger.debug("Starter Initialization Fineshed");

		return starter;
	}

	@Override
	public void run() {

		try {
			this.applyStartupDebugDelayIfConfigured();

			for (ProcessController controller : this.currentControllers) {

				if (controller.getRelatedEtlConf().isDisabled()) {
					logger.info("Operation " + controller.getControllerId() + " is disabled. Skipping...");
					controller.markAsFinished();
					handleControllerFinalization(controller);
					return;
				}

				startController(controller);
			}

			waitUntilFinalized();
			logFinalStatus();
		} catch (Exception e) {
			logger.err("ProcessStarter failed", e);

			this.requestStop();
		}
	}

	public synchronized void requestStop() {

		if (this.stopRequested) {
			return;
		} else {

			for (ProcessController controller : this.currentControllers) {
				controller.requestStop();
			}
		}
	}

	private synchronized void removeFinalizedController(ProcessController controller) {
		this.currentControllers.remove(controller);
	}

	@Override
	public void handleControllerFinalization(Controller controllerToFinalize) {
		controllerToFinalize.killSelfCreatedThreads();

		ProcessController controller = (ProcessController) controllerToFinalize;

		if (controllerToFinalize.isFinished()) {

			if (controller.getRelatedEtlConf().getChildConfigFilePath() != null) {

				try {
					logger.warn(
							"Loading next child conf file " + controller.getRelatedEtlConf().getChildConfigFilePath());

					EtlConfiguration childConfig = EtlConfiguration
							.loadFromFile(new File(controller.getRelatedEtlConf().getChildConfigFilePath()));

					while (childConfig != null && childConfig.isDisabled()) {
						logger.warn("Disabled config was found on the tree", childConfig.getConfigFilePath());

						childConfig = EtlConfiguration.loadFromFile(new File(childConfig.getChildConfigFilePath()));
					}

					if (childConfig != null) {
						ProcessController childController = new ProcessController(this, childConfig);

						this.currentControllers.add(childController);

						if (childController.isDisabled()) {
							logger.info("Operation " + childController.getControllerId()
									+ " is marked as disabled... skipping...");

							childController.markAsFinished();

							this.handleControllerFinalization(childController);
						} else {
							ExecutorService executor = ThreadPoolService.getInstance()
									.createNewThreadPoolExecutor(childController.getControllerId());

							executor.execute(childController);

							if (!controllerToFinalize.isDisabled()) {
								ThreadPoolService.getInstance().terminateTread(logger,
										controllerToFinalize.getControllerId(), controllerToFinalize);
							}
						}
					}
				} catch (DBException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					finalizeController(controller);
				}
			} else {
				finalizeController(controller);
			}
		} else if (controllerToFinalize.isStopped()) {
			finalizeController(controller);
		}
	}

	private void finalizeController(ProcessController controller) {
		controller.handleFinalization();
		this.removeFinalizedController(controller);
	}

	public void applyStartupDebugDelayIfConfigured() {

		if (EtlLogger.determineLogLevel().equals(Level.DEBUG)) {
			TimeCountDown.sleep(10);
		}
	}

	protected void startController(ProcessController controller) {
		ExecutorService executor = ThreadPoolService.getInstance()
				.createNewThreadPoolExecutor(controller.getControllerId());

		executor.execute(controller);
	}

	public void waitUntilFinalized() {

		while (!this.isFinalized()) {
			TimeCountDown.sleep(15);

			logger.warn("THE APPLICATION IS STILL RUNNING...", 60 * 15, true);
		}
	}

	public boolean isFinalized() {
		return this.currentControllers.isEmpty();
	}

	private void logFinalStatus() {

		if (this.stopRequested) {
			logger.warn("ALL JOBS ARE STOPPED");
		} else {
			logger.warn("ALL JOBS ARE FINISHED");
		}
	}

	public synchronized void runExternalController(ProcessController controller) {

		if (this.getCurrentController(controller.getRelatedEtlConf()) != null) {
			throw new ForbiddenOperationException(
					"The controller youu tried to start is already started within this starter!!");
		}

		this.currentControllers.add(controller);

		startController(controller);
	}
}
