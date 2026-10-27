package org.openmrs.module.epts.etl.connector;

import java.sql.Connection;

import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.file.FileConnectorRecordReader;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Selects the source execution adapter for a resolved resource. */
public final class ConnectorRecordReaderFactory {

	private ConnectorRecordReaderFactory() {
	}

	public static <T extends EtlDatabaseObject> ConnectorRecordReader<T> create(ConnectorResource resource,
			AbstractEtlSearchParams<T> searchParams, Connection srcConnection, Connection dstConnection) {

		if (resource == null) {
			throw new IllegalArgumentException("Resolved source resource cannot be null");
		}

		if (resource.getDirection() != ConnectorDirection.SOURCE) {
			throw new IllegalArgumentException("Record reader requires a SOURCE resource");
		}

		resource.getEndpoint().requireCapabilities(java.util.Collections.singleton(ConnectorCapability.READ));

		if (resource.getEndpoint().getType() == ConnectorType.JDBC) {
			return new JdbcConnectorRecordReader<>(resource, searchParams, srcConnection, dstConnection);
		}
		if (resource.getEndpoint().getType() == ConnectorType.FILE) {
			return new FileConnectorRecordReader<>(resource, searchParams.getSrcConf(), srcConnection);
		}

		throw new UnsupportedConnectorTypeException(resource.getEndpoint().getType(), ConnectorDirection.SOURCE);
	}
}
