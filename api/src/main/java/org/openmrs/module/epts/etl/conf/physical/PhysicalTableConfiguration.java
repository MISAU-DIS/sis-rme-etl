package org.openmrs.module.epts.etl.conf.physical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.model.Field;

/**
 * Reusable metadata of a physical table. Context-dependent data such as alias,
 * extraction conditions and conflict policies must never be stored here.
 */
public final class PhysicalTableConfiguration {

	private final PhysicalTableIdentity identity;
	private List<PhysicalColumnMetadata> fields;
	private boolean primaryKeyLoaded;
	private PhysicalKeyMetadata primaryKey;
	private boolean uniqueKeysLoaded;
	private List<PhysicalKeyMetadata> uniqueKeys;
	private boolean importedForeignKeysLoaded;
	private List<PhysicalForeignKeyMetadata> importedForeignKeys;

	public PhysicalTableConfiguration(PhysicalTableIdentity identity) {
		this.identity = identity;
	}

	public PhysicalTableIdentity getIdentity() {
		return identity;
	}

	public synchronized boolean hasFields() {
		return fields != null;
	}

	public synchronized void initializeFields(List<Field> loadedFields) {
		if (fields != null) return;

		List<PhysicalColumnMetadata> copies = new ArrayList<>(loadedFields.size());
		for (Field field : loadedFields) {
			copies.add(PhysicalColumnMetadata.fromField(field));
		}
		fields = Collections.unmodifiableList(copies);
	}

	/**
	 * Returns context-local copies because Field is enriched with contextual
	 * AttDefinedElements later in TableConfiguration.fullLoad().
	 */
	public synchronized List<Field> copyFields() {
		if (fields == null) return null;

		List<Field> copies = new ArrayList<>(fields.size());
		for (PhysicalColumnMetadata field : fields) {
			copies.add(field.toField());
		}
		return copies;
	}

	public synchronized boolean isPrimaryKeyLoaded() {
		return primaryKeyLoaded;
	}

	public synchronized void initializePrimaryKey(PrimaryKey loadedPrimaryKey) {
		if (primaryKeyLoaded) return;
		this.primaryKey = loadedPrimaryKey == null ? null : PhysicalKeyMetadata.fromKey(loadedPrimaryKey);
		this.primaryKeyLoaded = true;
	}

	public synchronized PrimaryKey copyPrimaryKey(TableConfiguration context) {
		return primaryKey == null ? null : primaryKey.toPrimaryKey(context);
	}

	public synchronized boolean areUniqueKeysLoaded() {
		return uniqueKeysLoaded;
	}

	public synchronized void initializeUniqueKeys(List<UniqueKeyInfo> loadedUniqueKeys) {
		if (uniqueKeysLoaded) return;
		if (loadedUniqueKeys == null) {
			this.uniqueKeys = null;
		} else {
			List<PhysicalKeyMetadata> metadata = new ArrayList<>(loadedUniqueKeys.size());
			for (UniqueKeyInfo key : loadedUniqueKeys) metadata.add(PhysicalKeyMetadata.fromKey(key));
			this.uniqueKeys = Collections.unmodifiableList(metadata);
		}
		this.uniqueKeysLoaded = true;
	}

	public synchronized List<UniqueKeyInfo> copyUniqueKeys(TableConfiguration context) {
		if (uniqueKeys == null) return null;
		List<UniqueKeyInfo> copies = new ArrayList<>(uniqueKeys.size());
		for (PhysicalKeyMetadata uniqueKey : uniqueKeys) copies.add(uniqueKey.toUniqueKey(context));
		return copies;
	}

	public synchronized boolean areImportedForeignKeysLoaded() {
		return importedForeignKeysLoaded;
	}

	public synchronized void initializeImportedForeignKeys(List<PhysicalForeignKeyMetadata> loadedForeignKeys) {
		if (importedForeignKeysLoaded) return;
		this.importedForeignKeys = loadedForeignKeys == null ? null
				: Collections.unmodifiableList(new ArrayList<>(loadedForeignKeys));
		this.importedForeignKeysLoaded = true;
	}

	public synchronized List<PhysicalForeignKeyMetadata> getImportedForeignKeys() {
		return importedForeignKeys;
	}

	public synchronized void initialize(PhysicalTableMetadata metadata) {
		if (!hasFields()) {
			List<Field> loadedFields = new ArrayList<>();
			for (PhysicalColumnMetadata column : metadata.getColumns()) loadedFields.add(column.toField());
			initializeFields(loadedFields);
		}
		if (!isPrimaryKeyLoaded()) {
			initializePrimaryKey(metadata.getPrimaryKey() == null ? null : metadata.getPrimaryKey().toPrimaryKey(null));
		}
		if (!areUniqueKeysLoaded()) {
			List<UniqueKeyInfo> loadedKeys = new ArrayList<>();
			for (PhysicalKeyMetadata key : metadata.getUniqueKeys()) loadedKeys.add(key.toUniqueKey(null));
			initializeUniqueKeys(loadedKeys);
		}
		if (!areImportedForeignKeysLoaded()) initializeImportedForeignKeys(metadata.getImportedForeignKeys());
	}
}
