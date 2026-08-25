package org.openmrs.module.epts.etl.changedrecordsdetector.engine;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.changedrecordsdetector.controller.ChangedRecordsDetectorController;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class ChangedRecordsDetectorProcessor extends TaskProcessor<EtlDatabaseObject> {
	
	public ChangedRecordsDetectorProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
	    boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);
	}
	
	@Override
	public ChangedRecordsDetectorController getRelatedOperationController() {
		return (ChangedRecordsDetectorController) super.getRelatedOperationController();
	}
	
	@Override
	public void transformAndLoad(List<EtlDatabaseObject> records, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		utilities.throwForbiddenMethodException();
		
		/*
		 * List<EtlDatabaseObject> syncRecordsAsOpenMRSObjects =
		 * utilities.parseList(records, EtlDatabaseObject.class); List<ChangedRecord>
		 * processedRecords = new ArrayList<ChangedRecord>(records.size());
		 * 
		 * this.getEngine() .logInfo("PERFORMING CHANGE DETECTED ACTION '" +
		 * records.size() + "' " + getSrcConf().getTableName());
		 * 
		 * for (EtlDatabaseObject obj : syncRecordsAsOpenMRSObjects) { try {
		 * ((GenericDatabaseObject) obj).setRelatedConfiguration(getSrcConf());
		 * 
		 * processedRecords.add(DetectedRecordInfo.generate(obj,
		 * getEngine().getSrcConf().getOriginAppLocationCode()));
		 * 
		 * if (getRelatedOperationController().getActionPerformeApp().
		 * isSinglePerformingMode()) {
		 * DetectedRecordService.getInstance().performeAction(
		 * getRelatedOperationController().getActionPerformeApp().getApplicationCode(),
		 * processedRecords.get(processedRecords.size() - 1), getSrcConf()); }
		 * 
		 * } catch (Exception e) { e.printStackTrace();
		 * 
		 * logError( "Any error occurred processing dstRecord [uuid: " + obj.getUuid() +
		 * ", id: " + obj.getObjectId() + "]");
		 * 
		 * throw new RuntimeException(e); } }
		 * 
		 * if
		 * (getRelatedOperationController().getActionPerformeApp().isBatchPerformingMode
		 * ()) { DetectedRecordService.getInstance().performeAction(
		 * getRelatedOperationController().getActionPerformeApp().getApplicationCode(),
		 * processedRecords, getSrcConf()); }
		 * 
		 * getTaskResultInfo().addAllToRecordsWithNoError(EtlOperationItemResult
		 * .parseFromEtlDatabaseObject(utilities.parseList(processedRecords,
		 * EtlDatabaseObject.class)));
		 * 
		 * this.getEngine().logInfo( "ACTION PERFORMED FOR CHANGED RECORDS '" +
		 * records.size() + "' " + getSrcConf().getTableName() + "!");
		 */
	}
	
	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(IntervalExtremeRecord limits) {
		throw new ForbiddenOperationException("Forbiden Method");
	}
	
	@Override
	public EtlConfiguration getRelatedEtlConf() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void transform(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
	        EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		throw new ForbiddenOperationException("Unsupported method!");
		
	}
	
}
