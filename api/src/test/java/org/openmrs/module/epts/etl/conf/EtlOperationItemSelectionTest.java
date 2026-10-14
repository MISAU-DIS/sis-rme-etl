package org.openmrs.module.epts.etl.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openmrs.module.epts.etl.conf.types.EtlOperationType;

class EtlOperationItemSelectionTest {

	@Test
	void shouldApplyWhiteListAndThenBlackListToRootItems() {
		EtlConfiguration configuration = configurationWithItems("patient", "encounter", "observation");
		EtlOperationConfig operation = operation(EtlOperationType.ETL);
		operation.setEtlItemConfigCodeWhiteList(Arrays.asList("patient", "encounter", "observation"));
		operation.setEtlItemConfigCodeBlackList(Arrays.asList("observation"));

		assertEquals(Arrays.asList("patient", "encounter"), codes(configuration.resolveEtlItems(operation)));
	}

	@Test
	void shouldUseAllItemsWhenNoSelectionWasConfigured() {
		EtlConfiguration configuration = configurationWithItems("patient", "encounter");

		assertEquals(Arrays.asList("patient", "encounter"),
				codes(configuration.resolveEtlItems(operation(EtlOperationType.ETL))));
	}

	@Test
	void shouldInheritListsFromParentOperation() {
		EtlConfiguration configuration = configurationWithItems("patient", "encounter", "observation");
		EtlOperationConfig parent = operation(EtlOperationType.ETL);
		parent.setEtlItemConfigCodeWhiteList(Arrays.asList("patient", "encounter"));
		parent.setEtlItemConfigCodeBlackList(Arrays.asList("encounter"));
		EtlOperationConfig child = operation(EtlOperationType.DB_EXTRACT);
		child.setParent(parent);

		assertEquals(Arrays.asList("patient"), codes(configuration.resolveEtlItems(child)));
	}

	@Test
	void shouldValidateUnknownAndDuplicatedRootCodesBeforeExecution() {
		EtlConfiguration configuration = configurationWithItems("patient", "patient");
		EtlOperationConfig operation = operation(EtlOperationType.ETL);
		operation.setEtlItemConfigCodeWhiteList(Arrays.asList("missing"));
		configuration.setOperations(Arrays.asList(operation));

		List<String> issues = configuration.validateEtlItemOperationSelections();

		assertTrue(issues.stream().anyMatch(issue -> issue.contains("'patient' is duplicated")));
		assertTrue(issues.stream().anyMatch(issue -> issue.contains("unknown") && issue.contains("'missing'")));
	}

	@Test
	void shouldAcceptAndNormalizeManualConfigCode() {
		EtlItemConfiguration item = new EtlItemConfiguration();

		item.setConfigCode("  manually_defined_code  ");

		assertTrue(item.hasManualConfigCode());
		assertEquals("manually_defined_code", item.getConfigCode());
	}

	private EtlConfiguration configurationWithItems(String... configCodes) {
		EtlConfiguration configuration = new EtlConfiguration();
		configuration.setEtlItemConfiguration(Arrays.stream(configCodes).map(code -> {
			EtlItemConfiguration item = new EtlItemConfiguration();
			item.setConfigCode(code);
			return item;
		}).collect(Collectors.toList()));
		return configuration;
	}

	private EtlOperationConfig operation(EtlOperationType type) {
		EtlOperationConfig operation = new EtlOperationConfig();
		operation.setOperationType(type);
		return operation;
	}

	private List<String> codes(List<EtlItemConfiguration> items) {
		return items.stream().map(EtlItemConfiguration::getConfigCode).collect(Collectors.toList());
	}
}
