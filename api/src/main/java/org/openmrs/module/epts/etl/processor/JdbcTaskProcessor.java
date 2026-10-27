package org.openmrs.module.epts.etl.processor;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Temporary JDBC compatibility boundary for processors not migrated yet.
 * Connector-neutral orchestration ends at {@link TaskProcessor}; subclasses are
 * moved off this bridge incrementally.
 */
public abstract class JdbcTaskProcessor<T extends EtlDatabaseObject> extends TaskProcessor<T> {

	protected JdbcTaskProcessor(Engine<T> monitor, IntervalExtremeRecord limits, boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);
	}

	@Override
	public void transformAndLoad(List<T> records, ConnectorRecordReader<?> reader, ConnectorRecordWriters writers)
			throws DBException {
		transformAndLoad(records, reader.unwrap(Connection.class), writers.unwrapShared(Connection.class));
	}

	@Override
	public void transform(List<EtlDatabaseObject> records, ConnectorRecordReader<?> reader,
			ConnectorRecordWriters writers) throws DBException {
		transform(records, reader.unwrap(Connection.class), writers.unwrapShared(Connection.class));
	}

	@Override
	public void load(List<EtlDatabaseObject> records, ConnectorRecordReader<?> reader, ConnectorRecordWriters writers)
			throws DBException {
		load(records, reader.unwrap(Connection.class), writers.unwrapShared(Connection.class));
	}

	public abstract void transformAndLoad(List<T> records, Connection srcConn, Connection dstConn) throws DBException;

	public void transform(List<EtlDatabaseObject> records, Connection srcConn, Connection dstConn) throws DBException {
		throw new ForbiddenOperationException("This processor does not support an independent transform phase");
	}

	public void transform(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> records,
			EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn, Connection dstConn)
			throws DBException {
		throw new ForbiddenOperationException("This processor does not support an independent transform phase");
	}

	public void load(List<EtlDatabaseObject> records, Connection srcConn, Connection dstConn) throws DBException {
		throw new ForbiddenOperationException("This processor does not support an independent load phase");
	}
}
