package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.model.Field;

class ParentTableConditionalFieldsTest {

	private final ParentTableImpl parent = new ParentTableImpl();

	@Test
	void shouldKeepSupportingASingleConditionalValue() {
		Field conditionalField = Field.fastCreateWithValue("type", "PATIENT");

		assertTrue(parent.matchesConditionalFieldValue(conditionalField, "PATIENT"));
		assertFalse(parent.matchesConditionalFieldValue(conditionalField, "PERSON"));
	}

	@Test
	void shouldMatchAnyCommaSeparatedConditionalValue() {
		Field conditionalField = Field.fastCreateWithValue("type", "PATIENT, PERSON,PROVIDER");

		assertTrue(parent.matchesConditionalFieldValue(conditionalField, "PATIENT"));
		assertTrue(parent.matchesConditionalFieldValue(conditionalField, "PERSON"));
		assertTrue(parent.matchesConditionalFieldValue(conditionalField, "PROVIDER"));
		assertFalse(parent.matchesConditionalFieldValue(conditionalField, "LOCATION"));
	}

	@Test
	void shouldNotMatchWhenConfiguredOrRecordValueIsNull() {
		Field conditionalField = Field.fastCreateWithValue("type", "PATIENT,PERSON");

		assertFalse(parent.matchesConditionalFieldValue(conditionalField, null));
		assertFalse(parent.matchesConditionalFieldValue(Field.fastCreateWithValue("type", null), "PATIENT"));
	}
}
