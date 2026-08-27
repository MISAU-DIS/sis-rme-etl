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
		File javaDirectory = new File(databaseModelDirectory, "java");
		assertEquals(databaseModelDirectory.getPath(), configuration.getDatabaseModelDirectory().getPath());
		assertEquals(javaDirectory.getPath(), configuration.getDatabaseModelJavaDirectory().getPath());
		assertEquals(new File(javaDirectory, "bin").getPath(),
				configuration.getPOJOCompiledFilesDirectory().getPath());
		assertEquals(new File(javaDirectory, "src").getPath(),
				configuration.getPOJOSourceFilesDirectory().getPath());
		assertEquals(new File(databaseModelDirectory, "schema-metadata").getPath(),
				configuration.getSchemaMetadataDirectory().getPath());
		assertEquals(configuration.getDatabaseModelJavaDirectory().getParentFile(),
				configuration.getSchemaMetadataDirectory().getParentFile());
	}
}
