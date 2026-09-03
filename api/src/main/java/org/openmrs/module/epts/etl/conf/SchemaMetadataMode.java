package org.openmrs.module.epts.etl.conf;

/** Selects where physical database metadata is resolved. */
public enum SchemaMetadataMode {
	LIVE_DATABASE,
	PRECOMPILED,
	PRECOMPILED_WITH_FALLBACK;

	public boolean usesFilesFirst() { return this != LIVE_DATABASE; }
	public boolean allowsJdbcFallback() { return this != PRECOMPILED; }
	public boolean isStrictlyPrecompiled() { return this == PRECOMPILED; }
}
