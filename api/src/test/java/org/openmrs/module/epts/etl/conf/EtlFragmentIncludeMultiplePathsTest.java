package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;

class EtlFragmentIncludeMultiplePathsTest {

	@TempDir
	Path temporaryDirectory;

	@Test
	void shouldAccumulateMultiplePathsFromLeftToRight() throws Exception {
		Path firstDirectory = Files.createDirectories(temporaryDirectory.resolve("first"));
		Path secondDirectory = Files.createDirectories(temporaryDirectory.resolve("second"));
		Files.writeString(firstDirectory.resolve("02.json"), "{\"operationType\":\"DB_EXTRACT\"}");
		Files.writeString(firstDirectory.resolve("01.json"), "{\"operationType\":\"ETL\"}");
		Files.writeString(secondDirectory.resolve("03.json"), "{\"operationType\":\"DB_PREPARATION\"}");

		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlConfDir(temporaryDirectory.toString());
		EtlFragmentInclude include = new EtlFragmentInclude();
		include.setTarget("operations");
		include.setSrcPath(" first/*.json , second/03.json ");

		include.include(configuration);

		assertEquals("ETL,DB_EXTRACT,DB_PREPARATION", configuration.getOperations().stream()
				.map(operation -> operation.getOperationType().name()).collect(Collectors.joining(",")));
	}

	@Test
	void shouldRejectEmptyPathBetweenCommas() {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlConfDir(temporaryDirectory.toString());
		EtlFragmentInclude include = new EtlFragmentInclude();
		include.setTarget("operations");
		include.setSrcPath("first.json, ,second.json");

		assertThrows(EtlConfException.class, () -> include.include(configuration));
	}
}
