package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.EtlSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonMainOutputDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonSimpleOutputDataSource;
import org.openmrs.module.epts.etl.conf.datasource.QueryDataSourceConfig;
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.TableDataSourceConfig;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSource;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.conf.ConnectorEndpointConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

class EtlConnectorConfigurationTest {

	@Test
	void shouldDeserializeResourceOnAdditionalDataSource() throws Exception {
		String json = "{\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"},"
				+ "\"tableDataSource\":{\"tableName\":\"patient\"},"
				+ "\"extraTableDataSource\":[{\"tableName\":\"person\",\"resource\":{"
				+ "\"connector\":\"demographics-db\",\"name\":\"person\"}}]}";

		EtlSourceConfiguration source = new ObjectMapperProvider().getContext(EtlSourceConfiguration.class)
				.readValue(json, EtlSourceConfiguration.class);

		assertEquals("demographics-db", source.getExtraTableDataSource().get(0).getResource().getConnector());
		assertEquals("person", source.getExtraTableDataSource().get(0).getResource().getName());
	}

	@Test
	void shouldDeserializeResourcesOnNonTableAdditionalDataSources() throws Exception {
		String json = "{\"resource\":{\"connector\":\"source-file\",\"name\":\"patients.json\"},"
				+ "\"jsonDataSource\":{\"name\":\"patient\",\"fields\":[{\"name\":\"uuid\"}]},"
				+ "\"extraQueryDataSource\":[{\"name\":\"visits\",\"query\":\"select 1\","
				+ "\"resource\":{\"connector\":\"clinical-db\",\"name\":\"visits\"}}],"
				+ "\"extraObjectDataSource\":[{\"name\":\"calculated\",\"resource\":{"
				+ "\"connector\":\"rules\",\"name\":\"calculate\"}}],"
				+ "\"extraJsonRecordDataSource\":[{\"name\":\"address\",\"path\":\"/address\","
				+ "\"fields\":[{\"name\":\"uuid\"}],\"resource\":{"
				+ "\"connector\":\"address-file\",\"name\":\"addresses.json\"}}]}";

		EtlSourceConfiguration source = new ObjectMapperProvider().getContext(EtlSourceConfiguration.class)
				.readValue(json, EtlSourceConfiguration.class);

		assertEquals("clinical-db", source.getExtraQueryDataSource().get(0).getResource().getConnector());
		assertEquals("rules", source.getExtraObjectDataSource().get(0).getResource().getConnector());
		assertEquals("address-file", source.getExtraJsonRecordDataSource().get(0).getResource().getConnector());
	}

	@Test
	void shouldResolveOwnAndInheritedResourcesForAdditionalDataSources() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		configuration.setConnectors(java.util.Arrays.asList(
				jdbcConnector("source-db", "jdbc:mysql://source/openmrs"),
				jdbcConnector("demographics-db", "jdbc:mysql://demographics/openmrs"),
				jdbcConnector("json-db", "jdbc:mysql://json/openmrs"),
				jdbcConnector("json-parent-db", "jdbc:mysql://json-parent/openmrs")));

		JdbcTablePrimaryDataSourceConfiguration primary = new JdbcTablePrimaryDataSourceConfiguration();
		primary.setTableName("patient");
		TableDataSourceConfig table = new TableDataSourceConfig();
		table.setTableName("person");
		table.setResource(resource("demographics-db", "person"));
		TransformableDataSource calculated = new TransformableDataSource();
		calculated.setName("calculated");
		QueryDataSourceConfig visits = new QueryDataSourceConfig();
		visits.setName("visits");
		visits.setQuery("select 1");
		JsonDataSource json = new JsonDataSource();
		json.setName("jsonDetails");
		json.setResource(resource("json-db", "json-details"));
		JsonMainOutputDataSource jsonOutput = new JsonMainOutputDataSource();
		jsonOutput.setName("jsonOutput");
		JsonSimpleOutputDataSource jsonParent = new JsonSimpleOutputDataSource();
		jsonParent.setName("jsonParent");
		jsonParent.setResource(resource("json-parent-db", "json-parent"));
		jsonOutput.setParentOutputDataSource(java.util.Collections.singletonList(jsonParent));
		json.setOutputDataSource(jsonOutput);

		EtlSourceConfiguration source = new EtlSourceConfiguration();
		SourceResourceConfiguration primaryResource = new SourceResourceConfiguration();
		primaryResource.setConnector("source-db");
		primaryResource.setName("patient");
		source.setResource(primaryResource);
		source.setTableDataSource(primary);
		source.setExtraTableDataSource(java.util.Collections.singletonList(table));
		source.setExtraObjectDataSource(java.util.Collections.singletonList(calculated));
		source.setExtraQueryDataSource(java.util.Collections.singletonList(visits));
		source.setExtraJsonDataSource(java.util.Collections.singletonList(json));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		item.setSource(source);
		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://demographics/openmrs", table.getResolvedResource().getEndpoint().getUri());
		assertSame(item.getResolvedSrcResource(), calculated.getResolvedResource());
		assertEquals("visits", visits.getResolvedResource().getName());
		assertEquals("jdbc:mysql://source/openmrs", visits.getResolvedResource().getEndpoint().getUri());
		assertEquals("jdbc:mysql://json/openmrs", json.getResolvedResource().getEndpoint().getUri());
		assertSame(json.getResolvedResource(), jsonOutput.getResolvedResource());
		assertEquals("jdbc:mysql://json-parent/openmrs", jsonParent.getResolvedResource().getEndpoint().getUri());
	}

	@Test
	void shouldDeserializeResourcesEmbeddedInSourceAndDestinationConfigurations() throws Exception {
		String json = "{\"etlItemConfiguration\":[{"
				+ "\"source\":{\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"},"
				+ "\"tableDataSource\":{\"tableName\":\"patient\"}},"
				+ "\"destinations\":[{\"resource\":{\"connector\":\"jsonl-output\","
				+ "\"name\":\"patients.jsonl\",\"properties\":{\"partition\":\"daily\"}}}]}]}";

		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class)
				.readValue(json, EtlConfiguration.class);
		EtlItemConfiguration item = configuration.getEtlItemConfiguration().get(0);
		JdbcTablePrimaryDataSourceConfiguration source = item.getSource().getTableDataSource();
		EtlDestinationConfiguration destination = configuration.getEtlItemConfiguration().get(0).getDestinations().get(0);

		assertEquals("source-db", item.getSource().getResource().getConnector());
		assertEquals("patient", item.getSource().getResource().getName());
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
				configuration.getInitializedConnectorRegistry().select(null, "source-db", EtlSide.SRC,
						false).getUri());
		assertEquals("reader", configuration.getInitializedConnectorRegistry()
				.select(null, "source-db", EtlSide.SRC, false).getUserName());
		assertEquals(ConnectorType.JDBC,
				configuration.getInitializedConnectorRegistry()
						.select(null, "destination-db", EtlSide.DST, false).getType());
		assertEquals("writer",
				configuration.getInitializedConnectorRegistry()
						.select(null, "destination-db", EtlSide.DST, false).getUserName());
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
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		source.setTableName("patient");
		setJdbcSource(item, source);
		JdbcTableDestinationConfiguration destination = new JdbcTableDestinationConfiguration("patient_copy");
		destination.setResource(resource(null, "patient_copy"));
		item.setDestinations(java.util.Collections.singletonList(destination(destination)));

		initializeConnectors(configuration);
		initializeResources(item);
		ConnectorResource resolvedSource = item.getResolvedSrcResource();
		ConnectorResource resolvedDestination = item.getResolvedDstResources().get(0);

		assertEquals("patient", resolvedSource.getName());
		assertEquals(EtlSide.SRC, resolvedSource.getDirection());
		assertEquals("patient_copy", resolvedDestination.getName());
		assertEquals(EtlSide.DST, resolvedDestination.getDirection());
		assertSame(source, resolvedSource.unwrap(JdbcTablePrimaryDataSourceConfiguration.class));
		assertSame(resolvedSource, source.getResolvedResource());
		assertSame(resolvedDestination,
				item.getDestinations().get(0).requireJdbcTableDestination().getResolvedResource());
		assertFalse(item.usesLegacyDestinationResource(item.getDestinations().get(0)));
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
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		source.setTableName("patient");
		source.setResource(resource("source-db", "patient"));
		setJdbcSource(item, source);
		JdbcTableDestinationConfiguration databaseDestination = new JdbcTableDestinationConfiguration("patient_copy");
		databaseDestination.setResource(resource("destination-db", "patient_copy"));
		JdbcTableDestinationConfiguration fileDestination = new JdbcTableDestinationConfiguration("patient_export");
		fileDestination.setResource(resource("jsonl-output", "patients.jsonl"));
		EtlDestinationConfiguration database = destination(databaseDestination);
		EtlDestinationConfiguration file = destination(fileDestination);
		item.setDestinations(java.util.Arrays.asList(database, file));

		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://source/openmrs", item.getResolvedSrcResource().getEndpoint().getUri());
		assertEquals(ConnectorType.JDBC,
				item.getResolvedDstResource(database).getEndpoint().getType());
		assertEquals(ConnectorType.FILE, item.getResolvedDstResource(file).getEndpoint().getType());
		assertEquals("patients.jsonl", item.getResolvedDstResource(file).getName());
		assertFalse(item.usesLegacyDestinationResource(database));
		assertFalse(item.usesLegacyDestinationResource(file));
		assertSame(item.getResolvedSrcResource(), source.getResolvedResource());
		assertSame(item.getResolvedDstResource(database), database.getResolvedResource());
		assertSame(item.getResolvedDstResource(file), file.getResolvedResource());
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
		JdbcTablePrimaryDataSourceConfiguration sourceConfiguration = new JdbcTablePrimaryDataSourceConfiguration();
		sourceConfiguration.setTableName("patient");
		sourceConfiguration.setResource(resource("source-db", "patient"));
		JdbcTableDestinationConfiguration destinationConfiguration = new JdbcTableDestinationConfiguration();
		destinationConfiguration.setTableName("patient_archive");
		destinationConfiguration.setResource(resource("archive-db", "patient_archive"));
		setJdbcSource(item, sourceConfiguration);
		EtlDestinationConfiguration destination = destination(destinationConfiguration);
		item.setDestinations(java.util.Collections.singletonList(destination));
		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://source/openmrs", item.getResolvedSrcResource().getEndpoint().getUri());
		assertEquals("jdbc:mysql://archive/history", item.getResolvedDstResources().get(0).getEndpoint().getUri());
		assertSame(item.getResolvedSrcResource().getEndpoint(), item.getResolvedSrcResource().getEndpoint());
		assertSame(item.getResolvedSrcResource(), sourceConfiguration.getResolvedResource());
		assertSame(item.getResolvedDstResource(destination), destination.getResolvedResource());
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
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		source.setTableName("patient");
		setJdbcSource(item, source);
		JdbcTableDestinationConfiguration first = new JdbcTableDestinationConfiguration("patient_first");
		first.setResource(resource("first-db", "patient_first"));
		JdbcTableDestinationConfiguration second = new JdbcTableDestinationConfiguration("patient_second");
		second.setResource(resource("second-db", "patient_second"));
		item.setDestinations(java.util.Arrays.asList(destination(first), destination(second)));

		initializeConnectors(configuration);
		initializeResources(item);

		JdbcTableDestinationConfiguration resolvedFirst = item.getDestinations().get(0).requireJdbcTableDestination();
		JdbcTableDestinationConfiguration resolvedSecond = item.getDestinations().get(1).requireJdbcTableDestination();
		assertEquals("jdbc:mysql://first/etl", resolvedFirst.getRelatedConnInfo().getConnectionURI());
		assertEquals("jdbc:mysql://second/etl", resolvedSecond.getRelatedConnInfo().getConnectionURI());
		assertNotSame(resolvedFirst.getRelatedConnInfo(), resolvedSecond.getRelatedConnInfo());
	}

	@Test
	void shouldBindJdbcPrimarySourceToItsOwnConnectorConnectionInfo() throws Exception {
		DBConnectionInfo globalSource = new DBConnectionInfo();
		globalSource.setConnectionURI("jdbc:mysql://global/openmrs");

		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(globalSource);
		configuration.setConnectors(java.util.Collections
				.singletonList(jdbcConnector("clinical-db", "jdbc:mysql://clinical/openmrs")));

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		source.setTableName("patient");
		source.setResource(resource("clinical-db", "patient"));
		setJdbcSource(item, source);

		initializeConnectors(configuration);
		initializeResources(item);

		assertEquals("jdbc:mysql://clinical/openmrs", source.getRelatedConnInfo().getConnectionURI());
		assertNotSame(globalSource, source.getRelatedConnInfo());
		assertSame(item.getResolvedSrcResource().getEndpoint().unwrap(DBConnectionInfo.class),
				source.getRelatedConnInfo());
	}

	@Test
	void shouldPreserveOneConnectorEndpointAcrossJdbcTableResources() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setConnectors(java.util.Collections
				.singletonList(jdbcConnector("shared-source", "jdbc:mysql://source/openmrs")));
		configuration.setDefaultSourceConnector("shared-source");

		EtlItemConfiguration parent = new EtlItemConfiguration();
		parent.setRelatedEtlConf(configuration);
		JdbcTablePrimaryDataSourceConfiguration parentSource = new JdbcTablePrimaryDataSourceConfiguration();
		parentSource.setTableName("person");
		parentSource.setResource(resource("shared-source", "person"));
		setJdbcSource(parent, parentSource);

		EtlChildItemConfiguration child = new EtlChildItemConfiguration();
		child.setRelatedEtlConf(configuration);
		JdbcTablePrimaryDataSourceConfiguration childSource = new JdbcTablePrimaryDataSourceConfiguration();
		childSource.setTableName("address");
		childSource.setResource(resource("shared-source", "address"));
		setJdbcSource(child, childSource);

		initializeConnectors(configuration);
		initializeResources(parent);
		initializeResources(child);

		assertSame(parent.getResolvedSrcResource().getEndpoint(), child.getResolvedSrcResource().getEndpoint());
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
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		source.setTableName("patient");
		setJdbcSource(item, source);
		JdbcTableDestinationConfiguration jdbcDestination = new JdbcTableDestinationConfiguration("patient_copy");
		jdbcDestination.setResource(resource("destination-db", "patient_copy"));
		JdbcTableDestinationConfiguration fileDestination = new JdbcTableDestinationConfiguration("patient_export");
		fileDestination.setResource(resource("jsonl-output", "patients.jsonl"));
		item.setDestinations(java.util.Arrays.asList(destination(jdbcDestination), destination(fileDestination)));

		initializeConnectors(configuration);
		initializeResources(item);

		DestinationConnectorConnections connections = item.createDestinationConnections(item);

		assertTrue(connections.isJdbc(item.getDestinations().get(0)));
		assertFalse(connections.isJdbc(item.getDestinations().get(1)));
		assertSame(item.getDestinations().get(0).getResolvedResource(),
				connections.getResource(item.getDestinations().get(0)));
		assertTrue(connections.getOpenedConnections().isEmpty());
		assertNull(connections.getIfJdbc(item.getDestinations().get(1)));
		assertThrows(ForbiddenOperationException.class, () -> connections.get(item.getDestinations().get(1)));

		connections.close();
		assertThrows(IllegalStateException.class, () -> connections.get(item.getDestinations().get(0)));
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
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		source.setTableName("patient");
		setJdbcSource(item, source);

		initializeConnectors(configuration);
		Method prepare = EtlItemConfiguration.class
		        .getDeclaredMethod("prepareDestinationConfigurationsForConnectorFullLoad");
		prepare.setAccessible(true);
		prepare.invoke(item);

		assertEquals(1, item.getDestinations().size());
		JdbcTableDestinationConfiguration generated = item.getDestinations().get(0).requireJdbcTableDestination();
		assertEquals("patient", generated.getTableName());
		assertEquals("target_schema", generated.getSchema());
		assertEquals("jdbc:invalid:must-not-open", generated.getRelatedConnInfo().getConnectionURI());
		assertSame(generated.getResolvedResource(), item.getResolvedDstResource(item.getDestinations().get(0)));
	}

	@Test
	void shouldPreferLegacyStateConnectorWhenSourceConnectorIsOmitted() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSrcConnInfo(new DBConnectionInfo());
		configuration.setConnectors(java.util.Arrays.asList(jdbcConnector("first", "jdbc:first"),
				jdbcConnector("second", "jdbc:second")));
		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		JdbcTablePrimaryDataSourceConfiguration sourceConfiguration = new JdbcTablePrimaryDataSourceConfiguration();
		sourceConfiguration.setTableName("patient");
		sourceConfiguration.setResource(resource(null, "patient"));
		setJdbcSource(item, sourceConfiguration);
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

		JdbcTablePrimaryDataSourceConfiguration primary = new JdbcTablePrimaryDataSourceConfiguration();
		primary.setTableName("patient");
		primary.setFields(java.util.Collections.singletonList(Field.fastCreateField("patient_id")));
		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		setJdbcSource(item, primary);
		item.prepareSourceConfiguration();

		JdbcTablePrimaryDataSourceConfiguration etlDatabaseSource = item.getEtlDatabaseDataSource();
		assertEquals("jdbc:mysql://source/openmrs",
				etlDatabaseSource.getResolvedResource().getEndpoint().getUri());
		assertEquals(ConnectorType.JDBC, etlDatabaseSource.getResolvedResource().getEndpoint().getType());
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
	void shouldCreateEtlDatabaseDataSourceFromSrcConnInfoWithoutReplacingJsonPrimary() throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		DBConnectionInfo stateConnection = new DBConnectionInfo();
		stateConnection.setConnectionURI("jdbc:mysql://state/etl_state");
		configuration.setSrcConnInfo(stateConnection);
		initializeConnectors(configuration);

		JsonPrimaryDataSourceConfiguration jsonPrimary = new JsonPrimaryDataSourceConfiguration();
		jsonPrimary.setName("patient_event");
		jsonPrimary.setFields(java.util.Collections
				.singletonList(Field.fastCreateWithType("uuid", "java.lang.String")));
		PrimaryKey primaryKey = new PrimaryKey();
		primaryKey.addKey(Key.fastCreateKey("uuid"));
		jsonPrimary.setPrimaryKey(primaryKey);
		SourceResourceConfiguration resource = new SourceResourceConfiguration();
		resource.setName("patients.json");
		EtlSourceConfiguration source = new EtlSourceConfiguration();
		source.setResource(resource);
		source.setJsonDataSource(jsonPrimary);

		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(configuration);
		item.setSource(source);
		item.prepareSourceConfiguration();

		JdbcTablePrimaryDataSourceConfiguration etlDatabaseSource = item.getEtlDatabaseDataSource();

		assertSame(jsonPrimary, item.getPrimaryDataSource());
		assertNotSame(jsonPrimary, etlDatabaseSource);
		assertEquals("patient_event", etlDatabaseSource.getTableName());
		assertEquals("etl_state", etlDatabaseSource.getSchema());
		assertEquals(ConnectorType.JDBC, etlDatabaseSource.getResolvedResource().getEndpoint().getType());
		assertSame(stateConnection,
				etlDatabaseSource.getResolvedResource().getEndpoint().unwrap(DBConnectionInfo.class));
		assertEquals(1, etlDatabaseSource.getFields().size());
		assertTrue(etlDatabaseSource.hasPK());
		assertNotSame(primaryKey, etlDatabaseSource.getPrimaryKey());
		assertEquals("uuid", etlDatabaseSource.getPrimaryKey().asSimpleKey().getName());
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
				+ "\"etlItemConfiguration\":[{\"source\":{\"tableDataSource\":{\"tableName\":\"patient\"}},\"destinations\":[{"
				+ "\"resource\":{\"connector\":\"jsonl-output\",\"name\":\"patients.jsonl\"},\"fields\":["
				+ "{\"name\":\"uuid\",\"dataType\":\"java.lang.String\",\"allowNull\":false},"
				+ "{\"name\":\"birthdate\",\"dataType\":\"java.util.Date\"}]}]}]}";

		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class)
				.readValue(json, EtlConfiguration.class);
		EtlDestinationConfiguration destination = configuration.getEtlItemConfiguration().get(0).getDestinations().get(0);

		assertEquals(2, destination.getFields().size());
		assertEquals(String.class, destination.getFields().get(0).getTypeClass());
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
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		source.setTableName("patient");
		setJdbcSource(item, source);
		JdbcTableDestinationConfiguration destination = new JdbcTableDestinationConfiguration("patient-export");
		destination.setResource(resource("jsonl-output", "patients.jsonl"));
		EtlDestinationConfiguration configuredDestination = new EtlDestinationConfiguration();
		configuredDestination.setResource(destination.getResource());
		item.setDestinations(java.util.Collections.singletonList(configuredDestination));
		configuration.setEtlItemConfiguration(java.util.Collections.singletonList(item));

		assertFalse(configuration.validateConnectorConfigurations().isEmpty());
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

	private EtlDestinationConfiguration destination(JdbcTableDestinationConfiguration jdbcConfiguration) {
		EtlDestinationConfiguration destination = new EtlDestinationConfiguration();
		destination.setResource(jdbcConfiguration.getResource());
		if (jdbcConfiguration.getResource() == null
				|| !"jsonl-output".equals(jdbcConfiguration.getResource().getConnector())) {
			JdbcTableDestinationConfiguration table = new JdbcTableDestinationConfiguration(
					jdbcConfiguration.getTableName());
			table.setSchema(jdbcConfiguration.getSchema());
			destination.setTableDataDestination(table);
		} else {
			destination.setFields(java.util.Collections.singletonList(Field.fastCreateField("uuid")));
		}
		return destination;
	}

	private void setJdbcSource(EtlItemConfiguration item,
			JdbcTablePrimaryDataSourceConfiguration primaryDataSource) {
		EtlSourceConfiguration source = new EtlSourceConfiguration();
		ConnectorResourceConfiguration configuredResource = primaryDataSource.getResource();

		if (configuredResource != null) {
			SourceResourceConfiguration sourceResource = new SourceResourceConfiguration();
			sourceResource.setConnector(configuredResource.getConnector());
			sourceResource.setName(configuredResource.getName());
			sourceResource.setProperties(configuredResource.getProperties());
			source.setResource(sourceResource);
			primaryDataSource.setResource(null);
		}

		source.setTableDataSource(primaryDataSource);
		item.setSource(source);
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
