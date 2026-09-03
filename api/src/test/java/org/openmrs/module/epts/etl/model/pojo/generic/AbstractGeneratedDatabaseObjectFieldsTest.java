package org.openmrs.module.epts.etl.model.pojo.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.pojo.openmrs_2_6.sesp.ConceptClassVO;

public class AbstractGeneratedDatabaseObjectFieldsTest {

	@Test
	public void shouldKeepItsOwnStableListWithGeneratedAndInheritedFieldReferences() {
		GenericTableConfiguration configuration = new GenericTableConfiguration();
		configuration.setFields(Arrays.asList(Field.fastCreateField("concept_class_id"),
				Field.fastCreateField("date_created"), Field.fastCreateField("uuid")));

		ConceptClassVO object = new ConceptClassVO();
		object.setRelatedConfiguration(configuration);

		List<Field> firstResult = object.getFields();
		Field id = find(firstResult, "concept_class_id");
		Field inheritedDate = find(firstResult, "date_created");
		Field inheritedDateChanged = find(firstResult, "date_changed");
		Field inheritedDateVoided = find(firstResult, "date_voided");
		Field inheritedUuid = find(firstResult, "uuid");

		Date dateCreated = new Date();
		object.setDateCreated(dateCreated);
		object.setUuid("generated-uuid");

		List<Field> secondResult = object.getFields();
		assertSame(firstResult, secondResult);
		assertSame(id, find(secondResult, "concept_class_id"));
		assertSame(inheritedDate, find(secondResult, "date_created"));
		assertSame(inheritedDateChanged, find(secondResult, "date_changed"));
		assertSame(inheritedDateVoided, find(secondResult, "date_voided"));
		assertSame(inheritedUuid, find(secondResult, "uuid"));
		assertEquals(dateCreated, inheritedDate.getValue());
		assertEquals("generated-uuid", inheritedUuid.getValue());
	}

	@Test
	public void shouldReplaceTheGeneratedPrimaryKeyFieldDirectly() {
		ConceptClassVO object = new ConceptClassVO();

		object.tryToReplaceFieldValueWithKeyValue(Key.fastCreateValued("concept_class_id", 17));

		assertEquals(17, object.getConceptClassId().getValue());
	}

	@Test
	public void shouldRetrieveGeneratedAndInheritedFieldValuesDirectly() {
		ConceptClassVO object = new ConceptClassVO();
		Date created = new Date();
		object.setConceptClassIdValue(23);
		object.setDateCreated(created);
		object.setUuid("direct-uuid");

		assertEquals(23, object.getFieldValue("concept_class_id"));
		assertEquals(23, object.getFieldValue("conceptClassId"));
		assertSame(created, object.getFieldValue("date_created"));
		assertEquals("direct-uuid", object.getFieldValue("uuid"));
	}

	private Field find(List<Field> fields, String name) {
		return fields.stream().filter(field -> name.equalsIgnoreCase(field.getName())).findFirst()
				.orElseThrow(() -> new AssertionError("Field not found: " + name));
	}
}
