package org.openmrs.module.epts.etl.engine;

import java.util.List;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Executes one engine parallel-processing strategy. */
public interface EngineProcessingStrategy {

	<T extends EtlDatabaseObject> void process(Engine<T> engine, List<IntervalExtremeRecord> intervals)
			throws Exception;
}
