package org.openmrs.module.epts.etl.databasemodelgeneration.processor;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks database model elements while their dependency graph is traversed.
 * Both completed and currently active elements must be rejected because table
 * relationships may contain self-references or multi-table cycles.
 */
class DatabaseModelGenerationVisitTracker {

	private final Set<String> inProgress = new HashSet<>();

	private final Set<String> generated = new HashSet<>();

	boolean begin(String elementId) {
		if (generated.contains(elementId) || inProgress.contains(elementId))
			return false;

		inProgress.add(elementId);
		return true;
	}

	void complete(String elementId) {
		inProgress.remove(elementId);
		generated.add(elementId);
	}

	void fail(String elementId) {
		inProgress.remove(elementId);
	}
}
