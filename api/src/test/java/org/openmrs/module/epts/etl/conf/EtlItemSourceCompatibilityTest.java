package org.openmrs.module.epts.etl.conf;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.QueryDataSourceConfig;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

class EtlItemSourceCompatibilityTest {

	@Test
	void shouldNormalizeLegacySrcConfWithoutCopyingItsJdbcConfiguration() throws Exception {
		EtlItemConfiguration item = read("{" +
				"\"srcConf\":{\"tableName\":\"patient\"," +
				"\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"}," +
				"\"limitToOneResult\":true,\"trackProcessingState\":true," +
				"\"extraTableDataSource\":[{\"tableName\":\"person\"}]}}" );
		SrcConf legacySource = item.getSrcConf();

		item.prepareSourceConfiguration();

		assertSame(legacySource, item.getPrimaryDataSource());
		assertSame(legacySource, item.getSource().getTableDataSource());
		assertSame(legacySource, item.getSrcConf());
		assertNull(legacySource.getResource());
		assertEquals("source-db", item.getSource().getConnector());
		assertSame(item.getSource().getExtraTableDataSource(), legacySource.getExtraTableDataSource());
		assertTrue(item.getSource().getProcessingPolicy().getLimitToOneResult());
		assertTrue(item.getSource().getProcessingPolicy().getTrackProcessingState());
	}

	@Test
	void shouldExposeNewJdbcSourceThroughTheTemporaryLegacyView() throws Exception {
		EtlItemConfiguration item = read("{" +
				"\"source\":{\"connector\":\"source-db\"," +
				"\"tableDataSource\":{\"tableName\":\"patient\"}," +
				"\"extraQueryDataSource\":[{\"name\":\"visits\",\"query\":\"select 1\"}]}}" );

		item.prepareSourceConfiguration();

		assertSame(item.getSource().getTableDataSource(), item.getSrcConf());
		assertNull(item.getSrcConf().getResource());
		assertEquals("source-db", item.getSource().getConnector());
		assertSame(item.getSource().getExtraQueryDataSource(), item.getSrcConf().getExtraQueryDataSource());
		assertEquals("patient", item.getSrcConf().getTableName());

		QueryDataSourceConfig replacement = new QueryDataSourceConfig();
		replacement.setName("replacement");
		item.getSrcConf().setExtraQueryDataSource(Collections.singletonList(replacement));
		assertSame(item.getSource().getExtraQueryDataSource(), item.getSrcConf().getExtraQueryDataSource());
		assertEquals("replacement", item.getSource().getExtraQueryDataSource().get(0).getName());
	}

	@Test
	void shouldKeepNonJdbcPrimaryIndependentFromLegacySrcConf() throws Exception {
		EtlItemConfiguration item = read("{" +
				"\"source\":{\"connector\":\"patient-api\"," +
				"\"resource\":{\"name\":\"patients.json\"}," +
				"\"jsonDataSource\":{\"name\":\"patients\",\"fields\":[{\"name\":\"uuid\"}]}}}" );

		item.prepareSourceConfiguration();

		assertTrue(item.getPrimaryDataSource() instanceof JsonPrimaryDataSourceConfiguration);
		assertNull(item.getSrcConf());
		assertEquals("uuid", item.getPrimaryDataSource().getFields().get(0).getName());
	}

	@Test
	void shouldRejectSourceAndSrcConfDeclaredTogether() throws Exception {
		EtlItemConfiguration item = read("{" +
				"\"srcConf\":{\"tableName\":\"legacy_patient\"}," +
				"\"source\":{\"connector\":\"source-db\"," +
				"\"tableDataSource\":{\"tableName\":\"patient\"}}}" );

		EtlConfException error = assertThrows(EtlConfException.class, item::prepareSourceConfiguration);

		assertTrue(error.getMessage().contains("both 'source' and legacy 'srcConf'"));
	}

	private EtlItemConfiguration read(String json) throws Exception {
		return new ObjectMapperProvider().getContext(EtlItemConfiguration.class)
				.readValue(json, EtlItemConfiguration.class);
	}
}
