package org.openmrs.module.epts.etl.utilities.db.conn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;

public final class ConnectionKeepAlive implements AutoCloseable {

	private final Connection connection;
	private final Lock connectionLock;
	private final int queryTimeoutSeconds;
	private final EtlProcessor processor;

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private volatile ScheduledFuture<?> future;

	ConnectionKeepAlive(Connection connection, Lock connectionLock, int queryTimeoutSeconds, EtlProcessor processor) {

		this.connection = connection;
		this.connectionLock = connectionLock;
		this.queryTimeoutSeconds = queryTimeoutSeconds;
		this.processor = processor;
	}

	void attach(ScheduledFuture<?> future) {

		if (this.future != null) {
			throw new IllegalStateException("Keep-alive task already attached");
		}

		this.future = Objects.requireNonNull(future, "Scheduled future cannot be null");
	}

	void pingSafely() {

		this.processor.logDebug("Pinging connection: " + this.connection);

		if (closed.get()) {
			return;
		}

		/*
		 * O keep-alive nunca deve atrasar o trabalho real.
		 */
		if (!connectionLock.tryLock()) {
			return;
		}

		try {
			if (closed.get() || connection.isClosed()) {
				return;
			}

			try (PreparedStatement statement = connection.prepareStatement("SELECT 1")) {

				statement.setQueryTimeout(queryTimeoutSeconds);

				statement.execute();
			}

		} catch (SQLException e) {
			processor.logError("Destination connection keep-alive failed: " + e.getMessage());

		} finally {
			connectionLock.unlock();
		}
	}

	@Override
	public void close() {

		if (!closed.compareAndSet(false, true)) {
			return;
		}

		ScheduledFuture<?> currentFuture = future;

		if (currentFuture != null) {
			/*
			 * Não interrompe um SELECT 1 que já tenha começado.
			 */
			currentFuture.cancel(false);
		}

		/*
		 * Depois de adquirir este lock, temos garantia de que nenhum ping anterior
		 * continua a utilizar a conexão.
		 */
		connectionLock.lock();

		try {
			// Apenas sincronização.
		} finally {
			connectionLock.unlock();
		}
	}
}