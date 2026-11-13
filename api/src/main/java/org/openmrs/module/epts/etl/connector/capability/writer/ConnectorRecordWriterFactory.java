package org.openmrs.module.epts.etl.connector.capability.writer;

import java.sql.Connection;
import java.util.Collections;

import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
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
		return create(resource, null, destinationConnection);
	}

	public static <T extends EtlDatabaseObject> ConnectorRecordWriter<T> create(ConnectorResource resource,
			AbstractTableConfiguration tableConfiguration, Connection destinationConnection) {

		if (resource == null) {
			throw new IllegalArgumentException("A resolved destination resource is required");
		}

		if (resource.getDirection() != EtlSide.DST) {
			throw new IllegalArgumentException("Record writer requires a DESTINATION resource");
		}
		resource.getEndpoint().requireCapabilities(Collections.singleton(ConnectorCapability.WRITE));

		ConnectorEndpoint endpoint = resource.getEndpoint();

		if (endpoint.getType() == ConnectorType.JDBC) {

			if (!(destinationConnection instanceof OpenConnection)) {
				throw new IllegalArgumentException("A JDBC writer requires an OpenConnection");
			}

			if (tableConfiguration == null) {
				throw new IllegalArgumentException("A JDBC writer requires its physical table configuration");
			}
			return new JdbcConnectorRecordWriter<>(resource, tableConfiguration, (OpenConnection) destinationConnection);
		}

		if (endpoint.getType() == ConnectorType.FILE) {
			return new FileConnectorRecordWriter<>(resource);
		}

		throw new UnsupportedConnectorTypeException(endpoint.getType(), EtlSide.DST);
	}

}
