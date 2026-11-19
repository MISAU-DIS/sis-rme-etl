package org.openmrs.module.epts.etl.controller;

import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.etl.model.EtlDynamicSearchParams;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

public class DynamicProcessStarter extends ProcessStarter implements ControllerStarter {

	private static final EtlLogger LOG = EtlLogger.getLogger(DynamicProcessStarter.class);

	public static CommonUtilities utilities = CommonUtilities.getInstance();

	public DynamicProcessStarter(List<EtlConfiguration> configs, EtlLogger logger) {
		super(configs, logger);
	}

	public static DynamicProcessStarter init(List<EtlConfiguration> configs, EtlLogger logger)
			throws ForbiddenOperationException, DBException {

		if (configs == null || configs.size() != 1) {
			throw new ForbiddenOperationException(
					"DynamicProcessStarter requires exactly one Dynamic Configuration file");
		}

		if (!configs.get(0).isDynamic()) {
			throw new ForbiddenOperationException(
					"The ETL configuration is not dynamic: " + configs.get(0).getConfigFilePath());
		}

		DynamicProcessStarter starter = new DynamicProcessStarter(configs, logger);

		return starter;
	}

	@Override
	public ProcessStarter recreate() throws ForbiddenOperationException, DBException {
		return DynamicProcessStarter.init(configs, getLogger());
	}

	private List<EtlDatabaseObject> loadAvaliableSrcObjects(EtlConfiguration etlConfig) {
		OpenConnection conn = null;

		try {
			if (!etlConfig.hasMainConnInfo()) {
				throw new ForbiddenOperationException(
						"For dynamic etl configuration you must setup the mainConnInfo!!!");
			}

			conn = etlConfig.getMainConnInfo().openConnection(etlConfig);

			EtlItemConfiguration itemConf = EtlItemConfiguration.fastCreate(etlConfig.getDynamicSrcConf(), false, conn);
			itemConf.setRelatedEtlConf(etlConfig);

			etlConfig.getDynamicSrcConf().setParentConf(itemConf);
			etlConfig.getDynamicSrcConf().fullLoad(conn);

			EtlDynamicSearchParams searchParams = new EtlDynamicSearchParams(etlConfig.getDynamicSrcConf());

			return searchParams.search(null, null, null, conn, conn);
		} catch (DBException e) {
			throw new RuntimeException(e);
		} finally {

			if (conn != null) {
				conn.finalizeConnection(etlConfig);
			}
		}
	}

	ProcessController init(EtlConfiguration etlConfig, EtlDatabaseObject src)
			throws ForbiddenOperationException, DBException {
		ProcessController currentController;

		LOG.debug("Initializing ProcessController using " + etlConfig.getConfigFilePath());

		currentController = new ProcessController(this, etlConfig.cloneDynamic(src));

		currentController.setSchemaInfoSrc(src);

		LOG.debug("ProcessController Initialized");

		return currentController;
	}

	@Override
	public void run() {

		try {
			if (!markStarted()) {
				throw new ForbiddenOperationException("DynamicProcessStarter has already been started");
			}

			applyStartupDebugDelayIfConfigured();

			ProcessController controller = null;
			int discoveredSources = 0;

			for (EtlConfiguration etlConfig : this.configs) {

				for (EtlDatabaseObject src : loadAvaliableSrcObjects(etlConfig)) {
					discoveredSources++;

					controller = init(etlConfig, src);

					this.currentControllers.add(controller);

					ThreadPoolService.getInstance().createNewThreadPoolExecutor(controller.getControllerId())
							.execute(controller);

					while (!controller.isFinalized()) {
						TimeCountDown.sleep(30);

						LOG.warn("THE APPLICATION IS STILL RUNING...", 60 * 15, true);
					}
				}
			}

			if (discoveredSources == 0) {
				LOG.warn("No source objects were found for the dynamic ETL configuration");
				return;
			}

			if (controller != null && controller.isFinished()) {
				LOG.warn("ALL JOBS ARE FINISHED");
			} else if (controller != null && controller.isStopped()) {
				LOG.warn("ALL JOBS ARE STOPPED");
			}
		} catch (ForbiddenOperationException e) {
			throw e;
		} catch (DBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void handleControllerFinalization(Controller c) {
		c.killSelfCreatedThreads();

		ProcessController controller = (ProcessController) c;

		if (c.isFinished()) {

			if (controller.getRelatedEtlConf().getChildConfigFilePath() != null) {
				finalizeController(controller);
				throw new ForbiddenOperationException(
						"You cannot configure childConfigFilePath on dynamic etl configuration!!!!");
			}
		} else if (c.isStopped()) {
			LOG.warn("THE APPLICATION IS STOPPING DUE STOP REQUESTED!");
			finalizeController(controller);
		}

		if (c.isFinished()) {
			finalizeController(controller);
		}

	}

}
