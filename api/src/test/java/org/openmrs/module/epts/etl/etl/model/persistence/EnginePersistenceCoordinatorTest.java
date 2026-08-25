package org.openmrs.module.epts.etl.etl.model.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import org.junit.Test;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class EnginePersistenceCoordinatorTest {

	@Test
	public void shouldFlushOnlyTheSelectedPersistenceType() throws DBException {
		Object owner = new Object();
		TestRequest stageArea = new TestRequest(PersistenceType.STAGE_AREA, 2, false);
		TestRequest defaultParent = new TestRequest(PersistenceType.DEFAULT_PARENT, 3, false);
		EnginePersistenceCoordinator coordinator = new EnginePersistenceCoordinator();

		coordinator.register(owner, stageArea);
		coordinator.register(owner, defaultParent);
		coordinator.flush(PersistenceType.STAGE_AREA, null, null);

		assertTrue(stageArea.persisted);
		assertFalse(defaultParent.persisted);
		assertEquals(0, coordinator.pendingCount(PersistenceType.STAGE_AREA));
		assertEquals(3, coordinator.pendingCount(PersistenceType.DEFAULT_PARENT));
	}

	@Test
	public void shouldRestoreDrainedRequestsWhenPersistenceFails() {
		Object owner = new Object();
		EnginePersistenceCoordinator coordinator = new EnginePersistenceCoordinator();
		coordinator.register(owner, new TestRequest(PersistenceType.DEFAULT_PARENT, 2, true));

		try {
			coordinator.flush(PersistenceType.DEFAULT_PARENT, null, null);
		} catch (DBException expected) {
			assertEquals(2, coordinator.pendingCount(owner, PersistenceType.DEFAULT_PARENT));
			return;
		}

		throw new AssertionError("Expected the persistence failure");
	}

	private static final class TestRequest implements EnginePersistenceRequest {
		private final PersistenceType type;
		private final int size;
		private final boolean fail;
		private boolean persisted;

		private TestRequest(PersistenceType type, int size, boolean fail) {
			this.type = type;
			this.size = size;
			this.fail = fail;
		}

		@Override
		public PersistenceType getType() {
			return type;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void persist(Connection srcConn, Connection dstConn) throws DBException {
			if (fail) {
				throw new DBException("Expected test failure", null);
			}
			persisted = true;
		}
	}
}
