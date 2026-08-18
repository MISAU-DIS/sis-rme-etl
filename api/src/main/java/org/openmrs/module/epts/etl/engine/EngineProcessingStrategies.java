package org.openmrs.module.epts.etl.engine;

import org.openmrs.module.epts.etl.conf.types.ParallelProcessingStrategyType;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;

/** Resolves the implementation associated with a configured strategy type. */
public final class EngineProcessingStrategies {

	private static final EngineProcessingStrategy SINGLE_THREAD = new SingleThreadProcessingStrategy();

	private static final EngineProcessingStrategy RANGE_PARTITIONING = new RangePartitioningProcessingStrategy();

	private static final EngineProcessingStrategy RESULT_PARTITIONING = new ResultPartitioningProcessingStrategy();

	private static final EngineProcessingStrategy PARALLEL_TRANSFORM_SERIAL_PERSIST =
			new ParallelTransformSerialPersistProcessingStrategy();

	private EngineProcessingStrategies() {
	}

	public static EngineProcessingStrategy resolve(ParallelProcessingStrategyType type) {
		if (type.isSingleThread()) {
			return SINGLE_THREAD;
		}
		if (type.isRangePartitioning()) {
			return RANGE_PARTITIONING;
		}
		if (type.isResultPartitioning()) {
			return RESULT_PARTITIONING;
		}
		if (type.isParallelTransformSerialPersist()) {
			return PARALLEL_TRANSFORM_SERIAL_PERSIST;
		}

		throw new ForbiddenOperationException("Unsupported processing strategy: " + type);
	}
}
