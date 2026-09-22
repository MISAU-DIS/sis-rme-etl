package org.openmrs.module.epts.etl.utilities.db.conn;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;

public class ConnectionKeepAliveManager implements AutoCloseable {

	private static final Object LOCK = new Object();

	private static volatile ConnectionKeepAliveManager INSTANCE;

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(task -> {
		Thread thread = new Thread(task, "etl-connection-keep-alive");
		thread.setDaemon(true);
		return thread;
	});

	private boolean closed;

	private final ConcurrentMap<ConnectionKeepAlive, Lock> connections = new ConcurrentHashMap<>();

	public ConnectionKeepAliveManager() {
		scheduler.scheduleWithFixedDelay(this::pingConnections, 4, 4, TimeUnit.MINUTES);
	}

	public synchronized ConnectionKeepAlive register(Connection conn, Lock lock, EtlProcessor processor) {
		if (closed) {
			throw new IllegalStateException("Keep-alive manager is closed");
		}
		// Keep the public signature for compatibility, but do not retain the processor.
		ConnectionKeepAlive cka = new ConnectionKeepAlive(conn, lock, 600, this);

		connections.put(cka, lock);

		return cka;
	}

	public void unregister(ConnectionKeepAlive cka) {
		connections.remove(cka);
	}

	@Override
	public void close() {
		List<ConnectionKeepAlive> registrations;
		synchronized (this) {
			if (closed) {
				return;
			}
			closed = true;
			registrations = new ArrayList<>(connections.keySet());
		}
		// Do not interrupt JDBC calls already in progress; registration.close() waits for them.
		scheduler.shutdown();
		for (ConnectionKeepAlive registration : registrations) {
			registration.close();
		}
	}

	private void pingConnections() {

		for (Map.Entry<ConnectionKeepAlive, Lock> e : connections.entrySet()) {
			e.getKey().pingSafely();
		}
	}

	public static ConnectionKeepAliveManager getInstance() {
		if (INSTANCE != null)
			return INSTANCE;

		synchronized (LOCK) {
			if (INSTANCE != null)
				return INSTANCE;

			INSTANCE = new ConnectionKeepAliveManager();
		}

		return INSTANCE;
	}
}
