package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

class EtlOperationIdentifierTest {

	@Test
	void shouldLoadAndNormalizeConfiguredIdentifier() throws Exception {
		EtlOperationConfig operation = new ObjectMapperProvider().getContext(EtlOperationConfig.class)
				.readValue("{\"identifier\":\"  patient-migration  \"}", EtlOperationConfig.class);

		assertEquals("patient-migration", operation.getIdentifier());
	}
}
