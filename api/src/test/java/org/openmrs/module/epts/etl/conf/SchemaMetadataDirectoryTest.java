package org.openmrs.module.epts.etl.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;

import org.junit.Test;

public class SchemaMetadataDirectoryTest {

	@Test
	public void shouldKeepPojoAndSchemaMetadataUnderTheDatabaseModelDirectory() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory(new File("etl-root").getPath());
		configuration.getDataModel().init(configuration);

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
		String sourceDirectory = new File("database-model", "pojo/src/main/java").getPath();
		String compiledDirectory = new File("database-model", "pojo/target/classes").getPath();
		configuration.getDataModel().setSrcPojoDirectory(sourceDirectory);
		configuration.getDataModel().setBinPojoDirectory(compiledDirectory);
		configuration.getDataModel().init(configuration);

		assertEquals(new File("etl-root", sourceDirectory).getPath(),
				configuration.getPOJOSourceFilesDirectory().getPath());
		assertEquals(new File("etl-root", compiledDirectory).getPath(),
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
		configuration.getDataModel().init(configuration);

		assertEquals(absoluteSource.getPath(), configuration.getPOJOSourceFilesDirectory().getPath());
		assertEquals(absoluteBin.getPath(), configuration.getPOJOCompiledFilesDirectory().getPath());
	}

	@Test
	public void shouldCalculatePojoDirectoriesOnlyDuringDataModelInitialization() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlRootDirectory(new File("etl-root").getPath());
		configuration.getDataModel().setSrcPojoDirectory("initial-src");
		configuration.getDataModel().setBinPojoDirectory("initial-bin");
		configuration.getDataModel().init(configuration);

		File sourceDirectory = configuration.getPOJOSourceFilesDirectory();
		File compiledDirectory = configuration.getPOJOCompiledFilesDirectory();
		assertSame(configuration.getDataModel().getPOJOSourceFilesDirectory(), sourceDirectory);
		assertSame(configuration.getDataModel().getPOJOCompiledFilesDirectory(), compiledDirectory);

		configuration.getDataModel().setSrcPojoDirectory("changed-src");
		configuration.getDataModel().setBinPojoDirectory("changed-bin");

		assertSame(sourceDirectory, configuration.getPOJOSourceFilesDirectory());
		assertSame(compiledDirectory, configuration.getPOJOCompiledFilesDirectory());
	}
}
