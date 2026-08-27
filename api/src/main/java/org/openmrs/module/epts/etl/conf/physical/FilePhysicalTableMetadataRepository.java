package org.openmrs.module.epts.etl.conf.physical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

/** JSON file repository rooted at EtlConfiguration.getSchemaMetadataDirectory(). */
public final class FilePhysicalTableMetadataRepository implements WritablePhysicalTableMetadataRepository {

	private final Path rootDirectory;
	private final ObjectMapper objectMapper;

	public FilePhysicalTableMetadataRepository(File rootDirectory) {
		this(rootDirectory.toPath(), new ObjectMapperProvider().getContext(PhysicalTableMetadata.class));
	}

	FilePhysicalTableMetadataRepository(Path rootDirectory, ObjectMapper objectMapper) {
		this.rootDirectory = rootDirectory.toAbsolutePath().normalize();
		this.objectMapper = objectMapper;
	}

	@Override
	public Optional<PhysicalTableMetadata> find(PhysicalTableKey key) throws IOException {
		Path file = pathFor(key);
		if (!Files.isRegularFile(file)) return Optional.empty();
		PhysicalTableMetadata metadata = objectMapper.readValue(file.toFile(), PhysicalTableMetadata.class);
		if (metadata.getFormatVersion() != PhysicalTableMetadata.CURRENT_FORMAT_VERSION) {
			throw new IOException("Unsupported schema metadata format version " + metadata.getFormatVersion()
					+ " in " + file);
		}
		if (!key.equals(metadata.getKey())) throw new IOException("Schema metadata key mismatch in " + file);
		return Optional.of(metadata);
	}

	@Override
	public void save(PhysicalTableMetadata metadata) throws IOException {
		Path target = pathFor(metadata.getKey());
		Files.createDirectories(target.getParent());
		Path temporary = Files.createTempFile(target.getParent(), target.getFileName().toString(), ".tmp");
		try {
			objectMapper.writeValue(temporary.toFile(), metadata);
			try {
				Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			} catch (java.nio.file.AtomicMoveNotSupportedException exception) {
				Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			Files.deleteIfExists(temporary);
		}
	}

	Path pathFor(PhysicalTableKey key) throws IOException {
		Path path = rootDirectory.resolve(safe(key.getLogicalDatabaseId())).resolve(safe(key.getDatabaseDialect()))
				.resolve(safe(key.getCatalog())).resolve(safe(key.getSchema()))
				.resolve(safe(key.getTableName()) + ".json").normalize();
		if (!path.startsWith(rootDirectory)) throw new IOException("Invalid schema metadata path for " + key);
		return path;
	}

	private String safe(String value) throws IOException {
		if (value == null || value.trim().isEmpty()) return "_default";
		String normalized = value.trim();
		if (!normalized.matches("[A-Za-z0-9_.-]+")) throw new IOException("Invalid metadata path element: " + value);
		return normalized;
	}
}
