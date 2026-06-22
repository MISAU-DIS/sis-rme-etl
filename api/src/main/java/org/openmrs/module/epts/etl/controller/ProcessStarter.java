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

public class ProcessStarter implements ControllerStarter {
	
	public static CommonUtilities utilities = CommonUtilities.getInstance();
	
	private volatile boolean initialized;
	
	private EtlConfiguration etlConfig;
	
	protected ProcessController currentController;
	
	protected EtlLogger logger;
	
	private final Object LOCK = new Object();
	
	public ProcessStarter(EtlConfiguration etlConfig) {
		this.etlConfig = etlConfig;
		
		this.logger = new EtlLogger(this.getClass());
	}
	
	public ProcessController getCurrentController() {
		return currentController;
	}
	
	public ProcessStarter(EtlConfiguration etlConfig, Logger logger) {
		this.etlConfig = etlConfig;
		
		this.logger = new EtlLogger(logger);
	}
	
	public EtlLogger getLogger() {
		return logger;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * Initialize this starter. The initialization include the initialization of related
	 * controllers. Not that initialization doesn't start any process
	 * 
	 * @throws ForbiddenOperationException
	 * @throws IOException
	 * @throws DBException
	 */
	public void init() throws ForbiddenOperationException, DBException {
		if (this.initialized) {
			return;
		}
		
		synchronized (LOCK) {
			if (this.initialized) {
				return;
			}
			
			logger.debug("Initializing the ProcessStarter...");
			
			logger.debug("Initializing ProcessController using " + this.etlConfig.getConfigFilePath());
			
			this.currentController = new ProcessController(this, this.etlConfig);
			
			logger.debug("ProcessController Initialized");
			
			this.initialized = true;
			
			logger.debug("Starter Initialization Fineshed");
		}
		
	}
	
	@Override
	public void run() {
		try {
			applyStartupDebugDelayIfConfigured();
			
			init();
			
			ProcessController controller = this.currentController;
			
			if (controller.getRelatedEtlConf().isDisabled()) {
				logger.info("Operation " + controller.getControllerId() + " is disabled. Skipping...");
				controller.markAsFinished();
				handleControllerFinalization(controller);
				return;
			}
			
			startController(controller);
			
			waitUntilFinalized(controller);
			
			logFinalStatus(controller);
			
		}
		catch (Exception e) {
			logger.error("ProcessStarter failed", e);
			
			if (this.currentController != null) {
				this.currentController.requestStop();
			}
			
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void handleControllerFinalization(Controller controllerToFinalize) {
		controllerToFinalize.killSelfCreatedThreads();
		
		ProcessController controller = (ProcessController) controllerToFinalize;
		
		if (controllerToFinalize.isFinished()) {
			if (controller.getRelatedEtlConf().getChildConfigFilePath() != null) {
				try {
					EtlConfiguration childConfig = EtlConfiguration
					        .loadFromFile(new File(controller.getRelatedEtlConf().getChildConfigFilePath()));
					
					ProcessController child = new ProcessController(this, childConfig);
					
					this.currentController = child;
					
					if (this.currentController.isDisabled()) {
						logger.info("Operation " + this.currentController.getControllerId()
						        + " is marked as disabled... skipping...");
						
						this.currentController.markAsFinished();
						
						this.handleControllerFinalization(this.currentController);
					} else {
						ExecutorService executor = ThreadPoolService.getInstance()
						        .createNewThreadPoolExecutor(this.currentController.getControllerId());
						
						executor.execute(this.currentController);
						
						if (!controllerToFinalize.isDisabled()) {
							ThreadPoolService.getInstance().terminateTread(logger, controllerToFinalize.getControllerId(),
							    controllerToFinalize);
						}
					}
					
				}
				catch (DBException e) {
					throw new RuntimeException(e);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
				finally {
					controller.handleFinalization();
				}
			} else {
				controller.handleFinalization();
			}
		} else if (controllerToFinalize.isStopped()) {
			logger.warn("THE APPLICATION IS STOPPING DUE STOP REQUESTED!");
			controller.handleFinalization();
		}
		
	}
	
	public void applyStartupDebugDelayIfConfigured() {
		if (EtlLogger.determineLogLevel().equals(Level.DEBUG)) {
			TimeCountDown.sleep(10);
		}
	}
	
	private void startController(ProcessController controller) {
		ExecutorService executor = ThreadPoolService.getInstance().createNewThreadPoolExecutor(controller.getControllerId());
		
		executor.execute(controller);
	}
	
	private void waitUntilFinalized(ProcessController controller) {
		while (!controller.isFinalized()) {
			TimeCountDown.sleep(60);
			
			logger.warn("THE APPLICATION IS STILL RUNNING...", 60 * 15, true);
		}
	}
	
	private void logFinalStatus(ProcessController controller) {
		if (controller.isFinished()) {
			logger.warn("ALL JOBS ARE FINISHED");
		} else if (controller.isStopped()) {
			logger.warn("ALL JOBS ARE STOPPED");
		}
	}
	
}
