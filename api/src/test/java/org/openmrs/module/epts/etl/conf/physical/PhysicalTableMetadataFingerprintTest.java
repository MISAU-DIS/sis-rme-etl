package org.openmrs.module.epts.etl.conf.physical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;

import org.junit.Test;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PhysicalTableMetadataFingerprintTest {

	@Test
	public void shouldIgnoreJsonFormattingAndPropertyOrder() throws Exception {
		String firstJson = "{\"formatVersion\":2,\"key\":{"
				+ "\"logicalDatabaseId\":\"openmrs_2_6_sesp\",\"databaseDialect\":\"\","
				+ "\"catalog\":\"\",\"schema\":\"\",\"tableName\":\"encounter\"},"
				+ "\"columns\":[{\"name\":\"encounter_id\",\"dataType\":\"int\",\"length\":11,"
				+ "\"decimalDigits\":0,\"nullable\":false,\"autoIncrement\":true,\"timestamp\":false}],"
				+ "\"primaryKey\":null,\"uniqueKeys\":[],\"importedForeignKeys\":[],"
				+ "\"exportedForeignKeys\":[]}";
		String reorderedJson = "{\n  \"exportedForeignKeys\" : [], \"importedForeignKeys\" : [],"
				+ " \"uniqueKeys\" : [], \"primaryKey\" : null, \"columns\" : [{"
				+ "\"timestamp\":false,\"autoIncrement\":true,\"nullable\":false,\"decimalDigits\":0,"
				+ "\"length\":11,\"dataType\":\"int\",\"name\":\"encounter_id\"}],"
				+ "\"key\":{\"tableName\":\"encounter\",\"schema\":\"\",\"catalog\":\"\","
				+ "\"databaseDialect\":\"\",\"logicalDatabaseId\":\"openmrs_2_6_sesp\"},"
				+ "\"formatVersion\":2}";
		ObjectMapper mapper = new ObjectMapperProvider().getContext(PhysicalTableMetadata.class);

		PhysicalTableMetadata first = mapper.readValue(firstJson, PhysicalTableMetadata.class);
		PhysicalTableMetadata reordered = mapper.readValue(reorderedJson, PhysicalTableMetadata.class);

		assertEquals(first, reordered);
		assertEquals(PhysicalTableMetadataFingerprint.sha256(first),
				PhysicalTableMetadataFingerprint.sha256(reordered));
	}

	@Test
	public void shouldRemainStableAfterJsonRoundTripForCompleteMetadata() throws Exception {
		PhysicalTableMetadata metadata = completeMetadata();
		ObjectMapper mapper = new ObjectMapperProvider().getContext(PhysicalTableMetadata.class);
		PhysicalTableMetadata restored = mapper.readValue(mapper.writerWithDefaultPrettyPrinter()
				.writeValueAsBytes(metadata), PhysicalTableMetadata.class);

		assertEquals(PhysicalTableMetadataFingerprint.sha256(metadata),
				PhysicalTableMetadataFingerprint.sha256(restored));
	}

	@Test
	public void shouldChangeWhenMetadataChanges() throws Exception {
		PhysicalTableMetadata original = completeMetadata();
		PhysicalTableMetadata changed = new PhysicalTableMetadata(original.getKey(),
				Arrays.asList(new PhysicalColumnMetadata("encounter_id", "bigint", 20, 0, false, true, false)),
				original.getPrimaryKey(), original.getUniqueKeys(), original.getImportedForeignKeys(),
				original.getExportedForeignKeys());

		assertNotEquals(PhysicalTableMetadataFingerprint.sha256(original),
				PhysicalTableMetadataFingerprint.sha256(changed));
	}

	private PhysicalTableMetadata completeMetadata() {
		PhysicalTableKey key = new PhysicalTableKey("openmrs_2_6_sesp", "", "", "", "encounter");
		PhysicalColumnMetadata id = new PhysicalColumnMetadata("encounter_id", "int", 11, 0, false, true, false);
		PhysicalColumnMetadata patientId = new PhysicalColumnMetadata("patient_id", "int", 11, null, true, false,
				false);
		PhysicalKeyMetadata primaryKey = new PhysicalKeyMetadata("PRIMARY",
				Arrays.asList(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("encounter_id", "int")), false);
		PhysicalKeyMetadata uniqueKey = new PhysicalKeyMetadata("uk_encounter",
				Arrays.asList(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("patient_id", "int")), true);
		PhysicalForeignKeyMetadata imported = new PhysicalForeignKeyMetadata("fk_encounter_patient", null, "openmrs",
				"patient", Arrays.asList(
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("patient_id", "patient_id")));
		PhysicalExportedForeignKeyMetadata exported = new PhysicalExportedForeignKeyMetadata("fk_obs_encounter", "",
				"openmrs", "obs", Arrays.asList(
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("encounter_id", "encounter_id")));
		return new PhysicalTableMetadata(key, Arrays.asList(id, patientId), primaryKey, Arrays.asList(uniqueKey),
				Arrays.asList(imported), Arrays.asList(exported));
	}
}
