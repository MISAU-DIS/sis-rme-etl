package org.openmrs.module.epts.etl.conf.physical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Immutable physical primary-key or unique-key description. */
public final class PhysicalKeyMetadata {

	private final String name;
	private final List<PhysicalKeyColumnMetadata> columns;
	private final boolean manuallyConfigured;

	@JsonCreator
	public PhysicalKeyMetadata(@JsonProperty("name") String name,
			@JsonProperty("columns") List<PhysicalKeyColumnMetadata> columns,
			@JsonProperty("manuallyConfigured") boolean manuallyConfigured) {
		this.name = name;
		this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
		this.manuallyConfigured = manuallyConfigured;
	}

	public static PhysicalKeyMetadata fromKey(UniqueKeyInfo key) {
		List<PhysicalKeyColumnMetadata> columns = new ArrayList<>();
		if (key.getFields() != null) {
			for (Key column : key.getFields()) {
				columns.add(new PhysicalKeyColumnMetadata(column.getName(), column.getDataType()));
			}
		}
		return new PhysicalKeyMetadata(key.getKeyName(), columns, key.isManualConfigured());
	}

	public PrimaryKey toPrimaryKey(TableConfiguration context) {
		PrimaryKey key = new PrimaryKey();
		populate(key, context);
		return key;
	}

	public UniqueKeyInfo toUniqueKey(TableConfiguration context) {
		UniqueKeyInfo key = new UniqueKeyInfo();
		populate(key, context);
		return key;
	}

	private void populate(UniqueKeyInfo key, TableConfiguration context) {
		key.setKeyName(name);
		key.setManualConfigured(manuallyConfigured);
		for (PhysicalKeyColumnMetadata column : columns) {
			key.addKey(Key.fastCreateTyped(column.getName(), column.getDataType()));
		}
		if (context != null) key.setTabConf(context, false);
	}

	public String getName() { return name; }
	public List<PhysicalKeyColumnMetadata> getColumns() { return columns; }
	public boolean isManuallyConfigured() { return manuallyConfigured; }

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof PhysicalKeyMetadata)) return false;
		PhysicalKeyMetadata other = (PhysicalKeyMetadata) object;
		return manuallyConfigured == other.manuallyConfigured && Objects.equals(name, other.name)
				&& Objects.equals(columns, other.columns);
	}

	@Override
	public int hashCode() { return Objects.hash(name, columns, manuallyConfigured); }

	@Override
	public String toString() {
		return "PhysicalKeyMetadata{name='" + name + "', columns=" + columns + ", manuallyConfigured="
				+ manuallyConfigured + "}";
	}

	public static final class PhysicalKeyColumnMetadata {
		private final String name;
		private final String dataType;

		@JsonCreator
		public PhysicalKeyColumnMetadata(@JsonProperty("name") String name,
				@JsonProperty("dataType") String dataType) {
			this.name = name;
			this.dataType = dataType;
		}

		public String getName() { return name; }
		public String getDataType() { return dataType; }

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof PhysicalKeyColumnMetadata)) return false;
			PhysicalKeyColumnMetadata other = (PhysicalKeyColumnMetadata) object;
			return Objects.equals(name, other.name) && Objects.equals(dataType, other.dataType);
		}

		@Override
		public int hashCode() { return Objects.hash(name, dataType); }

		@Override
		public String toString() {
			return "PhysicalKeyColumnMetadata{name='" + name + "', dataType='" + dataType + "'}";
		}
	}
}
