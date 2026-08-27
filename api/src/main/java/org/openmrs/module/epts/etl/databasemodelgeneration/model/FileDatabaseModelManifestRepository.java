package org.openmrs.module.epts.etl.databasemodelgeneration.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

/** Atomically maintains schema-metadata/manifest.json. */
public final class FileDatabaseModelManifestRepository {

	private final Path manifestFile;
	private final ObjectMapper mapper;

	public FileDatabaseModelManifestRepository(File schemaMetadataDirectory) {
		this.manifestFile = schemaMetadataDirectory.toPath().toAbsolutePath().normalize().resolve("manifest.json");
		this.mapper = new ObjectMapperProvider().getContext(DatabaseModelManifest.class);
	}

	public synchronized void record(DatabaseModelManifest.Entry entry) throws IOException {
		List<DatabaseModelManifest.Entry> entries = new ArrayList<>();
		if (Files.isRegularFile(manifestFile)) {
			DatabaseModelManifest current = mapper.readValue(manifestFile.toFile(), DatabaseModelManifest.class);
			entries.addAll(current.getEntries());
		}
		entries.removeIf(existing -> existing.getMetadataKey().equals(entry.getMetadataKey()));
		entries.add(entry);
		entries.sort((left, right) -> left.getMetadataKey().compareTo(right.getMetadataKey()));
		writeAtomically(new DatabaseModelManifest(entries));
	}

	public DatabaseModelManifest read() throws IOException {
		return mapper.readValue(manifestFile.toFile(), DatabaseModelManifest.class);
	}

	private void writeAtomically(DatabaseModelManifest manifest) throws IOException {
		Files.createDirectories(manifestFile.getParent());
		Path temporary = Files.createTempFile(manifestFile.getParent(), "manifest", ".tmp");
		try {
			mapper.writeValue(temporary.toFile(), manifest);
			try {
				Files.move(temporary, manifestFile, StandardCopyOption.ATOMIC_MOVE,
						StandardCopyOption.REPLACE_EXISTING);
			} catch (java.nio.file.AtomicMoveNotSupportedException exception) {
				Files.move(temporary, manifestFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			Files.deleteIfExists(temporary);
		}
	}
}
