package org.openmrs.module.epts.etl.etl.model.persistence;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Coordinates all auxiliary persistence produced by concurrent ETL workers.
 * Requests may be flushed globally, by owner, by type, or by owner and type.
 */
public class EnginePersistenceCoordinator {

	private Map<Object, List<EnginePersistenceRequest>> pendingByOwner = new LinkedHashMap<>();

	private final ReentrantLock flushLock = new ReentrantLock();

	public synchronized void register(Object owner, EnginePersistenceRequest request) {
		if (request == null || request.size() == 0) {
			return;
		}

		List<EnginePersistenceRequest> ownerPending = pendingByOwner.get(owner);
		if (ownerPending == null) {
			ownerPending = new ArrayList<>();
			pendingByOwner.put(owner, ownerPending);
		}
		ownerPending.add(request);
	}

	public void flush(Connection srcConn, Connection dstConn) throws DBException {
		flush(null, null, srcConn, dstConn);
	}

	public void flush(Object owner, Connection srcConn, Connection dstConn) throws DBException {
		flush(owner, null, srcConn, dstConn);
	}

	public void flush(PersistenceType type, Connection srcConn, Connection dstConn) throws DBException {
		flush(null, type, srcConn, dstConn);
	}

	public void flush(Object owner, PersistenceType type, Connection srcConn, Connection dstConn) throws DBException {
		flushLock.lock();
		try {
			Map<Object, List<EnginePersistenceRequest>> toPersist = drain(owner, type);
			persist(toPersist, srcConn, dstConn);
		} finally {
			flushLock.unlock();
		}
	}

	private void persist(Map<Object, List<EnginePersistenceRequest>> toPersist, Connection srcConn,
			Connection dstConn) throws DBException {
		try {
			for (List<EnginePersistenceRequest> batch : toPersist.values()) {
				for (EnginePersistenceRequest request : batch) {
					request.persist(srcConn, dstConn);
				}
			}
		} catch (DBException e) {
			restore(toPersist);
			throw e;
		} catch (RuntimeException e) {
			restore(toPersist);
			throw e;
		}
	}

	private synchronized Map<Object, List<EnginePersistenceRequest>> drain(Object owner, PersistenceType type) {
		if (owner == null && type == null) {
			Map<Object, List<EnginePersistenceRequest>> drained = pendingByOwner;
			pendingByOwner = new LinkedHashMap<>();
			return drained;
		}

		Map<Object, List<EnginePersistenceRequest>> drained = new LinkedHashMap<>();
		Iterator<Map.Entry<Object, List<EnginePersistenceRequest>>> owners = pendingByOwner.entrySet().iterator();
		while (owners.hasNext()) {
			Map.Entry<Object, List<EnginePersistenceRequest>> entry = owners.next();
			if (owner != null && !Objects.equals(owner, entry.getKey())) {
				continue;
			}

			List<EnginePersistenceRequest> selected = new ArrayList<>();
			Iterator<EnginePersistenceRequest> requests = entry.getValue().iterator();
			while (requests.hasNext()) {
				EnginePersistenceRequest request = requests.next();
				if (type == null || request.getType() == type) {
					selected.add(request);
					requests.remove();
				}
			}

			if (!selected.isEmpty()) {
				drained.put(entry.getKey(), selected);
			}
			if (entry.getValue().isEmpty()) {
				owners.remove();
			}
		}
		return drained;
	}

	private synchronized void restore(Map<Object, List<EnginePersistenceRequest>> notPersisted) {
		Map<Object, List<EnginePersistenceRequest>> restored = new LinkedHashMap<>();
		merge(restored, notPersisted);
		merge(restored, pendingByOwner);
		pendingByOwner = restored;
	}

	private void merge(Map<Object, List<EnginePersistenceRequest>> target,
			Map<Object, List<EnginePersistenceRequest>> source) {
		for (Map.Entry<Object, List<EnginePersistenceRequest>> entry : source.entrySet()) {
			List<EnginePersistenceRequest> targetBatch = target.get(entry.getKey());
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
		return pendingCount(null, null);
	}

	public synchronized int pendingCount(Object owner) {
		return pendingCount(owner, null);
	}

	public synchronized int pendingCount(PersistenceType type) {
		return pendingCount(null, type);
	}

	public synchronized int pendingCount(Object owner, PersistenceType type) {
		int count = 0;
		for (Map.Entry<Object, List<EnginePersistenceRequest>> entry : pendingByOwner.entrySet()) {
			if (owner != null && !Objects.equals(owner, entry.getKey())) {
				continue;
			}
			for (EnginePersistenceRequest request : entry.getValue()) {
				if (type == null || request.getType() == type) {
					count += request.size();
				}
			}
		}
		return count;
	}
}
