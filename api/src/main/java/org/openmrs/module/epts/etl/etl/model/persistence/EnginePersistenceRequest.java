package org.openmrs.module.epts.etl.etl.model.persistence;

import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** A unit of auxiliary persistence produced by an ETL worker. */
public interface EnginePersistenceRequest {

	PersistenceType getType();

	int size();

	void persist(ConnectorRecordReadersAggregator readers, ConnectorRecordWritersAggregator writers) throws DBException;
}
