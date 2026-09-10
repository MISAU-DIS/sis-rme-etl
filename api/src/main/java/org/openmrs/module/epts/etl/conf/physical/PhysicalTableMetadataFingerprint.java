package org.openmrs.module.epts.etl.conf.physical;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import org.openmrs.module.epts.etl.conf.physical.PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping;
import org.openmrs.module.epts.etl.conf.physical.PhysicalKeyMetadata.PhysicalKeyColumnMetadata;

/** Computes a stable SHA-256 fingerprint of a physical metadata snapshot. */
public final class PhysicalTableMetadataFingerprint {

	private static final String CANONICAL_FORMAT = "physical-table-metadata-fingerprint-v1";
	private static final char[] HEX = "0123456789abcdef".toCharArray();

	private PhysicalTableMetadataFingerprint() {
	}

	public static String sha256(PhysicalTableMetadata metadata) throws IOException {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(toCanonicalBytes(metadata));
			char[] value = new char[digest.length * 2];
			for (int index = 0; index < digest.length; index++) {
				int unsigned = digest[index] & 0xff;
				value[index * 2] = HEX[unsigned >>> 4];
				value[index * 2 + 1] = HEX[unsigned & 0x0f];
			}
			return new String(value);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 is not available", exception);
		}
	}

	private static byte[] toCanonicalBytes(PhysicalTableMetadata metadata) throws IOException {
		Objects.requireNonNull(metadata, "metadata");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream output = new DataOutputStream(bytes)) {
			writeString(output, CANONICAL_FORMAT);
			output.writeInt(metadata.getFormatVersion());
			writeTableKey(output, metadata.getKey());
			writeColumns(output, metadata.getColumns());
			writeKey(output, metadata.getPrimaryKey());
			writeKeys(output, metadata.getUniqueKeys());
			writeImportedForeignKeys(output, metadata.getImportedForeignKeys());
			writeExportedForeignKeys(output, metadata.getExportedForeignKeys());
		}
		return bytes.toByteArray();
	}

	private static void writeTableKey(DataOutputStream output, PhysicalTableKey key) throws IOException {
		writeString(output, key.getLogicalDatabaseId());
		writeString(output, key.getDatabaseDialect());
		writeString(output, key.getCatalog());
		writeString(output, key.getSchema());
		writeString(output, key.getTableName());
	}

	private static void writeColumns(DataOutputStream output, List<PhysicalColumnMetadata> columns)
			throws IOException {
		writeListSize(output, columns);
		if (columns == null) return;
		for (PhysicalColumnMetadata column : columns) {
			writeString(output, column.getName());
			writeString(output, column.getDataType());
			writeInteger(output, column.getLength());
			writeInteger(output, column.getDecimalDigits());
			output.writeBoolean(column.isNullable());
			output.writeBoolean(column.isAutoIncrement());
			output.writeBoolean(column.isTimestamp());
		}
	}

	private static void writeKeys(DataOutputStream output, List<PhysicalKeyMetadata> keys) throws IOException {
		writeListSize(output, keys);
		if (keys == null) return;
		for (PhysicalKeyMetadata key : keys) writeRequiredKey(output, key);
	}

	private static void writeKey(DataOutputStream output, PhysicalKeyMetadata key) throws IOException {
		output.writeBoolean(key != null);
		if (key != null) writeRequiredKey(output, key);
	}

	private static void writeRequiredKey(DataOutputStream output, PhysicalKeyMetadata key) throws IOException {
		writeString(output, key.getName());
		List<PhysicalKeyColumnMetadata> columns = key.getColumns();
		writeListSize(output, columns);
		if (columns != null) {
			for (PhysicalKeyColumnMetadata column : columns) {
				writeString(output, column.getName());
				writeString(output, column.getDataType());
			}
		}
		output.writeBoolean(key.isManuallyConfigured());
	}

	private static void writeImportedForeignKeys(DataOutputStream output,
			List<PhysicalForeignKeyMetadata> foreignKeys) throws IOException {
		writeListSize(output, foreignKeys);
		if (foreignKeys == null) return;
		for (PhysicalForeignKeyMetadata foreignKey : foreignKeys) {
			writeString(output, foreignKey.getName());
			writeString(output, foreignKey.getReferencedCatalog());
			writeString(output, foreignKey.getReferencedSchema());
			writeString(output, foreignKey.getReferencedTable());
			writeMappings(output, foreignKey.getMappings());
		}
	}

	private static void writeExportedForeignKeys(DataOutputStream output,
			List<PhysicalExportedForeignKeyMetadata> foreignKeys) throws IOException {
		writeListSize(output, foreignKeys);
		if (foreignKeys == null) return;
		for (PhysicalExportedForeignKeyMetadata foreignKey : foreignKeys) {
			writeString(output, foreignKey.getName());
			writeString(output, foreignKey.getChildCatalog());
			writeString(output, foreignKey.getChildSchema());
			writeString(output, foreignKey.getChildTable());
			writeMappings(output, foreignKey.getMappings());
		}
	}

	private static void writeMappings(DataOutputStream output, List<PhysicalForeignKeyMapping> mappings)
			throws IOException {
		writeListSize(output, mappings);
		if (mappings == null) return;
		for (PhysicalForeignKeyMapping mapping : mappings) {
			writeString(output, mapping.getChildColumn());
			writeString(output, mapping.getParentColumn());
		}
	}

	private static void writeListSize(DataOutputStream output, List<?> values) throws IOException {
		output.writeInt(values == null ? -1 : values.size());
	}

	private static void writeInteger(DataOutputStream output, Integer value) throws IOException {
		output.writeBoolean(value != null);
		if (value != null) output.writeInt(value);
	}

	private static void writeString(DataOutputStream output, String value) throws IOException {
		if (value == null) {
			output.writeInt(-1);
			return;
		}
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		output.writeInt(bytes.length);
		output.write(bytes);
	}
}
