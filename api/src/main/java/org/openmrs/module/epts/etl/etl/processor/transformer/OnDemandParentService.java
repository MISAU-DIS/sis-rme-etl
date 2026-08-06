package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.sql.SQLException;
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
			boolean useCurrentConnections = usesAutoCommit(currentDstConn)
					|| connectionIsSharedByConcurrentProcessors(processor) || !processor.isRunningInConcurrency();

			if (!useCurrentConnections) {
				commitCurrentDestinationBeforeIndependentTransaction(processor, currentDstConn, parentKey);
			}

			try {
				if (useCurrentConnections) {
					return retrieveOrCreateUsingConnections(transformer, processor, srcParent, srcObject,
							transformedRecord, additionalSrcObjects, field, currentSrcConn, currentDstConn);
				}

				return retrieveOrCreateInIndependentTransaction(transformer, processor, srcParent, srcObject,
						transformedRecord, additionalSrcObjects, field);
			} catch (DBException e) {
				if (!e.isDuplicatePrimaryOrUniqueKeyException()) {
					throw e;
				}

				EtlDatabaseObject parent = useCurrentConnections
						? retrieveUsingConnections(transformer, processor, srcParent, srcObject, transformedRecord,
								additionalSrcObjects, currentSrcConn, currentDstConn)
						: retrieveInIndependentTransaction(transformer, processor, srcParent, srcObject,
								transformedRecord, additionalSrcObjects);
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

	private boolean connectionIsSharedByConcurrentProcessors(EtlProcessor processor) {
		return processor.isRunningInConcurrency()
				&& processor.getRelatedEtlOperationConfig().isUseSharedConnectionPerThread();
	}

	private void commitCurrentDestinationBeforeIndependentTransaction(EtlProcessor processor, Connection dstConn,
			String parentKey) throws DBException {
		if (dstConn == null) {
			return;
		}

		try {
			processor.logDebug(
					"Committing current destination work before independent on-demand parent transaction: {}",
					parentKey);
			dstConn.commit();
		} catch (SQLException e) {
			throw new DBException("Could not commit current destination work before creating on-demand parent", e);
		}
	}

	private boolean usesAutoCommit(Connection dstConn) throws DBException {
		if (dstConn == null) {
			return false;
		}

		try {
			return dstConn.getAutoCommit();
		} catch (SQLException e) {
			throw new DBException("Could not determine the destination connection auto-commit mode", e);
		}
	}

	private EtlDatabaseObject retrieveOrCreateUsingConnections(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException {

		EtlDatabaseObject parent = retrieveUsingConnections(transformer, processor, srcParent, srcObject,
				transformedRecord, additionalSrcObjects, srcConn, dstConn);

		if (parent == null) {
			parent = transformer.createParent(processor, srcParent, srcObject, transformedRecord, additionalSrcObjects,
					field, srcConn, dstConn);
		}

		return parent;
	}

	private EtlDatabaseObject retrieveUsingConnections(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, Connection srcConn,
			Connection dstConn) throws DBException {
		return transformer.retrieveExistingParent(processor, srcParent, srcObject, transformedRecord,
				additionalSrcObjects, srcConn, dstConn);
	}

	private EtlDatabaseObject retrieveOrCreateInIndependentTransaction(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field)
			throws DBException {

		IndependentConnections connections = openIndependentConnections(processor);
		try {
			EtlDatabaseObject parent = retrieveOrCreateUsingConnections(transformer, processor, srcParent, srcObject,
					transformedRecord, additionalSrcObjects, field, connections.srcConn, connections.dstConn);

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
			EtlDatabaseObject parent = retrieveUsingConnections(transformer, processor, srcParent, srcObject,
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
