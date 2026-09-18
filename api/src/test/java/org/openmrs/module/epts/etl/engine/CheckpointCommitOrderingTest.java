package org.openmrs.module.epts.etl.engine;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.types.ParallelProcessingStrategyType;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadCurrentIntervals;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.db.conn.RecordingConnection;
import org.openmrs.module.epts.etl.utilities.db.conn.RecordingConnection.State;

@RunWith(Parameterized.class)
public class CheckpointCommitOrderingTest {
	@Parameters(name = "{0}, shared={1}")
	public static Collection<Object[]> modes() {
		return Arrays.asList(new Object[][] {
				{ ParallelProcessingStrategyType.SINGLE_THREAD, false },
				{ ParallelProcessingStrategyType.RANGE_PARTITIONING, false },
				{ ParallelProcessingStrategyType.RANGE_PARTITIONING, true },
				{ ParallelProcessingStrategyType.RESULT_PARTITIONING, false },
				{ ParallelProcessingStrategyType.RESULT_PARTITIONING, true },
				{ ParallelProcessingStrategyType.PARALLEL_TRANSFORM_SERIAL_PERSIST, false },
				{ ParallelProcessingStrategyType.PARALLEL_TRANSFORM_SERIAL_PERSIST, true }
		});
	}

	private final TestEngine engine;
	private final EngineProcessingStrategy strategy;

	public CheckpointCommitOrderingTest(ParallelProcessingStrategyType mode, boolean shared) {
		engine = new TestEngine(mode, shared);
		strategy = EngineProcessingStrategies.resolve(mode);
	}

	@Test
	public void checkpointMustFollowAllCommits() throws Exception {
		runStrategy();
		assertNull(engine.failure);
		assertTrue(engine.interval().isProcessed());
		assertEquals(1, Collections.frequency(engine.events, "checkpoint"));
		assertEquals("checkpoint", engine.events.get(engine.events.size() - 1));
		assertTrue(engine.connections.stream().anyMatch(c -> c.state.committed));
	}

	@Test
	public void destinationCommitFailureMustLeaveIntervalPending() {
		engine.failCommit = "dst";
		runExpectingFailure();
		assertFalse(engine.connections.stream().anyMatch(c -> c.state.committed));
	}

	@Test
	public void sourceCommitFailureMustLeaveIntervalPending() {
		engine.failCommit = "src";
		runExpectingFailure();
	}

	@Test
	public void auxiliaryPersistenceFailureMustLeaveIntervalPending() {
		engine.failAuxiliary = true;
		runExpectingFailure();
	}

	@Test
	public void emptyExtractionMustStillWaitForCommit() throws Exception {
		engine.emptyExtraction = true;
		runStrategy();
		assertTrue(engine.interval().isProcessed());
		assertEquals("checkpoint", engine.events.get(engine.events.size() - 1));
	}

	@Test
	public void emptyExtractionCommitFailureMustLeaveIntervalPending() {
		engine.emptyExtraction = true;
		engine.failCommit = "dst";
		runExpectingFailure();
	}

	@Test
	public void sourceOnlyTransactionMustCommitBeforeCheckpoint() throws Exception {
		engine.noDestinationConnection = true;
		runStrategy();
		assertTrue(engine.interval().isProcessed());
		assertEquals("checkpoint", engine.events.get(engine.events.size() - 1));
		assertTrue(engine.connections.stream().anyMatch(c -> c.state.committed));
	}

	private void runStrategy() throws Exception {
		strategy.process(engine, Collections.singletonList(engine.interval()));
	}

	private void runExpectingFailure() {
		Exception thrown = null;
		try {
			runStrategy();
		} catch (Exception e) {
			thrown = e;
		}
		assertTrue("Failure must be reported", thrown != null || engine.failure != null);
		assertFalse("Uncommitted interval must remain pending", engine.interval().isProcessed());
		assertFalse(engine.events.contains("checkpoint"));
		assertTrue(engine.connections.stream().allMatch(OpenConnection::isFinalized));
	}

	private static class TestEngine extends Engine<EtlDatabaseObject> {
		final List<String> events = Collections.synchronizedList(new ArrayList<>());
		final List<RecordingConnection> connections = Collections.synchronizedList(new ArrayList<>());
		final EtlConfiguration configuration = new EtlConfiguration();
		final EtlOperationConfig operation = new EtlOperationConfig();
		final ThreadRecordIntervalsManager<EtlDatabaseObject> intervals;
		final ParallelProcessingStrategyType mode;
		String failCommit;
		boolean failAuxiliary;
		boolean emptyExtraction;
		boolean noDestinationConnection;
		Exception failure;

		TestEngine(ParallelProcessingStrategyType mode, boolean shared) {
			super(null, null, new TableOperationProgressInfo() {
				@Override public String getOperationId() { return "checkpoint-test"; }
			});
			this.mode = mode;
			operation.setUseSharedConnectionPerThread(shared);
			operation.setMaxSupportedProcessors(1);
			intervals = new ThreadRecordIntervalsManager<EtlDatabaseObject>() {
				@Override public synchronized void save() {
					assertTrue("Connections must be finalized before checkpoint",
							connections.stream().allMatch(OpenConnection::isFinalized));
					assertTrue(interval().isProcessed());
					events.add("checkpoint");
				}
			};
			intervals.setCurrentLimits(new ThreadCurrentIntervals(1, 10, 1));
		}

		IntervalExtremeRecord interval() { return intervals.getCurrentLimits().getInternalIntervals().get(0); }
		@Override public EtlConfiguration getRelatedEtlConf() { return configuration; }
		@Override public EtlOperationConfig getRelatedEtlOperationConfig() { return operation; }
		@Override public ParallelProcessingStrategyType getParallelProcessingStrategy() { return mode; }
		@Override public ThreadRecordIntervalsManager<EtlDatabaseObject> getThreadRecordIntervalsManager() { return intervals; }
		@Override void stopOperationDueError(Exception e) { failure = e; }
		@Override public OpenConnection openSrcConn(BaseConfiguration owner) { return connection("src"); }
		@Override public OpenConnection tryToOpenDstConn(BaseConfiguration owner) {
			return noDestinationConnection ? null : connection("dst");
		}

		private OpenConnection connection(String name) {
			State state = new State();
			state.failCommit = name.equals(failCommit);
			RecordingConnection connection = new RecordingConnection(this, name, events, state);
			connections.add(connection);
			return connection;
		}

		@Override public List<EtlDatabaseObject> extract(IntervalExtremeRecord interval, Connection src, Connection dst) {
			if (emptyExtraction) return Collections.emptyList();
			EtlDatabaseObject record = (EtlDatabaseObject) Proxy.newProxyInstance(getClass().getClassLoader(),
					new Class<?>[] { EtlDatabaseObject.class }, (proxy, method, args) -> {
						if (method.getName().equals("createACopy")) return proxy;
						return null;
					});
			return Collections.singletonList(record);
		}

		@Override void completeExtractedTask(TaskProcessor<EtlDatabaseObject> processor, OpenConnection src,
				OpenConnection dst, boolean refresh) {
			// SQL work is stubbed; transaction and checkpoint lifecycle remain real.
			processor.changeStatusToFinished();
		}

		@Override void flushPendingPersistence(Connection src, Connection dst) {
			if (failAuxiliary) throw new IllegalStateException("Auxiliary persistence failed");
		}
		@Override void flushPendingPersistenceUsingDedicatedConnection() {
			flushPendingPersistence(null, null);
		}

		@Override TaskProcessor<EtlDatabaseObject> initConcurrentTaskProcessor(IntervalExtremeRecord interval, int index) {
			return initTaskProcessor(interval, true, "worker-" + index);
		}
		@Override TaskProcessor<EtlDatabaseObject> initTaskProcessor(IntervalExtremeRecord interval, boolean concurrent,
				String processorId) {
			TaskProcessor<EtlDatabaseObject> processor = new TaskProcessor<EtlDatabaseObject>(this, interval, concurrent) {
				@Override public void extractTransformAndLoad(boolean multi, Connection src, Connection dst) {}
				@Override public void transformAndLoadExtractedRecords(List<EtlDatabaseObject> records, Connection src, Connection dst) {}
				@Override public void transformExtractedRecords(List<EtlDatabaseObject> records, Connection src, Connection dst) {}
				@Override public void loadTransformedRecords(List<EtlDatabaseObject> records, Connection src, Connection dst) {}
				@Override public void transformAndLoad(List<EtlDatabaseObject> records, Connection src, Connection dst) throws DBException {}
				@Override public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(IntervalExtremeRecord limits) {
					throw new UnsupportedOperationException();
				}
			};
			processor.setProcessorId(processorId);
			return processor;
		}
	}
}
