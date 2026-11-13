package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.EtlSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlSide;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.capability.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.reader.ResourceBoundConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnectionTestFactory;

class ChildConnectorScopeTest {

	@Test
	void shouldRegisterChildPrimarySourceInReaderScope() throws Exception {
		TestItem parent = new TestItem();
		OpenConnection etlConnection = OpenConnectionTestFactory.create(parent);
		parent.setRelatedEtlConf(etlConfiguration(etlConnection));
		JsonPrimaryDataSourceConfiguration parentPrimary = configureSource(parent, "parent", resource("parent"));

		EtlChildItemConfiguration child = new EtlChildItemConfiguration();
		JsonPrimaryDataSourceConfiguration childPrimary = configureSource(child, "child", resource("child"));
		parent.setChildItemConf(Collections.singletonList(child));

		ConnectorRecordReadersAggregator readers = parent.createSourceReaders(null, parent, null);

		assertSame(parent.getResolvedSrcResource(), readers.get(parentPrimary).getResource());
		assertSame(child.getResolvedSrcResource(), readers.get(childPrimary).getResource());
	}

	@Test
	void shouldShareOneOpenConnectionForSourcesUsingTheSameConnector() throws Exception {
		ConnectorEndpoint sharedConnector = endpoint(ConnectorType.JDBC, "jdbc:test:shared");
		TestItem parent = new TestItem();
		OpenConnection sharedConnection = OpenConnectionTestFactory.create(parent);
		parent.setPrimaryConnection(sharedConnection);
		parent.setRelatedEtlConf(etlConfiguration(sharedConnection));

		configureSource(parent, "parent", resource("parent", sharedConnector));
		EtlChildItemConfiguration child = new EtlChildItemConfiguration();
		JsonPrimaryDataSourceConfiguration childPrimary = configureSource(child, "child",
				resource("child", sharedConnector));
		parent.setChildItemConf(Collections.singletonList(child));

		ConnectorRecordReadersAggregator readers = parent.createSourceReaders(null, parent, null);

		assertSame(sharedConnection, readers.get(childPrimary).unwrap(OpenConnection.class));
	}

	private JsonPrimaryDataSourceConfiguration configureSource(EtlItemConfiguration item, String name,
			ConnectorResource resource) {
		JsonPrimaryDataSourceConfiguration primary = new JsonPrimaryDataSourceConfiguration();
		primary.setName(name);
		primary.setFields(Collections.singletonList(Field.fastCreateField("id")));
		EtlSourceConfiguration source = new EtlSourceConfiguration();
		source.setJsonDataSource(primary);
		SourceResourceConfiguration configuredResource = new SourceResourceConfiguration();
		configuredResource.setConnector("file");
		configuredResource.setName(name + ".json");
		source.setResource(configuredResource);
		source.initialize(item);
		source.setResolvedResource(resource);
		item.setSource(source);
		return primary;
	}

	private EtlConfiguration etlConfiguration(OpenConnection etlConnection) {
		return new EtlConfiguration() {
			@Override
			public OpenConnection openSrcConn(BaseConfiguration openedFrom) {
				return etlConnection;
			}

			@Override
			OpenConnection openSourceConnectorConnection(ConnectorResource resource, BaseConfiguration openedFrom) {
				throw new AssertionError("The shared connector must not open a second connection");
			}
		};
	}

	private ConnectorResource resource(String name) {
		return resource(name, endpoint(ConnectorType.FILE, name + ".json"));
	}

	private ConnectorEndpoint endpoint(ConnectorType type, String uri) {
		return new ConnectorEndpoint() {
			@Override
			public ConnectorType getType() {
				return type;
			}

			@Override
			public String getUri() {
				return uri;
			}

			@Override
			public String getUserName() {
				return null;
			}

			@Override
			public String getPassword() {
				return null;
			}

			@Override
			public Set<ConnectorCapability> getCapabilities() {
				return Collections.singleton(ConnectorCapability.READ);
			}

			@Override
			public <T> T unwrap(Class<T> nativeType) {
				throw new IllegalArgumentException();
			}
		};
	}

	private ConnectorResource resource(String name, ConnectorEndpoint endpoint) {
		return new ConnectorResource() {
			@Override
			public String getName() {
				return name;
			}

			@Override
			public EtlSide getDirection() {
				return EtlSide.SRC;
			}

			@Override
			public ConnectorEndpoint getEndpoint() {
				return endpoint;
			}

			@Override
			public <T> T unwrap(Class<T> nativeType) {
				throw new IllegalArgumentException();
			}
		};
	}

	private static class TestItem extends EtlItemConfiguration {
		private OpenConnection primaryConnection;

		void setPrimaryConnection(OpenConnection primaryConnection) {
			this.primaryConnection = primaryConnection;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends EtlDatabaseObject> ConnectorRecordReader<T> createSourceReader(
				AbstractEtlSearchParams<T> searchParams, BaseConfiguration openedFrom, OpenConnection etlSrcConn,
				ConnectorRecordWritersAggregator writers) throws DBException {
			return (ConnectorRecordReader<T>) new ResourceBoundConnectorRecordReader<EtlDatabaseObject>(
					getResolvedSrcResource(), primaryConnection);
		}
	}
}
