package org.openmrs.module.epts.etl.etl.model.stage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Collects stage-area information produced by concurrent processors and writes
 * it at an engine synchronization point.
 * <p>
 * JDBC connections are deliberately supplied only to {@link #flush(Connection)}
 * and are never retained by this object.
 */
public class StageAreaPersistenceCoordinator {

	private Map<Object, List<EtlStageObjectInfo>> pendingByOwner = new LinkedHashMap<>();

	public synchronized void register(Object owner, List<EtlStageObjectInfo> stageInfo) {
		if (stageInfo == null || stageInfo.isEmpty()) {
			return;
		}

		List<EtlStageObjectInfo> ownerPending = pendingByOwner.get(owner);

		if (ownerPending == null) {
			ownerPending = new ArrayList<>();
			pendingByOwner.put(owner, ownerPending);
		}

		ownerPending.addAll(stageInfo);
	}

	public void flush(Connection srcConn) throws DBException {
		Map<Object, List<EtlStageObjectInfo>> toPersist;

		synchronized (this) {
			if (pendingByOwner.isEmpty()) {
				return;
			}

			toPersist = pendingByOwner;
			pendingByOwner = new LinkedHashMap<>();
		}

		try {
			EtlStageAreaObjectDAO.saveAll(flatten(toPersist), srcConn);
		} catch (DBException e) {
			restore(toPersist);
			throw e;
		} catch (RuntimeException e) {
			restore(toPersist);
			throw e;
		}
	}

	private List<EtlStageObjectInfo> flatten(Map<Object, List<EtlStageObjectInfo>> batches) {
		List<EtlStageObjectInfo> result = new ArrayList<>();
		for (List<EtlStageObjectInfo> batch : batches.values()) {
			result.addAll(batch);
		}
		return result;
	}

	private synchronized void restore(Map<Object, List<EtlStageObjectInfo>> notPersisted) {
		Map<Object, List<EtlStageObjectInfo>> restored = new LinkedHashMap<>();
		merge(restored, notPersisted);
		merge(restored, pendingByOwner);
		pendingByOwner = restored;
	}

	private void merge(Map<Object, List<EtlStageObjectInfo>> target, Map<Object, List<EtlStageObjectInfo>> source) {
		for (Map.Entry<Object, List<EtlStageObjectInfo>> entry : source.entrySet()) {
			List<EtlStageObjectInfo> targetBatch = target.get(entry.getKey());
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
		int count = 0;
		for (List<EtlStageObjectInfo> batch : pendingByOwner.values()) {
			count += batch.size();
		}
		return count;
	}
}
