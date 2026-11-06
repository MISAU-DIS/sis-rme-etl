package org.openmrs.module.epts.etl.utilities.db.conn;

import java.lang.reflect.Proxy;
import java.sql.Connection;

import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;

/** Creates inert connection identities for connector-scope unit tests. */
public final class OpenConnectionTestFactory {

	private OpenConnectionTestFactory() {
	}

	public static OpenConnection create(BaseConfiguration owner) {
		Connection connection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, method, args) -> defaultValue(method.getReturnType()));
		return new OpenConnection(connection, owner, null);
	}

	private static Object defaultValue(Class<?> type) {
		if (!type.isPrimitive()) return null;
		if (type == boolean.class) return false;
		if (type == byte.class) return (byte) 0;
		if (type == short.class) return (short) 0;
		if (type == int.class) return 0;
		if (type == long.class) return 0L;
		if (type == float.class) return 0F;
		if (type == double.class) return 0D;
		if (type == char.class) return '\0';
		return null;
	}
}
