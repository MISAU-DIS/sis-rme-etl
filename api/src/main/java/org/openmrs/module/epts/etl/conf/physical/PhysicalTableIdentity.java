package org.openmrs.module.epts.etl.conf.physical;

import java.util.Objects;

/**
 * Runtime cache identity for a physical table. Connection details intentionally
 * remain here and must not be used as the identity of persisted metadata; use
 * {@link PhysicalTableKey} for generated artifacts.
 */
public final class PhysicalTableIdentity {

	private final String connectionUrl;
	private final String databaseUser;
	private final String catalog;
	private final String schema;
	private final String tableName;
	public PhysicalTableIdentity(String connectionUrl, String databaseUser, String catalog, String schema,
			String tableName) {
		this.connectionUrl = normalize(connectionUrl);
		this.databaseUser = normalize(databaseUser);
		this.catalog = normalize(catalog);
		this.schema = normalize(schema);
		this.tableName = normalize(tableName);
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim();
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getTableName() {
		return tableName;
	}

	public PhysicalTableKey toPersistentKey(String logicalDatabaseId, String databaseDialect) {
		return new PhysicalTableKey(logicalDatabaseId, databaseDialect, catalog, schema, tableName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(connectionUrl, databaseUser, catalog, schema, tableName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PhysicalTableIdentity))
			return false;
		PhysicalTableIdentity other = (PhysicalTableIdentity) obj;

		return Objects.equals(connectionUrl, other.connectionUrl) && Objects.equals(databaseUser, other.databaseUser)
				&& Objects.equals(catalog, other.catalog) && Objects.equals(schema, other.schema)
				&& Objects.equals(tableName, other.tableName);
	}

	@Override
	public String toString() {
		return connectionUrl + "|" + databaseUser + "|" + catalog + "|" + schema + "." + tableName;
	}
}
