package org.openmrs.module.epts.etl.model.pojo.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableConfiguration;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableIdentity;
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

	@Test
	public void shouldLoadDefaultsDirectlyIntoGeneratedFields() throws Exception {
		GenericTableConfiguration configuration = new GenericTableConfiguration();
		configuration.setFields(Arrays.asList(Field.fastCreateWithType("concept_class_id", "INT"),
				Field.fastCreateWithType("name", "VARCHAR"),
				Field.fastCreateWithType("date_created", "DATETIME")));

		ConceptClassVO object = new ConceptClassVO();
		object.setRelatedConfiguration(configuration);
		object.loadWithDefaultValues(null, null);

		assertEquals(Integer.valueOf(0), object.getConceptClassId().getValue());
		assertEquals(Field.DEFAULT_STRING_VALUE, object.getName().getValue());
		assertEquals(Field.DEFAULT_DATE_VALUE, object.getDateCreated());
		assertEquals(Field.DEFAULT_DATE_VALUE, find(object.getFields(), "date_created").getValue());
	}

	@Test
	public void shouldSynchronizeCommonFieldAndInheritedAttributeWhenAssigned() {
		ConceptClassVO object = new ConceptClassVO();
		Date changed = new Date();

		object.setFieldValue("date_changed", changed);

		assertSame(changed, object.getDateChanged());
		assertSame(changed, find(object.getFields(), "date_changed").getValue());
	}

	@Test
	public void shouldRegenerateObjectIdWhenGeneratedPrimaryKeyFieldChanges() {
		GenericTableConfiguration configuration = new GenericTableConfiguration();
		configuration.setFields(Arrays.asList(Field.fastCreateWithType("concept_class_id", "INT")));
		PrimaryKey primaryKey = new PrimaryKey(configuration);
		primaryKey.addKey(Key.fastCreateTyped("concept_class_id", "INT"));
		configuration.setPrimaryKeyInfoLoaded(true);
		configuration.setPrimaryKey(primaryKey);

		ConceptClassVO object = new ConceptClassVO();
		object.setRelatedConfiguration(configuration);
		object.setFieldValue("concept_class_id", 31);

		assertEquals(31, object.getObjectId().asSimpleValue());
		assertEquals(31, object.getConceptClassId().getValue());
	}

	@Test
	public void shouldExposeAContextuallyIgnoredPhysicalFieldOnAnExistingPojo() throws Exception {
		GenericTableConfiguration configuration = new GenericTableConfiguration();
		configuration.setFields(Arrays.asList(Field.fastCreateWithType("concept_class_id", "INT")));
		PhysicalTableConfiguration physical = new PhysicalTableConfiguration(
				new PhysicalTableIdentity("jdbc:test", "user", "catalog", "schema", "concept_class"));
		physical.initializeFields(Arrays.asList(Field.fastCreateWithType("concept_class_id", "INT"),
				Field.fastCreateWithType("contextual_code", "VARCHAR")));
		java.lang.reflect.Field physicalConfiguration = AbstractTableConfiguration.class
				.getDeclaredField("physicalTableConfiguration");
		physicalConfiguration.setAccessible(true);
		physicalConfiguration.set(configuration, physical);

		ConceptClassVO object = new ConceptClassVO();
		object.setRelatedConfiguration(configuration);
		object.setFieldValue("contextual_code", "available");

		assertEquals("available", object.getFieldValue("contextual_code"));
		assertSame(find(object.getFields(), "contextual_code"), find(object.getFields(), "contextual_code"));
	}

	private Field find(List<Field> fields, String name) {
		return fields.stream().filter(field -> name.equalsIgnoreCase(field.getName())).findFirst()
				.orElseThrow(() -> new AssertionError("Field not found: " + name));
	}
}
