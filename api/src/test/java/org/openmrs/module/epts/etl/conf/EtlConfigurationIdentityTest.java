package org.openmrs.module.epts.etl.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;

import org.junit.Test;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

public class EtlConfigurationIdentityTest {

	@Test
	public void shouldGenerateStableEtlExecutionId() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setConfigFilePath(new File("conf", "main-workflow.json").getPath());
		configuration.setOriginAppLocationCode("central");

		assertEquals("main-workflow_on_central", configuration.generateProcessId());
	}

	@Test
	public void shouldUseConfigurationFileAsIdentity() {
		EtlConfiguration first = new EtlConfiguration();
		first.setConfigFilePath(new File("conf", "first.json").getPath());
		EtlConfiguration same = new EtlConfiguration();
		same.setConfigFilePath(new File("conf", "first.json").getPath());
		EtlConfiguration other = new EtlConfiguration();
		other.setConfigFilePath(new File("conf", "other.json").getPath());

		assertEquals(first, same);
		assertEquals(first.hashCode(), same.hashCode());
		assertNotEquals(first, other);
	}

	@Test
	public void shouldNotAddAProcessTypeToStatusDirectory() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory("work-root");
		configuration.setConfigFilePath(new File("conf", "main.json").getPath());

		assertEquals("work-root" + FileUtilities.getPathSeparator() + "process_status"
				+ FileUtilities.getPathSeparator() + "destination",
				configuration.generateProcessStatusFolder());
	}
}
