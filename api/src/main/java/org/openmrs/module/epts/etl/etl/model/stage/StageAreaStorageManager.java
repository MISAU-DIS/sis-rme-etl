package org.openmrs.module.epts.etl.etl.model.stage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.EtlConfigurationTableConf;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Resolves the JDBC storage used by the Stage Area independently from the
 * logical origin of a record.
 */
public final class StageAreaStorageManager {

	private StageAreaStorageManager() {
	}

	public static EtlConfigurationTableConf resolveStageTable(EtlObjectConfiguration configuration,
			EtlStageTableType type, Connection stageConnection) throws DBException {
		return resolvePhysicalTable(configuration, generateStageTableName(configuration, type), stageConnection);
	}

	public static EtlConfigurationTableConf resolveKeyTable(EtlObjectConfiguration configuration,
			EtlStageTableType type, Connection stageConnection) throws DBException {
		if (type.isSrc()) {
			return resolvePhysicalTable(configuration,
					generateStageTableName(configuration, type) + TableConfiguration.STAGE_TABLE_SRC_UNIQUE_KEYS_SUFIX,
					stageConnection);
		}
		if (type.isDst()) {
			return resolvePhysicalTable(configuration,
					generateStageTableName(configuration, type) + TableConfiguration.STAGE_TABLE_DST_UNIQUE_KEYS,
					stageConnection);
		}

		return null;
	}

	public static String resolveObjectName(EtlObjectConfiguration configuration) {
		return configuration instanceof TableConfiguration ? ((TableConfiguration) configuration).getTableName()
				: configuration.getObjectName();
	}

	public static void ensureSourceStageTables(EtlObjectConfiguration configuration, Connection stageConnection)
			throws DBException {
		EtlConfigurationTableConf stageDescriptor = createStageDescriptor(configuration);

		stageDescriptor.createRelatedSrcStageAreaTable(stageConnection);
		stageDescriptor.createRelatedStageAreaSrcUniqueKeysTable(stageConnection);

		if (configuration.getRelatedEtlConf().mustCreateArquiveTables()) {
			if (configuration instanceof TableConfiguration) {
				((TableConfiguration) configuration).createRelatedProcessedRecordStageAreaTable(stageConnection);
			} else {
				createProcessedRecordStageTable(configuration, stageConnection);
			}
		}
	}

	private static String generateStageTableName(EtlObjectConfiguration configuration, EtlStageTableType type) {
		String objectName = resolveObjectName(configuration);

		if (type.isSrc()) {
			return objectName + TableConfiguration.STAGE_TABLE_SRC_SUFIX;
		}
		if (type.isDst()) {
			return objectName + TableConfiguration.STAGE_TABLE_DST_SUFIX;
		}
		if (type.isProcessedRecord()) {
			return objectName + TableConfiguration.STAGE_TABLE_PROCESSED_SUFIX;
		}

		throw new ForbiddenOperationException("Unsupported Stage Area table type: " + type);
	}

	private static EtlConfigurationTableConf resolvePhysicalTable(EtlObjectConfiguration configuration,
			String tableName, Connection stageConnection) throws DBException {
		TableConfiguration existing = configuration.getRelatedEtlConf().findOnFullLoadedTables(tableName,
				configuration.getRelatedEtlConf().getSyncStageSchema());
		EtlConfigurationTableConf stageTable = existing instanceof EtlConfigurationTableConf
				? (EtlConfigurationTableConf) existing
				: new EtlConfigurationTableConf(tableName, configuration.getRelatedEtlConf());

		if (!stageTable.isFullLoaded()) {
			stageTable.setParentConf(configuration.getParentConf());
			stageTable.fullLoad(stageConnection);
		}

		return stageTable;
	}

	private static EtlConfigurationTableConf createStageDescriptor(EtlObjectConfiguration configuration) {
		EtlConfigurationTableConf descriptor = new EtlConfigurationTableConf(resolveObjectName(configuration),
				configuration.getRelatedEtlConf());
		descriptor.setParentConf(configuration.getParentConf());
		descriptor.setFields(copyFields(configuration.getFields()));
		return descriptor;
	}

	private static void createProcessedRecordStageTable(EtlObjectConfiguration configuration,
			Connection stageConnection) throws DBException {
		EtlConfigurationTableConf processed = new EtlConfigurationTableConf(
				generateStageTableName(configuration, EtlStageTableType.PROCESSED_RECORD),
				configuration.getRelatedEtlConf());
		processed.setParentConf(configuration.getParentConf());

		List<Field> fields = copyFields(configuration.getFields());
		addFieldIfMissing(fields, Field.fastCreateWithType("processing_status", "varchar"));
		addFieldIfMissing(fields, Field.fastCreateWithType("processing_date", "datetime"));
		addFieldIfMissing(fields, Field.fastCreateWithType("processing_error", "text"));
		addFieldIfMissing(fields, Field.fastCreateWithType("retry_count", "int"));
		processed.setFields(fields);

		if (configuration.getPrimaryKey() instanceof PrimaryKey) {
			PrimaryKey primaryKey = new PrimaryKey();
			primaryKey.copy(configuration.getPrimaryKey());
			processed.setPrimaryKeyInfoLoaded(true);
			processed.setPrimaryKey(primaryKey);
		}

		processed.createTable(stageConnection);
	}

	private static List<Field> copyFields(List<? extends Field> fields) {
		List<Field> copies = new ArrayList<>();

		if (fields != null) {
			for (Field field : fields) {
				if (field != null) {
					Field copy = new Field();
					copy.copyFrom(field);
					copies.add(copy);
				}
			}
		}

		return copies;
	}

	private static void addFieldIfMissing(List<Field> fields, Field field) {
		for (Field existing : fields) {
			if (existing.getName().equalsIgnoreCase(field.getName())) {
				return;
			}
		}

		field.setAllowNull(!"processing_status".equals(field.getName()) && !"processing_date".equals(field.getName())
				&& !"retry_count".equals(field.getName()));
		fields.add(field);
	}
}
