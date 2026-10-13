package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Characterizes the public JDBC configuration contract before connector-neutral
 * configuration is introduced. These assertions are compatibility requirements,
 * not a recommendation that future connector types use the same property names.
 */
class JdbcConfigurationCompatibilityTest {

	private final ObjectMapper mapper = new ObjectMapperProvider().getContext(EtlConfiguration.class);

	@Test
	void shouldKeepReadingTheExistingJdbcConnectionProperties() throws Exception {
		String json = "{" +
				"\"dataBaseUserName\":\"etl-user\"," +
				"\"dataBaseUserPassword\":\"etl-password\"," +
				"\"connectionURI\":\"jdbc:mysql://localhost:3306/openmrs\"," +
				"\"driveClassName\":\"com.mysql.cj.jdbc.Driver\"," +
				"\"schema\":\"openmrs\"," +
				"\"defaultAutoCommit\":false," +
				"\"maxActiveConnections\":12" +
				"}";

		DBConnectionInfo connection = mapper.readValue(json, DBConnectionInfo.class);

		assertEquals("etl-user", connection.getDataBaseUserName());
		assertEquals("etl-password", connection.getDataBaseUserPassword());
		assertEquals("jdbc:mysql://localhost:3306/openmrs", connection.getConnectionURI());
		assertEquals("com.mysql.cj.jdbc.Driver", connection.getDriveClassName());
		assertEquals("openmrs", connection.getSchema());
		assertEquals(12, connection.getMaxActiveConnections());
		assertFalse(connection.isAutoCommit());
	}

	@Test
	void shouldKeepTableNameAsTheConfiguredJdbcResourceName() throws Exception {
		SrcConf source = mapper.readValue("{\"tableName\":\"patient\"}", SrcConf.class);
		DstConf destination = mapper.readValue("{\"tableName\":\"patient_stage\"}", DstConf.class);

		assertEquals("patient", source.getTableName());
		assertEquals("patient", source.getName());
		assertEquals("patient_stage", destination.getTableName());
		assertEquals("patient_stage", destination.getName());
	}

	@Test
	void shouldKeepExistingNonDatabaseDestinationClassification() {
		assertTrue(org.openmrs.module.epts.etl.conf.types.EtlDstType.json.isFile());
		assertTrue(org.openmrs.module.epts.etl.conf.types.EtlDstType.csv.isFile());
		assertTrue(org.openmrs.module.epts.etl.conf.types.EtlDstType.dump.isFile());
		assertTrue(org.openmrs.module.epts.etl.conf.types.EtlDstType.console.isInstantaneo());
		assertTrue(org.openmrs.module.epts.etl.conf.types.EtlDstType.popup.isInstantaneo());
		assertTrue(org.openmrs.module.epts.etl.conf.types.EtlDstType.db.isDefault());
	}
}
