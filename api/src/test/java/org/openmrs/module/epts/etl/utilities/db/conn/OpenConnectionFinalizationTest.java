package org.openmrs.module.epts.etl.utilities.db.conn;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openmrs.module.epts.etl.utilities.db.conn.RecordingConnection.State;

public class OpenConnectionFinalizationTest {

	@Test
	public void commitFailureMustPropagateAndRollbackRemainingConnection() {
		List<String> events = new ArrayList<>();
		State destination = new State();
		destination.failCommit = true;
		State source = new State();
		OpenConnection dst = new RecordingConnection(null, "dst", events, destination);
		OpenConnection src = new RecordingConnection(null, "src", events, source);
		OpenConnection.markAllAsSuccessifullyTerminected(dst, src);

		RuntimeException failure = assertThrows(RuntimeException.class,
				() -> OpenConnection.finalizeAllConnections(null, dst, src));

		assertEquals("dst commit failed", failure.getCause().getMessage());
		assertEquals(Arrays.asList("dst:commit", "dst:close", "src:rollback", "src:close"), events);
		assertFalse(source.committed);
		assertTrue(dst.isFinalized());
		assertTrue(src.isFinalized());
		OpenConnection.finalizeAllConnections(null, dst, src);
		assertEquals(4, events.size());
	}

	@Test
	public void cleanupFailuresMustNotHideCommitFailure() {
		List<String> events = new ArrayList<>();
		State destination = new State();
		destination.failCommit = true;
		destination.failClose = true;
		State source = new State();
		source.failClose = true;
		OpenConnection dst = new RecordingConnection(null, "dst", events, destination);
		OpenConnection src = new RecordingConnection(null, "src", events, source);
		OpenConnection.markAllAsSuccessifullyTerminected(dst, src);

		RuntimeException failure = assertThrows(RuntimeException.class,
				() -> OpenConnection.finalizeAllConnections(null, dst, src));

		assertEquals("dst commit failed", failure.getCause().getMessage());
		assertEquals(2, failure.getSuppressed().length);
		assertTrue(source.rolledBack);
		assertTrue(dst.isFinalized());
		assertTrue(src.isFinalized());
	}

	@Test
	public void alreadyClosedConnectionMustNotBeReportedAsCommitted() {
		State state = new State();
		state.closed = true;
		OpenConnection conn = new RecordingConnection(null, "dst", new ArrayList<>(), state);
		conn.markAsSuccessifullyTerminated();
		assertThrows(RuntimeException.class, () -> conn.finalizeConnection(null));
		assertFalse(state.committed);
	}

	@Test
	public void autoCommitConnectionMustOnlyBeClosed() {
		List<String> events = new ArrayList<>();
		State state = new State();
		state.autoCommit = true;
		OpenConnection conn = new RecordingConnection(null, "dst", events, state);
		conn.markAsSuccessifullyTerminated();
		conn.finalizeConnection(null);
		assertEquals(Arrays.asList("dst:close"), events);
	}
}
