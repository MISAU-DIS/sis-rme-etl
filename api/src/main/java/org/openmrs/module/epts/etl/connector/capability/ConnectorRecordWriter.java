package org.openmrs.module.epts.etl.connector.capability;

import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Writes a batch to the resolved destination resources of one ETL item. */
public interface ConnectorRecordWriter<T extends EtlDatabaseObject> extends ConnectorCapabilityProvider<T> {

	ConnectorResource getResource();

	EtlOperationResultHeader<EtlDatabaseObject> write(ConnectorWriteRequest<T> request) throws DBException;

	<N> N unwrap(Class<N> nativeType);
}
