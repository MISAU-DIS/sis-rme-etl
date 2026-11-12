package org.openmrs.module.epts.etl.conf.destination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.EtlSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlItemConfigurationComponent;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.ActionOnLoad;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorWriteMode;
import org.openmrs.module.epts.etl.connector.conf.ConnectorConfigurationResolver;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorEndpoint;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

class EtlDestinationConfigurationTest {

	@Test
	void shouldInferJdbcTableAsTheUniqueDestinationVariant() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"destination-db\",\"name\":\"patient\"},"
				+ "\"tableDataDestination\":{\"tableName\":\"patient\"}"
				+ "}");

		destination.initialize(new EtlItemConfiguration());

		assertTrue(destination.getDestinationDataConfiguration() instanceof JdbcTableDestinationConfiguration);
		assertSame(destination, destination.getDestinationDataConfiguration().getParentDestination());
		assertEquals("patient", destination.getName());
		assertTrue(destination.getDestinationDataConfiguration().supports(ConnectorWriteMode.UPDATE));
		assertEquals(EtlDstType.db, destination.requireJdbcTableDestination().getDstType());
	}

	@Test
	void shouldExposeResourceAliasAndPropagateItToJdbcAdapter() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"destination-db\",\"name\":\"patient\","
				+ "\"alias\":\"destination_patient\"},"
				+ "\"tableDataDestination\":{}"
				+ "}");
		destination.initialize(new EtlItemConfiguration());
		destination.setResolvedResource(ConnectorConfigurationResolver.resolveResource(destination.getResource(), null,
				ConnectorDirection.DESTINATION, new JdbcConnectorEndpoint(new DBConnectionInfo())));

		assertEquals("patient", destination.getName());
		assertEquals("destination_patient", destination.getAlias());
		assertEquals("destination_patient", destination.requireJdbcTableDestination().getTableAlias());
	}

	@Test
	void shouldPreserveJdbcGeneratedAliasWhenResourceAliasIsNotConfigured() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"destination-db\",\"name\":\"patient\"},"
				+ "\"tableDataDestination\":{}"
				+ "}");
		destination.initialize(new EtlItemConfiguration());
		JdbcTableDestinationConfiguration jdbc = destination.requireJdbcTableDestination();
		jdbc.setTableAlias("patient_automatically_resolved");
		destination.setResolvedResource(ConnectorConfigurationResolver.resolveResource(destination.getResource(), null,
				ConnectorDirection.DESTINATION, new JdbcConnectorEndpoint(new DBConnectionInfo())));

		assertEquals("patient_automatically_resolved", jdbc.getTableAlias());
		assertEquals("patient_automatically_resolved", destination.getAlias());
	}

	@Test
	void shouldInferJsonAndCsvWithoutDependingOnConnectorType() throws Exception {
		EtlDestinationConfiguration json = read("{"
				+ "\"resource\":{\"connector\":\"output\",\"name\":\"patients.json\"},"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"jsonDataDestination\":{}"
				+ "}");
		EtlDestinationConfiguration csv = read("{"
				+ "\"resource\":{\"connector\":\"output\",\"name\":\"patients.csv\"},"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"csvDataDestination\":{\"delimiter\":\";\"}"
				+ "}");

		json.initialize(new EtlItemConfiguration());
		csv.initialize(new EtlItemConfiguration());

		assertTrue(json.getDestinationDataConfiguration() instanceof JsonDestinationConfiguration);
		assertEquals("output", json.getConnector());
		assertTrue(json.getDestinationDataConfiguration().supports(ConnectorWriteMode.INSERT));
		assertTrue(csv.getDestinationDataConfiguration() instanceof CsvDestinationConfiguration);
		assertEquals(";", csv.getCsvDataDestination().getDelimiter());
	}

	@Test
	void shouldRejectIncompleteLogicalDestinationOrAmbiguousAdapters() throws Exception {
		EtlDestinationConfiguration missing = read("{}");
		EtlDestinationConfiguration ambiguous = read("{"
				+ "\"resource\":{\"name\":\"records\"},"
				+ "\"jsonDataDestination\":{},"
				+ "\"csvDataDestination\":{}"
				+ "}");

		EtlConfException missingError = assertThrows(EtlConfException.class,
				() -> missing.initialize(new EtlItemConfiguration()));
		EtlConfException ambiguousError = assertThrows(EtlConfException.class,
				() -> ambiguous.initialize(new EtlItemConfiguration()));

		assertTrue(missingError.getMessage().contains("destination.fields"));
		assertTrue(ambiguousError.getMessage().contains("[jsonDataDestination, csvDataDestination]"));
	}

	@Test
	void shouldRequireResourceNameAndRecordFieldsForNonJdbcDestinations() throws Exception {
		EtlDestinationConfiguration noResource = read("{"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"jsonDataDestination\":{}"
				+ "}");
		EtlDestinationConfiguration noFields = read("{"
				+ "\"resource\":{\"name\":\"patients.json\"},"
				+ "\"jsonDataDestination\":{}"
				+ "}");

		assertTrue(assertThrows(EtlConfException.class,
				() -> noResource.initialize(new EtlItemConfiguration())).getMessage().contains("resource is required"));
		assertTrue(assertThrows(EtlConfException.class,
				() -> noFields.initialize(new EtlItemConfiguration())).getMessage()
						.contains("destination.fields"));
	}

	@Test
	void shouldKeepResourceOwnedByDestinationAggregate() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"tableDataDestination\":{\"tableName\":\"patient\","
				+ "\"resource\":{\"connector\":\"other-db\",\"name\":\"patient\"}}"
				+ "}");

		EtlConfException error = assertThrows(EtlConfException.class,
				() -> destination.initialize(new EtlItemConfiguration()));

		assertTrue(error.getMessage().contains("tableDataDestination.resource is not allowed"));
	}

	@Test
	void shouldRejectUnknownDestinationProperties() {
		assertThrows(UnrecognizedPropertyException.class,
				() -> read("{\"jdbcTable\":{\"tableName\":\"patient\"}}"));
		assertThrows(JsonMappingException.class,
				() -> read("{\"name\":\"patient\",\"connector\":\"database\"}"));
		assertThrows(UnrecognizedPropertyException.class,
				() -> read("{\"resource\":{\"name\":\"patients.json\"},"
						+ "\"jsonDataDestination\":{\"name\":\"patients\","
						+ "\"fields\":[{\"name\":\"uuid\"}]}}"));
	}

	@Test
	void shouldOwnLogicalTransformationRulesIndependentlyOfPhysicalVariant() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"output\",\"name\":\"patients.json\"},"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"jsonDataDestination\":{},"
				+ "\"mapping\":[{\"srcField\":\"person_uuid\",\"dstField\":\"uuid\"}],"
				+ "\"applyCondition\":\"person_id != null\","
				+ "\"unmappedFieldBehavior\":\"USE_DEFAULT\","
				+ "\"mappingResolutionStrategy\":\"MANUAL_WITH_DEFAULTS\","
				+ "\"identifiedRecordAction\":\"PATCH_UPDATE\","
				+ "\"patchFields\":[\"uuid\"],"
				+ "\"allowDuplicateDestinationMappings\":true"
				+ "}");

		destination.initialize(new EtlItemConfiguration());

		assertEquals("uuid", destination.getMapping().get(0).getDstField());
		assertEquals("person_id != null", destination.getCondition());
		assertEquals(ActionOnEtlIssue.USE_DEFAULT, destination.unmappedFieldBehavior());
		assertEquals(FieldMappingResolutionStrategy.MANUAL_WITH_DEFAULTS,
				destination.mappingResolutionStrategy());
		assertEquals(ActionOnLoad.PATCH_UPDATE, destination.getIdentifiedRecordAction());
		assertTrue(destination.executePatchUpdateOnIdentifiedRecordAction());
		assertEquals(Collections.singletonList("uuid"), destination.getPatchFields());
		assertTrue(destination.hasPatchFields());
		assertTrue(destination.allowDuplicateDestinationMappings());
		assertEquals("uuid", destination.getFields().get(0).getName());
	}

	@Test
	void shouldCopyOnlyLogicalRulesFromLegacyDestination() {
		JdbcTableDestinationConfiguration legacy = new JdbcTableDestinationConfiguration("patient");
		FieldsMapping mapping = new FieldsMapping();
		mapping.setSrcField("person_uuid");
		mapping.setDstField("uuid");
		legacy.setMapping(new ArrayList<>(Collections.singletonList(mapping)));
		legacy.setExcludedFields(new ArrayList<>(Collections.singletonList("internal_id")));
		legacy.setUnmappedFieldBehavior(ActionOnEtlIssue.IGNORE);
		legacy.setMappingResolutionStrategy(FieldMappingResolutionStrategy.MANUAL_ONLY);
		legacy.setIdentifiedRecordAction(ActionOnLoad.UPDATE);
		legacy.setPatchFields(new ArrayList<>(Collections.singletonList("uuid")));

		EtlDestinationConfiguration destination = new EtlDestinationConfiguration();
		destination.copyTransformationRulesFrom(legacy);
		legacy.getMapping().clear();
		legacy.getExcludedFields().clear();

		assertEquals(1, destination.getMapping().size());
		assertEquals("uuid", destination.getMapping().get(0).getDstField());
		assertEquals(Collections.singletonList("internal_id"), destination.getExcludedFields());
		assertEquals(ActionOnEtlIssue.IGNORE, destination.unmappedFieldBehavior());
		assertEquals(FieldMappingResolutionStrategy.MANUAL_ONLY, destination.mappingResolutionStrategy());
		assertTrue(destination.executeUpdateOnIdentifiedRecordAction());
		assertEquals(Collections.singletonList("uuid"), destination.getPatchFields());
	}

	@Test
	void shouldKeepRootPatchFieldsSynchronizedWithJdbcAdapter() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"destination-db\",\"name\":\"patient\"},"
				+ "\"patchFields\":[\"uuid\",\"date_changed\"],"
				+ "\"tableDataDestination\":{}"
				+ "}");

		destination.initialize(new EtlItemConfiguration());
		destination.requireJdbcTableDestination().pullConfigurationFromParentDestination();

		assertEquals(destination.getPatchFields(),
				destination.requireJdbcTableDestination().getPatchFields());
	}

	@Test
	void jdbcAdapterShouldPullLogicalRulesFromParentDestination() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"destination-db\",\"name\":\"patient\"},"
				+ "\"identifiedRecordAction\":\"PATCH_UPDATE\","
				+ "\"mappingResolutionStrategy\":\"MANUAL_ONLY\","
				+ "\"unmappedFieldBehavior\":\"IGNORE\","
				+ "\"patchFields\":[\"uuid\"],"
				+ "\"mapping\":[{\"srcField\":\"person_uuid\",\"dstField\":\"uuid\"}],"
				+ "\"tableDataDestination\":{}"
				+ "}");
		destination.initialize(new EtlItemConfiguration());
		JdbcTableDestinationConfiguration jdbc = destination.requireJdbcTableDestination();

		jdbc.pullConfigurationFromParentDestination();

		assertEquals(ActionOnLoad.PATCH_UPDATE, jdbc.getIdentifiedRecordAction());
		assertEquals(FieldMappingResolutionStrategy.MANUAL_ONLY, jdbc.getMappingResolutionStrategy());
		assertEquals(ActionOnEtlIssue.IGNORE, jdbc.getUnmappedFieldBehavior());
		assertEquals(Collections.singletonList("uuid"), jdbc.getPatchFields());
		assertEquals("uuid", jdbc.getMapping().get(0).getDstField());
	}

	@Test
	void shouldExposeLegacyDestinationCapabilitiesWithoutMakingNonJdbcDestinationAJdbcTable() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"output\",\"name\":\"patients.json\"},"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"jsonDataDestination\":{}"
				+ "}");

		destination.initialize(jsonSourceItem());
		destination.setDisabled(Boolean.TRUE);

		assertTrue(destination instanceof EtlTransformTarget);
		assertTrue(destination instanceof EtlDataSource);
		assertTrue(destination instanceof EtlDstConf);
		assertTrue(destination instanceof EtlItemConfigurationComponent);
		assertTrue(destination.isDisabled());
		assertTrue(destination.shouldBeProcessed(null, null, null, null));

		destination.setDisabled(Boolean.FALSE);
		destination.fullLoad();

		assertTrue(destination.isFullLoaded());
		assertEquals(1, destination.getAllAvaliableDataSource().size());
		assertEquals(1, destination.getAllPrefferredDataSource().size());
		assertEquals("uuid", destination.getAllMapping().get(0).getDstField());
		assertTrue(destination.useDefaultTransformer());
	}

	@Test
	void shouldBeFullyLoadableWithoutAPhysicalAdapterAndOwnItsPrimaryKey() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"primaryKey\":{\"fields\":[{\"name\":\"uuid\"}]}"
				+ "}");

		destination.initialize(jsonSourceItem());
		destination.fullLoad();

		assertTrue(!destination.hasDestinationDataConfiguration());
		assertEquals(null, destination.getName());
		assertEquals("uuid", destination.getPrimaryKey().asSimpleKey().getName());
		assertEquals("uuid", destination.getAllMapping().get(0).getDstField());
		assertTrue(destination.isFullLoaded());
	}

	@Test
	void shouldInferJdbcAdapterFromTheResolvedConnectorResource() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"database\",\"name\":\"patient\"}"
				+ "}");
		destination.initialize(new EtlItemConfiguration());

		DBConnectionInfo connectionInfo = new DBConnectionInfo();
		connectionInfo.setConnectionURI("jdbc:mysql://localhost/openmrs");
		destination.setResolvedResource(ConnectorConfigurationResolver.resolveResource(destination.getResource(), null,
				ConnectorDirection.DESTINATION, new JdbcConnectorEndpoint(connectionInfo)));

		assertTrue(destination.getDestinationDataConfiguration() instanceof JdbcTableDestinationConfiguration);
		assertEquals("patient", destination.getTableDataDestination().getTableName());
		assertSame(destination, destination.getTableDataDestination().getParentDestination());
		assertSame(connectionInfo, destination.getTableDataDestination().getRelatedConnInfo());
	}

	private EtlItemConfiguration jsonSourceItem() throws Exception {
		EtlItemConfiguration item = new EtlItemConfiguration();
		item.setRelatedEtlConf(new EtlConfiguration());

		JsonPrimaryDataSourceConfiguration primary = new JsonPrimaryDataSourceConfiguration();
		primary.setName("patients");
		primary.setFields(Collections.singletonList(Field.fastCreateWithType("uuid", "java.lang.String")));
		SourceResourceConfiguration sourceResource = new SourceResourceConfiguration();
		sourceResource.setName("patients.json");
		EtlSourceConfiguration source = new EtlSourceConfiguration();
		source.setResource(sourceResource);
		source.setJsonDataSource(primary);
		item.setSource(source);
		source.initialize(item);
		return item;
	}

	private EtlDestinationConfiguration read(String json) throws Exception {
		return new ObjectMapperProvider().getContext(EtlDestinationConfiguration.class)
				.readValue(json, EtlDestinationConfiguration.class);
	}
}
