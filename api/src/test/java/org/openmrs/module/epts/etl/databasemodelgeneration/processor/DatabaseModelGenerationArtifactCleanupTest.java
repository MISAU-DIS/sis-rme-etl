package org.openmrs.module.epts.etl.databasemodelgeneration.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.physical.PhysicalTableIdentity;
import org.openmrs.module.epts.etl.conf.types.EtlOperationType;

public class DatabaseModelGenerationArtifactCleanupTest {

	private static final String POJO_ROOT = "org/openmrs/module/epts/etl/model/pojo";

	@TempDir
	Path temporaryFolder;

	@Test
	public void shouldUseLiveSchemaWithoutRemovingExistingArtifactsDuringOverrideGeneration() throws Exception {
		EtlConfiguration configuration = configuration(true);
		Path metadata = createFile(configuration.getSchemaMetadataDirectory().toPath().resolve("model/patient.json"));
		Path sourcePojo = createPojo(configuration.getPOJOSourceFilesDirectory(), "openmrs_2_6.sesp",
				"PatientVO.java");
		Path compiledPojo = createPojo(configuration.getPOJOCompiledFilesDirectory(), "openmrs_2_6.sisrme",
				"PatientVO.class");
		Path unrelatedSource = createPojo(configuration.getPOJOSourceFilesDirectory(), "custom.keep",
				"KeepVO.java");
		configuration.getPhysicalTableConfigurationRegistry().getOrCreate(
				new PhysicalTableIdentity("jdbc:test", "user", "", "openmrs", "patient"));

		DatabaseModelGenerationProcessor.prepareForRegeneration(configuration);

		assertTrue(configuration.isDatabaseModelRegenerationMode());
		assertTrue(Files.exists(metadata));
		assertTrue(Files.exists(sourcePojo));
		assertTrue(Files.exists(compiledPojo));
		assertTrue(Files.exists(unrelatedSource));
		assertEquals(0, configuration.getPhysicalTableConfigurationRegistry().size());
	}

	@Test
	public void shouldPreserveNormalMetadataLoadingWhenOverrideIsDisabled() throws Exception {
		EtlConfiguration configuration = configuration(false);
		Path metadata = createFile(configuration.getSchemaMetadataDirectory().toPath().resolve("model/patient.json"));
		Path sourcePojo = createPojo(configuration.getPOJOSourceFilesDirectory(), "openmrs_2_6.sesp",
				"PatientVO.java");

		DatabaseModelGenerationProcessor.prepareForRegeneration(configuration);

		assertFalse(configuration.isDatabaseModelRegenerationMode());
		assertTrue(Files.exists(metadata));
		assertTrue(Files.exists(sourcePojo));
	}

	private EtlConfiguration configuration(boolean override) throws Exception {
		EtlConfiguration configuration = new EtlConfiguration();
		File root = Files.createTempDirectory(temporaryFolder, "data-model-").toFile();
		configuration.setEtlRootDirectory(root.getAbsolutePath());
		configuration.getDataModel().setOverrideExistingDataModelElement(override);
		configuration.getDataModel().setSrcPojoPackageName("openmrs_2_6.sesp");
		configuration.getDataModel().setDstPojoPackageName("openmrs_2_6.sisrme");
		configuration.getDataModel().setSrcPojoDirectory(new File(root, "java-src").getAbsolutePath());
		configuration.getDataModel().setBinPojoDirectory(new File(root, "java-bin").getAbsolutePath());
		configuration.getDataModel().init(configuration);

		EtlOperationConfig operation = new EtlOperationConfig();
		operation.setOperationType(EtlOperationType.DATABASE_MODEL_GENERATION);
		configuration.setOperations(Collections.singletonList(operation));
		return configuration;
	}

	private Path createPojo(File root, String packageName, String fileName) throws Exception {
		return createFile(root.toPath().resolve(POJO_ROOT).resolve(packageName.replace('.', '/')).resolve(fileName));
	}

	private Path createFile(Path path) throws Exception {
		Files.createDirectories(path.getParent());
		Files.write(path, Collections.singletonList("generated"), StandardCharsets.UTF_8);
		return path;
	}
}
