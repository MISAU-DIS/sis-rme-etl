package org.openmrs.module.epts.etl.engine;

import java.util.List;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/** Executes extraction, transformation and loading sequentially. */
public class SingleThreadProcessingStrategy implements EngineProcessingStrategy {

	@Override
	public <T extends EtlDatabaseObject> void process(Engine<T> engine, List<IntervalExtremeRecord> intervals)
			throws Exception {

		for (IntervalExtremeRecord interval : intervals) {
			if (engine.stopRequested() || engine.isStopped()) {
				engine.logWarn("Aborting engine as stop requested!", 10, true);
				return;
			}

			TaskProcessor<T> processor = engine.initTaskProcessor(interval, false, engine.getEngineId());

			boolean persistWork = !engine.getRelatedEtlConf().hasTestingItem();

			OpenConnection srcConn = engine.openSrcConn(engine);
			OpenConnection dstConn = engine.tryToOpenDstConn(engine);

			try {
				engine.performExtractTransformationAndLoading(processor, true, persistWork, srcConn, dstConn);

				if (processor.getTaskResultInfo().hasFatalError()) {
					engine.stopOperationDueError(processor.getTaskResultInfo().getFatalException());
				}
			} finally {
				OpenConnection.finalizeAllConnections(engine, srcConn, dstConn);
			}
		}
	}
}
