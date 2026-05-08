package org.openmrs.module.epts.etl.databasepreparation.controller;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlCounter;
import org.openmrs.module.epts.etl.databasepreparation.model.DatabasePreparationRecord;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.TaskProcessor;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * This class is responsible for data base preparation
 * 
 * @author jpboane
 */
public class DatabasePreparationProcessor extends TaskProcessor<DatabasePreparationRecord> {
	
	private boolean done;
	
	public DatabasePreparationProcessor(Engine<DatabasePreparationRecord> monitr, IntervalExtremeRecord limits,
	    boolean runningInConcurrency) {
		
		super(monitr, limits, runningInConcurrency);
	}
	
	@Override
	public void performeEtl(List<DatabasePreparationRecord> records, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		getEtlItemConfiguration().ensureEtlStageTableExists(new EtlCounter(), getRelatedEtlOperationConfig(), srcConn,
		    dstConn);
		
		this.done = true;
	}
	
	public boolean done() {
		return done;
	}
	
	@Override
	public TaskProcessor<DatabasePreparationRecord> initReloadRecordsWithDefaultParentsTaskProcessor(
	        IntervalExtremeRecord limits) {
		
		return null;
	}
	
}
