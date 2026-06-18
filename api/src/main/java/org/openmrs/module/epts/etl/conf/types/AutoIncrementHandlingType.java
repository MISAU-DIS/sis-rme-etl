package org.openmrs.module.epts.etl.conf.types;

public enum AutoIncrementHandlingType {
	// @formatter:off
	AS_SCHEMA_DEFINED,
	IGNORE_SCHEMA_DEFINITION;
	// @formatter:on

	public boolean isAsSchemaDefined() {
		return this.equals(AS_SCHEMA_DEFINED);
	}

	public boolean isIgnoreSchemaDefinition() {
		return this.equals(IGNORE_SCHEMA_DEFINITION);
	}

}
