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
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

class SourceConfigurationTest {

	@Test
	void shouldRejectFormerPrimarySourcePropertyNames() {
		assertThrows(UnrecognizedPropertyException.class,
				() -> read("{\"jdbcTable\":{\"tableName\":\"patient\"}}"));
		assertThrows(UnrecognizedPropertyException.class,
				() -> read("{\"json\":{\"fields\":[{\"name\":\"uuid\"}]}}"));
		assertThrows(UnrecognizedPropertyException.class,
				() -> read("{\"csv\":{\"fields\":[{\"name\":\"uuid\"}]}}"));
	}

	@Test
	void shouldInferJdbcTableAsTheUniquePrimarySource() throws Exception {
		SourceConfiguration source = read("{" +
				"\"connector\":\"source-db\"," +
				"\"tableDataSource\":{\"tableName\":\"patient\"}" +
				"}");

		source.initialize(new EtlItemConfiguration());

		assertTrue(source.getPrimaryDataSource() instanceof org.openmrs.module.epts.etl.conf.datasource.SrcConf);
		assertSame(source, source.getPrimaryDataSource().getParentSource());
		assertEquals("source-db", source.getConnector());
		assertEquals("patient", source.getPrimaryDataSource().getResourceName());
		assertEquals("patient", source.getTableDataSource().getTableName());
	}

	@Test
	void shouldInferJsonAndCsvWithoutDependingOnConnectorType() throws Exception {
		SourceConfiguration json = read("{" +
				"\"connector\":\"patient-api\"," +
				"\"resource\":{\"name\":\"patients.json\",\"recordPath\":\"/results\",\"layout\":\"ARRAY\"}," +
				"\"jsonDataSource\":{\"name\":\"patients\"," +
				"\"fields\":[{\"name\":\"uuid\"}]}}");
		SourceConfiguration csv = read("{" +
				"\"connector\":\"input-files\"," +
				"\"resource\":{\"name\":\"patients.csv\"}," +
				"\"csvDataSource\":{\"name\":\"patients\",\"delimiter\":\";\",\"hasHeader\":true," +
				"\"fields\":[{\"name\":\"uuid\"}]}}");

		json.initialize(new EtlItemConfiguration());
		csv.initialize(new EtlItemConfiguration());

		assertTrue(json.getPrimaryDataSource() instanceof JsonPrimaryDataSourceConfiguration);
		assertTrue(json.getPrimaryDataSource() instanceof EtlDatabaseObjectConfiguration);
		assertEquals("/results", json.getResource().getRecordPath());
		json.fullLoad(null);
		assertTrue(json.isFullLoaded());
		assertTrue(json.getPrimaryDataSource().isFullLoaded());
		assertEquals(GenericDatabaseObject.class, json.getPrimaryDataSource().getEtlRecordClass());
		assertSame(json.getPrimaryDataSource(), json.getPrimaryDataSource().getLoadHealper().getTableConf());
		assertTrue(csv.getPrimaryDataSource() instanceof CsvPrimaryDataSourceConfiguration);
		assertTrue(csv.getPrimaryDataSource() instanceof EtlDatabaseObjectConfiguration);
		assertEquals(";", csv.getCsvDataSource().getDelimiter());
	}

	@Test
	void shouldRejectMissingOrAmbiguousPrimarySource() throws Exception {
		SourceConfiguration missing = read("{\"connector\":\"source\"}");
		SourceConfiguration ambiguous = read("{" +
				"\"connector\":\"source\"," +
				"\"jsonDataSource\":{\"name\":\"records.json\"},"
				+ "\"csvDataSource\":{\"name\":\"records.csv\"}}");

		EtlConfException missingError = assertThrows(EtlConfException.class,
				() -> missing.initialize(new EtlItemConfiguration()));
		EtlConfException ambiguousError = assertThrows(EtlConfException.class,
				() -> ambiguous.initialize(new EtlItemConfiguration()));

		assertTrue(missingError.getMessage().contains("exactly one"));
		assertTrue(ambiguousError.getMessage().contains("[jsonDataSource, csvDataSource]"));
	}

	@Test
	void shouldRequireFieldsOwnedByJsonAndCsv() throws Exception {
		SourceConfiguration json = read("{" +
				"\"connector\":\"api\",\"resource\":{\"name\":\"patients.json\"},"
				+ "\"jsonDataSource\":{\"name\":\"patients\"}}");
		SourceConfiguration csv = read("{" +
				"\"connector\":\"files\",\"resource\":{\"name\":\"patients.csv\"},"
				+ "\"csvDataSource\":{\"name\":\"patients\"}}");

		EtlConfException jsonError = assertThrows(EtlConfException.class,
				() -> json.initialize(new EtlItemConfiguration()));
		EtlConfException csvError = assertThrows(EtlConfException.class,
				() -> csv.initialize(new EtlItemConfiguration()));

		assertTrue(jsonError.getMessage().contains("source.jsonDataSource.fields"));
		assertTrue(csvError.getMessage().contains("source.csvDataSource.fields"));
	}

	@Test
	void shouldAcceptSourceResourceAndRejectNestedPrimaryResource() throws Exception {
		SourceConfiguration configured = read("{"
				+ "\"resource\":{\"connector\":\"source-db\",\"name\":\"patient\"},"
				+ "\"tableDataSource\":{\"tableName\":\"patient\"}}");
		configured.initialize(new EtlItemConfiguration());
		assertEquals("patient", configured.getResource().getName());

		SourceConfiguration source = read("{" +
				"\"connector\":\"source-db\"," +
				"\"tableDataSource\":{\"tableName\":\"patient\"," +
				"\"resource\":{\"connector\":\"other-db\",\"name\":\"patient\"}}}");

		EtlConfException error = assertThrows(EtlConfException.class,
				() -> source.initialize(new EtlItemConfiguration()));

		assertTrue(error.getMessage().contains("source.tableDataSource.resource is not allowed"));
	}

	@Test
	void shouldKeepAdditionalSourceFamiliesDirectlyOnSource() throws Exception {
		SourceConfiguration source = read("{" +
				"\"connector\":\"source-db\"," +
				"\"tableDataSource\":{\"tableName\":\"patient\"}," +
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
		source.setConnector("patient-api");
		SourceResourceConfiguration resource = new SourceResourceConfiguration();
		resource.setName("patients.json");
		source.setResource(resource);
		JsonPrimaryDataSourceConfiguration primary = new JsonPrimaryDataSourceConfiguration();
		primary.setName("/patients");
		primary.setFields(Collections.singletonList(Field.fastCreateWithType("uuid", "java.lang.String")));
		source.setJsonDataSource(primary);

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

	@Test
	void shouldInitializeExtraJsonRecordDataSources() throws Exception {
		SourceConfiguration source = read("{"
				+ "\"connector\":\"input-files\","
				+ "\"resource\":{\"name\":\"persons.json\",\"layout\":\"ARRAY\","
				+ "\"recordPath\":\"/person_data\"},"
				+ "\"jsonDataSource\":{\"name\":\"person\","
				+ "\"fields\":[{\"name\":\"id\",\"dataType\":\"java.lang.Integer\"}]},"
				+ "\"extraJsonRecordDataSource\":[{\"name\":\"address\",\"path\":\"/address\","
				+ "\"cardinality\":\"ONE\",\"fields\":[{\"name\":\"uuid\"}]}]}" );

		source.initialize(new EtlItemConfiguration());
		source.fullLoad(null);

		assertEquals(1, source.getExtraJsonRecordDataSource().size());
		assertSame(source.getPrimaryDataSource(),
				source.getExtraJsonRecordDataSource().get(0).getRelatedPrimaryDataSource());
		assertTrue(source.getExtraJsonRecordDataSource().get(0).isFullLoaded());
		assertTrue(source.getAvailableAdditionalDataSources().contains(source.getExtraJsonRecordDataSource().get(0)));
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
