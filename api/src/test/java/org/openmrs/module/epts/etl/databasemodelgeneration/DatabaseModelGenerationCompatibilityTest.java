package org.openmrs.module.epts.etl.databasemodelgeneration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.types.EtlOperationType;

public class DatabaseModelGenerationCompatibilityTest {

	@Test
	public void shouldRecognizeCanonicalOperationName() {
		EtlOperationConfig operation = new EtlOperationConfig();
		operation.setOperationType(EtlOperationType.DATABASE_MODEL_GENERATION);

		assertTrue(operation.isDatabaseModelGeneration());
	}
}
