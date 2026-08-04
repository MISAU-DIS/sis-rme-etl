package org.openmrs.module.epts.etl.conf.types;

/**
 * Defines how the ETL distributes the search, transformation, and persistence
 * stages across processing workers.
 */
public enum ParallelProcessingStrategyType {

	/**
	 * Divides the processing batch into identifier ranges. Each worker performs
	 * search, transformation and persistence within its assigned range.
	 */
	RANGE_PARTITIONING,

	/**
	 * Searches the complete processing batch once and places the resulting source
	 * records into a shared work queue. Workers dynamically consume records from
	 * the queue and perform both transformation and persistence.
	 */
	RESULT_PARTITIONING,

	/**
	 * Searches the complete processing batch once and distributes records through a
	 * shared transformation queue. Multiple workers perform transformations while a
	 * single dedicated worker performs persistence.
	 */
	PARALLEL_TRANSFORM_SERIAL_PERSIST,

	/**
	 * Executes the complete ETL pipeline using a single worker.
	 */
	SINGLE_THREAD;

	public boolean rangePartitioning() {
		return this == RANGE_PARTITIONING;
	}

	public boolean resultPartitioning() {
		return this == RESULT_PARTITIONING;
	}

	public boolean parallelTransformSerialPersist() {
		return this == PARALLEL_TRANSFORM_SERIAL_PERSIST;
	}

	public boolean useSingleThread() {
		return this == SINGLE_THREAD;
	}

	public boolean useMultiThreads() {
		return rangePartitioning() || resultPartitioning() || parallelTransformSerialPersist();
	}

	public boolean useMultiThreadsWithinSharedInterval() {
		return resultPartitioning() || parallelTransformSerialPersist();
	}
}