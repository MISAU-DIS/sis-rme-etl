package org.openmrs.module.epts.etl.engine;

import java.util.List;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
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

			ConnectorRecordWriters writers = engine.getEtlItemConfiguration().createDestinationWriters(engine);
			ConnectorRecordReader<T> reader = engine.getEtlItemConfiguration().createSourceReader(
					engine.getSearchParams(), engine, writers);
			OpenConnection srcConn = reader.unwrap(OpenConnection.class);
			List<OpenConnection> dstConnections = engine.destinationConnections(writers);

			try {
				engine.performExtractTransformationAndLoading(processor, true, persistWork, reader, writers);

				if (processor.getTaskResultInfo().hasFatalError()) {
					engine.stopOperationDueError(processor.getTaskResultInfo().getFatalException());
				}
			} finally {
				engine.finalizeConnections(srcConn, dstConnections);
			}
		}
	}
}
