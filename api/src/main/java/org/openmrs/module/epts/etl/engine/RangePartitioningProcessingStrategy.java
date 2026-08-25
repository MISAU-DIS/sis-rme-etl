package org.openmrs.module.epts.etl.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.concurrent.EtlThreadFactory;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/** Executes complete ETL pipelines over independent identifier ranges. */
public class RangePartitioningProcessingStrategy implements EngineProcessingStrategy {

	@Override
	public <T extends EtlDatabaseObject> void process(Engine<T> engine, List<IntervalExtremeRecord> intervals)
			throws Exception {

		engine.logDebug("Initializing " + intervals.size() + " processors to performe task on a interval "
				+ engine.getThreadRecordIntervalsManager().getCurrentLimits() + "!".toUpperCase());

		boolean sharedConnections = engine.getRelatedEtlOperationConfig().isUseSharedConnectionPerThread();

		engine.resetCurrentTaskProcessor(intervals.size());

		EtlThreadFactory<T> threadFactory = new EtlThreadFactory<>(engine);
		ExecutorService executor = Executors.newFixedThreadPool(intervals.size(), threadFactory);

		List<CompletableFuture<Void>> tasks = new ArrayList<>(intervals.size());

		OpenConnection sharedSrcConn = engine.openSrcConn(engine);
		OpenConnection sharedDstConn = engine.tryToOpenDstConn(engine);

		try {

			for (int i = 0; i < intervals.size(); i++) {
				TaskProcessor<T> processor = engine.initConcurrentTaskProcessor(intervals.get(i), i);
				engine.getCurrentTaskProcessor().add(processor);
				tasks.add(CompletableFuture.runAsync(
						() -> engine.processRangePartition(processor, sharedConnections, sharedSrcConn, sharedDstConn),
						executor));
			}

			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();

			List<EtlOperationResultHeader<T>> results = collectResults(engine.getCurrentTaskProcessor());

			if (EtlOperationResultHeader.hasAtLeastOneFatalError(results)) {
				engine.stopOperationDueError(
						EtlOperationResultHeader.getDefaultResultWithFatalError(results).getFatalException());
				return;
			}

			persistStageArea(engine, sharedConnections, sharedSrcConn, sharedDstConn);

			persistIntervalState(engine, sharedConnections, sharedSrcConn, sharedDstConn);

			if (!EtlOperationResultHeader.hasAtLeastOneRecordsWithRecursiveRelashionships(results)) {
				engine.getThreadRecordIntervalsManager().getCurrentLimits().markSkippedRecordsAsProcessed();
			}
		} finally {
			OpenConnection.finalizeAllConnections(engine, sharedSrcConn, sharedDstConn);
			engine.shutdownExecutor(executor);
		}
	}

	private <T extends EtlDatabaseObject> List<EtlOperationResultHeader<T>> collectResults(
			List<TaskProcessor<T>> processors) {
		List<EtlOperationResultHeader<T>> results = new ArrayList<>(processors.size());
		for (TaskProcessor<T> processor : processors) {
			results.add(processor.getTaskResultInfo());
		}
		return results;
	}

	private void persistStageArea(Engine<?> engine, boolean sharedConnections, OpenConnection srcConn,
			OpenConnection dstConn) throws Exception {
		if (engine.getRelatedEtlConf().hasTestingItem()) {
			engine.getStageAreaPersistenceCoordinator().discardPending();
		} else if (sharedConnections) {
			engine.flushStageArea(srcConn, dstConn);
		} else {
			engine.flushStageAreaUsingDedicatedConnection();
		}
	}

	private void persistIntervalState(Engine<?> engine, boolean sharedConnections, OpenConnection srcConn,
			OpenConnection dstConn) throws Exception {
		if (engine.getRelatedEtlConf().hasTestingItem()) {
			return;
		}

		if (sharedConnections) {
			OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
			engine.getThreadRecordIntervalsManager().getCurrentLimits().markAsProcessed();
		}
		engine.getThreadRecordIntervalsManager().save();
	}
}
