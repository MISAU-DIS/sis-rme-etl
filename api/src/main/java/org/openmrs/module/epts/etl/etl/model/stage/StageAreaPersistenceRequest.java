package org.openmrs.module.epts.etl.etl.model.stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/**
 * Immutable request envelope to generate and persist StageArea information at a
 * safe synchronization point. The source object references are preserved, while
 * the list structure is defensively copied.
 */
public final class StageAreaPersistenceRequest {

	private final Object owner;

	private final List<EtlDatabaseObject> sourceObjects;

	public StageAreaPersistenceRequest(Object owner, List<EtlDatabaseObject> sourceObjects) {
		this.owner = owner;
		this.sourceObjects = Collections.unmodifiableList(new ArrayList<>(sourceObjects));
	}

	public Object getOwner() {
		return owner;
	}

	public List<EtlDatabaseObject> getSourceObjects() {
		return sourceObjects;
	}
}
