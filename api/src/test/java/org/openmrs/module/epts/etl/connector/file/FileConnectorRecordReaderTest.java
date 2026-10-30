package org.openmrs.module.epts.etl.connector.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonRecordCardinality;
import org.openmrs.module.epts.etl.conf.datasource.JsonRecordDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonRecordLayout;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.conf.ConnectorConfigurationResolver;
import org.openmrs.module.epts.etl.connector.conf.ConnectorEndpointConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;

class FileConnectorRecordReaderTest {

	@TempDir Path directory;

	@Test
	void shouldReadJsonLinesWithinRequestedInterval() throws Exception {
		Files.write(directory.resolve("patients.jsonl"), Arrays.asList(
				"{\"patient_id\":1,\"name\":\"Ana\"}",
				"{\"patient_id\":2,\"name\":\"Bento\"}",
				"{\"patient_id\":3,\"name\":\"Carla\"}"), StandardCharsets.UTF_8);
		ConnectorResource resource = resource("patients.jsonl");
		FileConnectorRecordReader<EtlDatabaseObject> reader = new FileConnectorRecordReader<>(resource,
				sourceConfiguration(), null);

		java.util.List<EtlDatabaseObject> records = reader.read(
				new ConnectorReadRequest<>(new IntervalExtremeRecord(2, 3), null, null, false));

		assertEquals(2, records.size());
		assertEquals(2, records.get(0).getFieldValue("patient_id"));
		assertEquals("Carla", records.get(1).getFieldValue("name"));
		assertEquals(3, FileConnectorRecordReader.countRecords(resource));
	}

	@Test
	void shouldRejectUnknownJsonFields() throws Exception {
		Files.write(directory.resolve("patients.jsonl"),
				java.util.Collections.singletonList("{\"patient_id\":1,\"unexpected\":true}"),
				StandardCharsets.UTF_8);
		FileConnectorRecordReader<EtlDatabaseObject> reader = new FileConnectorRecordReader<>(
				resource("patients.jsonl"), sourceConfiguration(), null);

		Exception error = assertThrows(Exception.class,
				() -> reader.read(new ConnectorReadRequest<>(null, null, null, false)));

		assertEquals(true, error.getMessage().contains("Unknown JSON field 'unexpected'"));
	}

	@Test
	void shouldReadPrimaryAndExtraJsonRecordDataSources() throws Exception {
		String document = "{\"person_data\":[{\"id\":100,\"full_name\":\"Josh Muhamad\","
				+ "\"creation_date\":\"2026-10-27 22:08:14\","
				+ "\"uuid\":\"9acc18fb-44ef-11ef-97f9-e86a64ea1bc5\","
				+ "\"address\":{\"id\":1,\"person_id\":100,"
				+ "\"full_address\":\"Josh Muhamad's address\","
				+ "\"creation_date\":\"2024-07-18 14:20:41\","
				+ "\"uuid\":\"4b6f44d2-4500-11ef-97f9-e86a64ea1bc5\"},"
				+ "\"identifiers\":[{\"value\":\"A-1\"},{\"value\":\"B-2\"}]}]}";
		Files.writeString(directory.resolve("person-data.json"), document, StandardCharsets.UTF_8);
		JsonPrimaryDataSourceConfiguration source = nestedSourceConfiguration();
		ConnectorResource resource = jsonResource("person-data.json", JsonRecordLayout.ARRAY, "/person_data");
		FileConnectorRecordReader<EtlDatabaseObject> reader = new FileConnectorRecordReader<>(resource, source, null);

		java.util.List<EtlDatabaseObject> records = reader
				.read(new ConnectorReadRequest<>(null, null, null, false));

		assertEquals(1, records.size());
		assertEquals(100, records.get(0).getFieldValue("id"));
		assertEquals(3, records.get(0).getAuxLoadObject().size());
		EtlDatabaseObject address = records.get(0).getAuxLoadObject().get(0);
		assertEquals("address", address.getRelatedConfiguration().getObjectName());
		assertEquals("Josh Muhamad's address", address.getFieldValue("full_address"));
		assertEquals("4b6f44d2-4500-11ef-97f9-e86a64ea1bc5", address.getFieldValue("uuid"));
		address.setFieldValue("id", 2);
		assertEquals(2, address.getFieldValue("id"));
		assertEquals("A-1", records.get(0).getAuxLoadObject().get(1).getFieldValue("value"));
		assertEquals("B-2", records.get(0).getAuxLoadObject().get(2).getFieldValue("value"));
		assertEquals(1, FileConnectorRecordReader.countRecords(resource));
	}

	private PrimaryDataSourceConfiguration sourceConfiguration() throws Exception {
		JsonPrimaryDataSourceConfiguration source = new JsonPrimaryDataSourceConfiguration();
		Field id = Field.fastCreateWithType("patient_id", "java.lang.Integer");
		id.setAllowNull(false);
		Field name = Field.fastCreateWithType("name", "java.lang.String");
		name.setAllowNull(true);
		source.setFields(Arrays.asList(id, name));
		source.fullLoad(null);
		return source;
	}

	private JsonPrimaryDataSourceConfiguration nestedSourceConfiguration() throws Exception {
		JsonPrimaryDataSourceConfiguration source = new JsonPrimaryDataSourceConfiguration();
		source.setName("person");
		JsonRecordDataSourceConfiguration address = new JsonRecordDataSourceConfiguration();
		address.setName("address");
		address.setPath("/address");
		address.setCardinality(JsonRecordCardinality.ONE);
		address.setRequired(true);
		address.setFields(Arrays.asList(
				Field.fastCreateWithType("id", "java.lang.Integer"),
				Field.fastCreateWithType("person_id", "java.lang.Integer"),
				Field.fastCreateWithType("full_address", "java.lang.String"),
				Field.fastCreateWithType("creation_date", "java.lang.String"),
				Field.fastCreateWithType("uuid", "java.lang.String")));
		JsonRecordDataSourceConfiguration identifiers = new JsonRecordDataSourceConfiguration();
		identifiers.setName("identifiers");
		identifiers.setPath("/identifiers");
		identifiers.setCardinality(JsonRecordCardinality.MANY);
		identifiers.setFields(
				java.util.Collections.singletonList(Field.fastCreateWithType("value", "java.lang.String")));
		source.setFields(Arrays.asList(
				Field.fastCreateWithType("id", "java.lang.Integer"),
				Field.fastCreateWithType("full_name", "java.lang.String"),
				Field.fastCreateWithType("creation_date", "java.lang.String"),
				Field.fastCreateWithType("uuid", "java.lang.String")));
		SourceConfiguration parent = new SourceConfiguration();
		SourceResourceConfiguration configuredResource = new SourceResourceConfiguration();
		configuredResource.setName("person-data.json");
		configuredResource.setLayout(JsonRecordLayout.ARRAY);
		configuredResource.setRecordPath("/person_data");
		parent.setResource(configuredResource);
		parent.setJsonDataSource(source);
		parent.setExtraJsonRecordDataSource(Arrays.asList(address, identifiers));
		parent.initialize(new org.openmrs.module.epts.etl.conf.EtlItemConfiguration());
		parent.fullLoad(null);
		return source;
	}

	private ConnectorResource jsonResource(String name, JsonRecordLayout layout, String recordPath) {
		ConnectorEndpointConfiguration endpointConfiguration = new ConnectorEndpointConfiguration();
		endpointConfiguration.setType(ConnectorType.FILE);
		endpointConfiguration.setUri(directory.toString());
		SourceResourceConfiguration resourceConfiguration = new SourceResourceConfiguration();
		resourceConfiguration.setName(name);
		resourceConfiguration.setLayout(layout);
		resourceConfiguration.setRecordPath(recordPath);
		return ConnectorConfigurationResolver.resolveResource(resourceConfiguration, null, ConnectorDirection.SOURCE,
				ConnectorConfigurationResolver.resolveEndpoint(endpointConfiguration, null));
	}

	private ConnectorResource resource(String name) {
		ConnectorEndpointConfiguration endpointConfiguration = new ConnectorEndpointConfiguration();
		endpointConfiguration.setType(ConnectorType.FILE);
		endpointConfiguration.setUri(directory.toString());
		ConnectorResourceConfiguration resourceConfiguration = new ConnectorResourceConfiguration();
		resourceConfiguration.setName(name);
		return ConnectorConfigurationResolver.resolveResource(resourceConfiguration, null, ConnectorDirection.SOURCE,
				ConnectorConfigurationResolver.resolveEndpoint(endpointConfiguration, null));
	}
}
