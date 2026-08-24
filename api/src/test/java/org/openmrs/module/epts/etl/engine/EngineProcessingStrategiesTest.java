package org.openmrs.module.epts.etl.engine;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openmrs.module.epts.etl.conf.types.ParallelProcessingStrategyType;

public class EngineProcessingStrategiesTest {

	@Test
	public void shouldResolveEachProcessingStrategyToItsOwnImplementation() {
		assertTrue(EngineProcessingStrategies.resolve(ParallelProcessingStrategyType.SINGLE_THREAD)
				instanceof SingleThreadProcessingStrategy);
		assertTrue(EngineProcessingStrategies.resolve(ParallelProcessingStrategyType.RANGE_PARTITIONING)
				instanceof RangePartitioningProcessingStrategy);
		assertTrue(EngineProcessingStrategies.resolve(ParallelProcessingStrategyType.RESULT_PARTITIONING)
				instanceof ResultPartitioningProcessingStrategy);
		assertTrue(EngineProcessingStrategies.resolve(
				ParallelProcessingStrategyType.PARALLEL_TRANSFORM_SERIAL_PERSIST)
				instanceof ParallelTransformSerialPersistProcessingStrategy);
	}
}
