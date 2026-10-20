package org.openmrs.module.epts.etl.connector;

/** Persistence operation requested from a destination connector. */
public enum ConnectorWriteMode {
	INSERT,
	UPDATE,
	UPDATE_ALL,
	DELETE_ALL
}
