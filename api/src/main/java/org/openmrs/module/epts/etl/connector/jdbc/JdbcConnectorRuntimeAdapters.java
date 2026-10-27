package org.openmrs.module.epts.etl.connector.jdbc;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Adapters used only while JDBC-based internals migrate to connector contexts. */
public final class JdbcConnectorRuntimeAdapters {

	private JdbcConnectorRuntimeAdapters() {
	}

	public static ConnectorRecordReader<EtlDatabaseObject> reader(Connection connection) {
		return new ConnectorRecordReader<EtlDatabaseObject>() {
			@Override public ConnectorResource getResource() { return null; }
			@Override public List<EtlDatabaseObject> read(ConnectorReadRequest<EtlDatabaseObject> request) {
				throw new UnsupportedOperationException("Compatibility reader cannot extract records");
			}
			@Override public <N> N unwrap(Class<N> nativeType) { return nativeType.cast(connection); }
		};
	}
}
