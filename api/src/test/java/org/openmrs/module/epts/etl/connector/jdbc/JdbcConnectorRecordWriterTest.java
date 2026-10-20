package org.openmrs.module.epts.etl.connector.jdbc;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriterFactory;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

class JdbcConnectorRecordWriterTest {

	@Test
	void shouldBeAssociatedWithExactlyOneResource() {
		Connection connection = connectionMarker();
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
		Connection connection = connectionMarker();
		DstConf destination = new DstConf();
		destination.setTableName("patient");
		JdbcTableResource resource = new JdbcTableResource(destination, ConnectorDirection.DESTINATION,
				new DBConnectionInfo());
		ConnectorRecordWriter<GenericDatabaseObject> writer = new JdbcConnectorRecordWriter<>(resource, connection);
		Map<ConnectorResource, ConnectorRecordWriter<? extends org.openmrs.module.epts.etl.model.EtlDatabaseObject>> writers = new IdentityHashMap<>();
		writers.put(resource, writer);
		Map<DstConf, ConnectorResource> resources = new IdentityHashMap<>();
		resources.put(destination, resource);

		ConnectorRecordWriters aggregate = new ConnectorRecordWriters(writers, resources);

		assertSame(writer, aggregate.get(resource));
		assertSame(writer, aggregate.get(destination));
		assertSame(connection, aggregate.unwrap(resource, Connection.class));
	}

	private JdbcTableResource resource(ConnectorDirection direction, String name) {
		return new JdbcTableResource(new GenericTableConfiguration(name), direction, new DBConnectionInfo());
	}

	private Connection connectionMarker() {
		return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Connection.class },
				(proxy, method, args) -> null);
	}
}
