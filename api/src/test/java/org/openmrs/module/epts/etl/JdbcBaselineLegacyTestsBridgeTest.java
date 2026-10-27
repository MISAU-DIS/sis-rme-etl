package org.openmrs.module.epts.etl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.openmrs.module.epts.etl.engine.CheckpointCommitOrderingTest;
import org.openmrs.module.epts.etl.engine.EngineProcessingStrategiesTest;
import org.openmrs.module.epts.etl.engine.EngineStopLifecycleTest;
import org.openmrs.module.epts.etl.engine.ParallelSearchCompletionTest;
import org.openmrs.module.epts.etl.etl.model.persistence.EnginePersistenceCoordinatorTest;
import org.openmrs.module.epts.etl.etl.model.stage.StageAreaPersistenceRequestTest;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeaderTest;
import org.openmrs.module.epts.etl.utilities.db.conn.ConnectionKeepAliveLifecycleTest;

/**
 * Makes the selected JUnit 4 JDBC characterization suite visible to the JUnit 5
 * test runtime without enabling every legacy test globally.
 */
class JdbcBaselineLegacyTestsBridgeTest {

	@Test
	void shouldPreserveTheSelectedJdbcExecutionBaseline() {
		Result result = JUnitCore.runClasses(CheckpointCommitOrderingTest.class,
				EngineProcessingStrategiesTest.class, EngineStopLifecycleTest.class,
				ParallelSearchCompletionTest.class, EnginePersistenceCoordinatorTest.class,
				StageAreaPersistenceRequestTest.class, EtlOperationResultHeaderTest.class,
				ConnectionKeepAliveLifecycleTest.class);

		String failures = result.getFailures().stream().map(Object::toString)
				.collect(Collectors.joining(System.lineSeparator()));
		assertTrue(result.wasSuccessful(), failures);
	}
}
