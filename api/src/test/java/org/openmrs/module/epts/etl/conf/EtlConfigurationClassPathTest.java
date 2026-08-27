package org.openmrs.module.epts.etl.conf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;
import org.openmrs.module.epts.etl.utilities.ObjectMapperProvider;

public class EtlConfigurationClassPathTest {

	@Test
	public void shouldConfigureMultipleClassPaths() throws Exception {
		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class)
				.readValue("{\"classPath\":[\"lib/api.jar\",\"lib/driver.jar\"]}", EtlConfiguration.class);

		assertEquals(Arrays.asList("lib/api.jar", "lib/driver.jar"), configuration.getClassPath());
		assertEquals(Arrays.asList(new File("lib/api.jar"), new File("lib/driver.jar")),
				configuration.getClassPathAsFiles());
		assertEquals(new File("lib/api.jar"), configuration.getClassPathAsFile());
	}

	@Test
	public void shouldReadLegacySingleClassPathAsAList() throws Exception {
		EtlConfiguration configuration = new ObjectMapperProvider().getContext(EtlConfiguration.class)
				.readValue("{\"classPath\":\"lib/api.jar\"}", EtlConfiguration.class);

		assertEquals(Arrays.asList("lib/api.jar"), configuration.getClassPath());
	}

	@Test
	public void shouldDefensivelyCopyConfiguredClassPaths() {
		EtlConfiguration configuration = new EtlConfiguration();
		java.util.List<String> paths = new java.util.ArrayList<>(Arrays.asList("lib/api.jar"));

		configuration.setClassPath(paths);
		paths.add("lib/late.jar");

		assertEquals(Arrays.asList("lib/api.jar"), configuration.getClassPath());
	}
}
