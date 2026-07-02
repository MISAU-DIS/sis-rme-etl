package org.openmrs.module.epts.etl.conf.types;

/**
 * Defines the supported checks that can be executed against an ETL
 * configuration element.
 */
public enum EtlConfCheckType {

	/**
	 * Checks whether the referenced configuration exposes a given field.
	 */
	HAS_FIELD,

	/**
	 * Counts the number of records available for the referenced configuration.
	 */
	COUNT_RECORDS,

	/**
	 * Counts the number of fields or columns available in the referenced
	 * configuration.
	 */
	COUNT_FIELDS
}