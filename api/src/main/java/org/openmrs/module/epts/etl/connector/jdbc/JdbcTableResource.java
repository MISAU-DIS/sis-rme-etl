package org.openmrs.module.epts.etl.connector.jdbc;

import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

/** Compatibility adapter exposing a table configuration as a resource. */
public final class JdbcTableResource implements ConnectorResource {

	private final TableConfiguration tableConfiguration;
	private final ConnectorDirection direction;
	private final JdbcConnectorEndpoint endpoint;

	public JdbcTableResource(TableConfiguration tableConfiguration, ConnectorDirection direction) {
		this(tableConfiguration, direction,
				tableConfiguration == null ? null : tableConfiguration.getRelatedConnInfo());
	}

	public JdbcTableResource(TableConfiguration tableConfiguration, ConnectorDirection direction,
			DBConnectionInfo connectionInfo) {
		if (tableConfiguration == null) {
			throw new IllegalArgumentException("tableConfiguration cannot be null");
		}
		if (direction == null) {
			throw new IllegalArgumentException("direction cannot be null");
		}
		if (connectionInfo == null) {
			throw new IllegalArgumentException("tableConfiguration must have related connection information");
		}
		this.tableConfiguration = tableConfiguration;
		this.direction = direction;
		this.endpoint = new JdbcConnectorEndpoint(connectionInfo);
	}

	@Override
	public String getName() {
		return tableConfiguration.getTableName();
	}

	@Override
	public ConnectorDirection getDirection() {
		return direction;
	}

	@Override
	public ConnectorEndpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public <T> T unwrap(Class<T> nativeType) {
		if (nativeType != null && nativeType.isInstance(tableConfiguration)) {
			return nativeType.cast(tableConfiguration);
		}
		throw new IllegalArgumentException("JDBC table resource cannot be unwrapped as "
				+ (nativeType == null ? "null" : nativeType.getName()));
	}

	@Override
	public String toString() {
		return "JdbcTableResource[name=" + getName() + ", direction=" + direction + ", endpoint=" + endpoint + "]";
	}
}
