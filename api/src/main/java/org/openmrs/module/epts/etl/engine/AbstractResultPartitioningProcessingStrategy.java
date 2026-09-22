package org.openmrs.module.epts.etl.engine;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
			OpenConnection srcConn, OpenConnection dstConn) throws Exception;

	@Override
	public <T extends EtlDatabaseObject> void process(Engine<T> engine, List<IntervalExtremeRecord> intervals)
			throws Exception {
		EtlThreadFactory<T> threadFactory = new EtlThreadFactory<>(engine);

		for (IntervalExtremeRecord interval : intervals) {

			if (engine.stopRequested()) {
				engine.logWarn("Stopping the Task as Stop Requested!");
				engine.changeStatusToStopped();
				return;
			}

			processInterval(engine, interval, threadFactory);
		}
	}

	private <T extends EtlDatabaseObject> void processInterval(Engine<T> engine, IntervalExtremeRecord interval,
			EtlThreadFactory<T> threadFactory) throws Exception {

		boolean persistWork = !engine.getRelatedEtlConf().hasTestingItem();

		OpenConnection srcConn = engine.openSrcConn(engine);
		OpenConnection dstConn = engine.tryToOpenDstConn(engine);

		try {
			List<T> records = engine.extract(interval, srcConn, dstConn);

			if (!UTILITIES.listHasElement(records)) {
				persistCompletedInterval(engine, interval, srcConn, dstConn, persistWork);
				return;
			}

			int processorCount = engine.getRelatedEtlOperationConfig().getMaxSupportedProcessors();

			Queue<T> queue = new ConcurrentLinkedQueue<>(records);

			processQueue(engine, interval, records, queue, processorCount, threadFactory, srcConn, dstConn);

			persistCompletedInterval(engine, interval, srcConn, dstConn, persistWork);
		} catch (Exception e) {
			engine.stopOperationDueError(e);

			throw e;
		} finally {
			OpenConnection.finalizeAllConnections(engine, dstConn, srcConn);
		}
	}

	private void persistCompletedInterval(Engine<?> engine, IntervalExtremeRecord interval, OpenConnection srcConn,
			OpenConnection dstConn, boolean persistWork) throws Exception {

		if (persistWork) {
			engine.flushPendingPersistence(srcConn, dstConn);
			OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
			OpenConnection.finalizeAllConnections(engine, dstConn, srcConn);
			interval.markAsProcessed();
			engine.getThreadRecordIntervalsManager().save();
		} else {
			engine.discardPendingPersistence();
			interval.markAsProcessed();
		}
	}
}
