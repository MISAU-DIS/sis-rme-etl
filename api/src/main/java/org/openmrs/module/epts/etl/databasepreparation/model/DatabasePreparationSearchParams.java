package org.openmrs.module.epts.etl.databasepreparation.model;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.model.SearchClauses;
import org.openmrs.module.epts.etl.model.base.VOLoaderHelper;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class DatabasePreparationSearchParams extends AbstractEtlSearchParams<DatabasePreparationRecord> {
	
	private Engine<DatabasePreparationRecord> engine;
	
	public DatabasePreparationSearchParams(Engine<DatabasePreparationRecord> engine,
	    ThreadRecordIntervalsManager<DatabasePreparationRecord> intervalsMgt) {
		super(null, intervalsMgt);
		
		this.engine = engine;
	}
	
	@Override
	public AbstractEtlSearchParams<DatabasePreparationRecord> cloneMe() {
		return null;
	}
	
	@Override
	protected VOLoaderHelper getLoaderHealper() {
		return null;
	}
	
	@Override
	public int countAllRecords(OperationController<DatabasePreparationRecord> controller, Connection conn)
	        throws DBException {
		
		return 1;
	}
	
	@Override
	public int countNotProcessedRecords(OperationController<DatabasePreparationRecord> controller, Connection conn)
	        throws DBException {
		
		return this.engine.getCurrentIteration() == 0 ? 1 : 0;
	}
	
	@Override
	public String generateDestinationExclusionClause(Connection srcConn, Connection dstConn) throws DBException {
		return null;
	}
	
	@Override
	public SearchClauses<DatabasePreparationRecord> generateSearchClauses(IntervalExtremeRecord recordLimits,
	        DatabasePreparationRecord parentObject, List<DatabasePreparationRecord> auxDataSourceObjects, Connection srcConn,
	        Connection dstConn) throws DBException {
		return null;
	}
	
	@Override
	public List<DatabasePreparationRecord> search(IntervalExtremeRecord intervalExtremeRecord,
	        DatabasePreparationRecord parentObject, List<DatabasePreparationRecord> auxDataSourceObjects, Connection srcConn,
	        Connection dstCOnn) throws DBException {
		
		if (countAllRecords(null, dstCOnn) == 0) {
			return null;
		}
		
		return utilities.parseToList(new DatabasePreparationRecord(this.engine.getEtlItemConfiguration()));
	}
	
	@Override
	public List<DatabasePreparationRecord> searchNextRecordsInMultiThreads(IntervalExtremeRecord interval,
	        DatabasePreparationRecord parentObject, List<DatabasePreparationRecord> auxDataSourceObjects, Connection srcConn,
	        Connection dstConn) throws DBException {
		
		return this.search(interval, parentObject, auxDataSourceObjects, srcConn, dstConn);
	}
}
