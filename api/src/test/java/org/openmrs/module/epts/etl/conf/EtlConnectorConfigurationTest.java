package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.conf.ConnectorEndpointConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

class EtlConnectorConfigurationTest {

	@Test
	void shouldDeserializeConnectorCatalogWithoutRemovingLegacyJdbcConfiguration() throws Exception {
		String json = "{" + "\"srcConnInfo\":{\"connectionURI\":\"jdbc:mysql://legacy/source\"},"
				+ "\"dstConnInfo\":{\"connectionURI\":\"jdbc:mysql://legacy/destination\"},"
				+ "\"connectors\":["
				+ "{\"id\":\"source-db\",\"uri\":\"jdbc:mysql://neutral/source\",\"userName\":\"reader\"},"
				+ "{\"id\":\"destination-db\",\"type\":\"JDBC\",\"uri\":\"jdbc:mysql://neutral/destination\",\"userName\":\"writer\"}],"
				+ "\"defaultSourceConnector\":\"source-db\","
				+ "\"defaultDestinationConnector\":\"destination-db\""
				+ "}";
		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class)
				.readValue(json, EtlConfiguration.class);

		assertEquals("jdbc:mysql://legacy/source", configuration.getSrcConnInfo().getConnectionURI());
		initializeConnectors(configuration);
		assertEquals("jdbc:mysql://neutral/source",
				configuration.getInitializedConnectorRegistry().select(null, "source-db", ConnectorDirection.SOURCE,
						false).getUri());
		assertEquals("reader", configuration.getInitializedConnectorRegistry()
				.select(null, "source-db", ConnectorDirection.SOURCE, false).getUserName());
		assertEquals(ConnectorType.JDBC,
				configuration.getInitializedConnectorRegistry()
						.select(null, "destination-db", ConnectorDirection.DESTINATION, false).getType());
		assertEquals("writer",
				configuration.getInitializedConnectorRegistry()
						.select(null, "destination-db", ConnectorDirection.DESTINATION, false).getUserName());
	}

	@Test
	void shouldResolveItemResourcesFromLegacyConfigurationWhenNeutralResourcesAreAbsent() throws Exception {
		DBConnectionInfo srcConnection = new DBConnectionInfo();
		DBConnectionInfo dstConnection = new DBConnectionInfo();
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(srcConnection);
		configuration.setDstConnInfo(dstConnection);

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf source = new SrcConf();
		source.setTableName("patient");
		item.setSrcConf(source);
		DstConf destination = new DstConf("patient_copy");
		item.setDstConf(java.util.Collections.singletonList(destination));

		initializeConnectors(configuration);
		initializeResources(item);
		ConnectorResource resolvedSource = item.getResolvedSrcResource();
		ConnectorResource resolvedDestination = item.getResolvedDstResources().get(0);

		assertEquals("patient", resolvedSource.getName());
		assertEquals(ConnectorDirection.SOURCE, resolvedSource.getDirection());
		assertEquals("patient_copy", resolvedDestination.getName());
		assertEquals(ConnectorDirection.DESTINATION, resolvedDestination.getDirection());
		assertSame(source, resolvedSource.unwrap(SrcConf.class));
		assertSame(destination, resolvedDestination.unwrap(DstConf.class));
	}

	@Test
	void shouldReportUnavailableConnectorCapabilitiesDuringConfigurationValidation() {
		EtlConfiguration configuration = new EtlConfiguration();
		ConnectorEndpointConfiguration source = new ConnectorEndpointConfiguration();
		source.setId("events");
		source.setType(ConnectorType.MESSAGE_QUEUE);
		configuration.setConnectors(java.util.Collections.singletonList(source));
		configuration.setDefaultSourceConnector("events");

		assertTrue(configuration.validateConnectorConfigurations().get(0).contains("READ"));
	}

	@Test
	void shouldSelectDifferentConnectorsForResourcesInTheSameItem() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		ConnectorEndpointConfiguration sourceConnector = jdbcConnector("source-db", "jdbc:mysql://source/openmrs");
		ConnectorEndpointConfiguration archiveConnector = jdbcConnector("archive-db", "jdbc:mysql://archive/history");
		configuration.setConnectors(java.util.Arrays.asList(sourceConnector, archiveConnector));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		ConnectorResourceConfiguration source = resource("source-db", "patient");
		ConnectorResourceConfiguration destination = resource("archive-db", "patient_archive");
		item.setSrcResource(source);
		item.setDstResources(java.util.Collections.singletonList(destination));
		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://source/openmrs", item.getResolvedSrcResource().getEndpoint().getUri());
		assertEquals("jdbc:mysql://archive/history", item.getResolvedDstResources().get(0).getEndpoint().getUri());
		assertSame(item.getResolvedSrcResource().getEndpoint(), item.getResolvedSrcResource().getEndpoint());
	}

	@Test
	void shouldRejectAmbiguousConnectorSelection() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setConnectors(java.util.Arrays.asList(jdbcConnector("first", "jdbc:first"),
				jdbcConnector("second", "jdbc:second")));
		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		item.setSrcResource(resource(null, "patient"));
		initializeConnectors(configuration);

		InvocationTargetException error = org.junit.jupiter.api.Assertions.assertThrows(InvocationTargetException.class,
				() -> initializeResources(item));
		assertTrue(error.getCause().getMessage().contains("ambiguous"));
	}

	private ConnectorEndpointConfiguration jdbcConnector(String id, String uri) {
		ConnectorEndpointConfiguration connector = new ConnectorEndpointConfiguration();
		connector.setId(id);
		connector.setUri(uri);
		return connector;
	}

	private ConnectorResourceConfiguration resource(String connectorId, String name) {
		ConnectorResourceConfiguration resource = new ConnectorResourceConfiguration();
		resource.setConnector(connectorId);
		resource.setName(name);
		return resource;
	}

	private void initializeConnectors(EtlConfiguration configuration) throws Exception {
		Method method = EtlConfiguration.class.getDeclaredMethod("initializeConnectorRegistry");
		method.setAccessible(true);
		method.invoke(configuration);
	}

	private void initializeResources(EtlItemConfiguration item) throws Exception {
		Method method = EtlItemConfiguration.class.getDeclaredMethod("initializeConnectorResources");
		method.setAccessible(true);
		method.invoke(item);
	}
}
