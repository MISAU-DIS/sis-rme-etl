package org.openmrs.module.epts.etl.conf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;

public class EtlTemplateConfigurationTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void shouldRejectDuplicatedTemplateNamesWithinTheSameFile() throws Exception {
		File templatesFile = temporaryFolder.newFile("templates.json");
		write(templatesFile, "[{\"name\":\"duplicated\"},{\"name\":\"duplicated\"}]");

		assertDuplicatedTemplateIsRejected(configuration(templatesFile.getName()), "duplicated");
	}

	@Test
	public void shouldRejectDuplicatedTemplateNamesAcrossDifferentFiles() throws Exception {
		File templatesDir = temporaryFolder.newFolder("templates");
		write(new File(templatesDir, "first.json"), "{\"name\":\"duplicated\"}");
		write(new File(templatesDir, "second.json"), "{\"name\":\"duplicated\"}");

		assertDuplicatedTemplateIsRejected(configuration(templatesDir.getName()), "duplicated");
	}

	private EtlConfiguration configuration(String templatesPath) {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlConfDir(temporaryFolder.getRoot().getAbsolutePath());
		configuration.setTemplatesDir(templatesPath);
		return configuration;
	}

	private void assertDuplicatedTemplateIsRejected(EtlConfiguration configuration, String templateName) {
		try {
			EtlTemplateConfiguration.findTemplate(configuration, templateName);
			fail("Expected duplicated template names to be rejected");
		} catch (EtlExceptionImpl e) {
			assertTrue(e.getMessage().contains("Duplicated template names"));
			assertTrue(e.getMessage().contains(templateName));
		}
	}

	private void write(File file, String content) throws Exception {
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}
}
