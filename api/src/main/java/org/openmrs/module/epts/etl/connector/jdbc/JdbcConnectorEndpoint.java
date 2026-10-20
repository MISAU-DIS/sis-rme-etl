package org.openmrs.module.epts.etl.connector.jdbc;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.openmrs.module.epts.etl.connector.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

/** Compatibility adapter exposing {@link DBConnectionInfo} as an endpoint. */
public final class JdbcConnectorEndpoint implements ConnectorEndpoint {

	private static final Set<ConnectorCapability> CAPABILITIES = Collections.unmodifiableSet(EnumSet.of(
			ConnectorCapability.READ, ConnectorCapability.WRITE, ConnectorCapability.BATCH_READ,
			ConnectorCapability.BATCH_WRITE, ConnectorCapability.TRANSACTIONS,
			ConnectorCapability.SCHEMA_DISCOVERY, ConnectorCapability.RELATIONAL_KEYS,
			ConnectorCapability.FILTERING, ConnectorCapability.ORDERING, ConnectorCapability.PAGINATION));

	private final DBConnectionInfo connectionInfo;

	public JdbcConnectorEndpoint(DBConnectionInfo connectionInfo) {
		if (connectionInfo == null) {
			throw new IllegalArgumentException("connectionInfo cannot be null");
		}
		this.connectionInfo = connectionInfo;
	}

	@Override
	public ConnectorType getType() {
		return ConnectorType.JDBC;
	}

	@Override
	public String getUri() {
		return connectionInfo.getConnectionURI();
	}

	@Override
	public String getUserName() {
		return connectionInfo.getDataBaseUserName();
	}

	@Override
	public String getPassword() {
		return connectionInfo.getDataBaseUserPassword();
	}

	@Override
	public Set<ConnectorCapability> getCapabilities() {
		return CAPABILITIES;
	}

	@Override
	public <T> T unwrap(Class<T> nativeType) {
		if (nativeType != null && nativeType.isInstance(connectionInfo)) {
			return nativeType.cast(connectionInfo);
		}
		throw new IllegalArgumentException("JDBC endpoint cannot be unwrapped as "
				+ (nativeType == null ? "null" : nativeType.getName()));
	}

	@Override
	public String toString() {
		return "JdbcConnectorEndpoint[type=" + getType() + ", uri=" + getUri() + ", userName=" + getUserName()
				+ ", capabilities=" + getCapabilities() + "]";
	}
}
