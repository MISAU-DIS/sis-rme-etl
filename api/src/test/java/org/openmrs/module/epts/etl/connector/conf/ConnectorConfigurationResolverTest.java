package org.openmrs.module.epts.etl.connector.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.connector.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.UnsupportedConnectorCapabilityException;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

class ConnectorConfigurationResolverTest {

	private final ObjectMapper mapper = new ObjectMapperProvider().getContext(ConnectorEndpointConfiguration.class);

	@Test
	void shouldDefaultMissingTypeToJdbcAndMapNeutralPropertyNames() throws Exception {
		ConnectorEndpointConfiguration configured = mapper.readValue("{"
				+ "\"uri\":\"jdbc:mysql://server/openmrs\"," + "\"userName\":\"etl\"," + "\"password\":\"secret\","
				+ "\"properties\":{\"driverClassName\":\"com.mysql.cj.jdbc.Driver\",\"schema\":\"openmrs\"}}",
				ConnectorEndpointConfiguration.class);

		ConnectorEndpoint endpoint = ConnectorConfigurationResolver.resolveEndpoint(configured, null);
		DBConnectionInfo jdbc = endpoint.unwrap(DBConnectionInfo.class);

		assertEquals(ConnectorType.JDBC, configured.getType());
		assertEquals("jdbc:mysql://server/openmrs", jdbc.getConnectionURI());
		assertEquals("etl", jdbc.getDataBaseUserName());
		assertEquals("secret", jdbc.getDataBaseUserPassword());
		assertEquals("com.mysql.cj.jdbc.Driver", jdbc.getDriveClassName());
		assertEquals("openmrs", jdbc.getSchema());
		assertFalse(configured.toString().contains("secret"));
	}

	@Test
	void shouldUseTheUnmodifiedLegacyJdbcConfigurationWhenNewConfigurationIsAbsent() {
		DBConnectionInfo legacy = new DBConnectionInfo();
		ConnectorEndpoint endpoint = ConnectorConfigurationResolver.resolveEndpoint(null, legacy);

		assertSame(legacy, endpoint.unwrap(DBConnectionInfo.class));
		assertTrue(endpoint.supports(ConnectorCapability.TRANSACTIONS));
	}

	@Test
	void shouldMergeExplicitNeutralValuesOverLegacyJdbcValues() {
		DBConnectionInfo legacy = new DBConnectionInfo("old-user", "old-password", "jdbc:mysql://old/db",
				"old.Driver");
		ConnectorEndpointConfiguration configured = new ConnectorEndpointConfiguration();
		configured.setUserName("new-user");

		DBConnectionInfo resolved = ConnectorConfigurationResolver.resolveEndpoint(configured, legacy)
				.unwrap(DBConnectionInfo.class);

		assertEquals("new-user", resolved.getDataBaseUserName());
		assertEquals("old-password", resolved.getDataBaseUserPassword());
		assertEquals("jdbc:mysql://old/db", resolved.getConnectionURI());
		assertEquals("old.Driver", resolved.getDriveClassName());
	}

	@Test
	void shouldRejectAnEndpointWhoseConnectorImplementationIsNotInstalled() {
		ConnectorEndpointConfiguration configured = new ConnectorEndpointConfiguration();
		configured.setType(ConnectorType.REST);
		configured.setUri("https://example.test/patients");
		ConnectorEndpoint endpoint = ConnectorConfigurationResolver.resolveEndpoint(configured, null);

		assertEquals(ConnectorType.REST, endpoint.getType());
		assertThrows(UnsupportedConnectorCapabilityException.class,
				() -> ConnectorConfigurationResolver.requireDirectionCapability(endpoint,
						ConnectorDirection.SOURCE));
	}

	@Test
	void shouldResolveLegacyJdbcTableAndRejectDivergentTransitionalNames() {
		DBConnectionInfo jdbc = new DBConnectionInfo();
		ConnectorEndpoint endpoint = ConnectorConfigurationResolver.resolveEndpoint(null, jdbc);
		GenericTableConfiguration table = new GenericTableConfiguration("patient");

		ConnectorResource legacy = ConnectorConfigurationResolver.resolveResource(null, table,
				ConnectorDirection.SOURCE, endpoint);
		assertEquals("patient", legacy.getName());
		assertSame(table, legacy.unwrap(GenericTableConfiguration.class));

		ConnectorResourceConfiguration configured = new ConnectorResourceConfiguration();
		configured.setName("another_patient");
		assertThrows(IllegalArgumentException.class, () -> ConnectorConfigurationResolver.resolveResource(configured,
				table, ConnectorDirection.SOURCE, endpoint));
	}

	@Test
	void shouldRejectDuplicateAndReservedConnectorIds() {
		ConnectorEndpointConfiguration first = connector("shared");
		ConnectorEndpointConfiguration duplicate = connector("shared");
		assertThrows(IllegalArgumentException.class,
				() -> new ConnectorRegistry(Arrays.asList(first, duplicate), null, null));

		ConnectorEndpointConfiguration reserved = connector(ConnectorRegistry.LEGACY_SOURCE_CONNECTOR_ID);
		assertThrows(IllegalArgumentException.class,
				() -> new ConnectorRegistry(java.util.Collections.singletonList(reserved), null, null));
	}

	@Test
	void shouldKeepLegacySourceAndDestinationAsSeparateInternalConnectors() {
		DBConnectionInfo source = new DBConnectionInfo();
		DBConnectionInfo destination = new DBConnectionInfo();
		ConnectorRegistry registry = new ConnectorRegistry(null, source, destination);

		assertSame(source, registry.select(null, null, ConnectorDirection.SOURCE, true)
				.unwrap(DBConnectionInfo.class));
		assertSame(destination, registry.select(null, null, ConnectorDirection.DESTINATION, true)
				.unwrap(DBConnectionInfo.class));
	}

	private ConnectorEndpointConfiguration connector(String id) {
		ConnectorEndpointConfiguration connector = new ConnectorEndpointConfiguration();
		connector.setId(id);
		return connector;
	}
}
