package org.openmrs.module.epts.etl.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class EclipseJavaSourceFormatterTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void shouldLeaveSourceUnchangedWhenNoProfileIsConfigured() throws Exception {
		String source = "public class Sample{int value;}";
		assertEquals(source, EclipseJavaSourceFormatter.format(source, null));
	}

	@Test
	public void shouldFormatSourceUsingExportedEclipseProfile() throws Exception {
		File profile = temporaryFolder.newFile("formatter.xml");
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<profiles version=\"13\"><profile kind=\"CodeFormatterProfile\" name=\"ETL\" version=\"13\">"
				+ "<setting id=\"org.eclipse.jdt.core.formatter.tabulation.char\" value=\"space\"/>"
				+ "<setting id=\"org.eclipse.jdt.core.formatter.tabulation.size\" value=\"2\"/>"
				+ "</profile></profiles>";
		Files.write(profile.toPath(), xml.getBytes(StandardCharsets.UTF_8));

		String formatted = EclipseJavaSourceFormatter
				.format("package example;public class Sample{public void run(){int value=1;}}", profile);

		assertTrue(formatted.contains("public class Sample {"));
		assertTrue(formatted.contains(System.lineSeparator() + "  public void run()"));
	}
}
