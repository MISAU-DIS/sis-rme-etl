package org.openmrs.module.epts.etl.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReaders;
import org.openmrs.module.epts.etl.connector.ConnectorWritesAggregator;
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

		ConnectorWritesAggregator sharedWriters = engine.getEtlItemConfiguration().createDestinationWriters(engine);
		ConnectorRecordReaders sharedReaders = engine.getEtlItemConfiguration().createSourceReaders(
				engine.getSearchParams(), engine, sharedWriters);
		ConnectorRecordReader<T> sharedReader = engine.primaryReader(sharedReaders);

		try {

			for (int i = 0; i < intervals.size(); i++) {
				TaskProcessor<T> processor = engine.initConcurrentTaskProcessor(intervals.get(i), i);
				processor.setRecordReaders(sharedReaders);
				engine.getCurrentTaskProcessor().add(processor);
				tasks.add(CompletableFuture.runAsync(
						() -> engine.processRangePartition(processor, sharedConnections, sharedReaders, sharedWriters),
						executor));
			}

			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();

			List<EtlOperationResultHeader<T>> results = collectResults(engine.getCurrentTaskProcessor());

			if (EtlOperationResultHeader.hasAtLeastOneFatalError(results)) {
				engine.stopOperationDueError(
						EtlOperationResultHeader.getDefaultResultWithFatalError(results).getFatalException());
				return;
			}

			persistPendingData(engine, sharedConnections, sharedReaders, sharedWriters);

			if (!engine.getRelatedEtlConf().hasTestingItem() && sharedConnections) {
				engine.markConnectionsAsSuccessfullyTerminated(sharedReaders, sharedWriters);
			}
			engine.finalizeConnections(sharedReaders, sharedWriters);

			// Workers and auxiliary persistence have all committed before publishing completion.
			for (IntervalExtremeRecord interval : intervals) {
				interval.markAsProcessed();
			}

			if (!EtlOperationResultHeader.hasAtLeastOneRecordsWithRecursiveRelashionships(results)) {
				engine.getThreadRecordIntervalsManager().getCurrentLimits().markSkippedRecordsAsProcessed();
			}
			if (!engine.getRelatedEtlConf().hasTestingItem()) {
				engine.getThreadRecordIntervalsManager().save();
			}
		} finally {
			try {
				engine.finalizeConnections(sharedReaders, sharedWriters);
			} finally {
				engine.shutdownExecutor(executor);
			}
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

	private void persistPendingData(Engine<?> engine, boolean sharedConnections, ConnectorRecordReaders readers,
			ConnectorWritesAggregator writers) throws Exception {
		if (engine.getRelatedEtlConf().hasTestingItem()) {
			engine.discardPendingPersistence();
		} else if (sharedConnections) {
			engine.flushPendingPersistence(readers.getPrimary().unwrap(OpenConnection.class),
					engine.firstDestinationConnection(engine.destinationConnections(writers)));
		} else {
			engine.flushPendingPersistenceUsingDedicatedConnection();
		}
	}

}
