package org.openmrs.module.epts.etl.conf.physical;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stable identity used by generated/persisted metadata. Unlike
 * {@link PhysicalTableIdentity}, it contains no connection URL or database user.
 */
public final class PhysicalTableKey {

	private final String logicalDatabaseId;
	private final String databaseDialect;
	private final String catalog;
	private final String schema;
	private final String tableName;

	@JsonCreator
	public PhysicalTableKey(@JsonProperty("logicalDatabaseId") String logicalDatabaseId,
			@JsonProperty("databaseDialect") String databaseDialect, @JsonProperty("catalog") String catalog,
			@JsonProperty("schema") String schema, @JsonProperty("tableName") String tableName) {
		this.logicalDatabaseId = normalize(logicalDatabaseId);
		this.databaseDialect = normalize(databaseDialect);
		this.catalog = normalize(catalog);
		this.schema = normalize(schema);
		this.tableName = normalize(tableName);
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim();
	}

	public String getLogicalDatabaseId() { return logicalDatabaseId; }
	public String getDatabaseDialect() { return databaseDialect; }
	public String getCatalog() { return catalog; }
	public String getSchema() { return schema; }
	public String getTableName() { return tableName; }

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof PhysicalTableKey)) return false;
		PhysicalTableKey other = (PhysicalTableKey) object;
		return Objects.equals(logicalDatabaseId, other.logicalDatabaseId)
				&& Objects.equals(databaseDialect, other.databaseDialect) && Objects.equals(catalog, other.catalog)
				&& Objects.equals(schema, other.schema) && Objects.equals(tableName, other.tableName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(logicalDatabaseId, databaseDialect, catalog, schema, tableName);
	}

	@Override
	public String toString() {
		return logicalDatabaseId + "|" + databaseDialect + "|" + catalog + "|" + schema + "." + tableName;
	}
}
