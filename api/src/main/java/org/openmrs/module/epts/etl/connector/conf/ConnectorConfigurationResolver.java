package org.openmrs.module.epts.etl.connector.conf;

import java.util.Collections;

import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcTableResource;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

/** Resolves new configuration while retaining the legacy JDBC fallback. */
public final class ConnectorConfigurationResolver {

	private static final CommonUtilities UTILITIES = CommonUtilities.getInstance();

	private ConnectorConfigurationResolver() {
	}

	public static ConnectorEndpoint requireDirectionCapability(ConnectorEndpoint endpoint,
			ConnectorDirection direction) {

		if (endpoint == null) {
			return null;
		}

		ConnectorCapability required = direction == ConnectorDirection.SOURCE ? ConnectorCapability.READ
				: ConnectorCapability.WRITE;

		endpoint.requireCapabilities(Collections.singleton(required));

		return endpoint;
	}

	public static ConnectorEndpoint resolveEndpoint(ConnectorEndpointConfiguration configured,
			DBConnectionInfo legacyJdbc) {

		if (configured == null) {
			if (legacyJdbc == null) {
				return null;
			}

			return new JdbcConnectorEndpoint(legacyJdbc);
		}

		if (configured.getType() != ConnectorType.JDBC) {
			return new ConfiguredConnectorEndpoint(configured);
		}

		DBConnectionInfo jdbc = new DBConnectionInfo();

		if (legacyJdbc != null) {
			jdbc.copyFromOther(legacyJdbc);
		}

		if (UTILITIES.stringHasValue(configured.getUri())) {
			jdbc.setConnectionURI(configured.getUri());
		}

		if (UTILITIES.stringHasValue(configured.getUserName())) {
			jdbc.setDataBaseUserName(configured.getUserName());
		}

		if (configured.getPassword() != null) {
			jdbc.setDataBaseUserPassword(configured.getPassword());
		}

		applyJdbcProperties(jdbc, configured);

		return new JdbcConnectorEndpoint(jdbc);
	}

	private static void applyJdbcProperties(DBConnectionInfo jdbc, ConnectorEndpointConfiguration configured) {

		Object driver = configured.hasProperty("driverClassName") ? configured.getProperty("driverClassName")
				: configured.getProperty("driveClassName");

		if (driver != null) {
			jdbc.setDriveClassName(String.valueOf(driver));
		}

		Object schema = configured.getProperty("schema");

		if (schema != null) {
			jdbc.setSchema(String.valueOf(schema));
		}

		Object autoCommit = configured.getProperty("defaultAutoCommit");

		if (autoCommit != null) {
			jdbc.setDefaultAutoCommit(Boolean.valueOf(String.valueOf(autoCommit)));
		}
	}

	public static ConnectorResource resolveResource(ConnectorResourceConfiguration configured,
			TableConfiguration legacyTable, ConnectorDirection direction, ConnectorEndpoint endpoint) {

		if (configured == null && legacyTable == null) {
			return null;
		}

		if (configured == null) {
			DBConnectionInfo jdbc = endpoint.unwrap(DBConnectionInfo.class);
			return new JdbcTableResource(legacyTable, direction, jdbc);
		}

		if (endpoint == null) {
			throw new IllegalArgumentException("endpoint cannot be null");
		}

		if (!UTILITIES.stringHasValue(configured.getName())) {
			throw new IllegalArgumentException("Connector resource name cannot be empty");
		}

		if (legacyTable != null && endpoint.getType() == ConnectorType.JDBC) {
			if (!configured.getName().equals(legacyTable.getTableName())) {
				throw new IllegalArgumentException("Configured JDBC resource name '" + configured.getName()
						+ "' differs from legacy tableName '" + legacyTable.getTableName() + "'");
			}
			return new JdbcTableResource(legacyTable, direction, endpoint.unwrap(DBConnectionInfo.class));
		}

		return new ConfiguredConnectorResource(configured, direction, endpoint);
	}
}
