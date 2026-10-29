package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.CsvPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonDataSource;
import org.openmrs.module.epts.etl.conf.datasource.JsonMainOutputDataSource;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

class SourceConfigurationTest {

	@Test
	void shouldInferJdbcTableAsTheUniquePrimarySource() throws Exception {
		SourceConfiguration source = read("{" +
				"\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"}," +
				"\"jdbcTable\":{\"tableName\":\"patient\"}" +
				"}");

		source.initialize(new EtlItemConfiguration());

		assertTrue(source.getPrimaryDataSource() instanceof org.openmrs.module.epts.etl.conf.datasource.SrcConf);
		assertSame(source, source.getPrimaryDataSource().getParentSource());
		assertSame(source.getResource(), source.getPrimaryDataSource().getSourceResource());
		assertEquals("patient", source.getJdbcTable().getTableName());
	}

	@Test
	void shouldInferJsonAndCsvWithoutDependingOnConnectorType() throws Exception {
		SourceConfiguration json = read("{" +
				"\"resource\":{\"connector\":\"patient-api\",\"name\":\"/patients\"}," +
				"\"json\":{\"recordPath\":\"results\",\"layout\":\"ARRAY\"," +
				"\"fields\":[{\"name\":\"uuid\"}]}}");
		SourceConfiguration csv = read("{" +
				"\"resource\":{\"connector\":\"input-files\",\"name\":\"patients.csv\"}," +
				"\"csv\":{\"delimiter\":\";\",\"hasHeader\":true," +
				"\"fields\":[{\"name\":\"uuid\"}]}}");

		json.initialize(new EtlItemConfiguration());
		csv.initialize(new EtlItemConfiguration());

		assertTrue(json.getPrimaryDataSource() instanceof JsonPrimaryDataSourceConfiguration);
		assertTrue(json.getPrimaryDataSource() instanceof EtlDatabaseObjectConfiguration);
		assertEquals("results", json.getJson().getRecordPath());
		json.fullLoad(null);
		assertTrue(json.isFullLoaded());
		assertTrue(json.getPrimaryDataSource().isFullLoaded());
		assertEquals(GenericDatabaseObject.class, json.getPrimaryDataSource().getEtlRecordClass());
		assertSame(json.getPrimaryDataSource(), json.getPrimaryDataSource().getLoadHealper().getTableConf());
		assertTrue(csv.getPrimaryDataSource() instanceof CsvPrimaryDataSourceConfiguration);
		assertTrue(csv.getPrimaryDataSource() instanceof EtlDatabaseObjectConfiguration);
		assertEquals(";", csv.getCsv().getDelimiter());
	}

	@Test
	void shouldRejectMissingOrAmbiguousPrimarySource() throws Exception {
		SourceConfiguration missing = read("{\"resource\":{\"connector\":\"source\",\"name\":\"records\"}}");
		SourceConfiguration ambiguous = read("{" +
				"\"resource\":{\"connector\":\"source\",\"name\":\"records\"}," +
				"\"json\":{},\"csv\":{}}");

		EtlConfException missingError = assertThrows(EtlConfException.class,
				() -> missing.initialize(new EtlItemConfiguration()));
		EtlConfException ambiguousError = assertThrows(EtlConfException.class,
				() -> ambiguous.initialize(new EtlItemConfiguration()));

		assertTrue(missingError.getMessage().contains("exactly one"));
		assertTrue(ambiguousError.getMessage().contains("[json, csv]"));
	}

	@Test
	void shouldRequireFieldsOwnedByJsonAndCsv() throws Exception {
		SourceConfiguration json = read("{" +
				"\"resource\":{\"connector\":\"api\",\"name\":\"/patients\"},\"json\":{}}");
		SourceConfiguration csv = read("{" +
				"\"resource\":{\"connector\":\"files\",\"name\":\"patients.csv\"},\"csv\":{}}");

		EtlConfException jsonError = assertThrows(EtlConfException.class,
				() -> json.initialize(new EtlItemConfiguration()));
		EtlConfException csvError = assertThrows(EtlConfException.class,
				() -> csv.initialize(new EtlItemConfiguration()));

		assertTrue(jsonError.getMessage().contains("source.json.fields"));
		assertTrue(csvError.getMessage().contains("source.csv.fields"));
	}

	@Test
	void shouldKeepThePrimaryResourceAtSourceLevel() throws Exception {
		SourceConfiguration source = read("{" +
				"\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"}," +
				"\"jdbcTable\":{\"tableName\":\"patient\"," +
				"\"resource\":{\"connector\":\"other-db\",\"name\":\"patient\"}}}");

		EtlConfException error = assertThrows(EtlConfException.class,
				() -> source.initialize(new EtlItemConfiguration()));

		assertTrue(error.getMessage().contains("source.jdbcTable.resource is not allowed"));
	}

	@Test
	void shouldKeepAdditionalSourceFamiliesDirectlyOnSource() throws Exception {
		SourceConfiguration source = read("{" +
				"\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"}," +
				"\"jdbcTable\":{\"tableName\":\"patient\"}," +
				"\"extraTableDataSource\":[{\"tableName\":\"person\"}]," +
				"\"extraQueryDataSource\":[{\"name\":\"visits\",\"query\":\"select 1\"}]," +
				"\"extraObjectDataSource\":[{\"name\":\"calculatedValues\"}]," +
				"\"extraJsonDataSource\":[{\"name\":\"demographics\",\"payload\":\"{}\"}]}" );

		source.initialize(new EtlItemConfiguration());

		assertEquals("person", source.getExtraTableDataSource().get(0).getTableName());
		assertEquals("visits", source.getExtraQueryDataSource().get(0).getName());
		assertEquals("calculatedValues", source.getExtraObjectDataSource().get(0).getName());
		assertEquals("demographics", source.getExtraJsonDataSource().get(0).getName());
	}

	@Test
	void shouldAllowConnectorNeutralAdditionalSourcesWithJsonPrimary() throws Exception {
		SourceConfiguration source = new SourceConfiguration();
		ConnectorResourceConfiguration resource = new ConnectorResourceConfiguration();
		resource.setConnector("patient-api");
		resource.setName("/patients");
		source.setResource(resource);
		JsonPrimaryDataSourceConfiguration primary = new JsonPrimaryDataSourceConfiguration();
		primary.setFields(Collections.singletonList(Field.fastCreateWithType("uuid", "java.lang.String")));
		source.setJson(primary);

		TrackingTransformableDataSource transformable = new TrackingTransformableDataSource();
		TrackingJsonDataSource json = new TrackingJsonDataSource();
		JsonMainOutputDataSource output = new TrackingJsonOutputDataSource();
		json.setOutputDataSource(output);
		source.setExtraObjectDataSource(Collections.singletonList(transformable));
		source.setExtraJsonDataSource(Collections.singletonList(json));

		source.initialize(new EtlItemConfiguration());
		source.fullLoad(null);

		assertSame(primary, transformable.getRelatedPrimaryDataSource());
		assertSame(primary, json.getRelatedPrimaryDataSource());
		assertSame(primary, output.getRelatedPrimaryDataSource());
		assertTrue(transformable.loaded);
		assertTrue(json.loaded);
	}

	private static class TrackingTransformableDataSource extends TransformableDataSource {
		private boolean loaded;

		@Override
		public void init(EtlDataConfiguration parent, EtlDatabaseObject schema, Connection src, Connection dst) {
			setRelatedPrimaryDataSource((PrimaryDataSourceConfiguration) parent);
		}

		@Override
		public synchronized void fullLoad(Connection connection) {
			loaded = true;
		}
	}

	private static class TrackingJsonDataSource extends JsonDataSource {
		private boolean loaded;

		@Override
		public void init(EtlDataConfiguration parent, EtlDatabaseObject schema, Connection src, Connection dst)
				throws DBException {
			setRelatedPrimaryDataSource((PrimaryDataSourceConfiguration) parent);
			getOutputDataSource().init(parent, schema, src, dst);
		}

		@Override
		public void fullLoad(Connection connection) {
			loaded = true;
		}
	}

	private static class TrackingJsonOutputDataSource extends JsonMainOutputDataSource {
		@Override
		public void init(EtlDataConfiguration parent, EtlDatabaseObject schema, Connection src, Connection dst) {
			setParentConf(parent);
		}
	}

	private SourceConfiguration read(String json) throws Exception {
		return new ObjectMapperProvider().getContext(SourceConfiguration.class)
				.readValue(json, SourceConfiguration.class);
	}
}
