package org.openmrs.module.epts.etl.connector;

/**
 * Connector-neutral identity of the resource read or written by one ETL item.
 * A JDBC table, REST route, queue or file can all be represented by this
 * contract without leaking their technology into the item itself.
 */
public interface ConnectorResource {

	String getName();

	ConnectorDirection getDirection();

	ConnectorEndpoint getEndpoint();

	<T> T unwrap(Class<T> nativeType);
}
