package org.openmrs.module.epts.etl.conf.datasource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;

class PrimaryDataSourceSearchContractTest {

	@Test
	void shouldDelegateJdbcSearchToConnectorReader() throws Exception {
		JdbcTablePrimaryDataSourceConfiguration source = new JdbcTablePrimaryDataSourceConfiguration();
		EtlDatabaseObject parent = new GenericDatabaseObject();
		List<EtlDatabaseObject> auxiliary = Collections.singletonList(new GenericDatabaseObject());
		List<EtlDatabaseObject> expected = Collections.singletonList(new GenericDatabaseObject());
		AtomicReference<ConnectorReadRequest<EtlDatabaseObject>> received = new AtomicReference<>();

		ConnectorRecordReader<EtlDatabaseObject> reader = new ConnectorRecordReader<EtlDatabaseObject>() {
			@Override public ConnectorResource getResource() { return null; }
			@Override public List<EtlDatabaseObject> read(ConnectorReadRequest<EtlDatabaseObject> request) {
				received.set(request);
				return expected;
			}
			@Override public <N> N unwrap(Class<N> nativeType) {
				throw new AssertionError("The primary datasource must search through reader.read()");
			}
		};

		List<EtlDatabaseObject> actual = source.searchRecords(null, parent, auxiliary, reader);

		assertSame(expected, actual);
		assertSame(parent, received.get().getParentObject());
		assertSame(auxiliary, received.get().getAuxiliarySourceObjects());
		assertFalse(received.get().isParallel());
	}
}
