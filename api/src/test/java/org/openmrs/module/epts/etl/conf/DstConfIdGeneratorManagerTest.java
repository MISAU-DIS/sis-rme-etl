package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class DstConfIdGeneratorManagerTest {

	@Test
	public void shouldShareOneIdSequenceBetweenConcurrentWorkers() throws Exception {
		int workerCount = 8;
		int idsPerWorker = 100;
		DstConf dstConf = newInMemoryDstConf(1);
		ExecutorService executor = Executors.newFixedThreadPool(workerCount);
		CountDownLatch start = new CountDownLatch(1);
		Set<Future<Set<Long>>> results = new HashSet<>();

		try {
			for (int worker = 0; worker < workerCount; worker++) {
				results.add(executor.submit(() -> {
					start.await();
					Set<Long> ids = new HashSet<>();

					for (int i = 0; i < idsPerWorker; i++) {
						ids.add(dstConf.retrieveNextRecordId(null));
					}

					return ids;
				}));
			}

			start.countDown();
			Set<Long> generatedIds = new HashSet<>();

			for (Future<Set<Long>> result : results) {
				generatedIds.addAll(result.get(10, TimeUnit.SECONDS));
			}

			assertEquals(workerCount * idsPerWorker, generatedIds.size());
			assertEquals(Long.valueOf(1), generatedIds.stream().min(Long::compareTo).get());
			assertEquals(Long.valueOf(workerCount * idsPerWorker),
					generatedIds.stream().max(Long::compareTo).get());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	public void shouldKeepIndependentSequencesForDifferentDestinationConfigurations() throws Exception {
		DstConf first = newInMemoryDstConf(10);
		DstConf second = newInMemoryDstConf(10);

		assertEquals(10, first.retrieveNextRecordId(null));
		assertEquals(11, first.retrieveNextRecordId(null));
		assertEquals(10, second.retrieveNextRecordId(null));
	}

	private DstConf newInMemoryDstConf(int initialValue) {
		DstConf dstConf = new DstConf();
		dstConf.setInMemoryTable(true);
		dstConf.setPrimaryKeyInitialIncrementValue(initialValue);
		return dstConf;
	}
}
