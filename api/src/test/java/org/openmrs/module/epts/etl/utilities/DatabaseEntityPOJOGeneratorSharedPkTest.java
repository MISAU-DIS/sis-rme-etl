package org.openmrs.module.epts.etl.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.model.Field;

public class DatabaseEntityPOJOGeneratorSharedPkTest {

	@Test
	public void shouldGenerateEarlySharedPkLoadAndUuidFallback() {
		GenericTableConfiguration table = sharedPkTable(Field.fastCreateField("uuid"));

		String earlyLoad = DatabaseEntityPOJOGenerator.generateSharedPkLoad(table);
		String postLoad = DatabaseEntityPOJOGenerator.generateSharedPkPostLoad(table);

		assertTrue(earlyLoad.contains("getSharedPkObj().load(rs)"));
		assertFalse(earlyLoad.contains("setUuid"));
		assertTrue(postLoad.contains("!utilities.stringHasValue(getUuid())"));
		assertTrue(postLoad.contains("setUuid(getSharedPkObj().getUuid())"));
	}

	@Test
	public void shouldGenerateInheritedUuidFallbackWhenTableHasNoUuidColumn() {
		GenericTableConfiguration table = sharedPkTable(Field.fastCreateField("person_id"));

		assertTrue(DatabaseEntityPOJOGenerator.generateSharedPkPostLoad(table)
				.contains("setUuid(getSharedPkObj().getUuid())"));
	}

	private GenericTableConfiguration sharedPkTable(Field field) {
		GenericTableConfiguration table = new GenericTableConfiguration();
		table.setTableName("patient");
		table.setSharePkWith("person");
		table.setFields(Arrays.asList(field));
		return table;
	}
}
