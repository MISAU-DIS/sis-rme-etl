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

	@Test
	public void shouldAllowSourceAndCompiledPojoStorageInIndependentDirectories() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory(new File("etl-root").getPath());
		configuration.getDataModel().setSrcPojoDirectory(new File("project", "src/main/java").getPath());
		configuration.getDataModel().setBinPojoDirectory(new File("project", "target/classes").getPath());

		assertEquals(new File("etl-root", new File("project", "src/main/java").getPath()).getPath(),
				configuration.getPOJOSourceFilesDirectory().getPath());
		assertEquals(new File("etl-root", new File("project", "target/classes").getPath()).getPath(),
				configuration.getPOJOCompiledFilesDirectory().getPath());
		assertEquals(new File(new File("etl-root", "database-model"), "schema-metadata").getPath(),
				configuration.getSchemaMetadataDirectory().getPath());
	}

	@Test
	public void shouldUseAbsolutePojoDirectoriesAsConfigured() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory(new File("etl-root").getPath());
		File absoluteSource = new File(System.getProperty("java.io.tmpdir"), "etl-pojo-src").getAbsoluteFile();
		File absoluteBin = new File(System.getProperty("java.io.tmpdir"), "etl-pojo-bin").getAbsoluteFile();
		configuration.getDataModel().setSrcPojoDirectory(absoluteSource.getPath());
		configuration.getDataModel().setBinPojoDirectory(absoluteBin.getPath());

		assertEquals(absoluteSource.getPath(), configuration.getPOJOSourceFilesDirectory().getPath());
		assertEquals(absoluteBin.getPath(), configuration.getPOJOCompiledFilesDirectory().getPath());
	}
}
