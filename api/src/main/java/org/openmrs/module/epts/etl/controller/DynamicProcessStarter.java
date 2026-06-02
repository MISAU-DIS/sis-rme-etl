package org.openmrs.module.epts.etl.controller;

import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.etl.model.EtlDynamicSearchParams;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

public class DynamicProcessStarter extends ProcessStarter implements ControllerStarter {
	
	public static CommonUtilities utilities = CommonUtilities.getInstance();
	
	private EtlConfiguration etlConfig;
	
	public DynamicProcessStarter(EtlConfiguration etlConfig) {
		super(etlConfig);
		
		if (!etlConfig.isDynamic()) {
			throw new ForbiddenOperationException("The etl cong " + etlConfig.getConfigFilePath() + " is not dynamic!!!");
		}
		
		this.etlConfig = etlConfig;
	}
	
	private List<EtlDatabaseObject> loadAvaliableSrcObjects(EtlConfiguration etlConfig) {
		OpenConnection conn = null;
		try {
			conn = etlConfig.getMainConnInfo().openConnection(etlConfig);
			
			if (!etlConfig.hasMainConnInfo()) {
				throw new ForbiddenOperationException("For dynamic etl configuration you must setup the mainConnInfo!!!");
			}
			
			etlConfig.getDynamicSrcConf().setParentConf(etlConfig);
			etlConfig.getDynamicSrcConf().setRelatedEtlConfig(etlConfig);
			etlConfig.getDynamicSrcConf().fullLoad(conn);
			
			EtlDynamicSearchParams searchParams = new EtlDynamicSearchParams(etlConfig.getDynamicSrcConf());
			
			return searchParams.search(null, null, null, conn, conn);
		}
		catch (DBException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (conn != null) {
				conn.finalizeConnection(etlConfig);
			}
		}
	}
	
	ProcessController init(EtlDatabaseObject src) throws ForbiddenOperationException, DBException {
		ProcessController currentController;
		
		logger.debug("Initializing ProcessController using " + this.etlConfig.getConfigFilePath());
		
		currentController = new ProcessController(this, this.etlConfig.cloneDynamic(src));
		
		currentController.setSchemaInfoSrc(src);
		
		logger.debug("ProcessController Initialized");
		
		return currentController;
	}
	
	@Override
	public void run() {
		
		try {
			applyStartupDebugDelayIfConfigured();
			
			this.currentController = null;
			
			for (EtlDatabaseObject src : loadAvaliableSrcObjects(this.etlConfig)) {
				this.currentController = init(src);
				
				ThreadPoolService.getInstance().createNewThreadPoolExecutor(this.currentController.getControllerId())
				        .execute(this.currentController);
				
				while (!this.currentController.isFinalized()) {
					TimeCountDown.sleep(30);
					
					logger.warn("THE APPLICATION IS STILL RUNING...", 60 * 15, true);
				}
			}
			
			if (this.currentController.isFinished()) {
				logger.warn("ALL JOBS ARE FINISHED");
			} else if (this.currentController.isStopped()) {
				logger.warn("ALL JOBS ARE STOPPED");
			}
		}
		catch (ForbiddenOperationException e) {
			throw e;
		}
		catch (DBException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void handleControllerFinalization(Controller c) {
		c.killSelfCreatedThreads();
		
		ProcessController controller = (ProcessController) c;
		
		if (c.isFinished()) {
			if (controller.getEtlConf().getChildConfigFilePath() != null) {
				throw new ForbiddenOperationException(
				        "You cannot configure childConfigFilePath on dynamic etl configuration!!!!");
			} else {
				controller.handleFinalization();
			}
		} else if (c.isStopped()) {
			logger.warn("THE APPLICATION IS STOPPING DUE STOP REQUESTED!");
			controller.handleFinalization();
		}
		
	}
	
}
