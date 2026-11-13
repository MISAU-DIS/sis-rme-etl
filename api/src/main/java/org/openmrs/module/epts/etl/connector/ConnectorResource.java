package org.openmrs.module.epts.etl.connector;

import org.openmrs.module.epts.etl.conf.types.EtlSide;

/**
 * Connector-neutral identity of the resource read or written by one ETL item. A
 * JDBC table, REST route, queue or file can all be represented by this contract
 * without leaking their technology into the item itself.
 */
public interface ConnectorResource {

	String getName();

	/** Logical identity; defaults to the physical resource name. */
	default String getAlias() {
		return getName();
	}

	EtlSide getDirection();

	ConnectorEndpoint getEndpoint();

	<T> T unwrap(Class<T> nativeType);
}
