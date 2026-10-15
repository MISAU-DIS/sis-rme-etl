package org.openmrs.module.epts.etl.connector;

import java.sql.Connection;
import java.util.Collections;

import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.file.FileConnectorRecordWriter;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Selects the destination execution adapter for resolved resources. */
public final class ConnectorRecordWriterFactory {

	private ConnectorRecordWriterFactory() {
	}

	public static <T extends EtlDatabaseObject> ConnectorRecordWriter<T> create(ConnectorResource resource,
			Connection destinationConnection) {

		if (resource == null) {
			throw new IllegalArgumentException("A resolved destination resource is required");
		}
		if (resource.getDirection() != ConnectorDirection.DESTINATION) {
			throw new IllegalArgumentException("Record writer requires a DESTINATION resource");
		}
		resource.getEndpoint().requireCapabilities(Collections.singleton(ConnectorCapability.WRITE));

		ConnectorEndpoint endpoint = resource.getEndpoint();
		if (endpoint.getType() == ConnectorType.JDBC) {
			return new JdbcConnectorRecordWriter<>(resource, destinationConnection);
		}
		if (endpoint.getType() == ConnectorType.FILE) {
			return new FileConnectorRecordWriter<>(resource);
		}

		throw new UnsupportedConnectorTypeException(endpoint.getType(), ConnectorDirection.DESTINATION);
	}

}
