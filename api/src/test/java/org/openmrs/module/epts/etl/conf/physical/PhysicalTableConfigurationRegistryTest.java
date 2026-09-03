package org.openmrs.module.epts.etl.conf.physical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.TypePrecision;

public class PhysicalTableConfigurationRegistryTest {

	@Test
	public void shouldReuseConfigurationForTheSamePhysicalTable() {
		PhysicalTableConfigurationRegistry registry = new PhysicalTableConfigurationRegistry();
		PhysicalTableIdentity first = identity("OpenMRS", "person");
		PhysicalTableIdentity sameIdentity = identity("OpenMRS", "person");

		assertSame(registry.getOrCreate(first), registry.getOrCreate(sameIdentity));
		assertEquals(1, registry.size());
	}

	@Test
	public void shouldNotReuseConfigurationAcrossSchemas() {
		PhysicalTableConfigurationRegistry registry = new PhysicalTableConfigurationRegistry();

		assertNotSame(registry.getOrCreate(identity("openmrs", "person")),
				registry.getOrCreate(identity("reporting", "person")));
		assertEquals(2, registry.size());
	}

	@Test
	public void shouldReturnIndependentFieldCopiesWithCompletePhysicalMetadata() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		Field id = new Field("person_id");
		id.setDataType("int");
		id.setPrecision(TypePrecision.init(11, null));
		id.setAutoIncrement(true);
		id.setTimeStamp(true);
		configuration.initializeFields(Arrays.asList(id));

		List<Field> first = configuration.copyFields();
		List<Field> second = configuration.copyFields();

		assertNotSame(first, second);
		assertNotSame(first.get(0), second.get(0));
		assertNotSame(first.get(0).getPrecision(), second.get(0).getPrecision());
		assertEquals(first.get(0).getPrecision().getLength(), second.get(0).getPrecision().getLength());
		assertEquals(Boolean.TRUE, second.get(0).isAutoIncrement());
		assertEquals(Boolean.TRUE, second.get(0).isTimeStamp());

		first.get(0).setName("contextual_alias");
		assertEquals("person_id", second.get(0).getName());
	}

	@Test
	public void shouldReturnIndependentPrimaryAndUniqueKeyCopies() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		PrimaryKey primaryKey = new PrimaryKey();
		primaryKey.setKeyName("pk_person");
		primaryKey.addKey(Key.fastCreateTyped("person_id", "int"));
		UniqueKeyInfo uniqueKey = new UniqueKeyInfo();
		uniqueKey.setKeyName("uk_person_uuid");
		uniqueKey.addKey(Key.fastCreateTyped("uuid", "varchar"));

		configuration.initializePrimaryKey(primaryKey);
		configuration.initializeUniqueKeys(Arrays.asList(uniqueKey));

		PrimaryKey firstPk = configuration.copyPrimaryKey(null);
		PrimaryKey secondPk = configuration.copyPrimaryKey(null);
		List<UniqueKeyInfo> firstUks = configuration.copyUniqueKeys(null);
		List<UniqueKeyInfo> secondUks = configuration.copyUniqueKeys(null);

		assertNotSame(firstPk, secondPk);
		assertNotSame(firstPk.getFields().get(0), secondPk.getFields().get(0));
		assertEquals("pk_person", secondPk.getKeyName());
		assertNotSame(firstUks.get(0), secondUks.get(0));
		assertEquals("uk_person_uuid", secondUks.get(0).getKeyName());

		firstPk.getFields().get(0).setName("changed_pk");
		firstUks.get(0).getFields().get(0).setName("changed_uk");
		assertEquals("person_id", secondPk.getFields().get(0).getName());
		assertEquals("uuid", secondUks.get(0).getFields().get(0).getName());
	}

	private PhysicalTableIdentity identity(String schema, String table) {
		return new PhysicalTableIdentity("jdbc:mysql://localhost/openmrs", "etl", "openmrs", schema, table);
	}

	@Test
	public void shouldCreatePersistentKeyWithoutRuntimeConnectionDetails() {
		PhysicalTableIdentity runtimeIdentity = identity("openmrs", "person");

		PhysicalTableKey persistentKey = runtimeIdentity.toPersistentKey("source-openmrs", "mysql");

		assertEquals("source-openmrs", persistentKey.getLogicalDatabaseId());
		assertEquals("mysql", persistentKey.getDatabaseDialect());
		assertEquals("openmrs", persistentKey.getSchema());
		assertEquals("person", persistentKey.getTableName());
		org.junit.Assert.assertFalse(persistentKey.toString().contains("localhost"));
		org.junit.Assert.assertFalse(persistentKey.toString().contains("etl"));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldKeepImportedForeignKeyMetadataImmutable() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		PhysicalForeignKeyMetadata foreignKey = new PhysicalForeignKeyMetadata("fk_person_location", "openmrs",
				"openmrs", "location", Arrays.asList(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping(
						"location_id", "location_id")));

		configuration.initializeImportedForeignKeys(Arrays.asList(foreignKey));

		configuration.getImportedForeignKeys().add(foreignKey);
	}

	@Test
	public void shouldRepresentCompositeForeignKeysWithoutContextReferences() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		PhysicalForeignKeyMetadata foreignKey = new PhysicalForeignKeyMetadata("fk_person_site_location", "openmrs",
				"openmrs", "location", Arrays.asList(
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("site_id", "site_id"),
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("location_id", "location_id")));

		configuration.initializeImportedForeignKeys(Arrays.asList(foreignKey));

		assertEquals(1, configuration.getImportedForeignKeys().size());
		assertEquals(2, configuration.getImportedForeignKeys().get(0).getMappings().size());
		assertEquals("location", configuration.getImportedForeignKeys().get(0).getReferencedTable());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldKeepExportedForeignKeyMetadataImmutable() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		PhysicalExportedForeignKeyMetadata foreignKey = new PhysicalExportedForeignKeyMetadata("fk_obs_person",
				"openmrs", "openmrs", "obs", Arrays.asList(
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("person_id", "person_id")));

		configuration.initializeExportedForeignKeys(Arrays.asList(foreignKey));

		configuration.getExportedForeignKeys().add(foreignKey);
	}

	@Test
	public void shouldDescribeLoadedAndPendingConfigurationSections() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		Field id = new Field("person_id");
		id.setDataType("int");
		configuration.initializeFields(Arrays.asList(id));

		String description = configuration.toString();

		org.junit.Assert.assertTrue(description.contains("identity="));
		org.junit.Assert.assertTrue(description.contains("PhysicalColumnMetadata{name='person_id'"));
		org.junit.Assert.assertTrue(description.contains("primaryKeyLoaded=false"));
		org.junit.Assert.assertTrue(description.contains("exportedForeignKeysLoaded=false"));
	}

	@Test
	public void shouldCompleteEveryPendingSectionWhenFieldsWereLoadedEarlier() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		Field initialField = new Field("person_id");
		initialField.setDataType("int");
		configuration.initializeFields(Arrays.asList(initialField));

		PhysicalTableMetadata complete = metadata("person", "bigint", "pk_person");
		configuration.initialize(complete);

		assertEquals("int", configuration.copyFields().get(0).getDataType());
		assertEquals("pk_person", configuration.copyPrimaryKey(null).getKeyName());
		assertEquals(1, configuration.copyUniqueKeys(null).size());
		assertEquals(1, configuration.getImportedForeignKeys().size());
		assertEquals(1, configuration.getExportedForeignKeys().size());
	}

	@Test
	public void shouldReplaceInitialStateWithDefinitiveSnapshotBeforePersistence() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		configuration.initialize(metadata("person", "int", null));
		assertNull(configuration.copyPrimaryKey(null));

		PhysicalTableMetadata definitive = metadata("person", "bigint", "pk_person");
		configuration.replaceWith(definitive);
		PhysicalTableMetadata persisted = configuration.toMetadata(definitive.getKey());

		assertEquals("bigint", persisted.getColumns().get(0).getDataType());
		assertEquals("pk_person", persisted.getPrimaryKey().getName());
		assertEquals(definitive, persisted);
	}

	@Test
	public void shouldExplicitlySynchronizeEverySectionFromLoadedTableState() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "patient"));
		Field id = new Field("patient_id");
		id.setDataType("int");
		PrimaryKey primaryKey = new PrimaryKey();
		primaryKey.setKeyName("PRIMARY");
		primaryKey.addKey(Key.fastCreateTyped("patient_id", "int"));

		configuration.synchronizeFromLoadedTable(Arrays.asList(id), primaryKey, Arrays.asList(), Arrays.asList(),
				Arrays.asList());
		PhysicalTableKey key = identity("openmrs", "patient").toPersistentKey("source-openmrs", "mysql");
		PhysicalTableMetadata metadata = configuration.toMetadata(key);

		assertEquals(1, metadata.getColumns().size());
		assertEquals("PRIMARY", metadata.getPrimaryKey().getName());
		assertTrue(metadata.getUniqueKeys().isEmpty());
		assertTrue(metadata.getImportedForeignKeys().isEmpty());
		assertTrue(metadata.getExportedForeignKeys().isEmpty());
	}

	private PhysicalTableMetadata metadata(String table, String fieldType, String primaryKeyName) {
		PhysicalTableKey key = identity("openmrs", table).toPersistentKey("source-openmrs", "mysql");
		PhysicalColumnMetadata column = new PhysicalColumnMetadata("person_id", fieldType, 11, 0, false, true,
				false);
		PhysicalKeyMetadata primaryKey = primaryKeyName == null ? null
				: new PhysicalKeyMetadata(primaryKeyName,
						Arrays.asList(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("person_id", fieldType)), false);
		PhysicalKeyMetadata uniqueKey = new PhysicalKeyMetadata("uk_person_id",
				Arrays.asList(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("person_id", fieldType)), false);
		PhysicalForeignKeyMetadata imported = new PhysicalForeignKeyMetadata("fk_person_creator", "openmrs",
				"openmrs", "users", Arrays.asList(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping(
						"person_id", "user_id")));
		PhysicalExportedForeignKeyMetadata exported = new PhysicalExportedForeignKeyMetadata("fk_obs_person",
				"openmrs", "openmrs", "obs", Arrays.asList(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping(
						"person_id", "person_id")));
		return new PhysicalTableMetadata(key, Arrays.asList(column), primaryKey, Arrays.asList(uniqueKey),
				Arrays.asList(imported), Arrays.asList(exported));
	}
}
