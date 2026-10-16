package org.openmrs.module.epts.etl.connector;

import java.util.List;

import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Connector-neutral parameters for one source read. */
public final class ConnectorReadRequest<T extends EtlDatabaseObject> {

	private final IntervalExtremeRecord interval;
	private final T parentObject;
	private final List<T> auxiliarySourceObjects;
	private final boolean parallel;

	public ConnectorReadRequest(IntervalExtremeRecord interval, T parentObject, List<T> auxiliarySourceObjects,
			boolean parallel) {
		this.interval = interval;
		this.parentObject = parentObject;
		this.auxiliarySourceObjects = auxiliarySourceObjects;
		this.parallel = parallel;
	}

	public IntervalExtremeRecord getInterval() {
		return interval;
	}

	public T getParentObject() {
		return parentObject;
	}

	public List<T> getAuxiliarySourceObjects() {
		return auxiliarySourceObjects;
	}

	public boolean isParallel() {
		return parallel;
	}
}
