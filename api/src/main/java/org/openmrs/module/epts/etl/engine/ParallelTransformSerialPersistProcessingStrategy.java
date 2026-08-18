package org.openmrs.module.epts.etl.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.concurrent.EtlThreadFactory;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/** Transforms in parallel and loads the transformed result with one worker. */
public class ParallelTransformSerialPersistProcessingStrategy
		extends AbstractResultPartitioningProcessingStrategy {
	private static final CommonUtilities UTILITIES = CommonUtilities.getInstance();

	@Override
	protected <T extends EtlDatabaseObject> void processQueue(Engine<T> engine, IntervalExtremeRecord interval,
			List<T> records, Queue<T> queue, int processorCount, EtlThreadFactory<T> threadFactory,
			OpenConnection srcConn, OpenConnection dstConn) throws Exception {
		boolean sharedConnections = engine.getRelatedEtlOperationConfig().isUseSharedConnectionPerThread();
		ExecutorService executor = Executors.newFixedThreadPool(processorCount, threadFactory);
		engine.resetCurrentTaskProcessor(processorCount + 1);
		List<CompletableFuture<Void>> tasks = new ArrayList<>(processorCount);

		try {
			for (int i = 0; i < processorCount; i++) {
				TaskProcessor<T> transformer = engine.initConcurrentTaskProcessor(interval, i);
				engine.getCurrentTaskProcessor().add(transformer);
				tasks.add(CompletableFuture.runAsync(() -> engine.consumeTransformationQueue(transformer, queue,
						sharedConnections, srcConn, dstConn), executor));
			}
			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
			engine.assertNoProcessorFailed(engine.getCurrentTaskProcessor());
		} finally {
			engine.shutdownExecutor(executor);
		}

		TaskProcessor<T> loader = engine.initConcurrentTaskProcessor(interval, processorCount);
		engine.getCurrentTaskProcessor().add(loader);
		loader.changeStatusToRunning();
		loader.loadTransformedRecords(records, srcConn, dstConn);
		engine.completeExtractedTask(loader, srcConn, dstConn, true);
		engine.assertNoProcessorFailed(UTILITIES.parseToList(loader));
	}
}
