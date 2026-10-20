package org.openmrs.module.epts.etl.connector.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.connector.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.UnsupportedConnectorCapabilityException;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

class JdbcConnectorContractsTest {

	@Test
	void shouldExposeLegacyConnectionThroughNeutralNamesAndCapabilities() {
		DBConnectionInfo connection = new DBConnectionInfo("etl-user", "secret", "jdbc:mysql://host/openmrs",
				"com.mysql.cj.jdbc.Driver");
		JdbcConnectorEndpoint endpoint = new JdbcConnectorEndpoint(connection);

		assertEquals(ConnectorType.JDBC, endpoint.getType());
		assertEquals("jdbc:mysql://host/openmrs", endpoint.getUri());
		assertEquals("etl-user", endpoint.getUserName());
		assertEquals("secret", endpoint.getPassword());
		assertTrue(endpoint.supports(ConnectorCapability.TRANSACTIONS));
		assertTrue(endpoint.supportsAll(EnumSet.of(ConnectorCapability.READ,
				ConnectorCapability.SCHEMA_DISCOVERY, ConnectorCapability.RELATIONAL_KEYS)));
		assertFalse(endpoint.supports(ConnectorCapability.ACKNOWLEDGEMENT));
		assertSame(connection, endpoint.unwrap(DBConnectionInfo.class));
		assertFalse(endpoint.toString().contains("secret"));
	}

	@Test
	void shouldReportMissingCapabilitiesBeforeAnOperationStarts() {
		JdbcConnectorEndpoint endpoint = new JdbcConnectorEndpoint(new DBConnectionInfo());

		UnsupportedConnectorCapabilityException error = assertThrows(
				UnsupportedConnectorCapabilityException.class,
				() -> endpoint.requireCapabilities(EnumSet.of(ConnectorCapability.READ,
						ConnectorCapability.ACKNOWLEDGEMENT)));

		assertTrue(error.getMessage().contains("ACKNOWLEDGEMENT"));
		assertFalse(error.getMessage().contains("READ,"));
	}

	@Test
	void shouldExposeTableNameAsResourceNameWithoutChangingTheTableConfiguration() {
		DBConnectionInfo connection = new DBConnectionInfo();
		GenericTableConfiguration table = new GenericTableConfiguration("patient");

		JdbcTableResource resource = new JdbcTableResource(table, ConnectorDirection.SOURCE, connection);

		assertEquals("patient", resource.getName());
		assertEquals(ConnectorDirection.SOURCE, resource.getDirection());
		assertEquals(ConnectorType.JDBC, resource.getEndpoint().getType());
		assertSame(table, resource.unwrap(GenericTableConfiguration.class));
	}
}
