package org.openmrs.module.epts.etl.engine;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.model.pojo.generic.RecordWithDefaultParentInfo;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.concurrent.EtlThreadFactory;
import org.openmrs.module.epts.etl.utilities.concurrent.MonitoredOperation;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeController;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

/**
 * This class monitor all {@link TaskProcessor}s of an
 * {@link OperationController}
 * 
 * @author jpboane
 */
public class Engine<T extends EtlDatabaseObject> extends AbstractBaseConfiguration implements MonitoredOperation {

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

	public Engine(OperationController<T> controller, EtlItemConfiguration etlItemConfiguration,
			TableOperationProgressInfo tableOperationProgressInfo) {
		this.controller = controller;
		this.etlItemConfiguration = etlItemConfiguration;

		this.engineId = (this.getRelatedEtlOperationConfig().getOperationType() + "_" + getEtlConfigCode())
				.toLowerCase();

		this.operationStatus = EtlOperationStatus.NOT_INITIALIZED;
		this.tableOperationProgressInfo = tableOperationProgressInfo;

		this.finalCheckStatus = MigrationFinalCheckStatus.NOT_INITIALIZED;
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

	public void processIntervalsInSingleProcessor(List<IntervalExtremeRecord> availableIntervals) throws Exception {
		for (IntervalExtremeRecord interval : availableIntervals) {
			if (stopRequested() || isStopped()) {
				logWarn("Aborting engine as stop requested!", 10, true);
				return;
			}

			TaskProcessor<T> taskProcessor = getController().initRelatedTaskProcessor(this, interval, false);
			taskProcessor.setProcessorId(this.getEngineId());

			boolean persistTheWork = !getRelatedEtlConf().hasTestingItem();
			boolean useMultiThreadSearch = true;

			performExtractTransformationAndLoading(taskProcessor, useMultiThreadSearch, persistTheWork,
					openSrcConn(this), tryToOpenDstConn(this));

			if (taskProcessor.getTaskResultInfo().hasFatalError()) {
				stopOperationDueError(taskProcessor.getTaskResultInfo().getFatalException());
			}
		}
	}

	public void processRangePartitionedIntervals(List<IntervalExtremeRecord> availableIntervals) throws Exception {
		logDebug("Initializing " + availableIntervals.size() + " processors to performe task on a interval "
				+ getThreadRecordIntervalsManager().getCurrentLimits() + "!".toUpperCase());

		EtlThreadFactory<T> threadFactory = new EtlThreadFactory<>(this);

		boolean useSharedConnection = getRelatedEtlOperationConfig().isUseSharedConnectionPerThread();

		resetCurrentTaskProcessor(availableIntervals.size());

		List<CompletableFuture<Void>> tasks = new ArrayList<>(availableIntervals.size());

		ExecutorService executor = Executors.newFixedThreadPool(availableIntervals.size(), threadFactory);

		OpenConnection sharedSrcConn = openSrcConn(this);
		OpenConnection sharedDstConn = tryToOpenDstConn(this);

		try {
			for (int i = 0; i < availableIntervals.size(); i++) {
				TaskProcessor<T> processor = initTaskProcessor(availableIntervals.get(i), i);

				getCurrentTaskProcessor().add(processor);

				tasks.add(CompletableFuture.runAsync(
						() -> processRangePartition(processor, useSharedConnection, sharedSrcConn, sharedDstConn),
						executor));
			}

			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();

			List<EtlOperationResultHeader<T>> results = collectProcessorResults(getCurrentTaskProcessor());

			if (EtlOperationResultHeader.hasAtLeastOneFatalError(results)) {
				stopOperationDueError(
						EtlOperationResultHeader.getDefaultResultWithFatalError(results).getFatalException());
				return;
			}

			if (useSharedConnection && !getRelatedEtlConf().hasTestingItem()) {
				OpenConnection.markAllAsSuccessifullyTerminected(sharedSrcConn, sharedDstConn);
				getThreadRecordIntervalsManager().getCurrentLimits().markAsProcessed();
				getThreadRecordIntervalsManager().save();
			}

			if (!EtlOperationResultHeader.hasAtLeastOneRecordsWithRecursiveRelashionships(results)) {
				getThreadRecordIntervalsManager().getCurrentLimits().markSkippedRecordsAsProcessed();
			}
		} finally {
			OpenConnection.finalizeAllConnections(this, sharedSrcConn, sharedDstConn);

			shutdownExecutor(executor);
		}
	}

	private void processRangePartition(TaskProcessor<T> processor, boolean useSharedConnection,
			OpenConnection sharedSrcConn, OpenConnection sharedDstConn) {
		if (useSharedConnection) {
			performExtractTransformationAndLoading(processor, false, false, sharedSrcConn, sharedDstConn);
			return;
		}

		try {
			boolean persistWork = !getRelatedEtlConf().hasTestingItem();
			performExtractTransformationAndLoading(processor, false, persistWork, openSrcConn(this),
					tryToOpenDstConn(this));
		} catch (DBException e) {
			processor.changeStatusToStopped();
			processor.getTaskResultInfo().setFatalException(e);
		}
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

	private List<EtlOperationResultHeader<T>> collectProcessorResults(List<TaskProcessor<T>> processors) {
		List<EtlOperationResultHeader<T>> results = new ArrayList<>(processors.size());
		for (TaskProcessor<T> processor : processors) {
			results.add(processor.getTaskResultInfo());
		}
		return results;
	}

	private synchronized void increaseIteration() {
		this.currentIteration++;
	}

	public void tryToProcessSkippedrecords() throws DBException, Exception {
		if (stopRequested()) {
			return;
		}

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

	/**
	 * Extracts each engine interval once, partitions the resulting list, and then
	 * runs only the phases selected by the configured result-partitioning strategy.
	 */
	public void processResultPartitionedIntervals(ParallelProcessingStrategyType strategy,
			List<IntervalExtremeRecord> availableIntervals) throws Exception {

		EtlThreadFactory<T> threadFactory = new EtlThreadFactory<>(this);

		for (IntervalExtremeRecord availableInterval : availableIntervals) {
			if (stopRequested()) {
				logWarn("Stopping the Task as Stop Requested!");
				changeStatusToStopped();
				return;
			}

			processExtractedInterval(strategy, availableInterval, threadFactory);
		}
	}

	private void processExtractedInterval(ParallelProcessingStrategyType strategy,
			IntervalExtremeRecord availableInterval, EtlThreadFactory<T> threadFactory) throws Exception {
		boolean persistWork = !getRelatedEtlConf().hasTestingItem();
		OpenConnection extractionSrcConn = openSrcConn(this);
		OpenConnection extractionDstConn = tryToOpenDstConn(this);

		try {
			List<T> extractedRecords = extract(availableInterval, extractionSrcConn, extractionDstConn);

			if (!utilities.listHasElement(extractedRecords)) {
				availableInterval.markAsProcessed();
				persistCompletedResultPartition(extractionSrcConn, extractionDstConn, persistWork);
				return;
			}

			int processorCount = getController().getOperationConfig().getMaxSupportedProcessors();
			
			Queue<T> transformationQueue = new ConcurrentLinkedQueue<>(extractedRecords);

			if (strategy.isResultPartitioning()) {
				processResultPartitioning(availableInterval, transformationQueue, processorCount, threadFactory,
						extractionSrcConn, extractionDstConn);
			} else if (strategy.isParallelTransformSerialPersist()) {
				processParallelTransformSerialPersist(availableInterval, extractedRecords, transformationQueue,
						processorCount, threadFactory, extractionSrcConn, extractionDstConn);
			} else {
				throw new ForbiddenOperationException("Unsupported result-partitioning strategy: " + strategy);
			}

			availableInterval.markAsProcessed();

			persistCompletedResultPartition(extractionSrcConn, extractionDstConn, persistWork);
		} catch (Exception e) {
			stopOperationDueError(e);
			throw e;
		} finally {
			OpenConnection.finalizeAllConnections(this, extractionSrcConn, extractionDstConn);
		}
	}

	private void processResultPartitioning(IntervalExtremeRecord interval, Queue<T> transformationQueue,
			int processorCount, EtlThreadFactory<T> threadFactory, OpenConnection sharedSrcConn,
			OpenConnection sharedDstConn) throws Exception {

		boolean sharedConnections = getRelatedEtlOperationConfig().isUseSharedConnectionPerThread();
		ExecutorService executor = Executors.newFixedThreadPool(processorCount, threadFactory);
		resetCurrentTaskProcessor(processorCount);
		List<CompletableFuture<Void>> tasks = new ArrayList<>(processorCount);

		try {
			for (int i = 0; i < processorCount; i++) {
				TaskProcessor<T> processor = initTaskProcessor(interval, i);
				getCurrentTaskProcessor().add(processor);
				tasks.add(CompletableFuture.runAsync(() -> consumeTransformAndLoadQueue(processor, transformationQueue,
						sharedConnections, sharedSrcConn, sharedDstConn), executor));
			}

			CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
			assertNoProcessorFailed(getCurrentTaskProcessor());
		} finally {
			shutdownExecutor(executor);
		}
	}

	@SuppressWarnings("unchecked")
	private void processParallelTransformSerialPersist(IntervalExtremeRecord interval, List<T> extractedRecords,
			Queue<T> transformationQueue, int processorCount, EtlThreadFactory<T> threadFactory,
			OpenConnection sharedSrcConn, OpenConnection sharedDstConn) throws Exception {

		boolean sharedConnections = getRelatedEtlOperationConfig().isUseSharedConnectionPerThread();
		ExecutorService executor = Executors.newFixedThreadPool(processorCount, threadFactory);
		resetCurrentTaskProcessor(processorCount + 1);
		List<CompletableFuture<Void>> transformationTasks = new ArrayList<>(processorCount);

		try {
			for (int i = 0; i < processorCount; i++) {
				TaskProcessor<T> transformer = initTaskProcessor(interval, i);
				getCurrentTaskProcessor().add(transformer);
				transformationTasks.add(CompletableFuture.runAsync(() -> consumeTransformationQueue(transformer,
						transformationQueue, sharedConnections, sharedSrcConn, sharedDstConn), executor));
			}

			CompletableFuture.allOf(transformationTasks.toArray(new CompletableFuture[0])).get();
			assertNoProcessorFailed(getCurrentTaskProcessor());
		} finally {
			shutdownExecutor(executor);
		}

		TaskProcessor<T> loader = initTaskProcessor(interval, processorCount);
		getCurrentTaskProcessor().add(loader);
		loader.changeStatusToRunning();
		loader.loadTransformedRecords(extractedRecords, sharedSrcConn, sharedDstConn);
		completeExtractedTask(loader, sharedSrcConn, sharedDstConn, true);
		assertNoProcessorFailed(utilities.parseToList(loader));
	}

	private TaskProcessor<T> initTaskProcessor(IntervalExtremeRecord interval, int index) {
		TaskProcessor<T> processor = getController().initRelatedTaskProcessor(this, interval, true);
		processor.setProcessorId(getEngineId() + "_" + utilities.garantirXCaracterOnNumber(index, 2));
		return processor;
	}

	@SuppressWarnings("unchecked")
	private void consumeTransformAndLoadQueue(TaskProcessor<T> processor, Queue<T> transformationQueue,
			boolean sharedConnections, OpenConnection sharedSrcConn, OpenConnection sharedDstConn) {
		OpenConnection srcConn = sharedSrcConn;
		OpenConnection dstConn = sharedDstConn;

		try {
			if (!sharedConnections) {
				srcConn = openSrcConn(this);
				dstConn = tryToOpenDstConn(this);
			}

			processor.changeStatusToRunning();

			T record;

			while (!processor.getTaskResultInfo().hasFatalError() && (record = transformationQueue.poll()) != null) {
				processor.transformAndLoadExtractedRecords(utilities.parseToList(record), srcConn, dstConn);
			}
			completeExtractedTask(processor, srcConn, dstConn, true);

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

	@SuppressWarnings("unchecked")
	private void consumeTransformationQueue(TaskProcessor<T> processor, Queue<T> transformationQueue,
			boolean sharedConnections, OpenConnection sharedSrcConn, OpenConnection sharedDstConn) {
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

	private void completeExtractedTask(TaskProcessor<T> processor, OpenConnection srcConn, OpenConnection dstConn,
			boolean refreshProgress) throws DBException {
		if (processor.getTaskResultInfo().hasFatalError()) {
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

	private void assertNoProcessorFailed(List<TaskProcessor<T>> processors) throws Exception {
		List<EtlOperationResultHeader<T>> results = new ArrayList<>(processors.size());
		for (TaskProcessor<T> processor : processors) {
			results.add(processor.getTaskResultInfo());
		}

		if (EtlOperationResultHeader.hasAtLeastOneFatalError(results)) {
			throw EtlOperationResultHeader.getDefaultResultWithFatalError(results).getFatalException();
		}
	}

	private void persistCompletedResultPartition(OpenConnection srcConn, OpenConnection dstConn, boolean persistWork)
			throws DBException {
		if (persistWork) {
			getThreadRecordIntervalsManager().save();
			OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);
		}
	}

	private void shutdownExecutor(ExecutorService executor) {
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
	private void performExtractTransformationAndLoading(TaskProcessor<T> taskProcessor, boolean useMultiTreadSearch,
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

				taskProcessor.getLimits().markAsProcessed();

				if (persistTheWork) {
					logTrace("PERSISTING WORK OF TASK ON INTERVAL " + taskProcessor.getLimits());

					getThreadRecordIntervalsManager().save();

					OpenConnection.markAllAsSuccessifullyTerminected(srcConn, dstConn);

					logTrace("WORK OF TASK PERSISTED ON INTERVAL " + taskProcessor.getLimits());
				}

				taskProcessor.changeStatusToFinished();
			} else {
				taskProcessor.changeStatusToStopped();
			}
		} catch (Exception e) {
			taskProcessor.changeStatusToStopped();

			stopOperationDueError(e);

			taskProcessor.getTaskResultInfo().setFatalException(e);
		} finally {
			if (persistTheWork) {
				OpenConnection.finalizeAllConnections(this, srcConn, dstConn);
			}
		}
	}

	private void resetCurrentTaskProcessor(int qtyProcessors) {
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

	private void stopOperationDueError(Exception e) {
		getRelatedOperationController().requestStopDueError(this, e);
	}

	public void logErr(String msg, Exception e) {
		getRelatedOperationController().logErr(msg, e);
	}

	public void logErr(String msg, Exception e, Object... arguments) {
		getRelatedOperationController().logErr(msg, e, arguments);
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
		getRelatedOperationController().logErr(msg, throwable);
	}

	public OperationController<T> getRelatedOperationController() {
		return controller;
	}

	public EtlOperationConfig getRelatedEtlOperationConfig() {
		return getRelatedOperationController().getOperationConfig();
	}

	public void logInfo(String msg) {
		getRelatedOperationController().logInfo(msg);
	}

	public void logInfo(String msg, Object... arguments) {
		getRelatedOperationController().logInfo(msg, arguments);
	}

	public void logDebug(String msg) {
		getRelatedOperationController().logDebug(msg);
	}

	public void logDebug(String msg, Object... arguments) {
		getRelatedOperationController().logDebug(msg, arguments);
	}

	public void logWarn(String msg, Object... arguments) {
		getRelatedOperationController().logWarn(msg, arguments);
	}

	public void logWarn(String msg) {
		getRelatedOperationController().logWarn(msg);
	}

	public void logTrace(String msg, Object... arguments) {
		getRelatedOperationController().logTrace(msg, arguments);
	}

	public void logTrace(String msg) {
		getRelatedOperationController().logTrace(msg);
	}

	@Override
	public void logWarn(String msg, long interval, boolean suppressIfAnyRecentLog) {
		getRelatedOperationController().logWarn(msg, interval, suppressIfAnyRecentLog);
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

		log.append(formatReportLine("USING THREADS", qtyThreads));

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
