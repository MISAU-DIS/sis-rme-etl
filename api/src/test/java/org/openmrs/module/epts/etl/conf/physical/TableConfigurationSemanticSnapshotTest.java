package org.openmrs.module.epts.etl.conf.physical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.GenericTableConfiguration;
import org.openmrs.module.epts.etl.conf.Key;
import org.openmrs.module.epts.etl.conf.ParentTableImpl;
import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.conf.RefMapping;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.model.TypePrecision;

public class TableConfigurationSemanticSnapshotTest {

	@Test
	public void shouldIgnoreContextualStateAndUnorderedPhysicalCollections() {
		GenericTableConfiguration first = configuredTable();
		GenericTableConfiguration equivalent = configuredTable();

		first.setTableAlias("person_for_source");
		first.setExtraConditionForExtract("person_for_source.voided = 0");
		equivalent.setTableAlias("person_for_destination");
		equivalent.setExtraConditionForExtract("person_for_destination.date_changed is not null");

		// Discovery order of distinct UKs and FKs is not semantically relevant.
		equivalent.setUniqueKeys(Arrays.asList(equivalent.getUniqueKeys().get(1), equivalent.getUniqueKeys().get(0)));
		equivalent.setParentRefInfo(Arrays.asList(equivalent.getParentRefInfo().get(1),
				equivalent.getParentRefInfo().get(0)));

		assertEquals(TableConfigurationSemanticSnapshot.from(first),
				TableConfigurationSemanticSnapshot.from(equivalent));
	}

	@Test
	public void shouldDetectEveryRelevantColumnProperty() {
		assertDifferentAfter(columnChange("type"));
		assertDifferentAfter(columnChange("precision"));
		assertDifferentAfter(columnChange("scale"));
		assertDifferentAfter(columnChange("nullable"));
		assertDifferentAfter(columnChange("autoIncrement"));
		assertDifferentAfter(columnChange("timestamp"));
	}

	@Test
	public void shouldPreserveColumnAndCompositeKeyOrder() {
		GenericTableConfiguration reorderedColumns = configuredTable();
		reorderedColumns.setFields(Arrays.asList(reorderedColumns.getFields().get(1), reorderedColumns.getFields().get(0),
				reorderedColumns.getFields().get(2)));
		assertDifferentAfter(reorderedColumns);

		GenericTableConfiguration reorderedPk = configuredTable();
		reorderedPk.getPrimaryKey().setFields(Arrays.asList(Key.fastCreateTyped("site_id", "int"),
				Key.fastCreateTyped("person_id", "int")));
		assertDifferentAfter(reorderedPk);
	}

	@Test
	public void shouldDetectPrimaryUniqueAndRelationshipChanges() {
		GenericTableConfiguration changedPk = configuredTable();
		changedPk.getPrimaryKey().getFields().get(0).setName("other_id");
		assertDifferentAfter(changedPk);

		GenericTableConfiguration changedUk = configuredTable();
		changedUk.getUniqueKeys().get(0).getFields().get(0).setName("other_uuid");
		assertDifferentAfter(changedUk);

		GenericTableConfiguration changedParent = configuredTable();
		changedParent.getParentRefInfo().get(0).getRefMapping().get(0).setParentFieldName("other_id");
		assertDifferentAfter(changedParent);
	}

	private void assertDifferentAfter(GenericTableConfiguration changed) {
		assertNotEquals(TableConfigurationSemanticSnapshot.from(configuredTable()),
				TableConfigurationSemanticSnapshot.from(changed));
	}

	private GenericTableConfiguration columnChange(String property) {
		GenericTableConfiguration table = configuredTable();
		Field field = table.getFields().get(0);
		if ("type".equals(property)) field.setDataType("bigint");
		if ("precision".equals(property)) field.setPrecision(TypePrecision.init(20, 0));
		if ("scale".equals(property)) field.setPrecision(TypePrecision.init(11, 2));
		if ("nullable".equals(property)) field.setAllowNull(true);
		if ("autoIncrement".equals(property)) field.setAutoIncrement(false);
		if ("timestamp".equals(property)) field.setTimeStamp(true);
		return table;
	}

	private GenericTableConfiguration configuredTable() {
		GenericTableConfiguration table = new GenericTableConfiguration("person");
		table.setSchema("openmrs");
		table.setFields(Arrays.asList(field("person_id", "int", 11, 0, false, true, false),
				field("site_id", "int", 11, 0, false, false, false),
				field("uuid", "varchar", 38, null, false, false, false)));

		PrimaryKey pk = new PrimaryKey();
		pk.setKeyName("pk_person");
		pk.setFields(Arrays.asList(Key.fastCreateTyped("person_id", "int"),
				Key.fastCreateTyped("site_id", "int")));
		table.setPrimaryKeyInfoLoaded(true);
		table.setPrimaryKey(pk);

		UniqueKeyInfo uuid = key("uk_person_uuid", "uuid");
		UniqueKeyInfo legacyId = key("uk_person_legacy", "person_id", "site_id");
		table.setUniqueKeys(Arrays.asList(uuid, legacyId));

		ParentTableImpl location = parent("location", "fk_person_location", "site_id", "location_id");
		ParentTableImpl creator = parent("users", "fk_person_creator", "person_id", "user_id");
		table.setParentRefInfo(Arrays.asList(location, creator));
		return table;
	}

	private Field field(String name, String type, Integer length, Integer scale, boolean nullable,
			boolean autoIncrement, boolean timestamp) {
		Field field = new Field(name);
		field.setDataType(type);
		field.setPrecision(TypePrecision.init(length, scale));
		field.setAllowNull(nullable);
		field.setAutoIncrement(autoIncrement);
		field.setTimeStamp(timestamp);
		return field;
	}

	private UniqueKeyInfo key(String name, String... columns) {
		UniqueKeyInfo key = new UniqueKeyInfo();
		key.setKeyName(name);
		for (String column : columns) key.addKey(Key.fastCreateKey(column));
		return key;
	}

	private ParentTableImpl parent(String tableName, String code, String childColumn, String parentColumn) {
		ParentTableImpl parent = new ParentTableImpl(tableName, code);
		parent.setRefMapping(Arrays.asList(RefMapping.fastCreate(childColumn, parentColumn)));
		return parent;
	}
}
