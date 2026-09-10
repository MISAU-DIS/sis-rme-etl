package org.openmrs.module.epts.etl.conf.physical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.openmrs.module.epts.etl.conf.RefMapping;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.TypePrecision;

/**
 * Test-only canonical view of the physical facts currently produced by
 * {@link TableConfiguration#fullLoad(java.sql.Connection)}.
 *
 * Runtime/contextual state (aliases, extraction conditions, conflict policies,
 * generated SQL and back references) is intentionally excluded. This class is
 * the equivalence oracle that a future persisted metadata model must satisfy.
 */
final class TableConfigurationSemanticSnapshot {

	private final String schema;
	private final String tableName;
	private final List<ColumnFact> columns;
	private final KeyFact primaryKey;
	private final List<KeyFact> uniqueKeys;
	private final List<RelationshipFact> parents;

	private TableConfigurationSemanticSnapshot(TableConfiguration table) {
		this.schema = normalize(table.getSchema());
		this.tableName = normalize(table.getTableName());
		this.columns = columnFacts(table.getFields());
		this.primaryKey = table.getPrimaryKey() == null ? null : new KeyFact(table.getPrimaryKey());
		this.uniqueKeys = keyFacts(table.getUniqueKeys());
		this.parents = relationshipFacts(table.getParentRefInfo());
	}

	static TableConfigurationSemanticSnapshot from(TableConfiguration table) {
		return new TableConfigurationSemanticSnapshot(table);
	}

	private static List<ColumnFact> columnFacts(List<? extends Field> fields) {
		if (fields == null) return Collections.emptyList();

		List<ColumnFact> facts = new ArrayList<>(fields.size());
		for (Field field : fields) facts.add(new ColumnFact(field));
		return Collections.unmodifiableList(facts);
	}

	private static List<KeyFact> keyFacts(List<UniqueKeyInfo> keys) {
		if (keys == null) return Collections.emptyList();

		List<KeyFact> facts = new ArrayList<>(keys.size());
		for (UniqueKeyInfo key : keys) facts.add(new KeyFact(key));
		Collections.sort(facts);
		return Collections.unmodifiableList(facts);
	}

	private static List<RelationshipFact> relationshipFacts(List<ParentTable> relationships) {
		if (relationships == null) return Collections.emptyList();

		List<RelationshipFact> facts = new ArrayList<>(relationships.size());
		for (ParentTable relationship : relationships) facts.add(new RelationshipFact(relationship));
		Collections.sort(facts);
		return Collections.unmodifiableList(facts);
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim().toLowerCase();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof TableConfigurationSemanticSnapshot)) return false;
		TableConfigurationSemanticSnapshot other = (TableConfigurationSemanticSnapshot) object;
		return Objects.equals(schema, other.schema) && Objects.equals(tableName, other.tableName)
				&& Objects.equals(columns, other.columns) && Objects.equals(primaryKey, other.primaryKey)
				&& Objects.equals(uniqueKeys, other.uniqueKeys) && Objects.equals(parents, other.parents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(schema, tableName, columns, primaryKey, uniqueKeys, parents);
	}

	@Override
	public String toString() {
		return "TableSnapshot{" + schema + "." + tableName + ", columns=" + columns + ", primaryKey="
				+ primaryKey + ", uniqueKeys=" + uniqueKeys + ", parents=" + parents + "}";
	}

	private static final class ColumnFact {
		private final String name;
		private final String dataType;
		private final Integer length;
		private final Integer scale;
		private final boolean nullable;
		private final boolean autoIncrement;
		private final boolean timestamp;

		private ColumnFact(Field field) {
			TypePrecision precision = field.getPrecision();
			this.name = normalize(field.getName());
			this.dataType = normalize(field.getDataType());
			this.length = precision == null ? null : precision.getLength();
			this.scale = precision == null ? null : precision.getDecimalDigits();
			this.nullable = field.isAllowNull();
			this.autoIncrement = Boolean.TRUE.equals(field.isAutoIncrement());
			this.timestamp = Boolean.TRUE.equals(field.isTimeStamp());
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof ColumnFact)) return false;
			ColumnFact other = (ColumnFact) object;
			return nullable == other.nullable && autoIncrement == other.autoIncrement && timestamp == other.timestamp
					&& Objects.equals(name, other.name) && Objects.equals(dataType, other.dataType)
					&& Objects.equals(length, other.length) && Objects.equals(scale, other.scale);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, dataType, length, scale, nullable, autoIncrement, timestamp);
		}

		@Override
		public String toString() {
			return name + ":" + dataType + "(" + length + "," + scale + ")";
		}
	}

	private static final class KeyFact implements Comparable<KeyFact> {
		private final String name;
		private final List<String> columns;
		private final boolean manuallyConfigured;

		private KeyFact(UniqueKeyInfo key) {
			this.name = normalize(key.getKeyName());
			this.columns = new ArrayList<>();
			if (key.getFields() != null) {
				for (Field field : key.getFields()) columns.add(normalize(field.getName()));
			}
			this.manuallyConfigured = key.isManualConfigured();
		}

		@Override
		public int compareTo(KeyFact other) {
			int byName = name.compareTo(other.name);
			return byName != 0 ? byName : columns.toString().compareTo(other.columns.toString());
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof KeyFact)) return false;
			KeyFact other = (KeyFact) object;
			return manuallyConfigured == other.manuallyConfigured && Objects.equals(name, other.name)
					&& Objects.equals(columns, other.columns);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, columns, manuallyConfigured);
		}

		@Override
		public String toString() {
			return name + columns;
		}
	}

	private static final class RelationshipFact implements Comparable<RelationshipFact> {
		private final String tableName;
		private final String referenceCode;
		private final List<String> mappings;

		private RelationshipFact(ParentTable parent) {
			this.tableName = normalize(parent.getTableName());
			this.referenceCode = normalize(parent.getRefCode());
			this.mappings = new ArrayList<>();
			if (parent.getRefMapping() != null) {
				for (RefMapping mapping : parent.getRefMapping()) {
					mappings.add(normalize(mapping.getChildFieldName()) + "->"
							+ normalize(mapping.getParentFieldName()));
				}
			}
			Collections.sort(mappings);
		}

		@Override
		public int compareTo(RelationshipFact other) {
			int byTable = tableName.compareTo(other.tableName);
			return byTable != 0 ? byTable : referenceCode.compareTo(other.referenceCode);
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof RelationshipFact)) return false;
			RelationshipFact other = (RelationshipFact) object;
			return Objects.equals(tableName, other.tableName) && Objects.equals(referenceCode, other.referenceCode)
					&& Objects.equals(mappings, other.mappings);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tableName, referenceCode, mappings);
		}

		@Override
		public String toString() {
			return referenceCode + ":" + tableName + mappings;
		}
	}
}
