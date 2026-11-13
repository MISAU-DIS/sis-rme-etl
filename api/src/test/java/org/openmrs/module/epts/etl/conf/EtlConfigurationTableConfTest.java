package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.types.EtlSide;

public class EtlConfigurationTableConfTest {

	@Test
	public void shouldUseSourceSideForInternalEtlStateTables() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setSyncStageSchema("etl_stage");

		EtlConfigurationTableConf table = new EtlConfigurationTableConf("person_src_stage", configuration);

		assertEquals(EtlSide.SRC, table.requireEtlSide());
	}
}
