package org.openmrs.module.epts.etl.engine;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.openmrs.module.epts.etl.conf.AbstractBaseConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;
import org.openmrs.module.epts.etl.conf.types.EtlTotalRecordsCountStrategy;
import org.openmrs.module.epts.etl.conf.types.ParallelProcessingStrategyType;
import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.etl.model.parent.DefaultParentPersistenceRequest;
import org.openmrs.module.epts.etl.etl.model.persistence.EnginePersistenceCoordinator;
import org.openmrs.module.epts.etl.etl.model.persistence.PersistenceType;
import org.openmrs.module.epts.etl.etl.model.stage.StageAreaPersistenceRequest;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.model.pojo.generic.RecordWithDefaultParentInfo;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.MonitoredOperation;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeController;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

/**
 * This class monitor all {@link TaskProcessor}s of an
 * {@link OperationController}
 * 
 * @author jpboane
 */
public class Engine<T extends EtlDatabaseObject> extends AbstractBaseConfiguration implements MonitoredOperation {

	private static final EtlLogger LOG = EtlLogger.getLogger(Engine.class);

	private static final int MAX_TRANSACTION_ATTEMPTS = 5;

	private static CommonUtilities utilities = CommonUtilities.getInstance();

	private final Object LOCK = new Object();

	private OperationController<T> controller;

	private EtlItemConfiguration etlItemConfiguration;

	private String engineId;

	private volatile EtlOperationStatus operationStatus;

	private volatile boolean stopRequested;

	protected TableOperationProgressInfo tableOperationProgressInfo;

	protected List<IntervalExtremeRecord> excludedRecordsLimits;

	private AbstractEtlSearchParams<T> searchParams;

	private MigrationFinalCheckStatus finalCheckStatus;

	private List<TaskProcessor<T>> currentTaskProcessor;

	private Map<String, List<EtlDatabaseObject>> recordsToDisplay;

	private int currentIteration;

	private final EnginePersistenceCoordinator persistenceCoordinator;

	public Engine(OperationController<T> controller, EtlItemConfiguration etlItemConfiguration,
			TableOperationProgressInfo tableOperationProgressInfo) {
		this.controller = controller;
		this.etlItemConfiguration = etlItemConfiguration;

		this.engineId = (this.getRelatedEtlOperationConfig().getOperationType() + "_" + getEtlConfigCode())
				.toLowerCase();

		this.operationStatus = EtlOperationStatus.NOT_INITIALIZED;
		this.tableOperationProgressInfo = tableOperationProgressInfo;

		this.finalCheckStatus = MigrationFinalCheckStatus.NOT_INITIALIZED;
		this.persistenceCoordinator = new EnginePersistenceCoordinator();
	}

	public EnginePersistenceCoordinator getPersistenceCoordinator() {
		return persistenceCoordinator;
	}

	public void registerDefaultParentPersistence(TaskProcessor<?> owner, EtlInfo etlInfo) {
		getPersistenceCoordinator().register(owner, new DefaultParentPersistenceRequest(etlInfo));
	}

	public void registerStageAreaPersistence(TaskProcessor<?> owner, List<EtlDatabaseObject> sourceObjects,
			Connection srcConn, Connection dstConn) throws DBException {
		getPersistenceCoordinator().register(owner, new StageAreaPersistenceRequest(owner, sourceObjects));

		if (canFlushStageAreaImmediately(owner)) {
			flushStageArea(srcConn, dstConn);
		}
	}

	private boolean canFlushStageAreaImmediately(TaskProcessor<?> owner) {
		return !getRelatedEtlConf().hasTestingItem()
				&& (!owner.isRunningInConcurrency() || determineProcessingStrategy().usesSinglePersistenceWorker());
	}

	@Override
	public EtlOperationStatus getOperationStatus() {
		return this.operationStatus;
	}

	@Override
	public void setOperationStatus(EtlOperationStatus status) {
		this.operationStatus = status;
	}

	public Map<String, List<EtlDatabaseObject>> getRecordsToDisplay() {
		return recordsToDisplay;
	}

	public void setRecordsToDisplay(Map<String, List<EtlDatabaseObject>> recordsToDisplay) {
		this.recordsToDisplay = recordsToDisplay;
	}

	public EtlDstType getGlobalDstType() {
		return getRelatedEtlOperationConfig().getDstType();
	}

	public List<TaskProcessor<T>> getCurrentTaskProcessor() {
		return currentTaskProcessor;
	}

	public MigrationFinalCheckStatus getFinalCheckStatus() {
		return finalCheckStatus;
	}

	public OpenConnection openSrcConn(BaseConfiguration opendFrom) throws DBException {
		return getController().openSrcConnection(opendFrom);
	}

	public OpenConnection tryToOpenDstConn(BaseConfiguration opendFrom) throws DBException {
		return getController().tryToOpenDstConn(opendFrom);
	}

	public List<OpenConnection> openSrcConn(int qtyConnections, BaseConfiguration opendFrom) throws DBException {
		List<OpenConnection> conns = new ArrayList<>(qtyConnections);

		for (int i = 0; i < qtyConnections; i++) {
			conns.add(this.openSrcConn(opendFrom));
		}

		return conns;
	}

	public List<OpenConnection> tryToOpenDstConn(int qtyConnections, BaseConfiguration opendFrom) throws DBException {
		List<OpenConnection> conns = new ArrayList<>(qtyConnections);

		for (int i = 0; i < qtyConnections; i++) {
			conns.add(this.tryToOpenDstConn(opendFrom));
		}

		return conns;
	}

	public boolean isDbDst() {
		return getRelatedEtlOperationConfig().isDbDst();
	}

	public boolean isJsonDst() {
		return getRelatedEtlOperationConfig().isJsonDst();
	}

	public boolean isDumpDst() {
		return getRelatedEtlOperationConfig().isDumpDst();
	}

	public boolean isCsvDst() {
		return getRelatedEtlOperationConfig().isCsvDst();
	}

	public boolean isFileDst() {
		return getRelatedEtlOperationConfig().isFileDst();
	}

	protected boolean mustDoFinalCheck() {
		if (getRelatedOperationController().getOperationConfig().skipFinalDataVerification()) {
			return false;
		} else {
			OpenConnection srcConn = null;
			OpenConnection dstConn = null;

			try {
				srcConn = this.openSrcConn(this);
				dstConn = this.tryToOpenDstConn(this);

				if (dstConn != null && DBUtilities.isSameDatabaseServer(srcConn, dstConn)) {
					return utilities
							.stringHasValue(getSearchParams().generateDestinationExclusionClause(srcConn, dstConn));
				} else {
					return false;
				}
			} catch (DBException e) {
				throw new RuntimeException(e);
			} finally {
				finalizeConnection(dstConn, this);
				finalizeConnection(srcConn, this);
			}
		}
	}

	public AbstractEtlSearchParams<T> getSearchParams() {
		return searchParams;
	}

	public ThreadRecordIntervalsManager<T> getThreadRecordIntervalsManager() {
		return getSearchParams().getThreadRecordIntervalsManager();
	}

	public void setSearchParams(AbstractEtlSearchParams<T> searchParams) {
		this.searchParams = searchParams;
	}

	public List<IntervalExtremeRecord> getExcludedRecordsIntervals() {
		return excludedRecordsLimits;
	}

	public void setExcludedRecordsLimits(List<IntervalExtremeRecord> excludedRecordsLimits) {
		this.excludedRecordsLimits = excludedRecordsLimits;
	}

	public long getMinRecordId() {
		return getProgressMeter().getMinRecordId();
	}

	public long getMaxRecordId() {
		return getProgressMeter().getMaxRecordId();
	}

	public SrcConf getSrcConf() {
		return this.getEtlItemConfiguration().getSrcConf();
	}

	public String getEngineId() {
		return engineId;
	}

	@Override
	public String getOperationId() {
		return this.getEngineId();
	}

	public EtlItemConfiguration getEtlItemConfiguration() {
		return this.etlItemConfiguration;
	}

	public OperationController<T> getController() {
		return controller;
	}

	public String getEtlConfigCode() {
		return this.getEtlItemConfiguration().getConfigCode();
	}

	public EtlProgressMeter getProgressMeter() {
		return this.tableOperationProgressInfo != null ? this.tableOperationProgressInfo.getProgressMeter() : null;
	}

	public EtlConfiguration getRelatedEtlConf() {
		return this.getEtlItemConfiguration().getRelatedEtlConf();
	}

	public DBConnectionInfo getDstConnInfo() {
		return this.getRelatedOperationController().getDstConnInfo();
	}

	public DBConnectionInfo getSrcConnInfo() {
		return this.getRelatedOperationController().getSrcConnInfo();
	}

	@Override
	public void changeStatusToRunning() {
		MonitoredOperation.super.changeStatusToRunning();

		updateProgressInfo(EtlOperationStatus.RUNNING);
	}

	@Override
	public void changeStatusToStopping() {
		MonitoredOperation.super.changeStatusToStopping();

		updateProgressInfo(EtlOperationStatus.STOPPING);
	}

	@Override
	public void changeStatusToStopped() {
		MonitoredOperation.super.changeStatusToStopped();

		updateProgressInfo(EtlOperationStatus.STOPPED);
	}

	@Override
	public void changeStatusToFinished() {
		MonitoredOperation.super.changeStatusToFinished();

		updateProgressInfo(EtlOperationStatus.FINISHED);
	}

	@Override
	public void changeStatusToPaused() {
		MonitoredOperation.super.changeStatusToPaused();

		updateProgressInfo(EtlOperationStatus.PAUSED);
	}

	private void updateProgressInfo(EtlOperationStatus status) {
		this.getTableOperationProgressInfo().getProgressMeter().changeStatus(status);

		OpenConnection conn = null;

		try {
			conn = openSrcConn(this);

			this.getTableOperationProgressInfo().save(conn);

			conn.markAsSuccessifullyTerminated();
		} catch (DBException e) {
			throw new EtlExceptionImpl(e);
		} finally {
			finalizeConnection(conn, this);
		}
	}

	@Override
	public void run() {
		try {
			boolean restart;

			do {
				this.changeStatusToRunning();

				if (stopRequested() || isStopped()) {
					logWarn("Aborting engine as stop requested!", 10, true);

					return;
				}

				restart = this.runIteration();

				if (restart) {
					this.prepareAndInitializeRerun();
				}
			} while (restart && !this.stopRequested());
		} catch (Exception e) {
			this.stopOperationDueError(e);

			logErr(e.getLocalizedMessage(), e);
		}
	}

	private boolean runIteration() throws DBException, Exception {
		ensureIterationIsInitialized();

		// Currently this mean that the operation was finalized within the
		// #ensureIterationIsInitialized
		if (this.getOperationStatus().stopped()) {
			return mustRestartInTheEnd() && !stopRequested();
		}

		this.ensureSearchParamInitialized();

		EtlTotalRecordsCountStrategy countStrategy = this.getRelatedEtlOperationConfig().getTotalCountStrategy();

		this.calculateStatistics(getProgressMeter().getTotal() > 0 && countStrategy.isCountAlways()
				? EtlTotalRecordsCountStrategy.COUNT_ONCE
				: null);

		logDebug("Saving ThreadRecordIntervalsManager...");

		getSearchParams().getThreadRecordIntervalsManager().save();

		logTrace("ThreadRecordIntervalsManager saved!");

		this.logDebug("CREATING DEFAULT PARENT OBJECTS");

		this.getEtlItemConfiguration().tryToCreateDefaultRecordsForAllTables();

		this.logTrace("DEFAULT PARENT OBJECTS CREATED");

		process(determineProcessingStrategy());

		this.performEngineFinalization();

		return mustRestartInTheEnd() && !stopRequested();
	}

	public ParallelProcessingStrategyType determineProcessingStrategy() {
		ParallelProcessingStrategyType configuredStrategy = getParallelProcessingStrategy();

		if (configuredStrategy.isMultiThreaded() && getMaxSupportedProcessors() <= 1) {
			return ParallelProcessingStrategyType.SINGLE_THREAD;
		}

		return configuredStrategy;
	}

	private void ensureIterationIsInitialized() throws DBException {
		logWarn("INITIALIZING ENGINE FOR ETL CONFIG [" + getEtlItemConfiguration().getConfigCode().toUpperCase() + "]");

		long minRecId = this.tableOperationProgressInfo.getProgressMeter().getMinRecordId();

		if (minRecId == 0) {
			logInfo("DETERMINING MIN RECORD FOR " + getSrcConf().getTableName());

			minRecId = getController().getMinRecordId(this);

			this.logDebug("FOUND MIN RECORD " + getEtlItemConfiguration() + " = " + minRecId);

			tableOperationProgressInfo.getProgressMeter().setMinRecordId(minRecId);

		} else {
			logDebug("USING SAVED MIN RECORD " + getEtlItemConfiguration() + " = " + minRecId);
		}

		long maxRecId = 0;

		if (minRecId != 0) {
			maxRecId = tableOperationProgressInfo.getProgressMeter().getMaxRecordId();

			if (maxRecId == 0) {
				this.logInfo("DETERMINING MAX RECORD FOR CONFIG '" + getEtlItemConfiguration().getConfigCode() + "'");

				maxRecId = getController().getMaxRecordId(this);

				tableOperationProgressInfo.getProgressMeter().setMaxRecordId(maxRecId);

				this.logDebug("FOUND MAX RECORD " + getEtlItemConfiguration() + " = " + maxRecId);
			} else {
				logDebug("USING SAVED MAX RECORD " + getEtlItemConfiguration() + " = " + maxRecId);
			}
		} else {
			logWarn("MIN RECORD IS ZERO! SKIPING MAX RECORD VERIFICATION...");
		}

		if (maxRecId == 0 && minRecId == 0) {
			String msg = "NO RECORD TO PROCESS FOR ETL CONFIG '" + getSrcConf().getTableName().toUpperCase()
					+ "' NO ENGINE WILL BE CRIETED BY NOW!";

			if (mustRestartInTheEnd()) {
				msg += " GOING SLEEP....";
			} else {
				msg += " FINISHING....";

				this.changeStatusToFinished();

				this.getRelatedOperationController().markTableOperationAsFinished(getEtlItemConfiguration());
			}

			logWarn(msg);

			return;
		}

		if (getController().getOperationConfig().getMaxSupportedProcessors() > getMaxRecordsPerProcessing()) {
			setMaxRecordsPerProcessing(getController().getOperationConfig().getMaxSupportedProcessors());
		}

		this.changeStatusToRunning();
	}

	private void ensureSearchParamInitialized() {
		logDebug("Initializing search Params");

		ThreadRecordIntervalsManager<T> t = null;

		if (getRelatedOperationController().isResumable()) {
			t = ThreadRecordIntervalsManager.tryToLoadFromFile(getEngineId(), this);
		}

		if (getRelatedEtlConf().hasTestingItem()) {
			this.getRelatedEtlOperationConfig()
					.setProcessingBatch((int) tableOperationProgressInfo.getProgressMeter().getMaxRecordId());
		}

		if (t == null) {
			ParallelProcessingStrategyType threadingMode = this.getRelatedEtlOperationConfig()
					.getParallelProcessingStrategy();

			if (this.getEtlItemConfiguration().hasParallelProcessingStrategyType()) {
				threadingMode = this.getEtlItemConfiguration().getParallelProcessingStrategyType();
			}

			int processors = 1;

			if (threadingMode.isMultiThreaded() && this.getMaxSupportedProcessors() > 1) {
				processors = this.getMaxSupportedProcessors();
			} else {
				processors = 1;
			}

			t = new ThreadRecordIntervalsManager<>(this, processors);
		}

		this.setSearchParams(controller.initMainSearchParams(t, this));
		this.getSearchParams().setThreadRecordIntervalsManager(t);

		logTrace("Search Params Initialized!");

	}

	private void prepareAndInitializeRerun() throws DBException {
		if (!mustRestartInTheEnd()) {
			throw new ForbiddenOperationException("The operation is not set to mustRestartInTheEnd");
		}

		TimeCountDown tcd = TimeCountDown.wait(15,
				"Current Operation Iteration is Finished! Preparing to start the nex iteration...");

		while (tcd.isInExecution()) {
			TimeCountDown.sleep(5);
		}

		logWarn("Restarting the Operation...");

		ThreadRecordIntervalsManager<T> oldLImitsManagers = ThreadRecordIntervalsManager
				.tryToLoadFromFile(this.getEngineId(), this);

		if (oldLImitsManagers != null) {
			oldLImitsManagers.remove(this);
		}

		logDebug("Reseting minRecordId");
		getProgressMeter().setMinRecordId(getController().getMinRecordId(this));

		logDebug("Reseting maxRecordId");
		getProgressMeter().setMaxRecordId(getController().getMaxRecordId(this));

		logTrace("Extreme records set!");

		getProgressMeter().resetTotal();
	}

	void processRangePartition(TaskProcessor<T> processor, boolean useSharedConnection, OpenConnection sharedSrcConn,
			OpenConnection sharedDstConn) {

		if (useSharedConnection) {
			performExtractTransformationAndLoading(processor, false, false, sharedSrcConn, sharedDstConn);

			return;
		}

		for (int attempt = 1; attempt <= MAX_TRANSACTION_ATTEMPTS; attempt++) {
			OpenConnection srcConn = null;
			OpenConnection dstConn = null;
			boolean flushWorkerPersistence = false;
			boolean retryTransaction = false;

			try {
				srcConn = openSrcConn(this);
				dstConn = tryToOpenDstConn(this);

				performExtractTransformationAndLoading(processor, false, false, srcConn, dstConn);

				Exception failure = processor.getTaskResultInfo().getFatalException();
				retryTransaction = failure != null && isRetryableTransactionFailure(failure, dstConn)
						&& attempt < MAX_TRANSACTION_ATTEMPTS;

				if (!retryTransaction && !getRelatedEtlConf().hasTestingItem()
						&& !processor.getTaskResultInfo().hasFatalError()) {
					OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
					flushWorkerPersistence = true;
				}
			} catch (DBException e) {
				retryTransaction = attempt < MAX_TRANSACTION_ATTEMPTS && isRetryableTransactionFailure(e, dstConn);
				if (!retryTransaction) {
					failRangeProcessor(processor, e, true);
				}
			} finally {
				try {
					finalizeRangeWorkerConnections(srcConn, dstConn);
				} catch (RuntimeException e) {
					retryTransaction = false;
					flushWorkerPersistence = false;
					failRangeProcessor(processor, e, true);
				}
			}

			if (retryTransaction) {
				discardPendingPersistence(processor);
				logWarn("Retrying complete transaction for processor {} on interval {} after temporary database error. "
						+ "Attempt {} of {}", processor.getProcessorId(), processor.getLimits(), attempt + 1,
						MAX_TRANSACTION_ATTEMPTS);
				processor.resetForTransactionRetry();
				if (!waitBeforeTransactionRetry(attempt, processor)) {
					return;
				}
				continue;
			}

			if (flushWorkerPersistence) {
				try {
					flushStageAreaUsingDedicatedConnection(processor);
				} catch (Exception e) {
					failRangeProcessor(processor, e, false);
				}
			}
			return;
		}
	}

	private boolean isRetryableTransactionFailure(Throwable failure, Connection conn) {
		if (conn == null) {
			return false;
		}

		Throwable current = failure;
		while (current != null) {
			if (current instanceof DBException) {
				try {
					return ((DBException) current).isTemporaryDBErrr(conn);
				} catch (DBException classificationFailure) {
					logWarn("Unable to classify database error for transaction retry: {}",
							classificationFailure.getMessage());
					return false;
				}
			}
			if (current.getCause() == current) {
				break;
			}
			current = current.getCause();
		}
		return false;
	}

	private boolean waitBeforeTransactionRetry(int failedAttempt, TaskProcessor<T> processor) {
		try {
			TimeUnit.MILLISECONDS.sleep(500L * failedAttempt);
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			failRangeProcessor(processor, e, true);
			return false;
		}
	}

	private void finalizeRangeWorkerConnections(OpenConnection srcConn, OpenConnection dstConn) {
		RuntimeException failure = null;

		try {
			if (srcConn != null) {
				srcConn.finalizeConnection(this);
			}
		} catch (RuntimeException e) {
			failure = e;
		}

		try {
			if (dstConn != null) {
				dstConn.finalizeConnection(this);
			}
		} catch (RuntimeException e) {
			if (failure == null) {
				failure = e;
			} else {
				failure.addSuppressed(e);
			}
		}

		if (failure != null) {
			throw failure;
		}
	}

	private void failRangeProcessor(TaskProcessor<T> processor, Exception failure, boolean discardPersistence) {
		if (discardPersistence) {
			discardPendingPersistence(processor);
		}

		processor.changeStatusToStopped();
		processor.getTaskResultInfo().setFatalException(failure);
	}

	private void process(ParallelProcessingStrategyType strategy) throws Exception {

		ThreadRecordIntervalsManager<T> intervalManager = getThreadRecordIntervalsManager();

		while (intervalManager.canGoNext() || !intervalManager.getCurrentLimits().isFullProcessed()) {
			if (stopRequested() || isStopped()) {
				logWarn("Stopping the Task as Stop Requested!");
				changeStatusToStopped();
				return;
			}

			increaseIteration();

			moveToNextCurrentLimitsWhenCompleted(intervalManager);

			if (shouldFinishBecauseNoRecordsRemain()) {
				return;
			}

			List<IntervalExtremeRecord> availableIntervals = intervalManager.getCurrentLimits().getAllNotProcessed();

			if (availableIntervals.isEmpty()) {
				tryToProcessSkippedrecords();

				continue;
			}

			logDebug("The current interval still has {} internal intervals to process", availableIntervals.size());

			strategy.process(this, availableIntervals);

			logWarn("Current iteration finished {}", intervalManager.getCurrentLimits());
		}
	}

	private synchronized void increaseIteration() {
		this.currentIteration++;
	}

	public void tryToProcessSkippedrecords() throws DBException, Exception {
		if (stopRequested()) {
			return;
		}

		// The reload query reads this auxiliary table, so it must only start after
		// every successful worker registration is durable.
		flushDefaultParentsUsingDedicatedConnection();

		ThreadRecordIntervalsManager<T> iManager = this.getThreadRecordIntervalsManager();

		logDebug("TRY TO PROCESS SKIPPED RECORDS ON INTERVAL " + iManager.getCurrentLimits());

		TaskProcessor<T> taskProcessor = getController()
				.initRelatedTaskProcessor(this, getThreadRecordIntervalsManager().getCurrentLimits(), false)
				.initReloadRecordsWithDefaultParentsTaskProcessor(iManager);

		if (taskProcessor != null) {
			String originalExtraCondition = getSearchParams().getExtraCondition();
			String originalExtraConditionForExtract = getSrcConf().getExtraConditionForExtract();

			try {
				getSrcConf().setExtraConditionForExtract(null);

				getSearchParams().setExtraCondition(getSrcConf().generateSkippedRecordInclusionClause());

				taskProcessor.setProcessorId(this.getEngineId());

				boolean persistTheWork = this.getRelatedEtlConf().hasTestingItem() ? false : true;
				boolean useMultiThreadSearch = true;

				performExtractTransformationAndLoading(taskProcessor, useMultiThreadSearch, persistTheWork,
						openSrcConn(this), tryToOpenDstConn(this));

				getSrcConf().setExtraConditionForExtract(originalExtraConditionForExtract);
				getSearchParams().setExtraCondition(originalExtraCondition);

				if (taskProcessor.getTaskResultInfo().hasFatalError()) {
					stopOperationDueError(taskProcessor.getTaskResultInfo().getFatalException());
				} else {
					OpenConnection srcConn = openSrcConn(this);

					try {
						RecordWithDefaultParentInfo.deleteAllSuccessifulyProcessed(getSrcConf(), srcConn);

						srcConn.markAsSuccessifullyTerminated();
					} finally {
						srcConn.finalizeConnection(this);
					}

					iManager.getCurrentLimits().markSkippedRecordsAsProcessed();
					iManager.save();
				}
			} finally {
				getSrcConf().setExtraConditionForExtract(originalExtraConditionForExtract);
				getSearchParams().setExtraCondition(originalExtraCondition);
			}
		} else {
			iManager.getCurrentLimits().markSkippedRecordsAsProcessed();
			iManager.save();
		}
	}

	private void moveToNextCurrentLimitsWhenCompleted(ThreadRecordIntervalsManager<T> intervalManager) {
		if (!intervalManager.getCurrentLimits().isFullProcessed()) {
			return;
		}

		String previousLimits = intervalManager.getCurrentLimits().toString();
		intervalManager.moveNext();
		logWarn("Moving from current limits {} to {}", previousLimits, intervalManager.getCurrentLimits());
	}

	private boolean shouldFinishBecauseNoRecordsRemain() {
		if (getProgressMeter().getRemain() != 0) {
			return false;
		}

		if (getRelatedEtlOperationConfig().finishOnNoRemainRecordsToProcess()) {
			logInfo("Finishing operation as there is no more record to process");
			return true;
		}

		logDebug(
				"No remain records to process but still checking... consider setting finishOnNoRemainRecordsToProcess to true");
		return false;
	}

	public ParallelProcessingStrategyType getParallelProcessingStrategy() {
		if (getEtlItemConfiguration().hasParallelProcessingStrategyType()) {
			return getEtlItemConfiguration().getParallelProcessingStrategyType();
		}

		return getRelatedEtlOperationConfig().getParallelProcessingStrategy();
	}

	TaskProcessor<T> initConcurrentTaskProcessor(IntervalExtremeRecord interval, int index) {
		TaskProcessor<T> processor = getController().initRelatedTaskProcessor(this, interval, true);
		processor.setProcessorId(getEngineId() + "_" + utilities.garantirXCaracterOnNumber(index, 2));

		return processor;
	}

	TaskProcessor<T> initTaskProcessor(IntervalExtremeRecord interval, boolean runningInConcurrency,
			String processorId) {
		TaskProcessor<T> processor = getController().initRelatedTaskProcessor(this, interval, runningInConcurrency);
		processor.setProcessorId(processorId);
		return processor;
	}

	@SuppressWarnings("unchecked")
	void consumeTransformAndLoadQueue(TaskProcessor<T> processor, Queue<T> transformationQueue,
			boolean sharedConnections, OpenConnection sharedSrcConn, OpenConnection sharedDstConn) {

		if (!sharedConnections) {
			consumeTransformAndLoadQueueWithWorkerTransactionRetry(processor, transformationQueue);

			return;
		}

		OpenConnection srcConn = sharedSrcConn;
		OpenConnection dstConn = sharedDstConn;

		try {
			processor.changeStatusToRunning();

			T record;

			while (!processor.getTaskResultInfo().hasFatalError() && (record = transformationQueue.poll()) != null) {
				processor.transformAndLoadExtractedRecords(utilities.parseToList(record), srcConn, dstConn);
			}

			completeExtractedTask(processor, srcConn, dstConn, true);

		} catch (Exception e) {
			processor.changeStatusToStopped();
			processor.getTaskResultInfo().setFatalException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void consumeTransformAndLoadQueueWithWorkerTransactionRetry(TaskProcessor<T> processor,
			Queue<T> transformationQueue) {
		List<T> claimedRecords = new ArrayList<>();

		for (int attempt = 1; attempt <= MAX_TRANSACTION_ATTEMPTS; attempt++) {
			OpenConnection srcConn = null;
			OpenConnection dstConn = null;
			boolean retryTransaction = false;

			try {
				srcConn = openSrcConn(this);
				dstConn = tryToOpenDstConn(this);
				processor.changeStatusToRunning();

				if (attempt == 1) {
					T sourceRecord;
					while ((sourceRecord = transformationQueue.poll()) != null) {
						claimedRecords.add(sourceRecord);
						T attemptRecord = (T) sourceRecord.createACopy();
						processor.transformAndLoadExtractedRecords(utilities.parseToList(attemptRecord), srcConn,
								dstConn);
						if (processor.getTaskResultInfo().hasFatalError()) {
							break;
						}
					}
				} else {
					for (T sourceRecord : claimedRecords) {
						T attemptRecord = (T) sourceRecord.createACopy();
						processor.transformAndLoadExtractedRecords(utilities.parseToList(attemptRecord), srcConn,
								dstConn);
						if (processor.getTaskResultInfo().hasFatalError()) {
							break;
						}
					}
				}

				completeExtractedTask(processor, srcConn, dstConn, true);

				Exception failure = processor.getTaskResultInfo().getFatalException();
				retryTransaction = failure != null && attempt < MAX_TRANSACTION_ATTEMPTS
						&& isRetryableTransactionFailure(failure, dstConn);

				if (!retryTransaction && !getRelatedEtlConf().hasTestingItem()
						&& !processor.getTaskResultInfo().hasFatalError()) {
					OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
				}
			} catch (Exception e) {
				retryTransaction = attempt < MAX_TRANSACTION_ATTEMPTS && isRetryableTransactionFailure(e, dstConn);
				if (!retryTransaction) {
					processor.changeStatusToStopped();
					processor.getTaskResultInfo().setFatalException(e);
				}
			} finally {
				OpenConnection.finalizeAllConnections(this, srcConn, dstConn);
			}

			if (!retryTransaction) {
				return;
			}

			discardPendingPersistence(processor);
			logWarn("Retrying complete RESULT_PARTITIONING worker transaction for processor {} after temporary "
					+ "database error. Attempt {} of {}", processor.getProcessorId(), attempt + 1,
					MAX_TRANSACTION_ATTEMPTS);
			processor.resetForTransactionRetry();
			if (!waitBeforeTransactionRetry(attempt, processor)) {
				return;
			}
		}
	}

	@SuppressWarnings("unchecked")
	void consumeTransformationQueue(TaskProcessor<T> processor, Queue<T> transformationQueue, boolean sharedConnections,
			OpenConnection sharedSrcConn, OpenConnection sharedDstConn) {

		OpenConnection srcConn = sharedSrcConn;
		OpenConnection dstConn = sharedDstConn;

		try {
			if (!sharedConnections) {
				srcConn = openSrcConn(this);
				dstConn = tryToOpenDstConn(this);
			}

			processor.changeStatusToRunning();

			T record;

			while ((record = transformationQueue.poll()) != null) {
				processor.transformExtractedRecords(utilities.parseToList(record), srcConn, dstConn);
			}

			processor.changeStatusToFinished();

			if (!sharedConnections && !getRelatedEtlConf().hasTestingItem()) {
				OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
			}
		} catch (Exception e) {
			processor.changeStatusToStopped();
			processor.getTaskResultInfo().setFatalException(e);
		} finally {
			if (!sharedConnections) {
				OpenConnection.finalizeAllConnections(this, srcConn, dstConn);
			}
		}
	}

	void completeExtractedTask(TaskProcessor<T> processor, OpenConnection srcConn, OpenConnection dstConn,
			boolean refreshProgress) throws DBException {
		if (processor.getTaskResultInfo().hasFatalError()) {
			discardPendingPersistence(processor);
			processor.changeStatusToStopped();
			return;
		}

		getController().afterEtl(processor.getTaskResultInfo().getAllSuccessfulyProcessedRecords(), srcConn, dstConn);
		if (processor.getTaskResultInfo().hasRecordsWithErrors()) {
			processor.getTaskResultInfo().documentErrors(srcConn, dstConn);
		}
		if (refreshProgress) {
			refreshProgressMeter(processor, srcConn);
		}
		processor.changeStatusToFinished();
	}

	void assertNoProcessorFailed(List<TaskProcessor<T>> processors) throws Exception {
		List<EtlOperationResultHeader<T>> results = new ArrayList<>(processors.size());
		for (TaskProcessor<T> processor : processors) {
			results.add(processor.getTaskResultInfo());
		}

		if (EtlOperationResultHeader.hasAtLeastOneFatalError(results)) {
			throw EtlOperationResultHeader.getDefaultResultWithFatalError(results).getFatalException();
		}
	}

	void flushStageArea(Connection srcConn, Connection dstConn) throws DBException {
		int pending = getPersistenceCoordinator().pendingCount(PersistenceType.STAGE_AREA);
		if (pending == 0) {
			return;
		}

		logInfo("Persisting {} pending StageArea records", pending);
		getPersistenceCoordinator().flush(PersistenceType.STAGE_AREA, srcConn, dstConn);
		logDebug("Pending StageArea records persisted");
	}

	void flushDefaultParents(Connection srcConn, Connection dstConn) throws DBException {
		int pending = getPersistenceCoordinator().pendingCount(PersistenceType.DEFAULT_PARENT);
		if (pending == 0) {
			return;
		}

		logInfo("Persisting {} pending records with default parents", pending);
		getPersistenceCoordinator().flush(PersistenceType.DEFAULT_PARENT, srcConn, dstConn);
		logDebug("Pending records with default parents persisted");
	}

	void flushPendingPersistence(Connection srcConn, Connection dstConn) throws DBException {
		int pending = getPersistenceCoordinator().pendingCount();
		if (pending == 0) {
			return;
		}

		logInfo("Persisting {} pending auxiliary records", pending);
		getPersistenceCoordinator().flush(srcConn, dstConn);
		logDebug("Pending auxiliary records persisted");
	}

	void flushPendingPersistenceUsingDedicatedConnection() throws DBException {
		if (getPersistenceCoordinator().pendingCount() == 0) {
			return;
		}

		OpenConnection srcConn = null;
		OpenConnection dstConn = null;
		try {
			srcConn = openSrcConn(this);
			dstConn = tryToOpenDstConn(this);
			flushPendingPersistence(srcConn, dstConn);
			OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
		} finally {
			OpenConnection.finalizeAllConnections(this, srcConn, dstConn);
		}
	}

	private void flushDefaultParentsUsingDedicatedConnection() throws DBException {
		if (getPersistenceCoordinator().pendingCount(PersistenceType.DEFAULT_PARENT) == 0) {
			return;
		}

		OpenConnection srcConn = null;
		OpenConnection dstConn = null;
		try {
			srcConn = openSrcConn(this);
			dstConn = tryToOpenDstConn(this);
			flushDefaultParents(srcConn, dstConn);
			OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
		} finally {
			OpenConnection.finalizeAllConnections(this, srcConn, dstConn);
		}
	}

	private void flushStageAreaUsingDedicatedConnection(TaskProcessor<?> owner) throws DBException {
		int pending = getPersistenceCoordinator().pendingCount(owner, PersistenceType.STAGE_AREA);
		if (pending == 0) {
			return;
		}

		OpenConnection srcConn = null;
		OpenConnection dstConn = null;
		try {
			srcConn = openSrcConn(this);
			dstConn = tryToOpenDstConn(this);
			logInfo("Persisting {} pending StageArea records for processor {}", pending, owner.getProcessorId());
			getPersistenceCoordinator().flush(owner, PersistenceType.STAGE_AREA, srcConn, dstConn);
			OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
			logDebug("Pending StageArea records persisted for processor {}", owner.getProcessorId());
		} finally {
			OpenConnection.finalizeAllConnections(this, srcConn, dstConn);
		}
	}

	void discardPendingPersistence(Object owner) {
		getPersistenceCoordinator().discard(owner);
	}

	void discardPendingPersistence() {
		getPersistenceCoordinator().discardPending();
	}

	void shutdownExecutor(ExecutorService executor) {
		if (executor == null) {
			return;
		}

		executor.shutdown();
		try {
			if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	public List<T> extract(IntervalExtremeRecord interval, Connection srcConn, Connection dstConn) throws DBException {
		boolean useMultiThreadSearch;

		if (getRelatedEtlOperationConfig().isDisableMultithreadingSearch()) {
			useMultiThreadSearch = false;
		} else {
			useMultiThreadSearch = true;
		}

		String threads = useMultiThreadSearch ? "MULTI-THREAD" : "SINGLE THREAD";

		if (interval != null) {
			logDebug("SERCHING NEXT RECORDS WITHIN INTERVAL {} USING {}", interval, threads);
		} else {
			logDebug("SERCHING NEXT RECORDS USING {}", threads);
		}

		List<T> records = null;

		if (useMultiThreadSearch) {
			records = getSearchParams().searchNextRecordsInMultiThreads(interval, null, null, srcConn, dstConn);
		} else {
			records = getSearchParams().search(interval, null, null, srcConn, dstConn);
		}

		logDebug("SERCH RECORDS FOR NEXT ITERATION ON ETL '{}' ON TABLE '{}' WAS FINISHED! FOUND '{}' RECORDS",
				this.getEtlItemConfiguration().getConfigCode(), getSrcConf().getTableName(),
				utilities.arraySize(records));

		return records;
	}

	/**
	 * @param persistTheWork
	 * @param sharedSrcConn
	 * @param sharedDstConn
	 * @param interval
	 * @param taskProcessor
	 * @return
	 */
	void performExtractTransformationAndLoading(TaskProcessor<T> taskProcessor, boolean useMultiTreadSearch,
			boolean persistTheWork, OpenConnection srcConn, OpenConnection dstConn) {

		try {
			taskProcessor.changeStatusToRunning();

			logTrace("INITIALIZING TASK FOR INTERVAL " + taskProcessor.getLimits());

			taskProcessor.extractTransformAndLoad(useMultiTreadSearch, srcConn, dstConn);

			if (!taskProcessor.getTaskResultInfo().hasFatalError()) {
				getController().afterEtl(taskProcessor.getTaskResultInfo().getAllSuccessfulyProcessedRecords(), srcConn,
						dstConn);

				if (taskProcessor.getTaskResultInfo().hasRecordsWithErrors()) {
					logWarn("Some errors where found loading '"
							+ taskProcessor.getTaskResultInfo().getRecordsWithErrorsAsEtlDatabaseObject().size()
							+ "! The errors will be documented");

					taskProcessor.getTaskResultInfo().documentErrors(srcConn, dstConn);
				}

				refreshProgressMeter(taskProcessor, srcConn);

				if (persistTheWork) {
					flushPendingPersistence(srcConn, dstConn);
				} else if (getRelatedEtlConf().hasTestingItem()) {
					discardPendingPersistence();
				}

				taskProcessor.getLimits().markAsProcessed();

				if (persistTheWork) {
					logTrace("PERSISTING WORK OF TASK ON INTERVAL " + taskProcessor.getLimits());

					getThreadRecordIntervalsManager().save();

					OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);

					logTrace("WORK OF TASK PERSISTED ON INTERVAL " + taskProcessor.getLimits());
				}

				taskProcessor.changeStatusToFinished();
			} else {
				discardPendingPersistence(taskProcessor);
				taskProcessor.changeStatusToStopped();
			}
		} catch (Exception e) {
			discardPendingPersistence(taskProcessor);
			taskProcessor.changeStatusToStopped();

			if (!canDeferFailureToWorkerTransactionRetry(taskProcessor, e, dstConn)) {
				stopOperationDueError(e);
			}

			taskProcessor.getTaskResultInfo().setFatalException(e);
		} finally {
			if (persistTheWork) {
				OpenConnection.finalizeAllConnections(this, srcConn, dstConn);
			}
		}
	}

	private boolean canDeferFailureToWorkerTransactionRetry(TaskProcessor<T> processor, Exception failure,
			Connection dstConn) {
		ParallelProcessingStrategyType strategy = getParallelProcessingStrategy();
		boolean hasWorkerTransactionRetry = strategy.isRangePartitioning() || strategy.isResultPartitioning();

		return processor.isRunningInConcurrency() && hasWorkerTransactionRetry
				&& !getRelatedEtlOperationConfig().isUseSharedConnectionPerThread()
				&& isRetryableTransactionFailure(failure, dstConn);
	}

	void resetCurrentTaskProcessor(int qtyProcessors) {
		this.currentTaskProcessor = new ArrayList<>(qtyProcessors);
	}

	private void calculateStatistics(EtlTotalRecordsCountStrategy overrideCountStrategy) throws DBException {
		OpenConnection conn = getController().openSrcConnection(this);

		EtlTotalRecordsCountStrategy countStrategy = overrideCountStrategy != null ? overrideCountStrategy
				: this.getRelatedEtlOperationConfig().getTotalCountStrategy();

		try {
			logWarn("CALCULATING STATISTICS! Using '"
					+ this.getRelatedEtlOperationConfig().getTotalCountStrategy().toString() + "' strategy...");

			int remaining = getProgressMeter().getRemain();
			int total = getProgressMeter().getTotal();
			int processed = total - remaining;

			if (countStrategy.isCountAlways()) {
				if (total > 0) {
					logDebug(
							"Recorded statistic found! But the statistics will be recalculated as per configuration totalCountStrategy set to COUNT_ALWAYS");

					total = 0;
				}

				total = 0;
				processed = 0;
			}

			if (total == 0) {
				if (countStrategy.isUseMaxRecordIdAsCount()) {
					total = (int) this.getProgressMeter().getMaxRecordId();
					remaining = total;
				} else if (countStrategy.isUseProvided()) {
					total = this.getRelatedEtlOperationConfig().getTotalAvaliableRecordsToProcess();
					remaining = total;
				} else {
					logDebug("Loading from Database...");

					total = getSearchParams().countAllRecords(this.getController(), conn);
					remaining = getSearchParams().countNotProcessedRecords(this.getController(), conn);
				}

				processed = total - remaining;
			}

			this.getProgressMeter().refresh(this.getProgressMeter().getStatusMsg(), total, processed,
					getThreadRecordIntervalsManager().getCurrentLastRecordId());

			if (getRelatedOperationController().isResumable()) {
				this.getTableOperationProgressInfo().save(conn);
			}

			logInfo("CALCULATION DONE. TOTAL RECORDS " + total + "! PROCESSED RECORDS " + processed);

			conn.markAsSuccessifullyTerminated();

			reportProgress(null);
		} catch (DBException e) {
			stopOperationDueError(e);
		} finally {
			conn.finalizeConnection(this);
		}
	}

	void stopOperationDueError(Exception e) {
		getRelatedOperationController().requestStopDueError(this, e);
	}

	public void logErr(String msg, Exception e) {
		LOG.err(msg, e);
	}

	public void logErr(String msg, Exception e, Object... arguments) {
		LOG.err(msg, e, arguments);
	}

	@Override
	public void requestStop() {
		if (isStopped() || isFinished() || stopRequested()) {
			return;
		}

		synchronized (LOCK) {
			this.stopRequested = true;

			changeStatusToStopping();

			boolean stopNow = false;

			if (utilities.listHasElement(this.getCurrentTaskProcessor())) {
				boolean atLeaseOneIsRunning = false;

				for (TaskProcessor<T> t : this.getCurrentTaskProcessor()) {
					if (!t.isStopped() && !t.isFinished()) {
						atLeaseOneIsRunning = true;
					}
				}

				if (!atLeaseOneIsRunning) {
					stopNow = true;
				}
			} else {
				stopNow = true;
			}

			if (stopNow) {
				logWarn("No task is running, stopping the Engine now: " + this.getEngineId());

				changeStatusToStopped();
			}

		}
	}

	public File getThreadsDir() {
		String subFolder = this.getRelatedOperationController().generateOperationStatusFolder();

		subFolder += FileUtilities.getPathSeparator() + "threads";

		return new File(subFolder);
	}

	public File getDataDir() {
		String subFolder = getRelatedEtlConf().getEtlRootDirectory();

		subFolder += FileUtilities.getPathSeparator() + "data";

		subFolder += FileUtilities.getPathSeparator() + getRelatedEtlConf().getOriginAppLocationCode();

		return new File(subFolder);
	}

	public int getMaxRecordsPerProcessing() {
		return getController().getOperationConfig().getProcessingBatch();
	}

	public int getMaxSupportedProcessors() {
		return getController().getOperationConfig().getMaxSupportedProcessors();
	}

	public void setMaxSupportedProcessors(int maxSupportedProcessors) {
		getController().getOperationConfig().setMaxSupportedProcessors(maxSupportedProcessors);
	}

	public void setMaxRecordsPerProcessing(int maxRecordsPerProcessing) {
		getController().getOperationConfig().setProcessingBatch(maxRecordsPerProcessing);
	}

	private boolean mustRestartInTheEnd() {
		return getController().mustRestartInTheEnd();
	}

	public void logErr(String msg, Throwable throwable) {
		LOG.err(msg, throwable);
	}

	public OperationController<T> getRelatedOperationController() {
		return controller;
	}

	public EtlOperationConfig getRelatedEtlOperationConfig() {
		return getRelatedOperationController().getOperationConfig();
	}

	public void logInfo(String msg) {
		LOG.info(msg);
	}

	public void logInfo(String msg, Object... arguments) {
		LOG.info(msg, arguments);
	}

	public void logDebug(String msg) {
		LOG.debug(msg);
	}

	public void logDebug(String msg, Object... arguments) {
		LOG.debug(msg, arguments);
	}

	public void logWarn(String msg, Object... arguments) {
		LOG.warn(msg, arguments);
	}

	public void logWarn(String msg) {
		LOG.warn(msg);
	}

	public void logTrace(String msg, Object... arguments) {
		LOG.trace(msg, arguments);
	}

	public void logTrace(String msg) {
		LOG.trace(msg);
	}

	@Override
	public void logWarn(String msg, long interval, boolean suppressIfAnyRecentLog) {
		LOG.warn(msg, interval, suppressIfAnyRecentLog);
	}

	@Override
	public String toString() {
		return this.getEngineId();
	}

	public static <T extends EtlDatabaseObject> Engine<T> init(OperationController<T> controller,
			EtlItemConfiguration etlItemConfiguration, TableOperationProgressInfo tableOperationProgressInfo) {

		Engine<T> monitor = new Engine<>(controller, etlItemConfiguration, tableOperationProgressInfo);

		return monitor;
	}

	@Override
	public void onStop() {
		getTotalTimer().stop();

		this.changeStatusToStopped();
	}

	@Override
	public void onFinish() {
		getTotalTimer().stop();

		this.changeStatusToFinished();
	}

	@Override
	public TimeController getTotalTimer() {
		return getProgressMeter() != null ? getProgressMeter().getTotalTimer() : null;
	}

	@Override
	public TimeController getProcessingTimer() {
		return getProgressMeter() != null ? getProgressMeter().getProcessingTimer() : null;
	}

	@Override
	public TimeController getPauseTimer() {
		return getProgressMeter() != null ? getProgressMeter().getPauseTimer() : null;
	}

	public TableOperationProgressInfo getTableOperationProgressInfo() {
		return tableOperationProgressInfo;
	}

	@Override
	public boolean stopRequested() {
		return this.stopRequested;
	}

	@Override
	public boolean isStopped() {
		if (isNotInitialized())
			return false;

		return MonitoredOperation.super.isStopped();
	}

	@Override
	public boolean isFinished() {
		if (isNotInitialized()) {
			return false;
		}

		return MonitoredOperation.super.isFinished();
	}

	@Override
	public int getWaitTimeToCheckStatus() {
		return 5;
	}

	public synchronized void refreshProgressMeter(TaskProcessor<T> taskProcessor, Connection conn) throws DBException {
		int newlyProcessedRecords = taskProcessor.getTaskResultInfo().countAllSuccessfulyProcessedRecords();

		logDebug("REFRESHING PROGRESS METER FOR MORE " + newlyProcessedRecords + " RECORDS.");
		this.getProgressMeter().refresh("RUNNING", this.getProgressMeter().getTotal(),
				this.getProgressMeter().getProcessed() + newlyProcessedRecords,
				this.getThreadRecordIntervalsManager().getCurrentLastRecordId());

		if (getRelatedOperationController().isResumable()) {
			this.getTableOperationProgressInfo().save(conn);
		}

		logDebug("PROGRESS METER REFRESHED");

		reportProgress(taskProcessor);
	}

	public void reportProgress(TaskProcessor<T> taskProcessor) {
		EtlProgressMeter globalProgressMeter = this.getProgressMeter();

		StringBuilder log = new StringBuilder();

		int qtyThreads = this.getRelatedEtlOperationConfig().getParallelProcessingStrategy().isMultiThreaded()
				? this.getThreadRecordIntervalsManager().getMaxSupportedProcessors()
				: 1;

		log.append("\n");
		log.append("-----------\n");
		log.append("PROGRESS").append(":\n");

		log.append(
				"------------------------------------------------------------------------------------------------------\n");

		log.append(formatReportLine("OPERATION", this.getEngineId()));
		log.append("\n");

		long diff = globalProgressMeter.getTotalToAnalyze() - globalProgressMeter.getTotal();

		if (!utilities.isBetween(diff, -10, 10)) {

			log.append(formatReportLine("TOTAL TO ANALYZE",
					utilities.generateCommaSeparetedNumber(globalProgressMeter.getTotalToAnalyze()) + ", PROCESSED: "
							+ globalProgressMeter.getDetailedProgressOfAnalyzedRecords() + ", REMAINING: "
							+ globalProgressMeter.getDetailedRemainingToAnalize()));
		}

		log.append(formatReportLine("TOTAL RECS TO PROCESS",
				utilities.generateCommaSeparetedNumber(globalProgressMeter.getTotal()) + ", PROCESSED: "
						+ globalProgressMeter.getDetailedProgress() + ", REMAINING: "
						+ globalProgressMeter.getDetailedRemaining()));

		log.append("\n");

		if (taskProcessor != null) {
			log.append(formatReportLine("REPORTING LIMITS", taskProcessor.getLimits()));
		}

		log.append(formatReportLine("PROCESSING TIME", globalProgressMeter.getHumanReadbleProcessingTime()));

		log.append(formatReportLine("STOP TIME", globalProgressMeter.getHumanReadblePauseTime()));

		log.append(formatReportLine("TOTAL TIME", globalProgressMeter.getHumanReadbleTotalTime()));

		log.append(formatReportLine("REMAINING TIME", globalProgressMeter.getHumanReadbleEstimatedRemainingTime()));

		log.append("\n");

		log.append(formatReportLine("THREADING MODE",
				this.determineProcessingStrategy().toString() + "(" + qtyThreads + ")"));

		log.append(
				"------------------------------------------------------------------------------------------------------\n");

		this.logWarn(log.toString());
	}

	private static String formatReportLine(String label, Object value) {
		return String.format("%-22s : %s%n", label, value);
	}

	public synchronized void requestDisplayOfEtlResult(DstConf dstConf, List<EtlDatabaseObject> resultObjs) {
		if (this.getRecordsToDisplay() == null) {
			this.setRecordsToDisplay(new HashMap<>());
		}

		List<EtlDatabaseObject> dstRecords = this.getRecordsToDisplay().get(dstConf.getTableName());

		if (dstRecords == null) {
			dstRecords = new ArrayList<>();
		}

		dstRecords.addAll(resultObjs);

		this.getRecordsToDisplay().put(dstConf.getTableName(), dstRecords);
	}

	public int getCurrentIteration() {
		return this.currentIteration;
	}

	@Override
	public void onStart() {
		this.changeStatusToRunning();
	}

	@Override
	public void onSleep() {
		this.changeStatusToSleeping();
	}

	private void performEngineFinalization() throws DBException, Exception {
		if (!stopRequested()) {
			if (mustDoFinalCheck()) {
				perfomeFinalization();
			}

			if (this.getRecordsToDisplay() != null) {
				for (DstConf dstConf : this.getEtlItemConfiguration().getDstConf()) {
					if (dstConf.getDstType().isConsole()) {
						displayResultInConsole(this.getRecordsToDisplay().get(dstConf.getTableName()));
					} else if (dstConf.getDstType().isPopUp()) {
						displayResultInPopUp(this.getRecordsToDisplay().get(dstConf.getTableName()));
					} else
						throw new ForbiddenOperationException("Unsupported display method " + dstConf.getDstType());
				}
			}

			if (!mustRestartInTheEnd()) {
				changeStatusToFinished();

				if (getRelatedOperationController().isResumable()) {
					getRelatedOperationController().markTableOperationAsFinished(this.getEtlItemConfiguration());
				}

				getRelatedOperationController().finalize(this);
			}
		}
	}

	/**
	 * @throws DBException
	 */
	public void perfomeFinalization() throws DBException, Exception {
		this.finalCheckStatus = MigrationFinalCheckStatus.ONGOING;

		logDebug("INITIALIZING FINAL CHECK...");

		if (getThreadRecordIntervalsManager().getFinalCheckIntervalsManager() == null) {
			getThreadRecordIntervalsManager().initializeFinalCheckIntervalManager();
		}

		getSearchParams().setFinalCheckStatus(finalCheckStatus);

		getSearchParams()
				.setThreadRecordIntervalsManager(getThreadRecordIntervalsManager().getFinalCheckIntervalsManager());

		process(ParallelProcessingStrategyType.SINGLE_THREAD);

		logDebug("FINAL CHECK FINISHED!");

		this.finalCheckStatus = MigrationFinalCheckStatus.DONE;
	}

	private void displayResultInPopUp(List<EtlDatabaseObject> list) {
		throw new ForbiddenOperationException("Currently popup not supported!");
	}

	private void displayResultInConsole(List<EtlDatabaseObject> objs) {
		if (utilities.listHasElement(objs)) {
			String header = utilities.generateTabDelimitedHeader(objs.get(0));
			String separator = utilities.maskToken(header, header, '#');
			System.out.println(separator);
			System.out.println(header);
			System.out.println(utilities.parseToTabDelimitedWithoutHeader(objs));
			System.out.println(separator);
		} else {
			System.out.println("No result");
		}
	}
}
