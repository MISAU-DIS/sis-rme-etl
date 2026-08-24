package org.openmrs.module.epts.etl.utilities.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SQLUtilitiesTest {

	private static final String QUERY = "encounter_id = (select encounter_id from encounter e "
			+ "where e.patient_id = prep_encounter_src.patient_id and e.voided = 0 "
			+ "and location_dst_ds.location_id is not null)";

	@Test
	public void shouldRecognizeCandidateOnRightHandSideOfEquality() {
		assertTrue(SQLUtilities.isImmediatelyPrecededByEquality(QUERY, "prep_encounter_src.patient_id"));
	}

	@Test
	public void shouldNotRecognizeCandidateFollowingAndAsEqualityOperand() {
		assertFalse(SQLUtilities.isImmediatelyPrecededByEquality(QUERY, "location_dst_ds.location_id"));
	}

	@Test
	public void shouldEvaluateTheExactOccurrenceOfRepeatedCandidate() {
		String candidate = "prep_encounter_src.patient_id";
		String queryWithRepeatedCandidate = QUERY.replace("location_dst_ds.location_id", candidate);
		int secondOccurrence = queryWithRepeatedCandidate.indexOf(candidate) + candidate.length();

		assertTrue(SQLUtilities.isImmediatelyPrecededByEquality(queryWithRepeatedCandidate, candidate, 0));
		assertFalse(SQLUtilities.isImmediatelyPrecededByEquality(queryWithRepeatedCandidate, candidate,
				secondOccurrence));
	}

	@Test
	public void shouldNotTreatOtherComparisonOperatorsAsEquality() {
		assertFalse(SQLUtilities.isImmediatelyPrecededByEquality("value >= source.value", "source.value"));
		assertFalse(SQLUtilities.isImmediatelyPrecededByEquality("value <= source.value", "source.value"));
		assertFalse(SQLUtilities.isImmediatelyPrecededByEquality("value != source.value", "source.value"));
	}
}
