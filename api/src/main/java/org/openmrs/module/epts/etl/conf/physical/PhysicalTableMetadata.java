package org.openmrs.module.epts.etl.conf.physical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Complete, immutable and persistible snapshot of one physical table. */
public final class PhysicalTableMetadata {

	public static final int CURRENT_FORMAT_VERSION = 2;

	private final int formatVersion;
	private final PhysicalTableKey key;
	private final List<PhysicalColumnMetadata> columns;
	private final PhysicalKeyMetadata primaryKey;
	private final List<PhysicalKeyMetadata> uniqueKeys;
	private final List<PhysicalForeignKeyMetadata> importedForeignKeys;
	private final List<PhysicalExportedForeignKeyMetadata> exportedForeignKeys;

	@JsonCreator
	public PhysicalTableMetadata(@JsonProperty("formatVersion") int formatVersion,
			@JsonProperty("key") PhysicalTableKey key,
			@JsonProperty("columns") List<PhysicalColumnMetadata> columns,
			@JsonProperty("primaryKey") PhysicalKeyMetadata primaryKey,
			@JsonProperty("uniqueKeys") List<PhysicalKeyMetadata> uniqueKeys,
			@JsonProperty("importedForeignKeys") List<PhysicalForeignKeyMetadata> importedForeignKeys,
			@JsonProperty("exportedForeignKeys") List<PhysicalExportedForeignKeyMetadata> exportedForeignKeys) {
		this.formatVersion = formatVersion;
		this.key = Objects.requireNonNull(key, "key");
		this.columns = immutable(columns);
		this.primaryKey = primaryKey;
		this.uniqueKeys = immutable(uniqueKeys);
		this.importedForeignKeys = immutable(importedForeignKeys);
		this.exportedForeignKeys = immutable(exportedForeignKeys);
	}

	public PhysicalTableMetadata(PhysicalTableKey key, List<PhysicalColumnMetadata> columns,
			PhysicalKeyMetadata primaryKey, List<PhysicalKeyMetadata> uniqueKeys,
			List<PhysicalForeignKeyMetadata> importedForeignKeys) {
		this(CURRENT_FORMAT_VERSION, key, columns, primaryKey, uniqueKeys, importedForeignKeys,
				Collections.emptyList());
	}

	public PhysicalTableMetadata(PhysicalTableKey key, List<PhysicalColumnMetadata> columns,
			PhysicalKeyMetadata primaryKey, List<PhysicalKeyMetadata> uniqueKeys,
			List<PhysicalForeignKeyMetadata> importedForeignKeys,
			List<PhysicalExportedForeignKeyMetadata> exportedForeignKeys) {
		this(CURRENT_FORMAT_VERSION, key, columns, primaryKey, uniqueKeys, importedForeignKeys, exportedForeignKeys);
	}

	private static <T> List<T> immutable(List<T> values) {
		return values == null ? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(values));
	}

	public int getFormatVersion() { return formatVersion; }
	public PhysicalTableKey getKey() { return key; }
	public List<PhysicalColumnMetadata> getColumns() { return columns; }
	public PhysicalKeyMetadata getPrimaryKey() { return primaryKey; }
	public List<PhysicalKeyMetadata> getUniqueKeys() { return uniqueKeys; }
	public List<PhysicalForeignKeyMetadata> getImportedForeignKeys() { return importedForeignKeys; }
	public List<PhysicalExportedForeignKeyMetadata> getExportedForeignKeys() { return exportedForeignKeys; }

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof PhysicalTableMetadata)) return false;
		PhysicalTableMetadata other = (PhysicalTableMetadata) object;
		return formatVersion == other.formatVersion && Objects.equals(key, other.key)
				&& Objects.equals(columns, other.columns) && Objects.equals(primaryKey, other.primaryKey)
				&& Objects.equals(uniqueKeys, other.uniqueKeys)
				&& Objects.equals(importedForeignKeys, other.importedForeignKeys)
				&& Objects.equals(exportedForeignKeys, other.exportedForeignKeys);
	}

	@Override
	public int hashCode() {
		return Objects.hash(formatVersion, key, columns, primaryKey, uniqueKeys, importedForeignKeys,
				exportedForeignKeys);
	}
}
