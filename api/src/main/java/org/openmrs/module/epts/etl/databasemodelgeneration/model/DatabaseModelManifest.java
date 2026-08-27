package org.openmrs.module.epts.etl.databasemodelgeneration.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Versioned index connecting generated classes to their physical snapshots. */
public final class DatabaseModelManifest {

	public static final int CURRENT_FORMAT_VERSION = 1;

	private final int formatVersion;
	private final List<Entry> entries;

	@JsonCreator
	public DatabaseModelManifest(@JsonProperty("formatVersion") int formatVersion,
			@JsonProperty("entries") List<Entry> entries) {
		this.formatVersion = formatVersion;
		this.entries = entries == null ? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(entries));
	}

	public DatabaseModelManifest(List<Entry> entries) {
		this(CURRENT_FORMAT_VERSION, entries);
	}

	public int getFormatVersion() { return formatVersion; }
	public List<Entry> getEntries() { return entries; }

	public static final class Entry {
		private final String metadataKey;
		private final String generatedClassName;

		@JsonCreator
		public Entry(@JsonProperty("metadataKey") String metadataKey,
				@JsonProperty("generatedClassName") String generatedClassName) {
			this.metadataKey = metadataKey;
			this.generatedClassName = generatedClassName;
		}

		public String getMetadataKey() { return metadataKey; }
		public String getGeneratedClassName() { return generatedClassName; }

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof Entry)) return false;
			Entry other = (Entry) object;
			return Objects.equals(metadataKey, other.metadataKey)
					&& Objects.equals(generatedClassName, other.generatedClassName);
		}

		@Override
		public int hashCode() { return Objects.hash(metadataKey, generatedClassName); }
	}
}
