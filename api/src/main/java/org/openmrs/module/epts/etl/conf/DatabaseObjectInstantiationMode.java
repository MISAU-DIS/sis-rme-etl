package org.openmrs.module.epts.etl.conf;

public enum DatabaseObjectInstantiationMode {

	/**
	 * Database objects are created dynamically at runtime from the table schema
	 * using the generic database object implementation.
	 */
	DYNAMIC_GENERIC,

	/**
	 * Database objects are created using pre-generated and pre-compiled
	 * table-specific POJO classes.
	 */
	PRECOMPILED_POJO;

	public boolean isDynamic() {
		return this == DYNAMIC_GENERIC;
	}

	public boolean isPreCompiled() {
		return this == PRECOMPILED_POJO;
	}
}
