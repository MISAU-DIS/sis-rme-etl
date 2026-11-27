package org.openmrs.module.epts.etl.utilities.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openmrs.module.epts.etl.model.Field;

public class SQLUtilitiesTest {

	private static final String QUERY = "encounter_id = (select encounter_id from encounter e "
			+ "where e.patient_id = prep_encounter_src.patient_id and e.voided = 0 "
			+ "and location_dst_ds.location_id is not null)";

	@Test
	public void shouldNotQualifySelectExpressionAlias() {
		String query = "1 = (select 1 as value from obs o "
				+ "where o.encounter_id = encounter_id and o.value_coded in (165214,165215))";

		String qualified = SQLUtilities.qualifyUnqualifiedSqlFields(query, "encounter");

		assertEquals("1 = (select 1 as value from obs o "
				+ "where o.encounter_id = encounter.encounter_id and o.value_coded in (165214,165215))",
				qualified);
	}

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

	@Test
	public void shouldLocateTransformableElementsInTheirOriginalQueryOrder() {
		String query = "encounter_id = encounter_src_ds.encounter_id "
				+ "and concept_id = @data_proxima_consulta_concept_id";
		String parameter = "@data_proxima_consulta_concept_id";
		String dataSourceElement = "encounter_src_ds.encounter_id";

		assertEquals(dataSourceElement,
				SQLUtilities.findFirstLiteralToken(query, Arrays.asList(parameter, dataSourceElement)));
	}

	@Test
	public void shouldOnlyLocateCompleteLiteralTokens() {
		String query = "source.patient_id_extra = 1 and source.patient_id = 2";

		assertEquals(query.lastIndexOf("source.patient_id"),
				SQLUtilities.findFirstLiteralTokenIndex(query, "source.patient_id"));
	}

	@Test
	public void shouldDetermineFieldsFromFinalSelectAfterMysqlCtes() {
		String query = "WITH enrollment_candidates AS (\n"
				+ "  SELECT MIN(e.encounter_datetime) AS date_enrolled FROM encounter e\n"
				+ "  UNION ALL\n"
				+ "  SELECT MIN(pg.date_enrolled) FROM patient_program pg\n"
				+ "),\n"
				+ "enrollment AS (\n"
				+ "  SELECT MIN(date_enrolled) AS date_enrolled FROM enrollment_candidates\n"
				+ "),\n"
				+ "-- Choose the latest transfer\n"
				+ "latest_transfer AS (\n"
				+ "  SELECT date_transferred, transferred_from_location_uuid\n"
				+ "  FROM transfer_candidates ORDER BY date_transferred DESC LIMIT 1\n"
				+ ")\n"
				+ "SELECT en.date_enrolled,\n"
				+ "       en.date_enrolled AS start_date,\n"
				+ "       tr.date_transferred,\n"
				+ "       91 AS state,\n"
				+ "       2 AS program_id,\n"
				+ "       @dst_location_id AS location_id,\n"
				+ "       tr.transferred_from_location_uuid,\n"
				+ "       1065 AS yes_concept_id\n"
				+ "FROM enrollment en LEFT JOIN latest_transfer tr ON TRUE";

		List<String> fieldNames = SQLUtilities.determineFieldsFromQuery(query).stream().map(Field::getName)
				.collect(Collectors.toList());

		assertEquals(Arrays.asList("date_enrolled", "start_date", "date_transferred", "state", "program_id",
				"location_id", "transferred_from_location_uuid", "yes_concept_id"), fieldNames);
	}
}
