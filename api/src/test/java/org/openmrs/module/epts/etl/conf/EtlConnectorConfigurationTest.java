package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

class EtlConnectorConfigurationTest {

	@Test
	void shouldDeserializeResourcesEmbeddedInSourceAndDestinationConfigurations() throws Exception {
		String json = "{\"etlItemConfiguration\":[{"
				+ "\"srcConf\":{\"tableName\":\"patient\",\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"}},"
				+ "\"dstConf\":[{\"tableName\":\"patient_export\",\"resource\":{\"connector\":\"jsonl-output\","
				+ "\"name\":\"patients.jsonl\",\"properties\":{\"partition\":\"daily\"}}}]}]}";

		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class)
				.readValue(json, EtlConfiguration.class);
		SrcConf source = configuration.getEtlItemConfiguration().get(0).getSrcConf();
		DstConf destination = configuration.getEtlItemConfiguration().get(0).getDstConf().get(0);

		assertEquals("source-db", source.getResource().getConnector());
		assertEquals("patient", source.getResource().getName());
		assertEquals("jsonl-output", destination.getResource().getConnector());
		assertEquals("patients.jsonl", destination.getResource().getName());
		assertEquals("daily", destination.getResource().getProperties().get("partition"));
		assertNull(source.getResolvedResource());
		assertNull(destination.getResolvedResource());

		ConnectorResourceConfiguration copy = destination.getResource().copy();
		assertNotSame(destination.getResource(), copy);
		assertEquals(destination.getResource().getProperties(), copy.getProperties());
	}

	@Test
	void shouldRejectResourcesDeclaredAtEtlItemRoot() {
		String json = "{\"etlItemConfiguration\":[{\"srcResource\":{"
				+ "\"connector\":\"source-db\",\"name\":\"patient\"}}]}";

		Exception error = org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
				() -> new ObjectMapperProvider().getContext(EtlConfiguration.class)
						.readValue(json, EtlConfiguration.class));

		assertTrue(error.getMessage().contains("srcResource"));
	}

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
		assertEquals("jdbc:mysql://legacy/source", configuration.getSrcConnInfo().getConnectionURI());
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
		assertSame(resolvedSource, source.getResolvedResource());
		assertSame(resolvedDestination, destination.getResolvedResource());
		assertTrue(item.usesLegacyDestinationResource(destination));
	}

	@Test
	void shouldResolveResourcesEmbeddedInSourceAndDestinations() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		ConnectorEndpointConfiguration sourceConnector = jdbcConnector("source-db", "jdbc:mysql://source/openmrs");
		ConnectorEndpointConfiguration databaseConnector = jdbcConnector("destination-db",
				"jdbc:mysql://destination/openmrs");
		ConnectorEndpointConfiguration fileConnector = new ConnectorEndpointConfiguration();
		fileConnector.setId("jsonl-output");
		fileConnector.setType(ConnectorType.FILE);
		fileConnector.setUri("data/output");
		configuration.setConnectors(java.util.Arrays.asList(sourceConnector, databaseConnector, fileConnector));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf source = new SrcConf();
		source.setTableName("patient");
		source.setResource(resource("source-db", "patient"));
		item.setSrcConf(source);
		DstConf databaseDestination = new DstConf("patient_copy");
		databaseDestination.setResource(resource("destination-db", "patient_copy"));
		DstConf fileDestination = new DstConf("patient_export");
		fileDestination.setResource(resource("jsonl-output", "patients.jsonl"));
		item.setDstConf(java.util.Arrays.asList(databaseDestination, fileDestination));

		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://source/openmrs", item.getResolvedSrcResource().getEndpoint().getUri());
		assertEquals(ConnectorType.JDBC,
				item.getResolvedDstResource(databaseDestination).getEndpoint().getType());
		assertEquals(ConnectorType.FILE, item.getResolvedDstResource(fileDestination).getEndpoint().getType());
		assertEquals("patients.jsonl", item.getResolvedDstResource(fileDestination).getName());
		assertFalse(item.usesLegacyDestinationResource(databaseDestination));
		assertFalse(item.usesLegacyDestinationResource(fileDestination));
		assertSame(item.getResolvedSrcResource(), source.getResolvedResource());
		assertSame(item.getResolvedDstResource(databaseDestination), databaseDestination.getResolvedResource());
		assertSame(item.getResolvedDstResource(fileDestination), fileDestination.getResolvedResource());
	}

	@Test
	void shouldReportUnavailableConnectorCapabilitiesDuringConfigurationValidation() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
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
		configuration.setSrcConnInfo(new DBConnectionInfo());
		ConnectorEndpointConfiguration sourceConnector = jdbcConnector("source-db", "jdbc:mysql://source/openmrs");
		ConnectorEndpointConfiguration archiveConnector = jdbcConnector("archive-db", "jdbc:mysql://archive/history");
		configuration.setConnectors(java.util.Arrays.asList(sourceConnector, archiveConnector));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf sourceConfiguration = new SrcConf();
		sourceConfiguration.setTableName("patient");
		DstConf destinationConfiguration = new DstConf();
		destinationConfiguration.setTableName("patient_archive");
		item.setSrcConf(sourceConfiguration);
		item.setDstConf(java.util.Collections.singletonList(destinationConfiguration));
		sourceConfiguration.setResource(resource("source-db", "patient"));
		destinationConfiguration.setResource(resource("archive-db", "patient_archive"));
		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://source/openmrs", item.getResolvedSrcResource().getEndpoint().getUri());
		assertEquals("jdbc:mysql://archive/history", item.getResolvedDstResources().get(0).getEndpoint().getUri());
		assertSame(item.getResolvedSrcResource().getEndpoint(), item.getResolvedSrcResource().getEndpoint());
		assertSame(item.getResolvedSrcResource(), sourceConfiguration.getResolvedResource());
		assertSame(item.getResolvedDstResource(destinationConfiguration), destinationConfiguration.getResolvedResource());
	}

	@Test
	void shouldBindEachJdbcDestinationToItsOwnConnectorConnectionInfo() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		ConnectorEndpointConfiguration firstConnector = jdbcConnector("first-db", "jdbc:mysql://first/etl");
		ConnectorEndpointConfiguration secondConnector = jdbcConnector("second-db", "jdbc:mysql://second/etl");
		configuration.setConnectors(java.util.Arrays.asList(firstConnector, secondConnector));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf source = new SrcConf();
		source.setTableName("patient");
		item.setSrcConf(source);
		DstConf first = new DstConf("patient_first");
		first.setResource(resource("first-db", "patient_first"));
		DstConf second = new DstConf("patient_second");
		second.setResource(resource("second-db", "patient_second"));
		item.setDstConf(java.util.Arrays.asList(first, second));

		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://first/etl", first.getRelatedConnInfo().getConnectionURI());
		assertEquals("jdbc:mysql://second/etl", second.getRelatedConnInfo().getConnectionURI());
		assertNotSame(first.getRelatedConnInfo(), second.getRelatedConnInfo());
	}

	@Test
	void shouldCreateLazyDestinationConnectionAggregator() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		ConnectorEndpointConfiguration database = jdbcConnector("destination-db", "jdbc:invalid:must-not-open");
		ConnectorEndpointConfiguration file = new ConnectorEndpointConfiguration();
		file.setId("jsonl-output");
		file.setType(ConnectorType.FILE);
		file.setUri("data/output");
		configuration.setConnectors(java.util.Arrays.asList(database, file));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf source = new SrcConf();
		source.setTableName("patient");
		item.setSrcConf(source);
		DstConf jdbcDestination = new DstConf("patient_copy");
		jdbcDestination.setResource(resource("destination-db", "patient_copy"));
		DstConf fileDestination = new DstConf("patient_export");
		fileDestination.setResource(resource("jsonl-output", "patients.jsonl"));
		item.setDstConf(java.util.Arrays.asList(jdbcDestination, fileDestination));

		initializeConnectors(configuration);
		initializeResources(item);

		DestinationConnectorConnections connections = item.createDestinationConnections(item);

		assertTrue(connections.isJdbc(jdbcDestination));
		assertFalse(connections.isJdbc(fileDestination));
		assertSame(jdbcDestination.getResolvedResource(), connections.getResource(jdbcDestination));
		assertTrue(connections.getOpenedConnections().isEmpty());
		assertNull(connections.getIfJdbc(fileDestination));
		assertThrows(ForbiddenOperationException.class, () -> connections.get(fileDestination));

		connections.close();
		assertThrows(IllegalStateException.class, () -> connections.get(jdbcDestination));
	}

	@Test
	void shouldGenerateDefaultDestinationFromConnectorWithoutOpeningLegacyConnection() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		ConnectorEndpointConfiguration destination = jdbcConnector("destination-db", "jdbc:invalid:must-not-open");
		destination.setProperties(java.util.Collections.singletonMap("schema", "target_schema"));
		configuration.setConnectors(java.util.Collections.singletonList(destination));
		configuration.setDefaultDestinationConnector("destination-db");

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf source = new SrcConf();
		source.setTableName("patient");
		item.setSrcConf(source);

		initializeConnectors(configuration);
		Method prepare = EtlItemConfiguration.class
		        .getDeclaredMethod("prepareDestinationConfigurationsForConnectorFullLoad");
		prepare.setAccessible(true);
		prepare.invoke(item);

		assertEquals(1, item.getDstConf().size());
		DstConf generated = item.getDstConf().get(0);
		assertEquals("patient", generated.getTableName());
		assertEquals("target_schema", generated.getSchema());
		assertEquals("jdbc:invalid:must-not-open", generated.getRelatedConnInfo().getConnectionURI());
		assertSame(generated.getResolvedResource(), item.getResolvedDstResource(generated));
	}

	@Test
	void shouldPreferLegacyStateConnectorWhenSourceConnectorIsOmitted() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		configuration.setConnectors(java.util.Arrays.asList(jdbcConnector("first", "jdbc:first"),
				jdbcConnector("second", "jdbc:second")));
		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf sourceConfiguration = new SrcConf();
		sourceConfiguration.setTableName("patient");
		sourceConfiguration.setResource(resource(null, "patient"));
		item.setSrcConf(sourceConfiguration);
		initializeConnectors(configuration);

		initializeResources(item);
		assertSame(configuration.getSrcConnInfo(),
				item.getResolvedSrcResource().getEndpoint().unwrap(DBConnectionInfo.class));
	}

	@Test
	void shouldDeriveSrcConnInfoFromJdbcDefaultSourceConnectorWhenItIsAbsent() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setConnectors(java.util.Collections
				.singletonList(jdbcConnector("source-db", "jdbc:mysql://source/openmrs")));
		configuration.setDefaultSourceConnector("source-db");

		initializeConnectors(configuration);

		assertEquals("jdbc:mysql://source/openmrs", configuration.getSrcConnInfo().getConnectionURI());
	}

	@Test
	void shouldKeepExplicitSrcConnInfoWhenDefaultSourceConnectorIsNotJdbc() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		DBConnectionInfo stateConnection = new DBConnectionInfo();
		stateConnection.setConnectionURI("jdbc:mysql://state/etl");
		configuration.setSrcConnInfo(stateConnection);
		ConnectorEndpointConfiguration source = new ConnectorEndpointConfiguration();
		source.setId("events");
		source.setType(ConnectorType.MESSAGE_QUEUE);
		configuration.setConnectors(java.util.Collections.singletonList(source));
		configuration.setDefaultSourceConnector("events");

		initializeConnectors(configuration);

		assertSame(stateConnection, configuration.getSrcConnInfo());
	}

	@Test
	void shouldRequireSrcConnInfoWhenDefaultSourceConnectorIsAbsent() {
		EtlConfiguration configuration = new EtlConfiguration();

		assertTrue(configuration.validateConnectorConfigurations().stream()
				.anyMatch(issue -> issue.contains("srcConnInfo is required")));
	}

	@Test
	void shouldRequireSrcConnInfoWhenDefaultSourceConnectorIsNotJdbc() {
		EtlConfiguration configuration = new EtlConfiguration();
		ConnectorEndpointConfiguration source = new ConnectorEndpointConfiguration();
		source.setId("events");
		source.setType(ConnectorType.MESSAGE_QUEUE);
		configuration.setConnectors(java.util.Collections.singletonList(source));
		configuration.setDefaultSourceConnector("events");

		assertTrue(configuration.validateConnectorConfigurations().stream()
				.anyMatch(issue -> issue.contains("srcConnInfo is required")));
	}

	@Test
	void shouldDeserializeLogicalRecordSchemaForNonJdbcResource() throws Exception {
		String json = "{\"srcConnInfo\":{\"connectionURI\":\"jdbc:mysql://state/etl\"},"
				+ "\"connectors\":[{\"id\":\"jsonl-output\",\"type\":\"FILE\",\"uri\":\"data/output\"}],"
				+ "\"etlItemConfiguration\":[{\"srcConf\":{\"tableName\":\"patient\"},\"dstConf\":[{"
				+ "\"tableName\":\"patient-export\",\"resource\":{\"connector\":\"jsonl-output\","
				+ "\"name\":\"patients.jsonl\"},\"recordSchema\":{\"fields\":["
				+ "{\"name\":\"uuid\",\"dataType\":\"java.lang.String\",\"allowNull\":false},"
				+ "{\"name\":\"birthdate\",\"dataType\":\"java.util.Date\"}]}}]}]}";

		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class)
				.readValue(json, EtlConfiguration.class);
		DstConf destination = configuration.getEtlItemConfiguration().get(0).getDstConf().get(0);

		assertEquals(RecordFormat.JSON, destination.getRecordSchema().getFormat());
		assertEquals(2, destination.getRecordSchema().getFields().size());
		assertEquals(String.class, destination.getRecordSchema().getFields().get(0).getTypeClass());
	}

	@Test
	void shouldRejectNonJdbcResourceWithoutRecordSchema() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		ConnectorEndpointConfiguration output = new ConnectorEndpointConfiguration();
		output.setId("jsonl-output");
		output.setType(ConnectorType.FILE);
		configuration.setConnectors(java.util.Collections.singletonList(output));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		SrcConf source = new SrcConf();
		source.setTableName("patient");
		item.setSrcConf(source);
		DstConf destination = new DstConf("patient-export");
		destination.setResource(resource("jsonl-output", "patients.jsonl"));
		item.setDstConf(java.util.Collections.singletonList(destination));
		configuration.setEtlItemConfiguration(java.util.Collections.singletonList(item));

		assertTrue(configuration.validateConnectorConfigurations().stream()
				.anyMatch(issue -> issue.contains("recordSchema is required")));
	}

	@Test
	void shouldRejectDuplicatedLogicalFieldNamesIgnoringCase() {
		RecordSchemaConfiguration schema = new RecordSchemaConfiguration();
		schema.setFields(java.util.Arrays.asList(Field.fastCreateField("uuid"), Field.fastCreateField("UUID")));

		Exception error = org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
				() -> schema.resolveFields("test resource"));

		assertTrue(error.getMessage().contains("Duplicated recordSchema field"));
	}

	@Test
	void shouldLoadConfiguredFieldsWithoutJdbcMetadata() throws Exception {
		ExposedTableConfiguration configuration = new ExposedTableConfiguration();
		configuration.setTableName("patient-event");
		configuration.setTableAlias("patient_event");
		RecordSchemaConfiguration schema = new RecordSchemaConfiguration();
		Field configuredField = Field.fastCreateWithType("uuid", "java.lang.String");
		schema.setFields(java.util.Collections.singletonList(configuredField));
		configuration.setRecordSchema(schema);

		configuration.loadRecordSchema();

		assertEquals(1, configuration.getFields().size());
		assertEquals("uuid", configuration.getFields().get(0).getName());
		assertNotSame(configuredField, configuration.getFields().get(0));
		assertEquals(SchemaMetadataLoadSource.CONFIGURED_RECORD_SCHEMA,
				configuration.getSchemaMetadataLoadSource());
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

	private static class ExposedTableConfiguration extends GenericTableConfiguration {

		void loadRecordSchema() throws Exception {
			loadConfiguredRecordSchema();
		}
	}
}
