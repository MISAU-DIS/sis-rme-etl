package org.openmrs.module.epts.etl.conf.physical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.TypePrecision;

public class PhysicalTableConfigurationRegistryTest {

	@Test
	public void shouldReuseConfigurationForTheSamePhysicalTable() {
		PhysicalTableConfigurationRegistry registry = new PhysicalTableConfigurationRegistry();
		PhysicalTableIdentity first = identity("OpenMRS", "person");
		PhysicalTableIdentity sameIdentity = identity("OpenMRS", "person");

		assertSame(registry.getOrCreate(first), registry.getOrCreate(sameIdentity));
		assertEquals(1, registry.size());
	}

	@Test
	public void shouldNotReuseConfigurationAcrossSchemas() {
		PhysicalTableConfigurationRegistry registry = new PhysicalTableConfigurationRegistry();

		assertNotSame(registry.getOrCreate(identity("openmrs", "person")),
				registry.getOrCreate(identity("reporting", "person")));
		assertEquals(2, registry.size());
	}

	@Test
	public void shouldReturnIndependentFieldCopiesWithCompletePhysicalMetadata() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		Field id = new Field("person_id");
		id.setDataType("int");
		id.setPrecision(TypePrecision.init(11, null));
		id.setAutoIncrement(true);
		id.setTimeStamp(true);
		configuration.initializeFields(Arrays.asList(id));

		List<Field> first = configuration.copyFields();
		List<Field> second = configuration.copyFields();

		assertNotSame(first, second);
		assertNotSame(first.get(0), second.get(0));
		assertSame(first.get(0).getPrecision(), second.get(0).getPrecision());
		assertEquals(Boolean.TRUE, second.get(0).isAutoIncrement());
		assertEquals(Boolean.TRUE, second.get(0).isTimeStamp());

		first.get(0).setName("contextual_alias");
		assertEquals("person_id", second.get(0).getName());
	}

	@Test
	public void shouldReturnIndependentPrimaryAndUniqueKeyCopies() {
		PhysicalTableConfiguration configuration = new PhysicalTableConfiguration(identity("openmrs", "person"));
		PrimaryKey primaryKey = new PrimaryKey();
		primaryKey.setKeyName("pk_person");
		primaryKey.addKey(Key.fastCreateTyped("person_id", "int"));
		UniqueKeyInfo uniqueKey = new UniqueKeyInfo();
		uniqueKey.setKeyName("uk_person_uuid");
		uniqueKey.addKey(Key.fastCreateTyped("uuid", "varchar"));

		configuration.initializePrimaryKey(primaryKey);
		configuration.initializeUniqueKeys(Arrays.asList(uniqueKey));

		PrimaryKey firstPk = configuration.copyPrimaryKey(null);
		PrimaryKey secondPk = configuration.copyPrimaryKey(null);
		List<UniqueKeyInfo> firstUks = configuration.copyUniqueKeys(null);
		List<UniqueKeyInfo> secondUks = configuration.copyUniqueKeys(null);

		assertNotSame(firstPk, secondPk);
		assertNotSame(firstPk.getFields().get(0), secondPk.getFields().get(0));
		assertEquals("pk_person", secondPk.getKeyName());
		assertNotSame(firstUks.get(0), secondUks.get(0));
		assertEquals("uk_person_uuid", secondUks.get(0).getKeyName());

		firstPk.getFields().get(0).setName("changed_pk");
		firstUks.get(0).getFields().get(0).setName("changed_uk");
		assertEquals("person_id", secondPk.getFields().get(0).getName());
		assertEquals("uuid", secondUks.get(0).getFields().get(0).getName());
	}

	private PhysicalTableIdentity identity(String schema, String table) {
		return new PhysicalTableIdentity(new EtlConfiguration(), "jdbc:mysql://localhost/openmrs", "etl", "openmrs",
				schema, table);
	}
}
