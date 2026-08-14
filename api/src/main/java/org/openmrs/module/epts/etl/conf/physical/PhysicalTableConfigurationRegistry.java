package org.openmrs.module.epts.etl.conf.physical;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Registry scoped to one ETL configuration. */
public final class PhysicalTableConfigurationRegistry {

	private final ConcurrentMap<PhysicalTableIdentity, PhysicalTableConfiguration> tables = new ConcurrentHashMap<>();

	public PhysicalTableConfiguration getOrCreate(PhysicalTableIdentity identity) {
		PhysicalTableConfiguration existing = tables.get(identity);
		if (existing != null) return existing;

		PhysicalTableConfiguration created = new PhysicalTableConfiguration(identity);
		PhysicalTableConfiguration raced = tables.putIfAbsent(identity, created);
		return raced != null ? raced : created;
	}

	public int size() {
		return tables.size();
	}

	public void clear() {
		tables.clear();
	}
}
