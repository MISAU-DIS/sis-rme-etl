package org.openmrs.module.epts.etl.connector.capability.writer;

import java.sql.Connection;
import java.util.Collections;

import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.UnsupportedConnectorTypeException;
import org.openmrs.module.epts.etl.connector.capability.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.file.FileConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRecordWriter;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

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

			if (!(destinationConnection instanceof OpenConnection)) {
				throw new IllegalArgumentException("A JDBC writer requires an OpenConnection");
			}
			return new JdbcConnectorRecordWriter<>(resource, (OpenConnection) destinationConnection);
		}

		if (endpoint.getType() == ConnectorType.FILE) {
			return new FileConnectorRecordWriter<>(resource);
		}

		throw new UnsupportedConnectorTypeException(endpoint.getType(), ConnectorDirection.DESTINATION);
	}

}
