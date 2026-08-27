package org.openmrs.module.epts.etl.conf;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class SchemaMetadataDirectoryTest {

	@Test
	public void shouldKeepSchemaMetadataOutsidePojoDirectories() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory(new File("etl-root").getPath());

		assertEquals(new File("etl-root", "schema-metadata").getPath(),
				configuration.getSchemaMetadataDirectory().getPath());
	}
}
