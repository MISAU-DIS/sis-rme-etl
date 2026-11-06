package org.openmrs.module.epts.etl.conf.datasource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;

class PrimaryDataSourceSearchContractTest {

	@Test
	void shouldExposeOnlyReaderBasedSearchContracts() {
		assertReaderContract(EtlDataSource.class);
		assertReaderContract(PrimaryDataSourceConfiguration.class);
		assertReaderContract(JdbcTablePrimaryDataSourceConfiguration.class);
	}

	private void assertReaderContract(Class<?> type) {
		Method[] searchMethods = Arrays.stream(type.getMethods())
				.filter(method -> method.getName().equals("searchRecords"))
				.toArray(Method[]::new);

		assertTrue(searchMethods.length > 0);
		for (Method method : searchMethods) {
			Class<?> lastParameter = method.getParameterTypes()[method.getParameterCount() - 1];
			assertFalse(Connection.class.isAssignableFrom(lastParameter));
			assertTrue(ConnectorRecordReader.class.isAssignableFrom(lastParameter));
		}
	}
}
