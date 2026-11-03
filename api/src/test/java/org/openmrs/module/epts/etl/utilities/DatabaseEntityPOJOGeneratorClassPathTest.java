package org.openmrs.module.epts.etl.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseEntityPOJOGeneratorClassPathTest {

	@TempDir
	Path temporaryDirectory;

	@Test
	void shouldNormalizeDirectoryAndIgnoreIndividualClassFiles() throws Exception {
		Path classesDirectory = Files.createDirectories(temporaryDirectory.resolve("target/classes"));
		Path packageDirectory = Files.createDirectories(classesDirectory.resolve("example/model"));
		Path classFile = Files.createFile(packageDirectory.resolve("Person.class"));
		Path archive = Files.createFile(Files.createDirectories(classesDirectory.resolve("lib")).resolve("support.jar"));
		Path configuredPath = packageDirectory.resolve("../..");
		List<File> classPath = new ArrayList<>();

		DatabaseEntityPOJOGenerator.addAllToClassPath(classPath, configuredPath.toFile());

		File normalizedClassesDirectory = classesDirectory.toAbsolutePath().normalize().toFile();
		assertEquals(normalizedClassesDirectory, classPath.get(0));
		assertTrue(classPath.contains(archive.toAbsolutePath().normalize().toFile()));
		assertFalse(classPath.contains(classFile.toAbsolutePath().normalize().toFile()));
	}

	@Test
	void shouldPreserveRuntimeDependenciesWhenConfiguringCompilerClassPath() throws Exception {
		Path outputDirectory = Files.createDirectories(temporaryDirectory.resolve("compiled"));
		Path sourceFile = temporaryDirectory.resolve("CompileProbe.java");
		Files.write(sourceFile, ("import com.fasterxml.jackson.annotation.JsonIgnore;\n"
				+ "public class CompileProbe { @JsonIgnore public String value; }\n")
						.getBytes(StandardCharsets.UTF_8));
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(outputDirectory.toFile()));
			fileManager.setLocation(StandardLocation.CLASS_PATH, DatabaseEntityPOJOGenerator.resolveCompilationClassPath(
					fileManager, outputDirectory.toFile(), Collections.<File>emptyList()));

			Boolean compiled = compiler.getTask(null, fileManager, null, null, null,
					fileManager.getJavaFileObjects(sourceFile.toFile())).call();

			assertTrue(Boolean.TRUE.equals(compiled));
		}
	}
}
