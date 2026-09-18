package org.openmrs.module.epts.etl.engine;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.junit.Test;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class ParallelSearchCompletionTest {
	@Test(timeout = 10000)
	public void mustNotReturnWhileAggregationIsStillRunning() throws Exception {
		CountDownLatch releaseSearch = new CountDownLatch(1);
		CountDownLatch aggregating = new CountDownLatch(1);
		CountDownLatch releaseAggregation = new CountDownLatch(1);
		EtlDatabaseObject record = record();
		List<EtlDatabaseObject> partition = new ArrayList<EtlDatabaseObject>(Collections.singletonList(record)) {
			@Override public Stream<EtlDatabaseObject> stream() {
				aggregating.countDown();
				await(releaseAggregation);
				return super.stream();
			}
		};
		TestSearch search = new TestSearch(limits -> {
			await(releaseSearch);
			return partition;
		});
		FutureTask<List<EtlDatabaseObject>> result = new FutureTask<>(() -> search.searchAll(1));
		Thread caller = new Thread(result, "parallel-search-caller");
		caller.start();
		try {
			// Ensure completion callbacks are registered before the worker completes.
			awaitWaiting(caller);
			releaseSearch.countDown();
			assertTrue(aggregating.await(5, TimeUnit.SECONDS));
			assertThrows(TimeoutException.class, () -> result.get(100, TimeUnit.MILLISECONDS));
			releaseAggregation.countDown();
			List<EtlDatabaseObject> records = result.get(5, TimeUnit.SECONDS);
			assertEquals(1, records.size());
			assertSame(record, records.get(0));
		} finally {
			releaseSearch.countDown();
			releaseAggregation.countDown();
			caller.join(1000);
		}
	}

	@Test(timeout = 10000)
	public void mustCollectEveryPartitionInIntervalOrder() throws Exception {
		List<EtlDatabaseObject> expected = new ArrayList<>();
		for (int i = 0; i < 64; i++) expected.add(record());
		TestSearch search = new TestSearch(limits -> new ArrayList<>(expected.subList(
				(int) limits.getMinRecordId() - 1, (int) limits.getMaxRecordId())));
		List<EtlDatabaseObject> actual = search.searchAll(expected.size());
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) assertSame(expected.get(i), actual.get(i));
	}

	@Test(timeout = 10000)
	public void emptyPartitionsMustProduceAnEmptyResult() throws Exception {
		assertTrue(new TestSearch(limits -> Collections.emptyList()).searchAll(64).isEmpty());
	}

	@Test(timeout = 10000)
	public void failedPartitionMustPropagateItsCauseInsteadOfReturningPartialResults() {
		DBException failure = new DBException(new SQLException("partition failed"));
		TestSearch search = new TestSearch(limits -> {
			if (limits.getMinRecordId() == 1) throw failure;
			return Collections.singletonList(record());
		});
		EtlExceptionImpl thrown = assertThrows(EtlExceptionImpl.class, () -> search.searchAll(64));
		assertHasCause(thrown, failure);
	}

	@Test(timeout = 10000)
	public void aggregationFailureMustBeReportedToCaller() {
		IllegalStateException failure = new IllegalStateException("aggregation failed");
		List<EtlDatabaseObject> brokenPartition = new ArrayList<EtlDatabaseObject>() {
			@Override public Stream<EtlDatabaseObject> stream() { throw failure; }
		};
		TestSearch search = new TestSearch(limits -> brokenPartition);
		EtlExceptionImpl thrown = assertThrows(EtlExceptionImpl.class, () -> search.searchAll(1));
		assertHasCause(thrown, failure);
	}

	@Test(timeout = 10000)
	public void interruptionMustPreserveTheCallingThreadsInterruptFlag() throws Exception {
		CountDownLatch releaseSearch = new CountDownLatch(1);
		CountDownLatch workerFinished = new CountDownLatch(1);
		TestSearch search = new TestSearch(limits -> {
			try {
				await(releaseSearch);
				return Collections.emptyList();
			} finally {
				workerFinished.countDown();
			}
		});
		FutureTask<Boolean> result = new FutureTask<>(() -> {
			EtlExceptionImpl thrown = assertThrows(EtlExceptionImpl.class, () -> search.searchAll(1));
			assertTrue(thrown.getCause() instanceof InterruptedException);
			return Thread.currentThread().isInterrupted();
		});
		Thread caller = new Thread(result, "interrupted-search-caller");
		caller.start();
		try {
			awaitWaiting(caller);
			caller.interrupt();
			assertTrue(result.get(5, TimeUnit.SECONDS));
		} finally {
			releaseSearch.countDown();
			assertTrue(workerFinished.await(5, TimeUnit.SECONDS));
			caller.join(1000);
		}
	}

	private static void await(CountDownLatch latch) {
		try {
			if (!latch.await(5, TimeUnit.SECONDS)) throw new AssertionError("Timed out waiting for test gate");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError(e);
		}
	}

	private static void awaitWaiting(Thread thread) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
		while (thread.getState() != Thread.State.WAITING && System.nanoTime() < deadline) Thread.sleep(1);
		assertEquals(Thread.State.WAITING, thread.getState());
	}

	private static void assertHasCause(Throwable thrown, Throwable expected) {
		for (Throwable cause = thrown; cause != null; cause = cause.getCause()) {
			if (cause == expected) return;
		}
		fail("Original cause was lost");
	}

	private static EtlDatabaseObject record() {
		return (EtlDatabaseObject) Proxy.newProxyInstance(ParallelSearchCompletionTest.class.getClassLoader(),
				new Class<?>[] { EtlDatabaseObject.class }, (proxy, method, args) -> null);
	}

	private interface PartitionSearch {
		List<EtlDatabaseObject> search(IntervalExtremeRecord limits) throws DBException;
	}

	private static class TestSearch extends EtlDatabaseObjectSearchParams {
		private final PartitionSearch partitionSearch;
		TestSearch(PartitionSearch partitionSearch) {
			super(null, null);
			this.partitionSearch = partitionSearch;
		}
		List<EtlDatabaseObject> searchAll(int count) throws DBException {
			return searchNextRecordsInMultiThreads(new IntervalExtremeRecord(1, count), null, null, null, null);
		}
		@Override public List<EtlDatabaseObject> search(IntervalExtremeRecord limits, EtlDatabaseObject parent,
				List<EtlDatabaseObject> auxiliary, Connection src, Connection dst) throws DBException {
			return partitionSearch.search(limits);
		}
	}
}
