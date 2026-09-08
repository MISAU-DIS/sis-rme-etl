package org.openmrs.module.epts.etl.conf;

/** Identifies how a table configuration obtained its current schema metadata. */
public enum SchemaMetadataLoadSource {

	NOT_LOADED,

	STATIC_DATA,

	JDBC
}
