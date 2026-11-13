package org.openmrs.module.epts.etl.connector.capability.reader;

import java.sql.Connection;

import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.UnsupportedConnectorTypeException;
import org.openmrs.module.epts.etl.connector.capability.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.file.FileConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRecordReader;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/** Selects the source execution adapter for a resolved resource. */
public final class ConnectorRecordReaderFactory {

	private ConnectorRecordReaderFactory() {
	}

	public static <T extends EtlDatabaseObject> ConnectorRecordReader<T> create(ConnectorResource resource,
			AbstractEtlSearchParams<T> searchParams, Connection srcConnection) {

		if (resource == null) {
			throw new IllegalArgumentException("Resolved source resource cannot be null");
		}

		if (resource.getDirection() != EtlSide.SRC) {
			throw new IllegalArgumentException("Record reader requires a SOURCE resource");
		}

		resource.getEndpoint().requireCapabilities(java.util.Collections.singleton(ConnectorCapability.READ));

		if (resource.getEndpoint().getType() == ConnectorType.JDBC) {

			if (!(srcConnection instanceof OpenConnection)) {
				throw new IllegalArgumentException("A JDBC reader requires an OpenConnection");
			}
			return new JdbcConnectorRecordReader<>(resource, searchParams, (OpenConnection) srcConnection);
		}

		if (resource.getEndpoint().getType() == ConnectorType.FILE) {
			return new FileConnectorRecordReader<>(resource, searchParams.getPrimaryDataSource(), srcConnection);
		}

		throw new UnsupportedConnectorTypeException(resource.getEndpoint().getType(), EtlSide.SRC);
	}
}
