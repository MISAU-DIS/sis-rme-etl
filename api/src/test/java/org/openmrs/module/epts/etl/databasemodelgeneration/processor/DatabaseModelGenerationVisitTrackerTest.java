package org.openmrs.module.epts.etl.databasemodelgeneration.processor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableKey;

public class DatabaseModelGenerationVisitTrackerTest {

	@Test
	public void shouldStopSelfReferenceWhileElementIsBeingGenerated() {
		DatabaseModelGenerationVisitTracker<String> tracker = new DatabaseModelGenerationVisitTracker<>();

		assertTrue(tracker.begin("UsersVO"));
		assertFalse(tracker.begin("UsersVO"));
	}

	@Test
	public void shouldStopCycleBetweenTwoElements() {
		DatabaseModelGenerationVisitTracker<String> tracker = new DatabaseModelGenerationVisitTracker<>();

		assertTrue(tracker.begin("PersonVO"));
		assertTrue(tracker.begin("UsersVO"));
		assertFalse(tracker.begin("PersonVO"));
	}

	@Test
	public void shouldNotGenerateCompletedElementAgain() {
		DatabaseModelGenerationVisitTracker<String> tracker = new DatabaseModelGenerationVisitTracker<>();

		assertTrue(tracker.begin("ObsVO"));
		tracker.complete("ObsVO");

		assertFalse(tracker.begin("ObsVO"));
	}

	@Test
	public void shouldAllowRetryAfterFailedGeneration() {
		DatabaseModelGenerationVisitTracker<String> tracker = new DatabaseModelGenerationVisitTracker<>();

		assertTrue(tracker.begin("OrdersVO"));
		tracker.fail("OrdersVO");

		assertTrue(tracker.begin("OrdersVO"));
	}

	@Test
	public void shouldIdentifyRepeatedPhysicalTableKeysByValue() {
		DatabaseModelGenerationVisitTracker<PhysicalTableKey> tracker = new DatabaseModelGenerationVisitTracker<>();
		PhysicalTableKey first = new PhysicalTableKey("openmrs-2.6", "", "", "", "person");
		PhysicalTableKey sameTable = new PhysicalTableKey("openmrs-2.6", "", "", "", "person");

		assertTrue(tracker.begin(first));
		tracker.complete(first);

		assertFalse(tracker.begin(sameTable));
	}
}
