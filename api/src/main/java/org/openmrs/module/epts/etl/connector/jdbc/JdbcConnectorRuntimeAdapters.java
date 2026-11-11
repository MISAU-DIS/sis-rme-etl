package org.openmrs.module.epts.etl.connector.jdbc;

import java.sql.Connection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.reader.AbstractConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.writer.AbstractConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWriterFactory;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/**
 * Adapters used only while JDBC-based internals migrate to connector contexts.
 */
public final class JdbcConnectorRuntimeAdapters {

	private JdbcConnectorRuntimeAdapters() {
	}

	public static ConnectorRecordReader<EtlDatabaseObject> reader(Connection connection) {
		return new AbstractConnectorRecordReader<EtlDatabaseObject>() {
			@Override
			public ConnectorResource getResource() {
				return null;
			}

			@Override
			public List<EtlDatabaseObject> read(ConnectorReadRequest<EtlDatabaseObject> request) {
				throw new UnsupportedOperationException("Compatibility reader cannot extract records");
			}

			@Override
			public <N> N unwrap(Class<N> nativeType) {
				return nativeType.cast(connection);
			}

			@Override
			public void finalizeCapability(BaseConfiguration openedFrom) {

				if (connection instanceof OpenConnection) {
					((OpenConnection) connection).finalizeConnection(openedFrom);
				}
			}

			@Override
			public void markAsSuccessifullyTerminected() {

				if (connection instanceof OpenConnection) {
					((OpenConnection) connection).markAsSuccessifullyTerminated();
				}
			}
		};
	}

	public static ConnectorRecordWriter<EtlDatabaseObject> writer(Connection connection) {
		return new AbstractConnectorRecordWriter<EtlDatabaseObject>() {
			@Override
			public ConnectorResource getResource() {
				return null;
			}

			@Override
			public EtlOperationResultHeader<EtlDatabaseObject> write(ConnectorWriteRequest<EtlDatabaseObject> request) {
				throw new UnsupportedOperationException("Compatibility writer cannot persist records");
			}

			@Override
			public <N> N unwrap(Class<N> nativeType) {
				return nativeType.cast(connection);
			}

			@Override
			public void finalizeCapability(BaseConfiguration openedFrom) {

				if (connection instanceof OpenConnection) {
					((OpenConnection) connection).finalizeConnection(openedFrom);
				}
			}

			@Override
			public void markAsSuccessifullyTerminected() {

				if (connection instanceof OpenConnection) {
					((OpenConnection) connection).markAsSuccessifullyTerminated();
				}
			}
		};
	}

	public static ConnectorRecordWritersAggregator writers(EtlItemConfiguration item, Connection connection) {
		Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers = new IdentityHashMap<>();
		Map<EtlDestinationConfiguration, ConnectorResource> resources = new IdentityHashMap<>();
		collectWriters(item, connection, writers, resources,
				java.util.Collections.newSetFromMap(new IdentityHashMap<>()));

		ConnectorRecordWritersAggregator ag = new ConnectorRecordWritersAggregator(writers, resources);

		ag.loadAggregatorToAvaliableCapabilities();

		return ag;
	}

	private static void collectWriters(EtlItemConfiguration item, Connection connection,
			Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers,
			Map<EtlDestinationConfiguration, ConnectorResource> resources, Set<EtlItemConfiguration> visited) {

		if (item == null || !visited.add(item)) {
			return;
		}

		if (item.getDestinations() != null) {

			for (EtlDestinationConfiguration destination : item.getDestinations()) {
				ConnectorResource resource = item.getResolvedDstResource(destination);
				writers.put(resource, ConnectorRecordWriterFactory.create(resource, connection));
				resources.put(destination, resource);
			}
		}

		if (item.getChildItemConf() != null) {

			for (EtlItemConfiguration child : item.getChildItemConf()) {
				collectWriters(child, connection, writers, resources, visited);
			}
		}
	}
}
