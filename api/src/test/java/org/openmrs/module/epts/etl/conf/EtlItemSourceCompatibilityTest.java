package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

class EtlItemSourceCompatibilityTest {

	@Test
	void shouldExposeJdbcPrimaryOnlyThroughSourceConfiguration() throws Exception {
		EtlItemConfiguration item = read("{" +
				"\"source\":{\"connector\":\"source-db\"," +
				"\"tableDataSource\":{\"tableName\":\"patient\"}}}");

		item.prepareSourceConfiguration();

		assertSame(item.getSource().getTableDataSource(), item.getPrimaryDataSource());
		assertTrue(item.getPrimaryDataSource() instanceof JdbcTablePrimaryDataSourceConfiguration);
		assertEquals("patient", item.getPrimaryDataSource().getObjectName());
	}

	@Test
	void shouldKeepNonJdbcPrimaryConnectorNeutral() throws Exception {
		EtlItemConfiguration item = read("{" +
				"\"source\":{\"connector\":\"patient-api\"," +
				"\"resource\":{\"name\":\"patients.json\"}," +
				"\"jsonDataSource\":{\"name\":\"patients\",\"fields\":[{\"name\":\"uuid\"}]}}}");

		item.prepareSourceConfiguration();

		assertTrue(item.getPrimaryDataSource() instanceof JsonPrimaryDataSourceConfiguration);
		assertEquals("uuid", item.getPrimaryDataSource().getFields().get(0).getName());
	}

	@Test
	void shouldRejectRemovedSrcConfProperty() {
		Exception error = assertThrows(Exception.class,
				() -> read("{\"srcConf\":{\"tableName\":\"patient\"}}"));

		assertTrue(error.getMessage().contains("srcConf"));
	}

	private EtlItemConfiguration read(String json) throws Exception {
		return new ObjectMapperProvider().getContext(EtlItemConfiguration.class)
				.readValue(json, EtlItemConfiguration.class);
	}
}
