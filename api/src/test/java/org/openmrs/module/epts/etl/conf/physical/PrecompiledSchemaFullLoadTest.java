package org.openmrs.module.epts.etl.conf.physical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.SchemaMetadataMode;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelManifest;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.FileDatabaseModelManifestRepository;

public class PrecompiledSchemaFullLoadTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void shouldFullLoadFromFilesWithoutJdbcMetadataIntrospection() throws Exception {
		EtlConfiguration etl = new EtlConfiguration();
		etl.setEtlRootDirectory(temporaryFolder.getRoot().getAbsolutePath());
		etl.setSchemaMetadataMode(SchemaMetadataMode.PRECOMPILED);
		DBConnectionInfo connectionInfo = new DBConnectionInfo();
		connectionInfo.setConnectionURI("jdbc:mysql://unused/openmrs");
		connectionInfo.setDataBaseUserName("etl");
		connectionInfo.setSchema("openmrs");
		connectionInfo.setPojoPackageName("source-openmrs");

		PhysicalTableKey key = new PhysicalTableKey("source-openmrs", "mysql", "openmrs", "openmrs", "person");
		PhysicalColumnMetadata id = new PhysicalColumnMetadata("person_id", "int", 11, 0, false, true, false);
		PhysicalColumnMetadata locationId = new PhysicalColumnMetadata("location_id", "int", 11, 0, false, false, false);
		PhysicalKeyMetadata primaryKey = new PhysicalKeyMetadata("PRIMARY", Arrays.asList(
				new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("person_id", "int")), false);
		PhysicalKeyMetadata uniqueKey = new PhysicalKeyMetadata("uk_person_location", Arrays.asList(
				new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("location_id", "int")), false);
		PhysicalForeignKeyMetadata parent = new PhysicalForeignKeyMetadata("fk_person_location", "openmrs",
				"openmrs", "location", Arrays.asList(
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("location_id", "location_id")));
		PhysicalTableMetadata metadata = new PhysicalTableMetadata(key, Arrays.asList(id, locationId), primaryKey,
				Arrays.asList(uniqueKey), Arrays.asList(parent));
		new FilePhysicalTableMetadataRepository(etl.getSchemaMetadataDirectory()).save(metadata);

		TestTable table = new TestTable(etl, connectionInfo);
		table.setTableName("person");
		table.setSchema("openmrs");
		new FileDatabaseModelManifestRepository(etl.getSchemaMetadataDirectory()).record(
				new DatabaseModelManifest.Entry(key.toString(), table.generateFullClassName(connectionInfo),
						PhysicalTableMetadataFingerprint.sha256(metadata)));
		table.fullLoad(connectionRejectingMetadataCalls());

		assertTrue(table.isFullLoaded());
		assertEquals(2, table.getFields().size());
		assertEquals("person_id", table.getPrimaryKey().retrieveSimpleKeyColumnName());
		assertEquals(1, table.getUniqueKeys().size());
		assertEquals("location", table.getParentRefInfo().get(0).getTableName());
		assertTrue(table.isAutoIncrementId());
	}

	private Connection connectionRejectingMetadataCalls() {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[] { Connection.class },
				(proxy, method, arguments) -> {
					if ("getMetaData".equals(method.getName())) {
						throw new AssertionError("JDBC DatabaseMetaData must not be used in PRECOMPILED mode");
					}
					Class<?> type = method.getReturnType();
					if (type == boolean.class) return false;
					if (type == int.class) return 0;
					if (type == long.class) return 0L;
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
		public EtlConfiguration getRelatedEtlConf() { return etl; }

		@Override
		public DBConnectionInfo getRelatedConnInfo() { return connectionInfo; }
	}
}
