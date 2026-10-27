package org.openmrs.module.epts.etl.connector.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
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

	private SrcConf sourceConfiguration() {
		SrcConf source = new SrcConf();
		Field id = Field.fastCreateWithType("patient_id", "java.lang.Integer");
		id.setAllowNull(false);
		Field name = Field.fastCreateWithType("name", "java.lang.String");
		name.setAllowNull(true);
		source.setFields(Arrays.asList(id, name));
		source.setFullLoaded(true);
		return source;
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
