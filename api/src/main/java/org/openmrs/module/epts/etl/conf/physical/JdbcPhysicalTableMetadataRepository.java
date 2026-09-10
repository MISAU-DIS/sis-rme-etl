package org.openmrs.module.epts.etl.conf.physical;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.DBUtilities;

/** Live-schema repository backed exclusively by JDBC DatabaseMetaData. */
public final class JdbcPhysicalTableMetadataRepository implements PhysicalTableMetadataRepository {

	private final Connection connection;

	public JdbcPhysicalTableMetadataRepository(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Optional<PhysicalTableMetadata> find(PhysicalTableKey key) throws IOException {
		try {
			if (!DBUtilities.isTableExists(key.getSchema(), key.getTableName(), connection)) return Optional.empty();
			List<PhysicalColumnMetadata> columns = columns(key);
			PhysicalKeyMetadata primaryKey = primaryKey(key, columns);
			List<PhysicalKeyMetadata> uniqueKeys = uniqueKeys(key, columns, primaryKey);
			List<PhysicalForeignKeyMetadata> foreignKeys = importedForeignKeys(key);
			List<PhysicalExportedForeignKeyMetadata> exportedForeignKeys = findExportedForeignKeys(key);
			return Optional.of(new PhysicalTableMetadata(key, columns, primaryKey, uniqueKeys, foreignKeys,
					exportedForeignKeys));
		} catch (Exception exception) {
			throw new IOException("Could not load JDBC schema metadata for " + key, exception);
		}
	}

	private List<PhysicalColumnMetadata> columns(PhysicalTableKey key) throws Exception {
		List<Field> fields = DBUtilities.getTableFields(key.getTableName(), key.getSchema(), connection);
		List<PhysicalColumnMetadata> columns = new ArrayList<>(fields.size());
		for (Field field : fields) columns.add(PhysicalColumnMetadata.fromField(field));
		return columns;
	}

	private PhysicalKeyMetadata primaryKey(PhysicalTableKey key, List<PhysicalColumnMetadata> columns)
			throws SQLException {
		List<PhysicalKeyMetadata.PhysicalKeyColumnMetadata> keyColumns = new ArrayList<>();
		String keyName = null;
		try (ResultSet result = metadata().getPrimaryKeys(catalogForMetadata(key), key.getSchema(), key.getTableName())) {
			while (result.next()) {
				keyName = result.getString("PK_NAME");
				String columnName = result.getString("COLUMN_NAME");
				keyColumns.add(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata(columnName,
						dataType(columns, columnName)));
			}
		}
		return keyColumns.isEmpty() ? null : new PhysicalKeyMetadata(keyName, keyColumns, false);
	}

	private List<PhysicalKeyMetadata> uniqueKeys(PhysicalTableKey key, List<PhysicalColumnMetadata> columns,
			PhysicalKeyMetadata primaryKey) throws SQLException {
		Map<String, List<PhysicalKeyMetadata.PhysicalKeyColumnMetadata>> indexes = new LinkedHashMap<>();
		try (ResultSet result = metadata().getIndexInfo(catalogForMetadata(key), key.getSchema(), key.getTableName(), true,
				true)) {
			while (result.next()) {
				String indexName = result.getString("INDEX_NAME");
				String columnName = result.getString("COLUMN_NAME");
				if (indexName == null || columnName == null) continue;
				indexes.computeIfAbsent(indexName, ignored -> new ArrayList<>())
						.add(new PhysicalKeyMetadata.PhysicalKeyColumnMetadata(columnName, dataType(columns, columnName)));
			}
		}
		List<PhysicalKeyMetadata> keys = new ArrayList<>();
		for (Map.Entry<String, List<PhysicalKeyMetadata.PhysicalKeyColumnMetadata>> index : indexes.entrySet()) {
			PhysicalKeyMetadata candidate = new PhysicalKeyMetadata(index.getKey(), index.getValue(), false);
			if (primaryKey == null || !primaryKey.getColumns().equals(candidate.getColumns())) keys.add(candidate);
		}
		return keys;
	}

	private List<PhysicalForeignKeyMetadata> importedForeignKeys(PhysicalTableKey key) throws SQLException {
		Map<String, ForeignKeyBuilder> builders = new LinkedHashMap<>();
		try (ResultSet result = metadata().getImportedKeys(catalogForMetadata(key), key.getSchema(), key.getTableName())) {
			while (result.next()) {
				String name = result.getString("FK_NAME");
				String stableName = name == null ? result.getString("PKTABLE_NAME") : name;
				ForeignKeyBuilder builder = builders.computeIfAbsent(stableName,
						ignored -> new ForeignKeyBuilder(name, read(result, "PKTABLE_CAT"), read(result, "PKTABLE_SCHEM"),
								read(result, "PKTABLE_NAME")));
				builder.mappings.add(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping(
						result.getString("FKCOLUMN_NAME"), result.getString("PKCOLUMN_NAME")));
			}
		}
		List<PhysicalForeignKeyMetadata> keys = new ArrayList<>(builders.size());
		for (ForeignKeyBuilder builder : builders.values()) keys.add(builder.build());
		return keys;
	}

	/** Loads relationships in which this table is the referenced parent. */
	public List<PhysicalExportedForeignKeyMetadata> findExportedForeignKeys(PhysicalTableKey key) throws SQLException {
		Map<String, ExportedForeignKeyBuilder> builders = new LinkedHashMap<>();
		try (ResultSet result = metadata().getExportedKeys(catalogForMetadata(key), key.getSchema(), key.getTableName())) {
			while (result.next()) {
				String name = result.getString("FK_NAME");
				String stableName = name == null ? result.getString("FKTABLE_NAME") : name;
				ExportedForeignKeyBuilder builder = builders.computeIfAbsent(stableName,
						ignored -> new ExportedForeignKeyBuilder(name, read(result, "FKTABLE_CAT"),
								read(result, "FKTABLE_SCHEM"), read(result, "FKTABLE_NAME")));
				builder.mappings.add(new PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping(
						result.getString("FKCOLUMN_NAME"), result.getString("PKCOLUMN_NAME")));
			}
		}
		List<PhysicalExportedForeignKeyMetadata> keys = new ArrayList<>(builders.size());
		for (ExportedForeignKeyBuilder builder : builders.values()) keys.add(builder.build());
		return keys;
	}

	private String read(ResultSet result, String column) {
		try { return result.getString(column); } catch (SQLException exception) { return null; }
	}

	private String dataType(List<PhysicalColumnMetadata> columns, String columnName) {
		for (PhysicalColumnMetadata column : columns) {
			if (column.getName().equalsIgnoreCase(columnName)) return column.getDataType();
		}
		return null;
	}

	private DatabaseMetaData metadata() throws SQLException { return connection.getMetaData(); }

	private String catalogForMetadata(PhysicalTableKey key) throws SQLException {
		return DBUtilities.isMySQLDB(connection) ? key.getSchema()
				: (key.getCatalog().isEmpty() ? connection.getCatalog() : key.getCatalog());
	}

	private static final class ForeignKeyBuilder {
		private final String name;
		private final String catalog;
		private final String schema;
		private final String table;
		private final List<PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping> mappings = new ArrayList<>();

		private ForeignKeyBuilder(String name, String catalog, String schema, String table) {
			this.name = name; this.catalog = catalog; this.schema = schema; this.table = table;
		}

		private PhysicalForeignKeyMetadata build() {
			return new PhysicalForeignKeyMetadata(name, catalog, schema, table, mappings);
		}
	}

	private static final class ExportedForeignKeyBuilder {
		private final String name;
		private final String catalog;
		private final String schema;
		private final String table;
		private final List<PhysicalForeignKeyMetadata.PhysicalForeignKeyMapping> mappings = new ArrayList<>();

		private ExportedForeignKeyBuilder(String name, String catalog, String schema, String table) {
			this.name = name; this.catalog = catalog; this.schema = schema; this.table = table;
		}

		private PhysicalExportedForeignKeyMetadata build() {
			return new PhysicalExportedForeignKeyMetadata(name, catalog, schema, table, mappings);
		}
	}
}
