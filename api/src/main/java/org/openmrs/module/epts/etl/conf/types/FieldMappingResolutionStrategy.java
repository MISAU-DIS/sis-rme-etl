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
	 * Uses manually configured mappings and automatically assigns default values to destination
	 * fields that were not explicitly mapped.
	 * <p>
	 * The ETL engine applies all mappings defined in the {@code mapping} section. After that, any
	 * remaining destination fields that do not have a mapped value are populated using their
	 * configured default values.
	 * </p>
	 * <p>
	 * Default values may originate from:
	 * </p>
	 * <ul>
	 * <li>field-level default values;</li>
	 * <li>table-level default values defined in the destination schema;</li>
	 * <li>global ETL {@code defaultFieldValues} configuration, when applicable.</li>
	 * </ul>
	 * <p>
	 * No automatic field mapping is performed in this mode.
	 * </p>
	 */
	MANUAL_WITH_DEFAULTS,
	
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
	
	public boolean manualThenAuto() {
		return this == MANUAL_THEN_AUTO;
	}
	
	public boolean manualWithDefault() {
		return this == MANUAL_WITH_DEFAULTS;
	}
	
	public boolean allowAuto() {
		return manualThenAuto() || autoOnly();
	}
	
	public boolean allowManual() {
		return manualOnly() || manualThenAuto() || manualWithDefault();
	}
	
	public boolean allowDefault() {
		return manualWithDefault();
	}
	
	
	
}
