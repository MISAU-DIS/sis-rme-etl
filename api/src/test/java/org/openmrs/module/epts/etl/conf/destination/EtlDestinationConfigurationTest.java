package org.openmrs.module.epts.etl.conf.destination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.EtlSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDstConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlItemConfigurationComponent;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.ActionOnLoad;
import org.openmrs.module.epts.etl.conf.types.FieldMappingResolutionStrategy;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

class EtlDestinationConfigurationTest {

	@Test
	void shouldInferJdbcTableAsTheUniqueDestinationVariant() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"connector\":\"destination-db\","
				+ "\"tableDataDestination\":{\"tableName\":\"patient\"}"
				+ "}");

		destination.initialize(new EtlItemConfiguration());

		assertTrue(destination.getDestinationDataConfiguration() instanceof JdbcTableDestinationConfiguration);
		assertSame(destination, destination.getDestinationDataConfiguration().getParentDestination());
		assertEquals("patient", destination.getDestinationDataConfiguration().getName());
		assertTrue(destination.getDestinationDataConfiguration().supports(ConnectorWriteMode.UPDATE));
	}

	@Test
	void shouldInferJsonAndCsvWithoutDependingOnConnectorType() throws Exception {
		EtlDestinationConfiguration json = read("{"
				+ "\"resource\":{\"connector\":\"output\",\"name\":\"patients.json\"},"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"jsonDataDestination\":{\"name\":\"patients\"}"
				+ "}");
		EtlDestinationConfiguration csv = read("{"
				+ "\"connector\":\"output\","
				+ "\"resource\":{\"name\":\"patients.csv\"},"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"csvDataDestination\":{\"name\":\"patients\",\"delimiter\":\";\"}"
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
	void shouldRejectMissingOrAmbiguousDestinationVariant() throws Exception {
		EtlDestinationConfiguration missing = read("{\"connector\":\"output\"}");
		EtlDestinationConfiguration ambiguous = read("{"
				+ "\"resource\":{\"name\":\"records\"},"
				+ "\"jsonDataDestination\":{\"name\":\"json\"},"
				+ "\"csvDataDestination\":{\"name\":\"csv\"}"
				+ "}");

		EtlConfException missingError = assertThrows(EtlConfException.class,
				() -> missing.initialize(new EtlItemConfiguration()));
		EtlConfException ambiguousError = assertThrows(EtlConfException.class,
				() -> ambiguous.initialize(new EtlItemConfiguration()));

		assertTrue(missingError.getMessage().contains("exactly one"));
		assertTrue(ambiguousError.getMessage().contains("[jsonDataDestination, csvDataDestination]"));
	}

	@Test
	void shouldRequireResourceNameAndRecordFieldsForNonJdbcDestinations() throws Exception {
		EtlDestinationConfiguration noResource = read("{"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"jsonDataDestination\":{\"name\":\"patients\"}"
				+ "}");
		EtlDestinationConfiguration noFields = read("{"
				+ "\"resource\":{\"name\":\"patients.json\"},"
				+ "\"jsonDataDestination\":{\"name\":\"patients\"}"
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
				+ "\"connector\":\"destination-db\","
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
				+ "\"jsonDataDestination\":{\"name\":\"patients\"},"
				+ "\"mapping\":[{\"srcField\":\"person_uuid\",\"dstField\":\"uuid\"}],"
				+ "\"applyCondition\":\"person_id != null\","
				+ "\"unmappedFieldBehavior\":\"USE_DEFAULT\","
				+ "\"mappingResolutionStrategy\":\"MANUAL_WITH_DEFAULTS\","
				+ "\"identifiedRecordAction\":\"PATCH_UPDATE\","
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
		assertTrue(destination.allowDuplicateDestinationMappings());
		assertEquals("uuid", destination.getFields().get(0).getName());
	}

	@Test
	void shouldCopyOnlyLogicalRulesFromLegacyDestination() {
		DstConf legacy = new DstConf("patient");
		FieldsMapping mapping = new FieldsMapping();
		mapping.setSrcField("person_uuid");
		mapping.setDstField("uuid");
		legacy.setMapping(new ArrayList<>(Collections.singletonList(mapping)));
		legacy.setExcludedFields(new ArrayList<>(Collections.singletonList("internal_id")));
		legacy.setUnmappedFieldBehavior(ActionOnEtlIssue.IGNORE);
		legacy.setMappingResolutionStrategy(FieldMappingResolutionStrategy.MANUAL_ONLY);
		legacy.setIdentifiedRecordAction(ActionOnLoad.UPDATE);

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
	}

	@Test
	void shouldExposeLegacyDestinationCapabilitiesWithoutMakingNonJdbcDestinationAJdbcTable() throws Exception {
		EtlDestinationConfiguration destination = read("{"
				+ "\"resource\":{\"connector\":\"output\",\"name\":\"patients.json\"},"
				+ "\"fields\":[{\"name\":\"uuid\"}],"
				+ "\"jsonDataDestination\":{\"name\":\"patients\"}"
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
