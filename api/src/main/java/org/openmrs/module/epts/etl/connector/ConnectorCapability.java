package org.openmrs.module.epts.etl.connector;

/**
 * Behaviour that a connector can explicitly advertise to the ETL runtime.
 * Operations must depend on capabilities, rather than infer behaviour from a
 * connector implementation class.
 */
public enum ConnectorCapability {
	READ,
	WRITE,
	BATCH_READ,
	BATCH_WRITE,
	TRANSACTIONS,
	SCHEMA_DISCOVERY,
	RELATIONAL_KEYS,
	FILTERING,
	ORDERING,
	PAGINATION,
	ACKNOWLEDGEMENT
}
