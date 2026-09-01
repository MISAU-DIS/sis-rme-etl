package org.openmrs.module.epts.etl.databasemodelgeneration.processor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DatabaseModelGenerationVisitTrackerTest {

	@Test
	public void shouldStopSelfReferenceWhileElementIsBeingGenerated() {
		DatabaseModelGenerationVisitTracker tracker = new DatabaseModelGenerationVisitTracker();

		assertTrue(tracker.begin("UsersVO"));
		assertFalse(tracker.begin("UsersVO"));
	}

	@Test
	public void shouldStopCycleBetweenTwoElements() {
		DatabaseModelGenerationVisitTracker tracker = new DatabaseModelGenerationVisitTracker();

		assertTrue(tracker.begin("PersonVO"));
		assertTrue(tracker.begin("UsersVO"));
		assertFalse(tracker.begin("PersonVO"));
	}

	@Test
	public void shouldNotGenerateCompletedElementAgain() {
		DatabaseModelGenerationVisitTracker tracker = new DatabaseModelGenerationVisitTracker();

		assertTrue(tracker.begin("ObsVO"));
		tracker.complete("ObsVO");

		assertFalse(tracker.begin("ObsVO"));
	}

	@Test
	public void shouldAllowRetryAfterFailedGeneration() {
		DatabaseModelGenerationVisitTracker tracker = new DatabaseModelGenerationVisitTracker();

		assertTrue(tracker.begin("OrdersVO"));
		tracker.fail("OrdersVO");

		assertTrue(tracker.begin("OrdersVO"));
	}
}
