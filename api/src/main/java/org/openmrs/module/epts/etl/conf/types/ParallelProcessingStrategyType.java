package org.openmrs.module.epts.etl.conf.types;

import java.util.List;

import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;

/**
 * Defines how the ETL distributes the search, transformation, and persistence
 * stages across processing workers.
 */
public enum ParallelProcessingStrategyType {

	/**
	 * Divides the processing batch into identifier ranges. Each worker performs
	 * search, transformation and persistence within its assigned range.
	 */
	RANGE_PARTITIONING(true, false, false) {

		@Override
		public void process(Engine<?> engine, List<IntervalExtremeRecord> intervals) throws Exception {

			engine.processRangePartitionedIntervals(intervals);
		}
	},

	/**
	 * Searches the complete processing batch once and places the resulting source
	 * records into a shared work queue. Workers dynamically consume records from
	 * the queue and perform both transformation and persistence.
	 */
	RESULT_PARTITIONING(true, true, false) {

		@Override
		public void process(Engine<?> engine, List<IntervalExtremeRecord> intervals) throws Exception {

			engine.processResultPartitionedIntervals(this, intervals);
		}
	},

	/**
	 * Searches the complete processing batch once and distributes records through a
	 * shared transformation queue. Multiple workers perform transformations while a
	 * single dedicated worker performs persistence.
	 */
	PARALLEL_TRANSFORM_SERIAL_PERSIST(true, true, true) {

		@Override
		public void process(Engine<?> engine, List<IntervalExtremeRecord> intervals) throws Exception {

			engine.processResultPartitionedIntervals(this, intervals);
		}
	},

	/**
	 * Executes the complete ETL pipeline using a single worker.
	 */
	SINGLE_THREAD(false, false, true) {

		@Override
		public void process(Engine<?> engine, List<IntervalExtremeRecord> intervals) throws Exception {

			engine.processIntervalsInSingleProcessor(intervals);
		}
	};

	private final boolean multiThreaded;

	private final boolean sharedTransformationQueue;

	private final boolean singlePersistenceWorker;

	ParallelProcessingStrategyType(boolean multiThreaded, boolean sharedTransformationQueue,
			boolean singlePersistenceWorker) {

		this.multiThreaded = multiThreaded;
		this.sharedTransformationQueue = sharedTransformationQueue;
		this.singlePersistenceWorker = singlePersistenceWorker;
	}

	public abstract void process(Engine<?> engine, List<IntervalExtremeRecord> intervals) throws Exception;

	/**
	 * Indicates whether this strategy uses multiple processing workers.
	 *
	 * @return {@code true} when multiple workers are used
	 */
	public boolean isMultiThreaded() {
		return multiThreaded;
	}

	/**
	 * Indicates whether source records are distributed through a shared queue for
	 * transformation.
	 *
	 * @return {@code true} when transformation workers consume records from a
	 *         shared queue
	 */
	public boolean usesSharedTransformationQueue() {
		return sharedTransformationQueue;
	}

	/**
	 * Indicates whether persistence is performed by a single worker.
	 *
	 * @return {@code true} when only one worker performs persistence
	 */
	public boolean usesSinglePersistenceWorker() {
		return singlePersistenceWorker;
	}

	/**
	 * Indicates whether search intervals are partitioned by identifier ranges.
	 *
	 * @return {@code true} for range-partitioned processing
	 */
	public boolean isRangePartitioning() {
		return this == RANGE_PARTITIONING;
	}

	/**
	 * Indicates whether transformation and persistence are both performed by
	 * workers consuming records from a shared queue.
	 *
	 * @return {@code true} for result-partitioned processing
	 */
	public boolean isResultPartitioning() {
		return this == RESULT_PARTITIONING;
	}

	/**
	 * Indicates whether transformations are performed in parallel while persistence
	 * is performed serially.
	 *
	 * @return {@code true} for parallel transformation with serial persistence
	 */
	public boolean isParallelTransformSerialPersist() {
		return this == PARALLEL_TRANSFORM_SERIAL_PERSIST;
	}

	/**
	 * Indicates whether the complete ETL pipeline is executed by a single worker.
	 *
	 * @return {@code true} for single-thread processing
	 */
	public boolean isSingleThread() {
		return this == SINGLE_THREAD;
	}

	/**
	 * Writes the initialization message associated with this processing strategy.
	 *
	 * @param engine active ETL engine
	 */
	public void logInitialization(Engine<?> engine) {
		engine.logDebug("INITIALIZING {} PROCESSING", this);
	}
}