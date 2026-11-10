package org.openmrs.module.epts.etl.connector.capability;

import java.util.List;

import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/** Reads ETL records from one resolved connector resource. */
public interface ConnectorRecordReader<T extends EtlDatabaseObject> extends ConnectorCapabilityProvider<T> {

	ConnectorResource getResource();

	List<T> read(ConnectorReadRequest<T> request) throws DBException;

	/**
	 * Access to implementation-native state while legacy processing is migrated.
	 */
	<N> N unwrap(Class<N> nativeType);
}
