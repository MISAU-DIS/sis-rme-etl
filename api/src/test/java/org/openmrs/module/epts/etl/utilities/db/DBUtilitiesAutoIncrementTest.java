package org.openmrs.module.epts.etl.utilities.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DBUtilitiesAutoIncrementTest {

	@Test
	public void shouldSetTheNativeMySqlAutoIncrementValue() throws Exception {
		List<String> statements = new ArrayList<>();

		DBUtilities.setMySQLAutoIncrementValue("openmrs.person", 1000, mysqlConnection(statements));

		assertEquals(1, statements.size());
		assertEquals("ALTER TABLE openmrs.person AUTO_INCREMENT = 1000", statements.get(0));
	}

	@Test
	public void shouldOnlyReportATableAsEmptyWhenNoRecordExists() throws Exception {
		assertEquals(true, DBUtilities.isTableEmpty("openmrs.person", tableContentConnection(false)));
		assertEquals(false, DBUtilities.isTableEmpty("openmrs.person", tableContentConnection(true)));
	}

	private Connection tableContentConnection(boolean hasRecord) {
		ResultSet resultSet = (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { ResultSet.class }, (proxy, method, args) -> {
					if ("next".equals(method.getName())) return hasRecord;
					return defaultValue(method.getReturnType());
				});

		PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { PreparedStatement.class }, (proxy, method, args) -> {
					if ("executeQuery".equals(method.getName())) return resultSet;
					return defaultValue(method.getReturnType());
				});

		return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Connection.class },
				(proxy, method, args) -> {
					if ("prepareStatement".equals(method.getName())) return statement;
					return defaultValue(method.getReturnType());
				});
	}

	private Connection mysqlConnection(List<String> statements) {
		DatabaseMetaData metadata = (DatabaseMetaData) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { DatabaseMetaData.class }, (proxy, method, args) -> {
					if ("getDriverName".equals(method.getName())) {
						return "MySQL Connector/J";
					}
					return defaultValue(method.getReturnType());
				});

		Statement statement = (Statement) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { Statement.class }, (proxy, method, args) -> {
					if ("addBatch".equals(method.getName())) {
						statements.add((String) args[0]);
						return null;
					}
					if ("executeBatch".equals(method.getName())) {
						return new int[statements.size()];
					}
					return defaultValue(method.getReturnType());
				});

		return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Connection.class },
				(proxy, method, args) -> {
					if ("getMetaData".equals(method.getName())) {
						return metadata;
					}
					if ("createStatement".equals(method.getName())) {
						return statement;
					}
					return defaultValue(method.getReturnType());
				});
	}

	private Object defaultValue(Class<?> type) {
		if (type == boolean.class) return false;
		if (type == int.class) return 0;
		if (type == long.class) return 0L;
		return null;
	}
}
