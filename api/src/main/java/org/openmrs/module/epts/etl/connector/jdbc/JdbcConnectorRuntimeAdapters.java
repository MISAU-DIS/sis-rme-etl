package org.openmrs.module.epts.etl.connector.jdbc;

import java.sql.Connection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriterFactory;
import org.openmrs.module.epts.etl.connector.ConnectorWritesAggregator;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;

/**
 * Adapters used only while JDBC-based internals migrate to connector contexts.
 */
public final class JdbcConnectorRuntimeAdapters {

	private JdbcConnectorRuntimeAdapters() {
	}

	public static ConnectorRecordReader<EtlDatabaseObject> reader(Connection connection) {
		return new ConnectorRecordReader<EtlDatabaseObject>() {
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
		};
	}

	public static ConnectorRecordWriter<EtlDatabaseObject> writer(Connection connection) {
		return new ConnectorRecordWriter<EtlDatabaseObject>() {
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
		};
	}

	public static ConnectorWritesAggregator writers(EtlItemConfiguration item, Connection connection) {
		Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers = new IdentityHashMap<>();
		Map<EtlDestinationConfiguration, ConnectorResource> resources = new IdentityHashMap<>();
		collectWriters(item, connection, writers, resources,
				java.util.Collections.newSetFromMap(new IdentityHashMap<>()));
		return new ConnectorWritesAggregator(writers, resources);
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
