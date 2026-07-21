package org.openmrs.module.epts.etl.utilities.db.conn;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;

public class ConnectionKeepAliveManager {

	private static final Object LOCK = new Object();

	private static ConnectionKeepAliveManager INSTANCE;

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private final ConcurrentMap<ConnectionKeepAlive, Lock> connections = new ConcurrentHashMap<>();

	public ConnectionKeepAliveManager() {
		scheduler.scheduleWithFixedDelay(this::pingConnections, 4, 4, TimeUnit.MINUTES);
	}

	public ConnectionKeepAlive register(Connection conn, Lock lock, EtlProcessor processor) {
		ConnectionKeepAlive cka = new ConnectionKeepAlive(conn, lock, 600, processor);

		connections.put(cka, lock);

		return cka;
	}

	public void unregister(ConnectionKeepAlive cka) {
		connections.remove(cka);
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