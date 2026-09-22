package org.openmrs.module.epts.etl.utilities.db.conn;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;

/** JDBC fault injection without a running database; uses the real finalization code. */
public class RecordingConnection extends OpenConnection {

	public static class State {
		public boolean closed;
		public boolean committed;
		public boolean rolledBack;
		public boolean failCommit;
		public boolean failClose;
		public boolean autoCommit;
	}

	public final State state;

	public RecordingConnection(BaseConfiguration owner, String name, List<String> events, State state) {
		super(jdbc(name, events, state), owner,
				DBConnectionService.init("org.postgresql.Driver", "jdbc:postgresql://localhost/unused", "unused", "unused"));
		this.state = state;
	}

	private static Connection jdbc(String name, List<String> events, State state) {
		return (Connection) Proxy.newProxyInstance(RecordingConnection.class.getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, method, args) -> {
					switch (method.getName()) {
						case "getAutoCommit": return state.autoCommit;
						case "isClosed": return state.closed;
						case "commit":
							events.add(name + ":commit");
							if (state.failCommit) throw new SQLException(name + " commit failed");
							state.committed = true;
							return null;
						case "rollback":
							events.add(name + ":rollback");
							state.rolledBack = true;
							return null;
						case "close":
							events.add(name + ":close");
							state.closed = true;
							if (state.failClose) throw new SQLException(name + " close failed");
							return null;
						default: throw new AssertionError("Unexpected JDBC call: " + method.getName());
					}
				});
	}
}
