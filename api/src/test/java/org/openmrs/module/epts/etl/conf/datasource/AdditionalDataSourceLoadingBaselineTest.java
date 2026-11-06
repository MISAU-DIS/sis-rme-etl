package org.openmrs.module.epts.etl.conf.datasource;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRuntimeAdapters;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.GenericDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Verifies that JDBC additional sources use the connection exposed by their
 * own connector reader.
 */
class AdditionalDataSourceLoadingBaselineTest {

	@Test
	void tableDataSourceUsesTheConnectionSuppliedByItsCaller() throws Exception {
		Connection connection = connectionMarker();
		GenericDatabaseObject expected = new GenericDatabaseObject();
		TrackingPreparedQuery query = new TrackingPreparedQuery(expected);
		TableDataSourceConfig dataSource = new TrackingTableDataSource();
		dataSource.setDefaultPreparedQuery(query);

		EtlDatabaseObject loaded = dataSource.loadRelatedSrcObject(null, null, null,
				Collections.emptyList(), JdbcConnectorRuntimeAdapters.reader(connection));

		assertSame(connection, query.connection);
		assertSame(expected, loaded);
	}

	@Test
	void queryDataSourceUsesTheConnectionSuppliedByItsCaller() throws Exception {
		Connection connection = connectionMarker();
		GenericDatabaseObject expected = new GenericDatabaseObject();
		TrackingPreparedQuery query = new TrackingPreparedQuery(expected);
		QueryDataSourceConfig dataSource = new TrackingQueryDataSource();
		dataSource.setQuery("select 1");
		dataSource.setDefaultPreparedQuery(query);

		EtlDatabaseObject loaded = dataSource.loadRelatedSrcObject(null, null, null, null,
				JdbcConnectorRuntimeAdapters.reader(connection));

		assertSame(connection, query.connection);
		assertSame(expected, loaded);
	}

	private static Connection connectionMarker() {
		return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, method, args) -> {
					Class<?> type = method.getReturnType();
					if (!type.isPrimitive()) {
						return null;
					}
					if (type == boolean.class) {
						return false;
					}
					if (type == char.class) {
						return '\0';
					}
					return 0;
				});
	}

	private static final class TrackingPreparedQuery extends PreparedQuery {

		private final EtlDatabaseObject result;
		private Connection connection;

		private TrackingPreparedQuery(EtlDatabaseObject result) {
			this.result = result;
		}

		@Override
		public List<EtlDatabaseObject> query(EtlConfiguration configuration, EtlProcessor processor,
				EtlDatabaseObject srcObject, EtlDatabaseObject dstObject,
				List<EtlDatabaseObject> availableSrcObjects, Connection connection) throws DBException {
			this.connection = connection;
			return Collections.singletonList(result);
		}
	}

	private static final class TrackingTableDataSource extends TableDataSourceConfig {
		@Override
		public EtlConfiguration getRelatedEtlConf() {
			return null;
		}
	}

	private static final class TrackingQueryDataSource extends QueryDataSourceConfig {
		@Override
		public EtlConfiguration getRelatedEtlConf() {
			return null;
		}
	}
}
