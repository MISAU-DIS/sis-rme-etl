package org.openmrs.module.epts.etl.databasepreparation.controller;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlCounter;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReaders;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.databasepreparation.model.DatabasePreparationRecord;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * This class is responsible for data base preparation
 * 
 * @author jpboane
 */
public class DatabasePreparationProcessor extends org.openmrs.module.epts.etl.processor.JdbcTaskProcessor<DatabasePreparationRecord> {
	
	private boolean done;

	/** JDBC datasource representing the current item on the ETL database. */
	public JdbcTablePrimaryDataSourceConfiguration getEtlDatabaseDataSource() {
		return getEtlItemConfiguration().getEtlDatabaseDataSource();
	}

	@Override
	public ConnectorRecordWriters createRecordWriters() throws DBException {
		return getEtlItemConfiguration().createEtlDatabaseWriters(getEngine());
	}

	@Override
	public ConnectorRecordReader<DatabasePreparationRecord> createRecordReader(ConnectorRecordWriters writers)
			throws DBException {
		return getEtlItemConfiguration().createEtlDatabaseReader(getSearchParams(), getEngine(), writers);
	}

	@Override
	public ConnectorRecordReaders createRecordReaders(ConnectorRecordWriters writers) throws DBException {
		ConnectorRecordReaders readers = ConnectorRecordReaders.ofPrimary(createRecordReader(writers));
		setRecordReaders(readers);
		return readers;
	}
	
	public DatabasePreparationProcessor(Engine<DatabasePreparationRecord> monitr, IntervalExtremeRecord limits,
	    boolean runningInConcurrency) {
		
		super(monitr, limits, runningInConcurrency);
	}
	
	@Override
	public void transformAndLoad(List<DatabasePreparationRecord> records, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		getEtlItemConfiguration().ensureEtlStageTableExists(new EtlCounter(), getRelatedEtlOperationConfig(), srcConn);
		
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
	
	@Override
	public void transform(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
	        EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		throw new ForbiddenOperationException("Unsupported method!");
	}
	
}
