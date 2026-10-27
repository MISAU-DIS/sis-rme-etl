package org.openmrs.module.epts.etl.connector;

/** Identifies the technology used by a data endpoint. */
public enum ConnectorType {
	JDBC,
	REST,
	MESSAGE_QUEUE,
	FILE,
	CUSTOM
}
