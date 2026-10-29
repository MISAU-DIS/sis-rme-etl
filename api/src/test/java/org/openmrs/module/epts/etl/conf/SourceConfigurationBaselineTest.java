package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

/**
 * Compatibility baseline for the source model that predates the neutral
 * SourceConfiguration aggregate.
 */
class SourceConfigurationBaselineTest {

	@Test
	void shouldKeepReadingAllLegacyAdditionalSourceFamilies() throws Exception {
		String json = "{" +
				"\"tableName\":\"patient\"," +
				"\"extraTableDataSource\":[{\"tableName\":\"person\"}]," +
				"\"extraQueryDataSource\":[{\"name\":\"latestVisit\",\"query\":\"select 1\"}]," +
				"\"extraObjectDataSource\":[{\"name\":\"calculatedValues\"}]," +
				"\"extraJsonDataSource\":[{\"name\":\"demographics\",\"payload\":\"{}\"}]" +
				"}";

		SrcConf source = new ObjectMapperProvider().getContext(SrcConf.class).readValue(json, SrcConf.class);

		assertEquals("patient", source.getTableName());
		assertEquals(1, source.getExtraTableDataSource().size());
		assertEquals("person", source.getExtraTableDataSource().get(0).getTableName());
		assertEquals(1, source.getExtraQueryDataSource().size());
		assertEquals("latestVisit", source.getExtraQueryDataSource().get(0).getName());
		assertEquals(1, source.getExtraObjectDataSource().size());
		assertEquals("calculatedValues", source.getExtraObjectDataSource().get(0).getName());
		assertEquals(1, source.getExtraJsonDataSource().size());
		assertEquals("demographics", source.getExtraJsonDataSource().get(0).getName());
	}

	@Test
	void shouldKeepReadingNonJdbcSourceSchemaThroughTheLegacySourceContract() throws Exception {
		String json = "{" +
				"\"tableName\":\"patient-event\"," +
				"\"resource\":{\"connector\":\"events-file\",\"name\":\"patients.jsonl\"}," +
				"\"recordSchema\":{\"fields\":[" +
				"{\"name\":\"uuid\",\"dataType\":\"java.lang.String\",\"allowNull\":false}" +
				"]}}";

		SrcConf source = new ObjectMapperProvider().getContext(SrcConf.class).readValue(json, SrcConf.class);

		assertEquals("events-file", source.getResource().getConnector());
		assertEquals("patients.jsonl", source.getResource().getName());
		assertNotNull(source.getRecordSchema());
		assertEquals("uuid", source.getRecordSchema().getFields().get(0).getName());
	}
}
