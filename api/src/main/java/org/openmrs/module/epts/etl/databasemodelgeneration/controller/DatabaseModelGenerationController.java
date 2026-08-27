package org.openmrs.module.epts.etl.databasemodelgeneration.controller;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.controller.ProcessController;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationRecord;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelGenerationSearchParams;
import org.openmrs.module.epts.etl.databasemodelgeneration.processor.DatabaseModelGenerationProcessor;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * This class is responsible for data base preparation
 * 
 * @author jpboane
 */
public class DatabaseModelGenerationController extends OperationController<DatabaseModelGenerationRecord> {

	public DatabaseModelGenerationController(ProcessController processController, EtlOperationConfig operationConfig) {
		super(processController, operationConfig);
	}

	@Override
	public TaskProcessor<DatabaseModelGenerationRecord> initRelatedTaskProcessor(Engine<DatabaseModelGenerationRecord> monitor,
			IntervalExtremeRecord limits, boolean runningInConcurrency) {

		return new DatabaseModelGenerationProcessor(monitor, limits, runningInConcurrency);
	}

	@Override
	public AbstractEtlSearchParams<DatabaseModelGenerationRecord> initMainSearchParams(
			ThreadRecordIntervalsManager<DatabaseModelGenerationRecord> intervalsMgt, Engine<DatabaseModelGenerationRecord> engine) {

		return new DatabaseModelGenerationSearchParams(engine, intervalsMgt);
	}

	@Override
	public long getMinRecordId(Engine<? extends EtlDatabaseObject> engine) {
		return 1;
	}

	@Override
	public long getMaxRecordId(Engine<? extends EtlDatabaseObject> engine) {
		return 1;
	}

	@Override
	public boolean mustRestartInTheEnd() {
		return false;
	}

	public EtlConfiguration getEtlConfiguration() {
		return getProcessController().getRelatedEtlConf();
	}

	@Override
	public boolean canBeRunInMultipleEngines() {
		return false;
	}

	@Override
	public void afterEtl(List<DatabaseModelGenerationRecord> objs, Connection srcConn, Connection dstConn) throws DBException {
	}
}
