package org.openmrs.module.epts.etl.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.concurrent.EtlThreadFactory;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/** Workers consume a shared queue and each performs transform and load. */
public class ResultPartitioningProcessingStrategy extends AbstractResultPartitioningProcessingStrategy {

	@Override
	protected <T extends EtlDatabaseObject> void processQueue(Engine<T> engine, IntervalExtremeRecord interval,
			List<T> records, Queue<T> queue, int processorCount, EtlThreadFactory<T> threadFactory,
			OpenConnection srcConn, OpenConnection dstConn) throws Exception {

		boolean sharedConnections = engine.getRelatedEtlOperationConfig().isUseSharedConnectionPerThread();

		ExecutorService executor = Executors.newFixedThreadPool(processorCount, threadFactory);

		engine.resetCurrentTaskProcessor(processorCount);

		List<CompletableFuture<Void>> tasks = new ArrayList<>(processorCount);

		try {
			for (int i = 0; i < processorCount; i++) {
				TaskProcessor<T> processor = engine.initConcurrentTaskProcessor(interval, i);

				engine.getCurrentTaskProcessor().add(processor);

				tasks.add(CompletableFuture.runAsync(() -> engine.consumeTransformAndLoadQueue(processor, queue,
						sharedConnections, srcConn, dstConn), executor));
			}

			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();

			engine.assertNoProcessorFailed(engine.getCurrentTaskProcessor());
		} finally {
			engine.shutdownExecutor(executor);
		}
	}
}
