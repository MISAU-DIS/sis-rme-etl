package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRuntimeAdapters;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.inconsistenceresolver.model.InconsistenceInfo;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.InconsistentStateException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;

/**
 * Resolves or creates on-demand parents in short, independent transactions.
 * Requests for the same logical parent are serialized inside this JVM.
 */
public final class OnDemandParentService {
	private static final int MAX_TRANSACTION_ATTEMPTS = 5;

	private static final OnDemandParentService INSTANCE = new OnDemandParentService();

	private final ConcurrentMap<String, ParentLock> parentLocks = new ConcurrentHashMap<>();

	private OnDemandParentService() {
	}

	public static OnDemandParentService getInstance() {
		return INSTANCE;
	}

	public EtlDatabaseObject retrieveOrCreate(ParentOnDemandLoadTransformer transformer, EtlProcessor processor,
			EtlDatabaseObject srcObject, EtlDatabaseObject transformedRecord,
			List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			ConnectorRecordReader<?> currentReader, ConnectorRecordWriter<?> currentWriter) throws DBException {

		EtlDatabaseObject srcParent = null;

		if (transformer.existingSrcParentIsApplicable()) {

			try {
				srcParent = transformer.resolveSrcParent(processor, srcObject, transformedRecord, additionalSrcObjects,
						currentReader, currentWriter);
			} catch (InconsistentStateException e) {

				ParentTable parentInfo = srcObject.getRelatedConfiguration().requireRelatedTableConf()
						.findParentRefInfoByParentTable(transformer.getParentTableName());

				parentInfo.setTableName(transformer.getParentTableName());

				InconsistenceInfo inconsistence = InconsistenceInfo.generate(srcObject, parentInfo,
						processor.getRelatedEtlConfiguration().getOriginAppLocationCode());

				srcObject.setFieldValue(transformer.getParentSourceField(), null);

				inconsistence.save(transformer.getRelatedEtlTransformTarget().requireRelatedTableConf(),
						currentReader.unwrap(Connection.class));
			}
		}

		String parentKey = transformer.buildParentRequestKey(srcParent, srcObject, additionalSrcObjects, currentReader,
				currentWriter);

		ParentLock parentLock = retainLock(parentKey);
		parentLock.lock.lock();

		try {
			boolean useCurrentConnections = usesAutoCommit(currentWriter)
					|| connectionIsSharedByConcurrentProcessors(processor) || !processor.isRunningInConcurrency();

			if (!useCurrentConnections) {
				this.commitCurrentDestinationBeforeIndependentTransaction(processor, currentWriter, parentKey);
			}

			try {

				if (useCurrentConnections) {
					return this.retrieveOrCreateUsingConnectors(transformer, processor, srcParent, srcObject,
							transformedRecord, additionalSrcObjects, field, currentReader, currentWriter);
				}

				return this.retrieveOrCreateInIndependentTransaction(transformer, processor, srcParent, srcObject,
						transformedRecord, additionalSrcObjects, field);

			} catch (DBException e) {

				if (!e.isDuplicatePrimaryOrUniqueKeyException()) {
					throw e;
				}

				EtlDatabaseObject parent = useCurrentConnections
						? retrieveUsingConnectors(transformer, processor, srcParent, srcObject, transformedRecord,
								additionalSrcObjects, currentReader, currentWriter)
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

	private void commitCurrentDestinationBeforeIndependentTransaction(EtlProcessor processor,
			ConnectorRecordWriter<?> writer, String parentKey) throws DBException {

		if (writer == null) {
			return;
		}

		try {
			processor.logDebug(
					"Committing current destination work before independent on-demand parent transaction: {}",
					parentKey);
			writer.unwrap(Connection.class).commit();
		} catch (SQLException e) {
			throw new DBException("Could not commit current destination work before creating on-demand parent", e);
		}
	}

	private boolean usesAutoCommit(ConnectorRecordWriter<?> writer) throws DBException {

		if (writer == null) {
			return false;
		}

		try {
			return writer.unwrap(Connection.class).getAutoCommit();
		} catch (SQLException e) {
			throw new DBException("Could not determine the destination connection auto-commit mode", e);
		}
	}

	private EtlDatabaseObject retrieveOrCreateUsingConnectors(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			ConnectorRecordReader<?> reader, ConnectorRecordWriter<?> writer) throws DBException {

		EtlDatabaseObject parent = retrieveUsingConnectors(transformer, processor, srcParent, srcObject,
				transformedRecord, additionalSrcObjects, reader, writer);

		if (parent == null) {
			parent = transformer.createParent(processor, srcParent, srcObject, transformedRecord, additionalSrcObjects,
					field, reader, writer);
		}

		return parent;
	}

	private EtlDatabaseObject retrieveUsingConnectors(ParentOnDemandLoadTransformer transformer, EtlProcessor processor,
			EtlDatabaseObject srcParent, EtlDatabaseObject srcObject, EtlDatabaseObject transformedRecord,
			List<EtlDatabaseObject> additionalSrcObjects, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer) throws DBException {

		EtlDatabaseObject dstParent = null;

		if (srcParent != null) {
			dstParent = transformer.resolveParent(processor, srcParent, srcObject, transformedRecord,
					additionalSrcObjects, reader, writer);
		}

		if (dstParent == null) {
			dstParent = transformer.retrieveExistingOnDemandParent(processor, srcObject, additionalSrcObjects, reader,
					writer);
		}

		return dstParent;
	}

	private EtlDatabaseObject retrieveOrCreateInIndependentTransaction(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field)
			throws DBException {

		for (int attempt = 1; attempt <= MAX_TRANSACTION_ATTEMPTS; attempt++) {
			IndependentConnections connections = openIndependentConnections(processor);
			boolean retry = false;

			try {
				EtlDatabaseObject parent = retrieveOrCreateUsingConnectors(transformer, processor, srcParent, srcObject,
						transformedRecord, additionalSrcObjects, field, connections.reader(), connections.writer());

				connections.markSuccessful();
				return parent;
			} catch (DBException e) {
				retry = attempt < MAX_TRANSACTION_ATTEMPTS && isTemporaryTransactionFailure(e, connections.dstConn);

				if (!retry) {
					throw e;
				}
			} finally {
				connections.close(processor);
			}

			processor.logWarn("Retrying complete independent on-demand parent transaction after temporary database "
					+ "error. Attempt {} of {}", attempt + 1, MAX_TRANSACTION_ATTEMPTS);
			waitBeforeRetry(attempt);
		}

		throw new DBException("On-demand parent transaction retry exhausted", (SQLException) null);
	}

	private boolean isTemporaryTransactionFailure(Throwable failure, Connection conn) {

		if (conn == null) {
			return false;
		}

		Throwable current = failure;

		while (current != null) {

			if (current instanceof DBException) {

				try {
					return ((DBException) current).isTemporaryDBErrr();
				} catch (DBException ignored) {
					return false;
				}
			}
			current = current.getCause();
		}

		return false;
	}

	private void waitBeforeRetry(int failedAttempt) throws DBException {

		try {
			TimeUnit.MILLISECONDS.sleep(500L * failedAttempt);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new DBException("Interrupted while waiting to retry on-demand parent transaction",
					new SQLException(e));
		}
	}

	private EtlDatabaseObject retrieveInIndependentTransaction(ParentOnDemandLoadTransformer transformer,
			EtlProcessor processor, EtlDatabaseObject srcParent, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects) throws DBException {

		IndependentConnections connections = openIndependentConnections(processor);

		try {
			EtlDatabaseObject parent = retrieveUsingConnectors(transformer, processor, srcParent, srcObject,
					transformedRecord, additionalSrcObjects, connections.reader(), connections.writer());
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
			srcConn = engine.getRelatedEtlConf().openSrcConn(processor);
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

		private ConnectorRecordReader<?> reader() {
			return JdbcConnectorRuntimeAdapters.reader(srcConn);
		}

		private ConnectorRecordWriter<?> writer() {
			return JdbcConnectorRuntimeAdapters.writer(dstConn);
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
