package org.openmrs.module.epts.etl.connector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.TransformableDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.reader.ResourceBoundConnectorRecordReader;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

class ConnectorRecordReadersAggregatorTest {

	@Test
	void shouldResolveReadersByConfigurationIdentity() {
		ConnectorResource primaryResource = resource("patient");
		ConnectorResource additionalResource = resource("person");
		JdbcTablePrimaryDataSourceConfiguration primary = new JdbcTablePrimaryDataSourceConfiguration();
		TransformableDataSource additional = new TransformableDataSource();
		Object primaryNativeState = new Object();
		Object additionalNativeState = new Object();
		ConnectorRecordReader<EtlDatabaseObject> primaryReader =
				new ResourceBoundConnectorRecordReader<>(primaryResource, primaryNativeState);
		ConnectorRecordReader<EtlDatabaseObject> additionalReader =
				new ResourceBoundConnectorRecordReader<>(additionalResource, additionalNativeState);

		Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers = new IdentityHashMap<>();
		readers.put(primaryResource, primaryReader);
		readers.put(additionalResource, additionalReader);
		Map<EtlDataSource, ConnectorResource> resources = new IdentityHashMap<>();
		resources.put(primary, primaryResource);
		resources.put(additional, additionalResource);

		ConnectorRecordReadersAggregator scope = new ConnectorRecordReadersAggregator(readers, resources);

		assertSame(primaryReader, scope.get(primary));
		assertSame(additionalReader, scope.get(additional));
		assertSame(additionalResource, scope.getResource(additional));
		assertEquals(2, scope.getAll().size());
		assertThrows(IllegalArgumentException.class, () -> scope.get(new TransformableDataSource()));
	}

	@Test
	void shouldReturnEachNativeStateOnlyOnceByIdentity() {
		ConnectorResource first = resource("first");
		ConnectorResource second = resource("second");
		Object sharedNativeState = new Object();
		Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers = new IdentityHashMap<>();
		readers.put(first, new ResourceBoundConnectorRecordReader<>(first, sharedNativeState));
		readers.put(second, new ResourceBoundConnectorRecordReader<>(second, sharedNativeState));

		ConnectorRecordReadersAggregator scope = new ConnectorRecordReadersAggregator(readers, Collections.emptyMap());

		assertEquals(1, scope.unwrapAll(Object.class).size());
		assertSame(sharedNativeState, scope.unwrapAll(Object.class).get(0));
	}

	private ConnectorResource resource(String name) {
		return new ConnectorResource() {
			@Override public String getName() { return name; }
			@Override public ConnectorDirection getDirection() { return ConnectorDirection.SOURCE; }
			@Override public ConnectorEndpoint getEndpoint() { return null; }
			@Override public <T> T unwrap(Class<T> nativeType) {
				throw new IllegalArgumentException("No native resource state");
			}
			@Override public String toString() { return name; }
		};
	}
}
