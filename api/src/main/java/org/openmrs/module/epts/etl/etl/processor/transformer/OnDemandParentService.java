package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/**
 * Resolves or creates on-demand parents in short, independent transactions.
 * Requests for the same logical parent are serialized inside this JVM.
 */
public final class OnDemandParentService {

	private static final OnDemandParentService INSTANCE = new OnDemandParentService();

	private final ConcurrentMap<String, ParentLock> parentLocks = new ConcurrentHashMap<>();

	private OnDemandParentService() {
	}

	public static OnDemandParentService getInstance() {
		return INSTANCE;
	}

	public EtlDatabaseObject retrieveOrCreate(ParentOnDemandLoadTransformer transformer, EtlProcessor processor,
			EtlDatabaseObject srcParent, EtlDatabaseObject srcObject, EtlDatabaseObject transformedRecord,
			List<EtlDatabaseObject> additionalSrcObjects, TransformableField field, Connection currentSrcConn,
			Connection currentDstConn) throws DBException {

		String parentKey = transformer.buildParentRequestKey(srcParent, srcObject, additionalSrcObjects, currentSrcConn,
				currentDstConn);

		ParentLock parentLock = retainLock(parentKey);
		parentLock.lock.lock();

		try {
			try {
				return retrieveOrCreateInIndependentTransaction(transformer, processor, srcParent, srcObject,
						transformedRecord, additionalSrcObjects, field);
			} catch (DBException e) {
				if (!e.isDuplicatePrimaryOrUniqueKeyException()) {
					throw e;
				}

				EtlDatabaseObject parent = retrieveInIndependentTransaction(transformer, processor, srcParent,
						srcObject, transformedRecord, additionalSrcObjects);
				if (parent != null) {
					return parent;
				}

				throw e;
			}
		} finally {
			parentLock.lock.unlock();
			releaseLock(parentKey, parentLock);
		}
	}

	private EtlDatabaseObject retrieveOrCreateInIndependentTransaction(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field)
			throws DBException {

		IndependentConnections connections = openIndependentConnections(processor);
		try {
			EtlDatabaseObject parent = transformer.retrieveExistingParent(processor, srcParent, srcObject,
					transformedRecord, additionalSrcObjects, connections.srcConn, connections.dstConn);

			if (parent == null) {
				parent = transformer.createParent(processor, srcParent, srcObject, transformedRecord,
						additionalSrcObjects, field, connections.srcConn, connections.dstConn);
			}

			connections.markSuccessful();
			return parent;
		} finally {
			connections.close(processor);
		}
	}

	private EtlDatabaseObject retrieveInIndependentTransaction(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects) throws DBException {

		IndependentConnections connections = openIndependentConnections(processor);
		try {
			EtlDatabaseObject parent = transformer.retrieveExistingParent(processor, srcParent, srcObject,
					transformedRecord, additionalSrcObjects, connections.srcConn, connections.dstConn);
			connections.markSuccessful();
			return parent;
		} finally {
			connections.close(processor);
		}
	}

	private IndependentConnections openIndependentConnections(EtlProcessor processor) throws DBException {
		Engine<EtlDatabaseObject> engine = (Engine<EtlDatabaseObject>) processor.getEngine();
		OpenConnection srcConn = null;

		try {
			srcConn = engine.openSrcConn(processor);
			OpenConnection dstConn = engine.tryToOpenDstConn(processor);
			return new IndependentConnections(srcConn, dstConn);
		} catch (DBException e) {
			OpenConnection.finalizeAllConnections(processor, srcConn);
			throw e;
		}
	}

	private ParentLock retainLock(String key) {
		return parentLocks.compute(key, (ignored, current) -> {
			ParentLock retained = current != null ? current : new ParentLock();
			retained.references++;
			return retained;
		});
	}

	private void releaseLock(String key, ParentLock released) {
		parentLocks.computeIfPresent(key, (ignored, current) -> {
			if (current != released) {
				return current;
			}

			current.references--;
			return current.references == 0 ? null : current;
		});
	}

	private static final class ParentLock {
		private final ReentrantLock lock = new ReentrantLock();
		private int references;
	}

	private static final class IndependentConnections {
		private final OpenConnection srcConn;
		private final OpenConnection dstConn;

		private IndependentConnections(OpenConnection srcConn, OpenConnection dstConn) {
			this.srcConn = srcConn;
			this.dstConn = dstConn;
		}

		private void markSuccessful() {
			OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
		}

		private void close(EtlProcessor processor) {
			RuntimeException failure = null;

			try {
				if (dstConn != null) {
					dstConn.finalizeConnection(processor);
				}
			} catch (RuntimeException e) {
				failure = e;
			} finally {
				try {
					if (srcConn != null) {
						srcConn.finalizeConnection(processor);
					}
				} catch (RuntimeException e) {
					if (failure == null) {
						failure = e;
					} else {
						failure.addSuppressed(e);
					}
				}
			}

			if (failure != null) {
				throw failure;
			}
		}
	}
}
