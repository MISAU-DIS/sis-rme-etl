package org.openmrs.module.epts.etl.databasemodelgeneration.model;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

/** Atomically maintains database-model/schema-metadata/manifest.json. */
public final class FileDatabaseModelManifestRepository {

	private static final int MOVE_ATTEMPTS = 5;
	private static final long INITIAL_RETRY_DELAY_MILLIS = 25L;
	private static final ConcurrentMap<Path, Object> MANIFEST_LOCKS = new ConcurrentHashMap<>();

	private final Path manifestFile;
	private final ObjectMapper mapper;
	private final Object manifestLock;

	public FileDatabaseModelManifestRepository(File schemaMetadataDirectory) {
		this.manifestFile = schemaMetadataDirectory.toPath().toAbsolutePath().normalize().resolve("manifest.json");
		this.mapper = new ObjectMapperProvider().getContext(DatabaseModelManifest.class);
		this.manifestLock = MANIFEST_LOCKS.computeIfAbsent(this.manifestFile, ignored -> new Object());
	}

	public void record(DatabaseModelManifest.Entry entry) throws IOException {
		synchronized (manifestLock) {
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
	}

	public DatabaseModelManifest read() throws IOException {
		synchronized (manifestLock) {
			DatabaseModelManifest manifest = mapper.readValue(manifestFile.toFile(), DatabaseModelManifest.class);
			if (manifest.getFormatVersion() != DatabaseModelManifest.CURRENT_FORMAT_VERSION) {
				throw new IOException("Unsupported database model manifest format version " + manifest.getFormatVersion()
						+ " in " + manifestFile);
			}
			return manifest;
		}
	}

	private void writeAtomically(DatabaseModelManifest manifest) throws IOException {
		Files.createDirectories(manifestFile.getParent());
		Path temporary = Files.createTempFile(manifestFile.getParent(), "manifest", ".tmp");
		try {
			mapper.writeValue(temporary.toFile(), manifest);
			moveReplacingWithRetry(temporary);
		} finally {
			Files.deleteIfExists(temporary);
		}
	}

	private void moveReplacingWithRetry(Path temporary) throws IOException {
		boolean useAtomicMove = true;
		int deniedAttempts = 0;

		while (true) {
			try {
				if (useAtomicMove) {
					Files.move(temporary, manifestFile, StandardCopyOption.ATOMIC_MOVE,
							StandardCopyOption.REPLACE_EXISTING);
				} else {
					Files.move(temporary, manifestFile, StandardCopyOption.REPLACE_EXISTING);
				}
				return;
			} catch (AtomicMoveNotSupportedException exception) {
				useAtomicMove = false;
			} catch (AccessDeniedException exception) {
				deniedAttempts++;
				if (deniedAttempts >= MOVE_ATTEMPTS)
					throw exception;
				waitBeforeRetry(deniedAttempts);
			}
		}
	}

	private void waitBeforeRetry(int attempt) throws InterruptedIOException {
		try {
			Thread.sleep(INITIAL_RETRY_DELAY_MILLIS * attempt);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			InterruptedIOException interrupted = new InterruptedIOException(
					"Interrupted while waiting to replace " + manifestFile);
			interrupted.initCause(exception);
			throw interrupted;
		}
	}
}
