package org.openmrs.module.epts.etl.etl.processor;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Temporary JDBC bridge for EtlProcessor subclasses awaiting phase-5 migration. */
public abstract class JdbcEtlProcessor extends EtlProcessor {

	protected JdbcEtlProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits,
			boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);
	}

	@Override
	public void transformAndLoad(List<EtlDatabaseObject> records, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws DBException {
		transformAndLoad(records, readers.getPrimary().unwrap(Connection.class), writers.unwrapShared(Connection.class));
	}

	public abstract void transformAndLoad(List<EtlDatabaseObject> records, Connection srcConn, Connection dstConn)
			throws DBException;
}
