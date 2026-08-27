package org.openmrs.module.epts.etl.databasemodelgeneration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.types.EtlOperationType;
import org.openmrs.module.epts.etl.conf.types.EtlProcessType;

public class DatabaseModelGenerationCompatibilityTest {

	@Test
	public void shouldRecognizeCanonicalOperationAndProcessNames() {
		EtlOperationConfig operation = new EtlOperationConfig();
		operation.setOperationType(EtlOperationType.DATABASE_MODEL_GENERATION);
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setProcessType(EtlProcessType.DATABASE_MODEL_GENERATION);

		assertTrue(operation.isDatabaseModelGeneration());
		assertTrue(configuration.isDatabaseModelGeneration());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldKeepLegacyPojoGenerationNamesAsAliases() {
		assertTrue(EtlOperationType.POJO_GENERATION.isDatabaseModelGeneration());
		assertTrue(EtlProcessType.POJO_GENERATION.isDatabaseModelGeneration());
		assertTrue(EtlOperationType.isDatabaseModelGeneration("POJO_GENERATION"));
	}
}
