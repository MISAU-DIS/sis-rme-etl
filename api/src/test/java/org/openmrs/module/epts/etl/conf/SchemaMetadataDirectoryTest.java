package org.openmrs.module.epts.etl.conf;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class SchemaMetadataDirectoryTest {

	@Test
	public void shouldKeepPojoAndSchemaMetadataUnderTheDatabaseModelDirectory() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory(new File("etl-root").getPath());

		File databaseModelDirectory = new File("etl-root", "database-model");
		assertEquals(databaseModelDirectory.getPath(), configuration.getDatabaseModelDirectory().getPath());
		assertEquals(new File(databaseModelDirectory, "bin").getPath(),
				configuration.getPOJOCompiledFilesDirectory().getPath());
		assertEquals(new File(databaseModelDirectory, "src").getPath(),
				configuration.getPOJOSourceFilesDirectory().getPath());
		assertEquals(new File(databaseModelDirectory, "schema-metadata").getPath(),
				configuration.getSchemaMetadataDirectory().getPath());
		assertEquals(configuration.getPOJOSourceFilesDirectory().getParentFile(),
				configuration.getSchemaMetadataDirectory().getParentFile());
	}
}
