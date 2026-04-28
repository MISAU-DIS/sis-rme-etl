package org.openmrs.module.epts.etl.controller;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.EtlOperationType;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.engine.TaskProcessor;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.OperationProgressInfo;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;
import org.openmrs.module.epts.etl.utilities.EptsEtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.MonitoredOperation;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeController;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class represent a controller of an synchronization operation. Eg. Export data from tables to
 * JSON files.
 * 
 * @author jpboane
 */
public abstract class StageTableGeneratorController<T extends EtlDatabaseObject> extends OperationController<T> implements Controller {
	
	public StageTableGeneratorController(ProcessController processController, EtlOperationConfig operationConfig) {
		super(processController, StageTableGeneratorController.generateOperationConfig(processController.getEtlConf()));
	}
	
	@Override
	protected synchronized void runInSequencialMode() throws DBException {
		changeStatusToRunning();
		
		List<EtlItemConfiguration> allSync = null;
		
		if (this.getEtlConfiguration().hasTestingItem()) {
			allSync = utilities().parseToList(this.getEtlConfiguration().getTestingEtlItemConfiguration());
			
			logInfo("Working on testing item");
		} else {
			allSync = getProcessController().getEtlConf().getEtlItemConfiguration();
		}
		
		this.enginesActivititieMonitor = new ArrayList<Engine<T>>();
		
		logTrace("Running the Process in Sequencial mode!");
		
		for (EtlItemConfiguration config : allSync) {
			if (config.isDisabled()) {
				logDebug(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Confinguration '"
				        + config.getConfigCode() + "' is disabled! Skipping...").toUpperCase());
				
				continue;
			} else if (operationTableIsAlreadyFinished(config)) {
				logDebug(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Confinguration '"
				        + config.getConfigCode() + "' was already finished!").toUpperCase());
			} else if (stopRequested()) {
				logWarn("ABORTING THE ENGINE PROCESS DUE STOP REQUESTED!");
				break;
			} else {
				
				logInfo(("Starting operation '" + getOperationType().name().toLowerCase() + "' On Etl Confinguration '"
				        + config.getConfigCode() + "'").toUpperCase());
				
				if (!config.isFullLoaded()) {
					try {
						logDebug("Performing the full load of etl item configuration");
						
						config.fullLoad(this.getOperationConfig());
					}
					catch (DBException e) {
						e.printStackTrace();
						
						throw new RuntimeException(e);
					}
				}
				
				TableOperationProgressInfo progressInfo = null;
				
				try {
					progressInfo = this.progressInfo.retrieveProgressInfo(config);
					
					if (progressInfo == null) {
						
					}
					
				}
				catch (NullPointerException e) {
					logErr("Error on thread " + this.getControllerId()
					        + ": Progress meter not found for Etl Confinguration [" + config.getConfigCode() + "].");
					
					e.printStackTrace();
					
					throw e;
				}
				
				Engine<T> engine = Engine.init(this, config, progressInfo);
				
				logTrace("Opening connection for saving Progress Info");
				
				OpenConnection conn = openDefaultConn(this);
				
				try {
					if (isResumable()) {
						logTrace("Saving Progress Info....");
						
						if (progressInfo != null) {
							progressInfo.save(conn);
						}
						
						logTrace("Progress Info Saved!");
						
					} else {
						logTrace("Skiping the saving as the operation  is not resumable");
					}
					conn.markAsSuccessifullyTerminated();
				}
				catch (DBException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				finally {
					finalizeConnection(conn, this);
				}
				
				this.enginesActivititieMonitor.add(engine);
				
				try {
					engine.run();
				}
				catch (Exception e) {
					new EtlExceptionImpl("Error occured on etl " + engine.getEngineId(), e);
				}
				
				if (stopRequested() && engine.isStopped()) {
					logInfo(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Configuration '"
					        + config.getConfigCode() + "' is stopped successifuly!").toUpperCase());
					break;
				} else {
					logInfo(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Configuration '"
					        + config.getConfigCode() + "' is finished!").toUpperCase());
					
					if (getOperationConfig().isRunOnce()) {
						break;
					}
				}
			}
		}
		
		if (!stopRequested()) {
			markAsFinished();
		} else {
			changeStatusToStopped();
		}
	}
	
	@Override
	protected synchronized void runInParallelMode() throws DBException {
		this.runInSequencialMode();
	}
	
	public abstract TaskProcessor<T> initRelatedTaskProcessor(Engine<T> monitor, IntervalExtremeRecord limits,
	        boolean runningInConcurrency);
	
	public abstract long getMinRecordId(Engine<? extends EtlDatabaseObject> engine);
	
	public abstract long getMaxRecordId(Engine<? extends EtlDatabaseObject> engine);
	
}
