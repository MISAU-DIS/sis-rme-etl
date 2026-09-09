package org.openmrs.module.epts.etl.databasemodelgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
		assertTrue(manifest.toString().contains("DatabaseModelManifest{formatVersion=2"));
		assertTrue(manifest.toString().contains("Entry{metadataKey='source|mysql|a', generatedClassName='model.A'"));
	}

	@Test
	public void shouldCoordinateConcurrentWritersAcrossRepositoryInstances() throws Exception {
		File directory = temporaryFolder.newFolder("concurrent-schema-metadata");
		FileDatabaseModelManifestRepository first = new FileDatabaseModelManifestRepository(directory);
		FileDatabaseModelManifestRepository second = new FileDatabaseModelManifestRepository(directory);
		CountDownLatch start = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(2);

		try {
			Future<?> firstWriter = executor.submit(() -> recordEntries(first, "source", start));
			Future<?> secondWriter = executor.submit(() -> recordEntries(second, "destination", start));
			start.countDown();
			firstWriter.get();
			secondWriter.get();

			assertEquals(40, first.read().getEntries().size());
		} finally {
			executor.shutdownNow();
		}
	}

	private void recordEntries(FileDatabaseModelManifestRepository repository, String model, CountDownLatch start) {
		try {
			start.await();
			for (int index = 0; index < 20; index++) {
				repository.record(new DatabaseModelManifest.Entry(model + "|table-" + index,
						model + ".Table" + index));
			}
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}
