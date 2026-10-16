package org.openmrs.module.epts.etl.connector.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriterFactory;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;
import org.openmrs.module.epts.etl.connector.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.connector.conf.ConnectorConfigurationResolver;
import org.openmrs.module.epts.etl.connector.conf.ConnectorEndpointConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;

class FileConnectorRecordWriterTest {

	@TempDir Path directory;

	@Test
	void shouldCreateJsonLinesDestinationWithoutJdbcConnection() throws Exception {
		ConnectorResource resource = resource("patients.jsonl");
		ConnectorRecordWriter<GenericDatabaseObject> writer = ConnectorRecordWriterFactory.create(resource, null);

		writer.write(new ConnectorWriteRequest<>(ConnectorWriteMode.INSERT, Collections.emptyList()));

		Path output = directory.resolve("patients.jsonl");
		assertTrue(Files.exists(output));
		assertEquals(0L, Files.size(output));
		assertEquals(output.toAbsolutePath(), writer.unwrap(Path.class));
	}

	@Test
	void shouldWriteOneCompactJsonDocumentPerRecord() throws Exception {
		ConnectorRecordWriter<GenericDatabaseObject> writer = ConnectorRecordWriterFactory.create(
				resource("records.jsonl"), null);
		GenericDatabaseObject record = new GenericDatabaseObject();
		Field id = new Field();
		id.setName("patient_id");
		id.setValue(17);
		Field name = new Field();
		name.setName("name");
		name.setValue("Ana");
		record.setFields(Arrays.asList(id, name));

		writer.write(new ConnectorWriteRequest<>(ConnectorWriteMode.INSERT,
				Collections.singletonList(record)));

		assertEquals(Collections.singletonList("{\"patient_id\":17,\"name\":\"Ana\"}"),
				Files.readAllLines(directory.resolve("records.jsonl")));
	}

	@Test
	void shouldRejectUnsupportedMutationAndPathTraversal() {
		ConnectorRecordWriter<GenericDatabaseObject> writer = ConnectorRecordWriterFactory.create(
				resource("patients.jsonl"), null);
		assertThrows(UnsupportedOperationException.class, () -> writer.write(
				new ConnectorWriteRequest<>(ConnectorWriteMode.UPDATE, Collections.emptyList())));
		assertThrows(IllegalArgumentException.class,
				() -> ConnectorRecordWriterFactory.create(resource("../outside.jsonl"), null));
	}

	private ConnectorResource resource(String name) {
		ConnectorEndpointConfiguration endpointConfiguration = new ConnectorEndpointConfiguration();
		endpointConfiguration.setType(ConnectorType.FILE);
		endpointConfiguration.setUri(directory.toString());
		ConnectorResourceConfiguration resourceConfiguration = new ConnectorResourceConfiguration();
		resourceConfiguration.setName(name);
		return ConnectorConfigurationResolver.resolveResource(resourceConfiguration, null,
				ConnectorDirection.DESTINATION,
				ConnectorConfigurationResolver.resolveEndpoint(endpointConfiguration, null));
	}
}
