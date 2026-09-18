package org.openmrs.module.epts.etl.utilities.db.conn;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

public class ConnectionKeepAliveLifecycleTest {
	@Test
	public void completedBatchesMustNotAccumulateInManager() throws Exception {
		try (ConnectionKeepAliveManager manager = new ConnectionKeepAliveManager()) {
			for (int i = 0; i < 1000; i++) {
				try (ConnectionKeepAlive registration = manager.register(null, new ReentrantLock(), null)) {
					assertEquals(1, registrations(manager).size());
				}
				assertTrue(registrations(manager).isEmpty());
			}
		}
	}

	@Test
	public void failedBatchMustAlsoReleaseRegistration() throws Exception {
		try (ConnectionKeepAliveManager manager = new ConnectionKeepAliveManager()) {
			RuntimeException failure = new IllegalStateException("transformation failed");
			RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
				try (ConnectionKeepAlive registration = manager.register(null, new ReentrantLock(), null)) {
					throw failure;
				}
			});
			assertSame(failure, thrown);
			assertTrue(registrations(manager).isEmpty());
		}
	}

	@Test
	public void closingOneRegistrationMustPreserveOtherRegistrations() throws Exception {
		try (ConnectionKeepAliveManager manager = new ConnectionKeepAliveManager()) {
			ConnectionKeepAlive first = manager.register(null, new ReentrantLock(), null);
			ConnectionKeepAlive second = manager.register(null, new ReentrantLock(), null);
			first.close();
			first.close();
			assertEquals(1, registrations(manager).size());
			assertTrue(registrations(manager).containsKey(second));
			second.close();
			assertTrue(registrations(manager).isEmpty());
		}
	}

	@Test
	public void managerCloseMustReleaseRegistrationsAndStopScheduler() throws Exception {
		try (ConnectionKeepAliveManager manager = new ConnectionKeepAliveManager()) {
			manager.register(null, new ReentrantLock(), null);
			manager.register(null, new ReentrantLock(), null);
			manager.close();
			manager.close();
			assertTrue(registrations(manager).isEmpty());
			assertTrue(scheduler(manager).isShutdown());
			assertTrue(scheduler(manager).awaitTermination(5, TimeUnit.SECONDS));
			assertThrows(IllegalStateException.class,
					() -> manager.register(null, new ReentrantLock(), null));
		}
	}

	@Test(timeout = 10000)
	public void concurrentBatchesMustLeaveNoRegistrationsBehind() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		try (ConnectionKeepAliveManager manager = new ConnectionKeepAliveManager()) {
			List<Future<?>> batches = new ArrayList<>();
			for (int worker = 0; worker < 4; worker++) {
				batches.add(executor.submit(() -> {
					for (int batch = 0; batch < 250; batch++) {
						try (ConnectionKeepAlive registration = manager.register(null, new ReentrantLock(), null)) {
							// A batch owns its registration only for this scope.
						}
					}
				}));
			}
			for (Future<?> batch : batches) batch.get(5, TimeUnit.SECONDS);
			assertTrue(registrations(manager).isEmpty());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test(timeout = 10000)
	public void closeMustWaitForActivePingAndPreventFurtherPings() throws Exception {
		CountDownLatch pingStarted = new CountDownLatch(1);
		CountDownLatch releasePing = new CountDownLatch(1);
		CountDownLatch closeStarted = new CountDownLatch(1);
		AtomicInteger executions = new AtomicInteger();
		AtomicInteger statementsClosed = new AtomicInteger();
		PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { PreparedStatement.class }, (proxy, method, args) -> {
					switch (method.getName()) {
						case "setQueryTimeout": return null;
						case "execute":
							executions.incrementAndGet();
							pingStarted.countDown();
							if (!releasePing.await(5, TimeUnit.SECONDS)) throw new AssertionError("Ping not released");
							return true;
						case "close": statementsClosed.incrementAndGet(); return null;
						default: throw new AssertionError("Unexpected statement call: " + method.getName());
					}
				});
		Connection connection = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class<?>[] { Connection.class }, (proxy, method, args) -> {
					switch (method.getName()) {
						case "isClosed": return false;
						case "prepareStatement": return statement;
						case "toString": return "keep-alive test connection";
						default: throw new AssertionError("Keep-alive must not close the JDBC connection");
					}
				});
		ExecutorService executor = Executors.newFixedThreadPool(2);
		try (ConnectionKeepAliveManager manager = new ConnectionKeepAliveManager()) {
			ConnectionKeepAlive registration = manager.register(connection, new ReentrantLock(), null);
			Future<?> ping = executor.submit(registration::pingSafely);
			try {
				assertTrue(pingStarted.await(5, TimeUnit.SECONDS));
				Future<?> closing = executor.submit(() -> {
					closeStarted.countDown();
					registration.close();
				});
				assertTrue(closeStarted.await(5, TimeUnit.SECONDS));
				assertThrows(TimeoutException.class, () -> closing.get(100, TimeUnit.MILLISECONDS));
				releasePing.countDown();
				ping.get(5, TimeUnit.SECONDS);
				closing.get(5, TimeUnit.SECONDS);
				assertTrue(registrations(manager).isEmpty());
				registration.pingSafely();
				assertEquals(1, executions.get());
				assertEquals(1, statementsClosed.get());
			} finally {
				releasePing.countDown();
			}
		} finally {
			executor.shutdownNow();
		}
	}

	private static Map<?, ?> registrations(ConnectionKeepAliveManager manager) throws Exception {
		Field field = ConnectionKeepAliveManager.class.getDeclaredField("connections");
		field.setAccessible(true);
		return (Map<?, ?>) field.get(manager);
	}

	private static ScheduledExecutorService scheduler(ConnectionKeepAliveManager manager) throws Exception {
		Field field = ConnectionKeepAliveManager.class.getDeclaredField("scheduler");
		field.setAccessible(true);
		return (ScheduledExecutorService) field.get(manager);
	}
}
