package org.openmrs.module.epts.etl.model.pojo.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;
import org.openmrs.module.epts.etl.model.Field;

class GenericDatabaseObjectDestinationConfigurationTest {

	@Test
	void shouldKeepLogicalDestinationAsRelatedConfigurationAndDelegateJdbcCapabilitiesToItsAdapter() {
		Field id = Field.fastCreateField("id");
		JdbcTableDestinationConfiguration table = new JdbcTableDestinationConfiguration("patient");
		table.setFields(Collections.singletonList(id));

		EtlDestinationConfiguration destination = new EtlDestinationConfiguration();
		ConnectorResourceConfiguration resource = new ConnectorResourceConfiguration();
		resource.setName("patient");
		destination.setResource(resource);
		destination.setFields(Collections.singletonList(id));
		destination.setTableDataDestination(table);
		destination.initialize(new EtlItemConfiguration());

		GenericDatabaseObject record = new GenericDatabaseObject(destination);
		record.setInsertSQLQuestionMarksWithObjectId("?, ?");

		assertSame(destination, record.getRelatedConfiguration());
		assertEquals("?, ?", record.getInsertSQLQuestionMarksWithObjectId());
		assertEquals("?, ?", table.getInsertSQLQuestionMarksWithObjectId());
		assertEquals("patient", record.generateTableName());
	}
}
