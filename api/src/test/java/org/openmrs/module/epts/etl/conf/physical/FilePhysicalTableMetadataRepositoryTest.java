package org.openmrs.module.epts.etl.conf.physical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FilePhysicalTableMetadataRepositoryTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void shouldAtomicallyPersistAndReadCompleteMetadata() throws Exception {
		Path root = temporaryFolder.newFolder("schema-metadata").toPath();
		FilePhysicalTableMetadataRepository repository = new FilePhysicalTableMetadataRepository(root.toFile());
		PhysicalTableMetadata expected = metadata(key("person"));

		repository.save(expected);

		assertEquals(expected, repository.find(expected.getKey()).get());
		Path expectedFile = root.resolve("source-openmrs/mysql/openmrs/openmrs/person.json");
		assertTrue(Files.isRegularFile(expectedFile));
		assertFalse(Files.list(expectedFile.getParent()).anyMatch(path -> path.toString().endsWith(".tmp")));
	}

	@Test
	public void shouldReturnEmptyWhenMetadataDoesNotExist() throws Exception {
		FilePhysicalTableMetadataRepository repository = new FilePhysicalTableMetadataRepository(
				temporaryFolder.newFolder("empty").toPath().toFile());

		assertFalse(repository.find(key("missing_table")).isPresent());
	}

	@Test
	public void shouldFindMetadataWithoutKnowingDialectOrCatalog() throws Exception {
		Path root = temporaryFolder.newFolder("schema-lookup").toPath();
		FilePhysicalTableMetadataRepository repository = new FilePhysicalTableMetadataRepository(root.toFile());
		PhysicalTableMetadata expected = metadata(key("person"));
		repository.save(expected);

		assertEquals(expected, repository.find("source-openmrs", "openmrs", "person").get());
	}

	@Test(expected = java.io.IOException.class)
	public void shouldRejectUnsafePathElements() throws Exception {
		FilePhysicalTableMetadataRepository repository = new FilePhysicalTableMetadataRepository(
				temporaryFolder.newFolder("safe-root").toPath().toFile());
		PhysicalTableKey unsafe = new PhysicalTableKey("../outside", "mysql", "openmrs", "openmrs", "person");

		repository.save(metadata(unsafe));
	}

	private PhysicalTableKey key(String table) {
		return new PhysicalTableKey("source-openmrs", "mysql", "openmrs", "openmrs", table);
	}

	private PhysicalTableMetadata metadata(PhysicalTableKey key) {
		PhysicalColumnMetadata id = new PhysicalColumnMetadata("person_id", "int", 11, 0, false, true, false);
		PhysicalColumnMetadata location = new PhysicalColumnMetadata("location_id", "int", 11, 0, false, false, false);
		PhysicalKeyMetadata primaryKey = new PhysicalKeyMetadata("PRIMARY",
				Arrays.asList(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("person_id", "int")), false);
		PhysicalKeyMetadata uniqueKey = new PhysicalKeyMetadata("uk_person_location", Arrays.asList(
				new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("person_id", "int"),
				new PhysicalKeyMetadata.PhysicalKeyColumnMetadata("location_id", "int")), false);
		PhysicalForeignKeyMetadata foreignKey = new PhysicalForeignKeyMetadata("fk_person_location", "openmrs",
				"openmrs", "location", Arrays.asList(
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("location_id", "location_id")));
		PhysicalExportedForeignKeyMetadata exportedForeignKey = new PhysicalExportedForeignKeyMetadata("fk_obs_person",
				"openmrs", "openmrs", "obs", Arrays.asList(
						new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping("person_id", "person_id")));
		return new PhysicalTableMetadata(key, Arrays.asList(id, location), primaryKey, Arrays.asList(uniqueKey),
				Arrays.asList(foreignKey), Arrays.asList(exportedForeignKey));
	}
}
