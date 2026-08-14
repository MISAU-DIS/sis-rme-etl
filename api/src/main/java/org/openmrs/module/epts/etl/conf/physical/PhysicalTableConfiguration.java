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
	private List<Field> fields;
	private boolean primaryKeyLoaded;
	private PrimaryKey primaryKey;
	private boolean uniqueKeysLoaded;
	private List<UniqueKeyInfo> uniqueKeys;

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

		List<Field> copies = new ArrayList<>(loadedFields.size());
		for (Field field : loadedFields) {
			copies.add(field.cloneMe());
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
		for (Field field : fields) {
			copies.add(field.cloneMe());
		}
		return copies;
	}

	public synchronized boolean isPrimaryKeyLoaded() {
		return primaryKeyLoaded;
	}

	public synchronized void initializePrimaryKey(PrimaryKey loadedPrimaryKey) {
		if (primaryKeyLoaded) return;
		this.primaryKey = copyPrimaryKey(loadedPrimaryKey, null);
		this.primaryKeyLoaded = true;
	}

	public synchronized PrimaryKey copyPrimaryKey(TableConfiguration context) {
		return copyPrimaryKey(primaryKey, context);
	}

	private PrimaryKey copyPrimaryKey(PrimaryKey source, TableConfiguration context) {
		if (source == null) return null;
		PrimaryKey copy = new PrimaryKey();
		copy.copy(source);
		copy.setKeyName(source.getKeyName());
		copy.setManualConfigured(source.isManualConfigured());
		if (context != null) copy.setTabConf(context, false);
		return copy;
	}

	public synchronized boolean areUniqueKeysLoaded() {
		return uniqueKeysLoaded;
	}

	public synchronized void initializeUniqueKeys(List<UniqueKeyInfo> loadedUniqueKeys) {
		if (uniqueKeysLoaded) return;
		this.uniqueKeys = copyUniqueKeys(loadedUniqueKeys, null);
		this.uniqueKeysLoaded = true;
	}

	public synchronized List<UniqueKeyInfo> copyUniqueKeys(TableConfiguration context) {
		return copyUniqueKeys(uniqueKeys, context);
	}

	private List<UniqueKeyInfo> copyUniqueKeys(List<UniqueKeyInfo> source, TableConfiguration context) {
		if (source == null) return null;
		List<UniqueKeyInfo> copies = new ArrayList<>(source.size());
		for (UniqueKeyInfo uniqueKey : source) {
			UniqueKeyInfo copy = uniqueKey.cloneMe();
			copy.setKeyName(uniqueKey.getKeyName());
			copy.setManualConfigured(uniqueKey.isManualConfigured());
			if (context != null) copy.setTabConf(context, false);
			copies.add(copy);
		}
		return copies;
	}
}
