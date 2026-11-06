package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.EtlSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.JsonPrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SourceResourceConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReaders;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.connector.ResourceBoundConnectorRecordReader;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

class ChildConnectorScopeTest {

	@Test
	void shouldRegisterChildPrimarySourceInReaderScope() throws Exception {
		TestItem parent = new TestItem();
		JsonPrimaryDataSourceConfiguration parentPrimary = configureSource(parent, "parent", resource("parent"));

		EtlChildItemConfiguration child = new EtlChildItemConfiguration();
		JsonPrimaryDataSourceConfiguration childPrimary = configureSource(child, "child", resource("child"));
		parent.setChildItemConf(Collections.singletonList(child));

		ConnectorRecordReaders readers = parent.createSourceReaders(null, parent, null);

		assertSame(parent.getResolvedSrcResource(), readers.get(parentPrimary).getResource());
		assertSame(child.getResolvedSrcResource(), readers.get(childPrimary).getResource());
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

	private ConnectorResource resource(String name) {
		ConnectorEndpoint endpoint = new ConnectorEndpoint() {
			@Override public ConnectorType getType() { return ConnectorType.FILE; }
			@Override public String getUri() { return name + ".json"; }
			@Override public String getUserName() { return null; }
			@Override public String getPassword() { return null; }
			@Override public Set<ConnectorCapability> getCapabilities() { return Collections.emptySet(); }
			@Override public <T> T unwrap(Class<T> nativeType) { throw new IllegalArgumentException(); }
		};

		return new ConnectorResource() {
			@Override public String getName() { return name; }
			@Override public ConnectorDirection getDirection() { return ConnectorDirection.SOURCE; }
			@Override public ConnectorEndpoint getEndpoint() { return endpoint; }
			@Override public <T> T unwrap(Class<T> nativeType) { throw new IllegalArgumentException(); }
		};
	}

	private static class TestItem extends EtlItemConfiguration {
		@Override
		@SuppressWarnings("unchecked")
		public <T extends EtlDatabaseObject> ConnectorRecordReader<T> createSourceReader(
				AbstractEtlSearchParams<T> searchParams, BaseConfiguration openedFrom,
				ConnectorRecordWriters writers) throws DBException {
			return (ConnectorRecordReader<T>) new ResourceBoundConnectorRecordReader<EtlDatabaseObject>(
					getResolvedSrcResource(), null);
		}
	}
}
