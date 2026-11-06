package org.openmrs.module.epts.etl.connector;

/** Role played by a configured resource in an ETL item. */
public enum ConnectorDirection {
	// @formatter:off
	SOURCE,
	DESTINATION;
	// @formatter:on

	public boolean isSource() {
		return this == SOURCE;
	}

	public boolean isDestination() {
		return this == DESTINATION;
	}
}
