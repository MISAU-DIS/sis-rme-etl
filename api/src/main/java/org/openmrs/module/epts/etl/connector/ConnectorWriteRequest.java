package org.openmrs.module.epts.etl.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Immutable connector-neutral description of one batch persistence request. */
public final class ConnectorWriteRequest<T extends EtlDatabaseObject> {

	private final ConnectorWriteMode mode;
	private final List<T> records;

	public ConnectorWriteRequest(ConnectorWriteMode mode, List<T> records) {
		if (mode == null) {
			throw new IllegalArgumentException("mode cannot be null");
		}

		this.mode = mode;
		this.records = records == null ? Collections.emptyList()
				: Collections.unmodifiableList(new ArrayList<>(records));
	}

	public ConnectorWriteMode getMode() {
		return mode;
	}

	public List<T> getRecords() {
		return records;
	}
}
