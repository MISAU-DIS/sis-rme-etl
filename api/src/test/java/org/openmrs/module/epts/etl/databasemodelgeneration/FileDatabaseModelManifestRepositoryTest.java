package org.openmrs.module.epts.etl.databasemodelgeneration;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.DatabaseModelManifest;
import org.openmrs.module.epts.etl.databasemodelgeneration.model.FileDatabaseModelManifestRepository;

public class FileDatabaseModelManifestRepositoryTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void shouldRecordSortAndReplaceManifestEntries() throws Exception {
		FileDatabaseModelManifestRepository repository = new FileDatabaseModelManifestRepository(
				temporaryFolder.newFolder("schema-metadata"));

		repository.record(new DatabaseModelManifest.Entry("source|mysql|z", "model.Z"));
		repository.record(new DatabaseModelManifest.Entry("source|mysql|a", "model.A"));
		repository.record(new DatabaseModelManifest.Entry("source|mysql|z", "model.NewZ"));

		DatabaseModelManifest manifest = repository.read();
		assertEquals(DatabaseModelManifest.CURRENT_FORMAT_VERSION, manifest.getFormatVersion());
		assertEquals(2, manifest.getEntries().size());
		assertEquals("source|mysql|a", manifest.getEntries().get(0).getMetadataKey());
		assertEquals("model.NewZ", manifest.getEntries().get(1).getGeneratedClassName());
	}
}
