package org.openmrs.module.epts.etl.etl.model.stage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public class StageAreaPersistenceRequestTest {

	@Test
	public void shouldDefensivelyCopyTheSourceObjectList() {
		Object owner = new Object();
		List<EtlDatabaseObject> sourceObjects = new ArrayList<>();
		sourceObjects.add(null);

		StageAreaPersistenceRequest request = new StageAreaPersistenceRequest(owner, sourceObjects);
		sourceObjects.clear();

		assertSame(owner, request.getOwner());
		assertEquals(1, request.getSourceObjects().size());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldExposeAnUnmodifiableSourceObjectList() {
		StageAreaPersistenceRequest request = new StageAreaPersistenceRequest(new Object(),
				new ArrayList<EtlDatabaseObject>());

		request.getSourceObjects().add(null);
	}

	@Test
	public void shouldKeepPendingRequestsIsolatedByOwner() {
		Object firstOwner = new Object();
		Object secondOwner = new Object();
		StageAreaPersistenceCoordinator coordinator = new StageAreaPersistenceCoordinator();

		coordinator.register(firstOwner, Arrays.asList(null, null));
		coordinator.register(secondOwner, Arrays.asList((EtlDatabaseObject) null));

		assertEquals(2, coordinator.pendingCount(firstOwner));
		assertEquals(1, coordinator.pendingCount(secondOwner));
		assertEquals(3, coordinator.pendingCount());

		coordinator.discard(firstOwner);

		assertEquals(0, coordinator.pendingCount(firstOwner));
		assertEquals(1, coordinator.pendingCount(secondOwner));
		assertEquals(1, coordinator.pendingCount());
	}
}
