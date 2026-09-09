package org.openmrs.module.epts.etl.databasemodelgeneration.processor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks database model elements while their dependency graph is traversed.
 * Both completed and currently active elements must be rejected because table
 * relationships may contain self-references or multi-table cycles.
 */
class DatabaseModelGenerationVisitTracker<T> {

	private final Set<T> inProgress = ConcurrentHashMap.newKeySet();

	private final Set<T> generated = ConcurrentHashMap.newKeySet();

	boolean begin(T elementId) {
		if (generated.contains(elementId) || inProgress.contains(elementId))
			return false;

		inProgress.add(elementId);
		return true;
	}

	void complete(T elementId) {
		inProgress.remove(elementId);
		generated.add(elementId);
	}

	void fail(T elementId) {
		inProgress.remove(elementId);
	}
}
