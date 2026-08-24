package org.openmrs.module.epts.etl.etl.model.stage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Collects stage-area information produced by concurrent processors and writes
 * it at an engine synchronization point.
 * <p>
 * JDBC connections are deliberately supplied only to
 * {@link #flush(Connection, Connection)} and are never retained by this object.
 */
public class StageAreaPersistenceCoordinator {

	private Map<Object, List<StageAreaPersistenceRequest>> pendingByOwner = new LinkedHashMap<>();

	private final ReentrantLock flushLock = new ReentrantLock();

	public synchronized void register(Object owner, List<EtlDatabaseObject> sourceObjects) {
		if (sourceObjects == null || sourceObjects.isEmpty()) {
			return;
		}

		List<StageAreaPersistenceRequest> ownerPending = pendingByOwner.get(owner);

		if (ownerPending == null) {
			ownerPending = new ArrayList<>();
			pendingByOwner.put(owner, ownerPending);
		}

		ownerPending.add(new StageAreaPersistenceRequest(owner, sourceObjects));
	}

	public void flush(Connection srcConn, Connection dstConn) throws DBException {
		flushLock.lock();
		try {
			drainAndPersist(srcConn, dstConn);
		} finally {
			flushLock.unlock();
		}
	}

	public void flush(Object owner, Connection srcConn, Connection dstConn) throws DBException {
		flushLock.lock();
		try {
			drainAndPersist(owner, srcConn, dstConn);
		} finally {
			flushLock.unlock();
		}
	}

	private void drainAndPersist(Connection srcConn, Connection dstConn) throws DBException {
		Map<Object, List<StageAreaPersistenceRequest>> toPersist;

		synchronized (this) {
			if (pendingByOwner.isEmpty()) {
				return;
			}

			toPersist = pendingByOwner;
			pendingByOwner = new LinkedHashMap<>();
		}

		persist(toPersist, srcConn, dstConn);
	}

	private void drainAndPersist(Object owner, Connection srcConn, Connection dstConn) throws DBException {
		Map<Object, List<StageAreaPersistenceRequest>> toPersist = new LinkedHashMap<>();

		synchronized (this) {
			List<StageAreaPersistenceRequest> ownerPending = pendingByOwner.remove(owner);
			if (ownerPending == null || ownerPending.isEmpty()) {
				return;
			}

			toPersist.put(owner, ownerPending);
		}

		persist(toPersist, srcConn, dstConn);
	}

	private void persist(Map<Object, List<StageAreaPersistenceRequest>> toPersist, Connection srcConn,
			Connection dstConn) throws DBException {
		try {
			EtlStageAreaObjectDAO.saveAll(generateStageInfo(toPersist, srcConn, dstConn), srcConn);
		} catch (DBException e) {
			restore(toPersist);
			throw e;
		} catch (RuntimeException e) {
			restore(toPersist);
			throw e;
		}
	}

	private List<EtlStageObjectInfo> generateStageInfo(Map<Object, List<StageAreaPersistenceRequest>> batches,
			Connection srcConn, Connection dstConn) throws DBException {
		List<EtlStageObjectInfo> result = new ArrayList<>();
		for (List<StageAreaPersistenceRequest> batch : batches.values()) {
			for (StageAreaPersistenceRequest request : batch) {
				for (EtlDatabaseObject sourceObject : request.getSourceObjects()) {
					result.add(EtlStageObjectInfo.generate(sourceObject, srcConn, dstConn));
				}
			}
		}
		return result;
	}

	private synchronized void restore(Map<Object, List<StageAreaPersistenceRequest>> notPersisted) {
		Map<Object, List<StageAreaPersistenceRequest>> restored = new LinkedHashMap<>();
		merge(restored, notPersisted);
		merge(restored, pendingByOwner);
		pendingByOwner = restored;
	}

	private void merge(Map<Object, List<StageAreaPersistenceRequest>> target,
			Map<Object, List<StageAreaPersistenceRequest>> source) {
		for (Map.Entry<Object, List<StageAreaPersistenceRequest>> entry : source.entrySet()) {
			List<StageAreaPersistenceRequest> targetBatch = target.get(entry.getKey());
			if (targetBatch == null) {
				targetBatch = new ArrayList<>();
				target.put(entry.getKey(), targetBatch);
			}
			targetBatch.addAll(entry.getValue());
		}
	}

	public synchronized void discard(Object owner) {
		pendingByOwner.remove(owner);
	}

	public synchronized void discardPending() {
		pendingByOwner.clear();
	}

	public synchronized int pendingCount() {
		return pendingCount(pendingByOwner);
	}

	public synchronized int pendingCount(Object owner) {
		List<StageAreaPersistenceRequest> ownerPending = pendingByOwner.get(owner);
		if (ownerPending == null) {
			return 0;
		}

		Map<Object, List<StageAreaPersistenceRequest>> ownerBatch = new LinkedHashMap<>();
		ownerBatch.put(owner, ownerPending);
		return pendingCount(ownerBatch);
	}

	private int pendingCount(Map<Object, List<StageAreaPersistenceRequest>> batches) {
		int count = 0;
		for (List<StageAreaPersistenceRequest> batch : batches.values()) {
			for (StageAreaPersistenceRequest request : batch) {
				count += request.getSourceObjects().size();
			}
		}
		return count;
	}
}
