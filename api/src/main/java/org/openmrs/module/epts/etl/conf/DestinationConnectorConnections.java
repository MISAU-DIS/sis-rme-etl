package org.openmrs.module.epts.etl.conf;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/**
 * Lazily opens and owns the JDBC connections required by the destinations of
 * one ETL item execution.
 * <p>
 * Destinations resolved against the same connector endpoint share one
 * connection. Non-JDBC destinations remain registered in the aggregator, but
 * never cause a database connection to be opened.
 */
public final class DestinationConnectorConnections implements AutoCloseable {

	private final EtlConfiguration etlConfiguration;

	private final BaseConfiguration openedFrom;

	private final Map<DstConf, ConnectorResource> resourcesByConfiguration;

	private final Map<ConnectorEndpoint, OpenConnection> connectionsByEndpoint = new IdentityHashMap<>();

	private boolean closed;

	DestinationConnectorConnections(EtlConfiguration etlConfiguration, List<DstConf> destinations,
	        BaseConfiguration openedFrom) {
		if (etlConfiguration == null) {
			throw new IllegalArgumentException("The related ETL configuration is required");
		}
		if (openedFrom == null) {
			throw new IllegalArgumentException("The connection owner is required");
		}
		this.etlConfiguration = etlConfiguration;
		this.openedFrom = openedFrom;
		Map<DstConf, ConnectorResource> resources = new IdentityHashMap<>();
		if (destinations != null) {
			for (DstConf destination : destinations) {
				if (destination == null || destination.getResolvedResource() == null) {
					throw new IllegalStateException("Every destination must have an initialized connector resource");
				}
				resources.put(destination, destination.getResolvedResource());
			}
		}
		this.resourcesByConfiguration = Collections.unmodifiableMap(resources);
	}

	public ConnectorResource getResource(DstConf destination) {
		ConnectorResource resource = resourcesByConfiguration.get(destination);
		if (resource == null) {
			throw new IllegalArgumentException("No destination resource registered for " + destination);
		}
		return resource;
	}

	public boolean isJdbc(DstConf destination) {
		return getResource(destination).getEndpoint().getType() == ConnectorType.JDBC;
	}

	/**
	 * Returns the connection for a JDBC destination, opening it on first use.
	 */
	public synchronized OpenConnection get(DstConf destination) throws DBException {
		ensureOpen();
		ConnectorResource resource = getResource(destination);
		if (resource.getEndpoint().getType() != ConnectorType.JDBC) {
			throw new ForbiddenOperationException(
			        "Destination resource " + resource.getName() + " is not backed by a JDBC connector");
		}
		ConnectorEndpoint endpoint = resource.getEndpoint();
		OpenConnection connection = connectionsByEndpoint.get(endpoint);
		if (connection == null) {
			connection = etlConfiguration.openDestinationConnectorConnection(resource, openedFrom);
			connectionsByEndpoint.put(endpoint, connection);
		}
		return connection;
	}

	/** Returns the lazy JDBC connection, or {@code null} for a non-JDBC destination. */
	public Connection getIfJdbc(DstConf destination) throws DBException {
		return isJdbc(destination) ? get(destination) : null;
	}

	public synchronized List<OpenConnection> getOpenedConnections() {
		return Collections.unmodifiableList(new ArrayList<>(connectionsByEndpoint.values()));
	}

	public synchronized void markAllAsSuccessfullyTerminated() {
		OpenConnection.markAllAsSuccessifullyTerminected(new ArrayList<>(connectionsByEndpoint.values()));
	}

	/**
	 * Finalizes every connection opened by this aggregator. Unless
	 * {@link #markAllAsSuccessfullyTerminated()} was called first, the existing
	 * {@link OpenConnection} rollback semantics are preserved.
	 */
	@Override
	public synchronized void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
			OpenConnection.finalizeAllConnections(new ArrayList<>(connectionsByEndpoint.values()), openedFrom);
		} finally {
			connectionsByEndpoint.clear();
		}
	}

	private void ensureOpen() {
		if (closed) {
			throw new IllegalStateException("Destination connector connections have already been finalized");
		}
	}
}
