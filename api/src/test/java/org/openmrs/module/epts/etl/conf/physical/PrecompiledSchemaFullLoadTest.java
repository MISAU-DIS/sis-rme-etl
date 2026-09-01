package org.openmrs.module.epts.etl.conf.physical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.ParentTableImpl;
import org.openmrs.module.epts.etl.conf.RefMapping;
import org.openmrs.module.epts.etl.conf.SchemaMetadataMode;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelManifest;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.FileDatabaseModelManifestRepository;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.DatabaseEntityPOJOGenerator;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

public class PrecompiledSchemaFullLoadTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void shouldFullLoadFromFilesWithoutJdbcMetadataIntrospection() throws Exception {
		assertFullLoadFromFilesWithoutJdbcMetadataIntrospection(SchemaMetadataMode.PRECOMPILED);
	}

	@Test
	public void shouldUseExistingFilesWithoutJdbcMetadataIntrospectionInFallbackMode() throws Exception {
		assertFullLoadFromFilesWithoutJdbcMetadataIntrospection(SchemaMetadataMode.PRECOMPILED_WITH_FALLBACK);
	}

	private void assertFullLoadFromFilesWithoutJdbcMetadataIntrospection(SchemaMetadataMode mode) throws Exception {
		RecordingEtlConfiguration etl = new RecordingEtlConfiguration();
		etl.setEtlRootDirectory(temporaryFolder.getRoot().getAbsolutePath());
		etl.setSchemaMetadataMode(mode);
		etl.getDataModel().setSrcPojoPackageName("source_openmrs");
		DBConnectionInfo connectionInfo = new DBConnectionInfo();
		connectionInfo.setConnectionURI("jdbc:mysql://unused/openmrs");
		connectionInfo.setDataBaseUserName("etl");
		connectionInfo.setSchema("openmrs");
		etl.setSrcConnInfo(connectionInfo);

		PhysicalTableKey key = new PhysicalTableKey("source_openmrs", "mysql", "openmrs", "openmrs", "person");
		PhysicalColumnMetadata id = new PhysicalColumnMetadata("person_id", "int", 11, 0, false, true, false);
		PhysicalColumnMetadata locationId = new PhysicalColumnMetadata("location_id", "int", 11, 0, false, false,
				false);
		PhysicalColumnMetadata uuid = new PhysicalColumnMetadata("uuid", "varchar", 38, 0, false, false, false);
		PhysicalColumnMetadata dateCreated = new PhysicalColumnMetadata("date_created", "datetime", null, null, true,
				false, false);
		PhysicalColumnMetadata dateVoided = new PhysicalColumnMetadata("date_voided", "datetime", null, null, true,
				false, false);
		PhysicalKeyMetadata primaryKey = new PhysicalKeyMetadata("PRIMARY",
				Arrays.asList(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("person_id", "int")), false);
		PhysicalKeyMetadata uniqueKey = new PhysicalKeyMetadata("uk_person_location",
				Arrays.asList(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("location_id", "int")), false);
		PhysicalForeignKeyMetadata parent = new PhysicalForeignKeyMetadata("fk_person_location", "openmrs", "openmrs",
				"location",
				Arrays.asList(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("location_id", "location_id")));
		PhysicalForeignKeyMetadata sharedPrimaryKeyParent = new PhysicalForeignKeyMetadata("fk_person_base", "openmrs",
				"openmrs", "base_person",
				Arrays.asList(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("person_id", "person_id")));
		PhysicalExportedForeignKeyMetadata child = new PhysicalExportedForeignKeyMetadata("fk_obs_person", "openmrs",
				"openmrs", "obs",
				Arrays.asList(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("person_id", "person_id")));
		PhysicalTableMetadata metadata = new PhysicalTableMetadata(key,
				Arrays.asList(id, locationId, uuid, dateCreated, dateVoided), primaryKey, Arrays.asList(uniqueKey),
				Arrays.asList(parent, sharedPrimaryKeyParent), Arrays.asList(child));
		new FilePhysicalTableMetadataRepository(etl.getSchemaMetadataDirectory()).save(metadata);

		TestTable table = new TestTable(etl, connectionInfo);
		table.setTableName("person");
		table.setSchema("openmrs");
		table.setMustLoadChildrenInfo(true);
		ParentTableImpl configuredLocation = new ParentTableImpl("location", "fk_person_location");
		RefMapping configuredMapping = RefMapping.fastCreate("location_id", "location_id");
		configuredMapping.setIgnorable(true);
		configuredMapping.setDefaultValueDueInconsistency(99);
		configuredLocation.setRefMapping(Arrays.asList(configuredMapping));
		table.setParents(Arrays.asList(configuredLocation));
		new FileDatabaseModelManifestRepository(etl.getSchemaMetadataDirectory())
				.record(new DatabaseModelManifest.Entry(key.toString(), table.generateFullClassName(connectionInfo),
						PhysicalTableMetadataFingerprint.sha256(metadata)));
		table.fullLoad(connectionRejectingMetadataCalls());

		assertTrue(table.isFullLoaded());
		assertEquals(5, table.getFields().size());
		assertEquals("person_id", table.getPrimaryKey().retrieveSimpleKeyColumnName());
		assertEquals(1, table.getUniqueKeys().size());
		assertEquals(2, table.getParentRefInfo().size());
		assertEquals("location", table.getParentRefInfo().get(0).getTableName());
		assertTrue(table.getParentRefInfo().get(0).getRefMapping().get(0).isIgnorable());
		assertEquals(99, table.getParentRefInfo().get(0).getRefMapping().get(0).getDefaultValueDueInconsistency());
		assertEquals("base_person", table.getSharePkWith());
		assertEquals(1, table.getChildRefInfo().size());
		assertEquals("obs", table.getChildRefInfo().get(0).getTableName());
		assertEquals("person_id", table.getChildRefInfo().get(0).getRefMapping().get(0).getChildFieldName());
		assertTrue(table.isAutoIncrementId());
		assertEquals(Arrays.asList("INFO:Full load done using existing static data"), etl.messages);

		if (mode == SchemaMetadataMode.PRECOMPILED) {
			etl.setClassPath(Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator)));
			table.generateRecordClass(connectionInfo, true);
			Class<? extends EtlDatabaseObject> generatedClass = table.getEtlRecordClass();
			assertSame(generatedClass, DatabaseEntityPOJOGenerator.tryToGetExistingCLass(
					table.generateFullClassName(connectionInfo), etl));
			assertSame(generatedClass, DatabaseEntityPOJOGenerator.tryToGetExistingCLass(
					table.generateFullClassName(connectionInfo), etl));
			EtlDatabaseObject generated = table.getEtlRecordClass().getConstructor().newInstance();
			generated.setRelatedConfiguration(table);
			Field generatedLocation = (Field) generated.getClass().getMethod("getLocationId").invoke(generated);
			assertEquals("location_id", generatedLocation.getName());
			assertEquals("int", generatedLocation.getDataType());
			Field generatedId = (Field) generated.getClass().getMethod("getPersonId").invoke(generated);
			assertTrue(generatedId.isAutoIncrement());
			generated.setFieldValue("location_id", 27);
			generated.setFieldValue("person_id", 7);
			assertEquals(27, generated.getFieldValue("locationId"));
			assertTrue(Arrays.asList(generated.getInsertParamsWithObjectId()).contains(27));
			assertEquals(5, generated.getFields().size());
			Field inheritedDateVoided = generated.getField("date_voided");
			assertSame(inheritedDateVoided, generated.getField("dateVoided"));
			Date dateVoidedValue = new Date();
			generated.setFieldValue("date_voided", dateVoidedValue);
			assertSame(inheritedDateVoided, generated.getField("date_voided"));
			assertSame(dateVoidedValue, inheritedDateVoided.getValue());
			EtlDatabaseObject copy = generated.createACopy();
			assertEquals(27, copy.getFieldValue("location_id"));
			assertNotSame(generated.getField("location_id"), copy.getField("location_id"));
		}
	}

	private Connection connectionRejectingMetadataCalls() {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, method, arguments) -> {
					if ("getMetaData".equals(method.getName())) {
						throw new AssertionError("JDBC DatabaseMetaData must not be used in PRECOMPILED mode");
					}
					Class<?> type = method.getReturnType();
					if (type == boolean.class)
						return false;
					if (type == int.class)
						return 0;
					if (type == long.class)
						return 0L;
					return null;
				});
	}

	private static final class TestTable extends GenericTableConfiguration {
		private final EtlConfiguration etl;
		private final DBConnectionInfo connectionInfo;

		private TestTable(EtlConfiguration etl, DBConnectionInfo connectionInfo) {
			this.etl = etl;
			this.connectionInfo = connectionInfo;
		}

		@Override
		public EtlConfiguration getRelatedEtlConf() {
			return etl;
		}

		@Override
		public DBConnectionInfo getRelatedConnInfo() {
			return connectionInfo;
		}

		@Override
		public List<File> getClassPath() {
			return etl.getClassPathAsFiles();
		}
	}

	private static final class RecordingEtlConfiguration extends EtlConfiguration {
		private final List<String> messages = new ArrayList<>();

		@Override
		public void info(String message) {
			messages.add("INFO:" + message);
		}

		@Override
		public void debug(String message) {
			messages.add("DEBUG:" + message);
		}

		@Override
		public void trace(String message) {
			messages.add("TRACE:" + message);
		}

		@Override
		public void trace(String message, Object... arguments) {
			messages.add("TRACE:" + message);
		}

		@Override
		public void warn(String message) {
			messages.add("WARN:" + message);
		}

		@Override
		public void warn(String message, Object... arguments) {
			messages.add("WARN:" + message);
		}
	}
}
