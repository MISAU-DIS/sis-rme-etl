package org.openmrs.module.epts.etl.connector.jdbc;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWriterFactory;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnectionTestFactory;

class JdbcConnectorRecordWriterTest {

	@Test
	void shouldBeAssociatedWithExactlyOneResource() {
		OpenConnection connection = connectionMarker();
		JdbcTableResource resource = resource(ConnectorDirection.DESTINATION, "patient");
		ConnectorRecordWriter<GenericDatabaseObject> writer = new JdbcConnectorRecordWriter<>(resource, connection);

		assertSame(connection, writer.unwrap(Connection.class));
		assertSame(resource, writer.getResource());
	}

	@Test
	void shouldRejectSourceResourceForDestinationWriter() {
		assertThrows(IllegalArgumentException.class, () -> ConnectorRecordWriterFactory.create(
				resource(ConnectorDirection.SOURCE, "patient"), connectionMarker()));
	}

	@Test
	void shouldResolveWriterByResourceAndDestinationConfiguration() {
		OpenConnection connection = connectionMarker();
		EtlDestinationConfiguration destination = new EtlDestinationConfiguration();
		JdbcTableDestinationConfiguration table = new JdbcTableDestinationConfiguration("patient");
		destination.setTableDataDestination(table);
		JdbcTableResource resource = new JdbcTableResource(table, ConnectorDirection.DESTINATION,
				new DBConnectionInfo());
		ConnectorRecordWriter<GenericDatabaseObject> writer = new JdbcConnectorRecordWriter<>(resource, connection);
		Map<ConnectorResource, ConnectorRecordWriter<? extends org.openmrs.module.epts.etl.model.EtlDatabaseObject>> writers = new IdentityHashMap<>();
		writers.put(resource, writer);
		Map<EtlDestinationConfiguration, ConnectorResource> resources = new IdentityHashMap<>();
		resources.put(destination, resource);

		ConnectorRecordWritersAggregator aggregate = new ConnectorRecordWritersAggregator(writers, resources);

		assertSame(writer, aggregate.get(resource));
		assertSame(writer, aggregate.get(destination));
		assertSame(connection, aggregate.unwrap(resource, Connection.class));
	}

	private JdbcTableResource resource(ConnectorDirection direction, String name) {
		return new JdbcTableResource(new GenericTableConfiguration(name), direction, new DBConnectionInfo());
	}

	private OpenConnection connectionMarker() {
		return OpenConnectionTestFactory.create(new GenericTableConfiguration("connection_owner"));
	}
}
