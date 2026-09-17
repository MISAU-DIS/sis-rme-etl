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
		configuration.setConfigFilePath(new File("conf", "main-etl.json").getPath());
		configuration.setOriginAppLocationCode("central");

		assertEquals("etl_on_central_using_main-etl", configuration.generateProcessId());
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
	public void shouldKeepEtlStatusDirectoryStable() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory("etl-root");

		assertEquals("etl-root" + FileUtilities.getPathSeparator() + "process_status"
				+ FileUtilities.getPathSeparator() + "destination" + FileUtilities.getPathSeparator() + "etl",
				configuration.generateProcessStatusFolder());
	}
}
