package org.openmrs.module.epts.etl.conf.types;

/**
 * Defines how field mappings should be resolved for a destination configuration.
 * <p>
 * A destination configuration may contain mappings explicitly declared in the configuration file,
 * and the ETL engine may also infer additional mappings dynamically based on source and destination
 * field names.
 * </p>
 * <p>
 * This strategy controls whether manual mappings, automatically inferred mappings, or both should
 * be used during transformation.
 * </p>
 */
public enum FieldMappingResolutionStrategy {
	
	/**
	 * Uses only mappings explicitly declared in the destination configuration.
	 * <p>
	 * Automatically inferred mappings are ignored. This mode is recommended when the destination
	 * mapping must be strictly controlled by the configuration.
	 * </p>
	 */
	MANUAL_ONLY,
	
	/**
	 * Uses only mappings automatically inferred by the ETL engine.
	 * <p>
	 * Manually declared mappings are ignored. This mode may be useful for simple migrations where
	 * field names are consistent between source and destination and no custom transformation rules
	 * are required.
	 * </p>
	 */
	AUTO_ONLY,
	
	/**
	 * Uses manually declared mappings first, then automatically infers mappings for destination
	 * fields that were not manually mapped.
	 * <p>
	 * This is the default behavior and provides a balance between explicit configuration and
	 * automatic field resolution.
	 * </p>
	 */
	MANUAL_THEN_AUTO;
	
	public boolean manualOnly() {
		return this == MANUAL_ONLY;
	}
	
	public boolean autoOnly() {
		return this == AUTO_ONLY;
	}
	
	public boolean isManualThenOnly() {
		return this == MANUAL_THEN_AUTO;
	}
}
