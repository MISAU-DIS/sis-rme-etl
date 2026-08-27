package org.openmrs.module.epts.etl.databasemodelgeneration.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.model.SearchClauses;
import org.openmrs.module.epts.etl.model.base.VOLoaderHelper;
import org.openmrs.module.epts.etl.databasemodelgeneration.processor.DatabaseModelGenerationProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class DatabaseModelGenerationSearchParams extends AbstractEtlSearchParams<DatabaseModelGenerationRecord> {

	private DatabaseModelGenerationProcessor processor;

	public DatabaseModelGenerationSearchParams(Engine<DatabaseModelGenerationRecord> engine,
			ThreadRecordIntervalsManager<DatabaseModelGenerationRecord> limits) {

		super(engine.getSrcConf(), limits);
	}

	public void setProcessor(DatabaseModelGenerationProcessor processor) {
		this.processor = processor;
	}

	@Override
	public List<DatabaseModelGenerationRecord> search(IntervalExtremeRecord intervalExtremeRecord,
			DatabaseModelGenerationRecord parentObject, List<DatabaseModelGenerationRecord> auxDataSourceObjects, Connection srcConn,
			Connection dstCOnn) throws DBException {

		if (processor.isDatabaseModelGenerated())
			return null;

		List<DatabaseModelGenerationRecord> records = new ArrayList<>();

		records.add(new DatabaseModelGenerationRecord(getSrcConf()));

		return records;
	}

	@Override
	public SearchClauses<DatabaseModelGenerationRecord> generateSearchClauses(IntervalExtremeRecord recordLimits,
			DatabaseModelGenerationRecord parentObject, List<DatabaseModelGenerationRecord> auxDataSourceObjects, Connection srcConn,
			Connection dstConn) throws DBException {

		return null;
	}

	@Override
	public int countAllRecords(OperationController<DatabaseModelGenerationRecord> controller, Connection conn)
			throws DBException {
		return 1;
	}

	@Override
	public synchronized int countNotProcessedRecords(OperationController<DatabaseModelGenerationRecord> controller,
			Connection conn) throws DBException {

		return processor != null && processor.isDatabaseModelGenerated() ? 0 : 1;
	}

	@Override
	protected VOLoaderHelper getLoaderHealper() {
		return null;
	}

	@Override
	public AbstractEtlSearchParams<DatabaseModelGenerationRecord> cloneMe() {
		return null;
	}

	@Override
	public String generateDestinationExclusionClause(Connection srcConn, Connection dstConn) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}
}
