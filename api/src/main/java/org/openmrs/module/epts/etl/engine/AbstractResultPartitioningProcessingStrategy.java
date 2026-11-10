package org.openmrs.module.epts.etl.engine;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.concurrent.EtlThreadFactory;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/** Common interval lifecycle for queue-based processing strategies. */
public abstract class AbstractResultPartitioningProcessingStrategy implements EngineProcessingStrategy {
	private static final CommonUtilities UTILITIES = CommonUtilities.getInstance();

	protected abstract <T extends EtlDatabaseObject> void processQueue(Engine<T> engine, IntervalExtremeRecord interval,
			List<T> extractedRecords, Queue<T> queue, int processorCount, EtlThreadFactory<T> threadFactory,
			ConnectorRecordReadersAggregator readers, ConnectorRecordWritersAggregator writers) throws Exception;

	@Override
	public <T extends EtlDatabaseObject> void process(Engine<T> engine, List<IntervalExtremeRecord> intervals)
			throws Exception {
		EtlThreadFactory<T> threadFactory = new EtlThreadFactory<>(engine);

		for (IntervalExtremeRecord interval : intervals) {

			if (engine.stopRequested()) {
				engine.logWarn("Stopping the Task as Stop Requested!");
				return;
			}

			processInterval(engine, interval, threadFactory);
		}
	}

	private <T extends EtlDatabaseObject> void processInterval(Engine<T> engine, IntervalExtremeRecord interval,
			EtlThreadFactory<T> threadFactory) throws Exception {

		boolean persistWork = !engine.getRelatedEtlConf().hasTestingItem();

		ConnectorRecordWritersAggregator writers = engine.getEtlItemConfiguration().createDestinationWriters(engine);
		ConnectorRecordReadersAggregator readers = engine.getEtlItemConfiguration()
				.createSourceReaders(engine.getSearchParams(), engine, writers);
		ConnectorRecordReader<T> reader = engine.primaryReader(readers);
		OpenConnection srcConn = reader.unwrap(OpenConnection.class);
		List<OpenConnection> dstConnections = engine.destinationConnections(writers);

		try {
			List<T> records = engine.extract(interval, reader);

			if (!UTILITIES.listHasElement(records)) {
				persistCompletedInterval(engine, interval, srcConn, dstConnections, readers, writers, persistWork);
				return;
			}

			int processorCount = engine.getRelatedEtlOperationConfig().getMaxSupportedProcessors();

			Queue<T> queue = new ConcurrentLinkedQueue<>(records);

			processQueue(engine, interval, records, queue, processorCount, threadFactory, readers, writers);

			persistCompletedInterval(engine, interval, srcConn, dstConnections, readers, writers, persistWork);
		} catch (Exception e) {
			engine.stopOperationDueError(e);

			throw e;
		} finally {
			engine.finalizeConnections(readers, writers);
		}
	}

	private void persistCompletedInterval(Engine<?> engine, IntervalExtremeRecord interval, OpenConnection srcConn,
			List<OpenConnection> dstConnections, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers, boolean persistWork) throws Exception {

		if (persistWork) {
			OpenConnection dstConn = engine.firstDestinationConnection(dstConnections);
			engine.flushPendingPersistence(srcConn, dstConn);
			engine.markConnectionsAsSuccessfullyTerminated(readers, writers);
			engine.finalizeConnections(readers, writers);
			interval.markAsProcessed();
			engine.getThreadRecordIntervalsManager().save();
		} else {
			engine.discardPendingPersistence();
			interval.markAsProcessed();
		}
	}
}
