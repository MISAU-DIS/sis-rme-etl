package org.openmrs.module.epts.etl.conf.physical;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

/** JSON file repository rooted at database-model/schema-metadata. */
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
		validate(metadata, key, file);
		return Optional.of(metadata);
	}

	/** Finds metadata without consulting JDBC for dialect or catalog. */
	public Optional<PhysicalTableMetadata> find(String logicalDatabaseId, String schema, String tableName)
			throws IOException {
		Path databaseRoot = rootDirectory.resolve(safe(logicalDatabaseId)).normalize();
		if (!databaseRoot.startsWith(rootDirectory) || !Files.isDirectory(databaseRoot)) return Optional.empty();

		List<Path> matches = new ArrayList<>();
		String metadataFileName = safe(tableName) + ".json";
		try (Stream<Path> paths = Files.walk(databaseRoot, 5)) {
			paths.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().equals(metadataFileName))
					.forEach(path -> collectIfMatchingSchema(path, schema, matches));
		} catch (MetadataReadRuntimeException exception) {
			throw (IOException) exception.getCause();
		}

		if (matches.isEmpty()) return Optional.empty();
		if (matches.size() > 1) throw new IOException("Ambiguous schema metadata for " + logicalDatabaseId + ":"
				+ schema + "." + tableName + " matches " + matches);

		PhysicalTableMetadata metadata = objectMapper.readValue(matches.get(0).toFile(), PhysicalTableMetadata.class);
		validate(metadata, metadata.getKey(), matches.get(0));
		return Optional.of(metadata);
	}

	private void collectIfMatchingSchema(Path path, String schema, List<Path> matches) {
		try {
			PhysicalTableMetadata metadata = objectMapper.readValue(path.toFile(), PhysicalTableMetadata.class);
			if (metadata.getKey().getSchema().equals(schema)) matches.add(path);
		} catch (IOException exception) {
			throw new MetadataReadRuntimeException(exception);
		}
	}

	private void validate(PhysicalTableMetadata metadata, PhysicalTableKey key, Path file) throws IOException {
		if (metadata.getFormatVersion() != PhysicalTableMetadata.CURRENT_FORMAT_VERSION) {
			throw new IOException("Unsupported schema metadata format version " + metadata.getFormatVersion()
					+ " in " + file);
		}
		if (!key.equals(metadata.getKey())) throw new IOException("Schema metadata key mismatch in " + file);
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

	private static final class MetadataReadRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private MetadataReadRuntimeException(IOException cause) { super(cause); }
	}
}
