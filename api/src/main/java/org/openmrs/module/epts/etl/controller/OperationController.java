package org.openmrs.module.epts.etl.controller;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractBaseConfiguration;
import org.openmrs.module.epts.etl.conf.AbstractTableConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;
import org.openmrs.module.epts.etl.conf.types.EtlOperationType;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.engine.TaskProcessor;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.ThreadRecordIntervalsManager;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.OperationProgressInfo;
import org.openmrs.module.epts.etl.model.TableOperationProgressInfo;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.DateAndTimeUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeController;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class represent a controller of an synchronization operation. Eg. Export
 * data from tables to JSON files.
 * 
 * @author jpboane
 */
public abstract class OperationController<T extends EtlDatabaseObject> extends AbstractBaseConfiguration
		implements Controller {

	public static final Object LOCK = new Object();

	protected EtlLogger logger;

	protected ProcessController processController;

	protected List<Engine<T>> enginesActivititieMonitor;

	protected List<Engine<T>> allGeneratedEngineMonitor;

	protected List<OperationController<? extends EtlDatabaseObject>> children;

	protected String controllerId;

	protected EtlOperationStatus operationStatus;

	protected volatile boolean stopRequested;

	protected EtlOperationConfig operationConfig;

	protected OperationController<? extends EtlDatabaseObject> parent;

	protected boolean selfTreadKilled;

	protected Exception lastException;

	protected OperationProgressInfo progressInfo;

	private List<EtlItemConfiguration> finalizedItems;

	public OperationController(ProcessController processController, EtlOperationConfig operationConfig) {
		this.logger = new EtlLogger(OperationController.class);

		this.processController = processController;
		this.operationConfig = operationConfig;

		this.operationStatus = EtlOperationStatus.NOT_INITIALIZED;

		this.controllerId = operationConfig.generateOperationId();

		OpenConnection conn = null;
		try {
			conn = openSrcConnection(this);

			this.progressInfo = this.processController.initOperationProgressMeter(this, conn);

			conn.markAsSuccessifullyTerminated();
		} catch (DBException e) {
			throw new RuntimeException(e);
		} finally {
			finalizeConnection(conn, this);
		}

	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return processController.getRelatedEtlConf();
	}

	@Override
	public EtlOperationStatus getOperationStatus() {
		return operationStatus;
	}

	@Override
	public void setOperationStatus(EtlOperationStatus status) {
		this.operationStatus = status;
	}

	private List<EtlItemConfiguration> getFinalizedItems() {
		return finalizedItems;
	}

	private void setFinalizedItems(List<EtlItemConfiguration> finalizedItems) {
		this.finalizedItems = finalizedItems;
	}

	public EtlDstType getDstType() {
		return getOperationConfig().getDstType();
	}

	public DBConnectionInfo getSrcConnInfo() {
		return getEtlConfiguration().getSrcConnInfo();
	}

	public DBConnectionInfo getDstConnInfo() {
		return getEtlConfiguration().getDstConnInfo();
	}

	public void resetProgressInfo(Connection conn) throws DBException {
		if (this.progressInfo != null) {
			this.progressInfo.reset(conn);
		}

		this.progressInfo = this.processController.initOperationProgressMeter(this, conn);
	}

	public OperationProgressInfo getProgressInfo() {
		return progressInfo;
	}

	public EtlLogger getLogger() {
		return logger;
	}

	public OperationController<? extends EtlDatabaseObject> getParentConf() {
		return parent;
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public boolean hasChild() {
		return this.children != null;
	}

	public boolean hasNestedController() {
		return hasChild() || hasParent();
	}

	public void setParent(OperationController<? extends EtlDatabaseObject> parent) {
		this.parent = parent;
	}

	public EtlOperationConfig getOperationConfig() {
		return operationConfig;
	}

	public List<OperationController<? extends EtlDatabaseObject>> getChildren() {
		return children;
	}

	public void setChildren(List<OperationController<? extends EtlDatabaseObject>> children) {
		this.children = children;
	}

	public ProcessController getProcessController() {
		return processController;
	}

	public boolean isParallelModeProcessing() {
		return this.getOperationConfig().isParallelModeProcessing();
	}

	public List<Engine<T>> getAllGeneratedEngineMonitor() {
		return allGeneratedEngineMonitor;
	}

	public void setAllGeneratedEngineMonitor(List<Engine<T>> allGeneratedEngineMonitor) {
		this.allGeneratedEngineMonitor = allGeneratedEngineMonitor;
	}

	void basicInitAllConfElements() throws DBException {

		if (getEtlConfiguration().hasEtlItemsConf() || getEtlConfiguration().hasTestingItem()) {

			OpenConnection srcConn = openSrcConnection(this);
			OpenConnection dstConn = tryToOpenDstConn(this);

			try {
				if (getEtlConfiguration().hasEtlItemsConf()) {
					for (EtlItemConfiguration config : getProcessController().getRelatedEtlConf()
							.getEtlItemConfiguration()) {
						config.doMinimalTableInitialization(srcConn, dstConn);
					}
				}

				if (getEtlConfiguration().hasTestingItem()) {
					getEtlConfiguration().getTestingEtlItemConfiguration().doMinimalTableInitialization(srcConn,
							dstConn);
				}
			} finally {
				finalizeConnection(srcConn, this);
				finalizeConnection(dstConn, this);
			}
		}
	}

	protected synchronized void runInSequencialMode() throws DBException {
		this.changeStatusToRunning();

		List<EtlItemConfiguration> allSync = null;

		if (this.getEtlConfiguration().hasTestingItem()) {
			allSync = utilities().parseToList(this.getEtlConfiguration().getTestingEtlItemConfiguration());

			logInfo("Working on testing item");
		} else {
			allSync = getProcessController().getRelatedEtlConf().getEtlItemConfiguration();
		}

		this.enginesActivititieMonitor = new ArrayList<Engine<T>>();

		logTrace("Running the Process in Sequencial mode!");

		for (EtlItemConfiguration config : allSync) {
			if (config.isDisabled()) {
				logDebug(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Confinguration '"
						+ config.getConfigCode() + "' is disabled! Skipping...").toUpperCase());

				continue;
			} else if (operationTableIsAlreadyFinished(config)) {
				logDebug(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Confinguration '"
						+ config.getConfigCode() + "' was already finished!").toUpperCase());
			} else if (stopRequested()) {
				logWarn("ABORTING THE ENGINE PROCESS DUE STOP REQUESTED!");
				break;
			} else {

				logInfo(("Starting operation '" + getOperationType().name().toLowerCase() + "' On Etl Confinguration '"
						+ config.getConfigCode() + "'").toUpperCase());

				if (!config.isFullLoaded()) {
					try {
						logDebug("Performing the full load of etl item configuration");

						config.fullLoad(this.getOperationConfig());
					} catch (DBException e) {
						e.printStackTrace();

						throw new RuntimeException(e);
					}
				}

				TableOperationProgressInfo progressInfo = null;

				try {
					progressInfo = this.progressInfo.retrieveProgressInfo(config);

					if (progressInfo == null) {

					}

				} catch (NullPointerException e) {
					logErr("Error on thread " + this.getControllerId()
							+ ": Progress meter not found for Etl Confinguration [" + config.getConfigCode() + "].");

					e.printStackTrace();

					throw e;
				}

				Engine<T> engine = Engine.init(this, config, progressInfo);

				logTrace("Opening connection for saving Progress Info");

				OpenConnection conn = openDefaultConn(this);

				try {
					if (isResumable()) {
						logTrace("Saving Progress Info....");

						if (progressInfo != null) {
							progressInfo.save(conn);
						}

						logTrace("Progress Info Saved!");

					} else {
						logTrace("Skiping the saving as the operation  is not resumable");
					}
					conn.markAsSuccessifullyTerminated();
				} catch (DBException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				} finally {
					finalizeConnection(conn, this);
				}

				this.enginesActivititieMonitor.add(engine);

				try {
					engine.run();
				} catch (Exception e) {
					new EtlExceptionImpl("Error occured on etl " + engine.getEngineId(), e);
				}

				if (stopRequested() && engine.isStopped()) {
					logInfo(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Configuration '"
							+ config.getConfigCode() + "' is stopped successifuly!").toUpperCase());
					break;
				} else {
					logInfo(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Configuration '"
							+ config.getConfigCode() + "' is finished!").toUpperCase());

					if (getOperationConfig().isRunOnce()) {
						break;
					}
				}
			}
		}

		if (!stopRequested()) {
			markAsFinished();
		} else {
			changeStatusToStopped();
		}
	}

	protected synchronized void runInParallelMode() throws DBException {

		this.enginesActivititieMonitor = new ArrayList<>();

		logInfo("Starting operations in parallel");

		List<EtlItemConfiguration> avaliableItems = this.determineAvaliableItems();

		this.getOperationConfig().recalculateThreads(avaliableItems);

		for (EtlItemConfiguration config : avaliableItems) {
			if (operationTableIsAlreadyFinished(config)) {
				logDebug(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Configuration '"
						+ config.getConfigCode() + "' was already finished!").toUpperCase());
			} else if (stopRequested()) {
				logWarn("ABORTING THE ENGINE INITIALIZER DUE STOP REQUESTED!");

				break;
			} else {
				logInfo("INITIALIZING '" + getOperationType().name().toLowerCase() + "' ENGINE FOR ETL CONFIGURATION '"
						+ config.getConfigCode().toUpperCase() + "'");

				if (!config.isFullLoaded()) {
					try {
						logDebug("Performing the full load of etl item configuration");

						config.fullLoad(this.getOperationConfig());
					} catch (DBException e) {
						throw new RuntimeException(e);
					}
				}

				TableOperationProgressInfo progressInfo = null;

				try {
					progressInfo = this.progressInfo.retrieveProgressInfo(config);
				} catch (NullPointerException e) {
					logErr("Error on thread " + this.getControllerId()
							+ ": Progress meter not found for Etl Confinguration [" + config.getConfigCode() + "].");

					e.printStackTrace();

					throw e;
				}

				if (this.progressInfo.getItemsProgressInfo() == null) {
					progressInfo = this.progressInfo.retrieveProgressInfo(config);
				}

				Engine<T> engine = Engine.init(this, config, progressInfo);

				OpenConnection conn = openDefaultConn(this);

				try {
					if (isResumable()) {
						logTrace("Saving Progress Info....");

						progressInfo.save(conn);

						logTrace("Progress Info Saved!");

					}
					conn.markAsSuccessifullyTerminated();
				} catch (DBException e) {
					throw new RuntimeException(e);
				} finally {
					conn.finalizeConnection(this);
				}

				startAndAddToEnginesActivititieMonitor(engine);
			}
		}

		changeStatusToRunning();
	}

	private List<EtlItemConfiguration> determineAvaliableItems() {
		List<EtlItemConfiguration> avaliableItems = new ArrayList<>();

		if (this.getEtlConfiguration().hasTestingItem()) {
			avaliableItems = utilities().parseToList(this.getEtlConfiguration().getTestingEtlItemConfiguration());

			logInfo("Working on testing item");
		} else {
			List<EtlItemConfiguration> allSync = getProcessController().getRelatedEtlConf().getEtlItemConfiguration();

			logDebug("Determine finalized operations...");

			for (EtlItemConfiguration config : allSync) {
				if (operationTableIsAlreadyFinished(config)) {
					logDebug(("The operation '" + getOperationType().name().toLowerCase() + "' On Etl Configuration '"
							+ config.getConfigCode() + "' was already finished!").toUpperCase());

					this.addItemToFinalized(config);
				} else {
					avaliableItems.add(config);
				}
			}
		}
		return avaliableItems;
	}

	public boolean operationTableIsAlreadyFinished(EtlItemConfiguration etlConfig) {
		try {
			TableOperationProgressInfo tableOpPm = this.retrieveProgressInfo(etlConfig);

			if (tableOpPm == null) {
				logWarn("No Table Operation Info found for [" + etlConfig.getConfigCode() + "]");
			} else {
				EtlProgressMeter sPm = tableOpPm.getProgressMeter();

				if (sPm == null) {
					logWarn("The progress meter for etl configuration is not exists [" + etlConfig.getConfigCode()
							+ "]");
				} else {
					return sPm.isFinished() && !this.mustRestartInTheEnd();
				}
			}
		} catch (Exception e) {
		}

		return false;
	}

	public boolean operationIsAlreadyFinished() {

		if (getEtlConfiguration().hasEtlItemsConf()) {

			for (EtlItemConfiguration config : getEtlItemConfiguration()) {
				if (!operationTableIsAlreadyFinished(config)) {
					return false;
				}
			}
		}

		return !getEtlConfiguration().hasTestingItem();
	}

	public boolean childOperationsAreAlreadyFinished() {
		if (hasChild()) {
			for (OperationController<? extends EtlDatabaseObject> child : this.getChildren()) {
				if (!child.operationIsAlreadyFinished()) {
					return false;
				}
			}
		}

		return true;

	}

	public String getControllerId() {
		return controllerId;
	}

	@Override
	public String getOperationId() {
		return this.getOperationId();
	}

	public List<Engine<T>> getEnginesActivititieMonitor() {
		return enginesActivititieMonitor;
	}

	@JsonIgnore
	public CommonUtilities utilities() {
		return CommonUtilities.getInstance();
	}

	private void startAndAddToEnginesActivititieMonitor(Engine<T> activitityMonitor) {
		this.getEnginesActivititieMonitor().add(activitityMonitor);

		ThreadPoolService.getInstance().createNewThreadPoolExecutor(activitityMonitor.getEngineId())
				.execute(activitityMonitor);
	}

	@Override
	public String toString() {
		return this.controllerId;
	}

	@Override
	public void run() {
		try {

			this.logDebug("Starting Processs...");

			this.onStart();

			if (this.stopRequested()) {
				this.logWarn("THE OPERATION " + getControllerId() + " COULD NOT BE INITIALIZED DUE STOP REQUESTED!!!!");

				this.changeStatusToStopped();

				if (this.hasChild()) {
					for (OperationController<? extends EtlDatabaseObject> child : this.getChildren()) {
						child.requestStop();
					}
				}
			} else if (this.operationIsAlreadyFinished()) {
				logWarn("THE OPERATION " + getControllerId() + " WAS ALREADY FINISHED!");

				this.changeStatusToFinished();
			} else {
				this.basicInitAllConfElements();

				if (this.isParallelModeProcessing()) {
					this.runInParallelMode();
				} else {
					this.runInSequencialMode();
				}
			}

			boolean running = true;

			while (running) {
				TimeCountDown.sleep(getWaitTimeToCheckStatus());

				if (this.isFinished()) {
					this.markAsFinished();
					this.onFinish();

					running = false;
				} else if (this.isStopped()) {
					running = false;

					this.onStop();
				}

				if (this.getOperationConfig().isParallelModeProcessing()) {

					if (this.getEnginesActivititieMonitor() != null) {

						int qty = 0;

						String msg = "\nRUNNING ITEMS...\n";

						msg += "----------------------------------------\n";

						for (Engine<T> engine : this.getEnginesActivititieMonitor()) {

							if (engine.isRunning()) {
								qty++;

								msg += qty + "." + engine.getEtlConfigCode() + "\n";
							}
						}

						msg += "----------------------------------------";

						logWarn(msg, 60, false);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			this.requestStopDueError(null, e);
		}
	}

	@Override
	public TimeController getTotalTimer() {
		return null;
	}

	@Override
	public TimeController getPauseTimer() {
		return null;
	}

	@Override
	public TimeController getProcessingTimer() {
		return null;
	}

	@Override
	public boolean stopRequested() {
		return this.stopRequested;
	}

	@Override
	public boolean isStopped() {
		if (isNotInitialized())
			return false;

		if (isParallelModeProcessing() && this.getEnginesActivititieMonitor() != null) {
			for (Engine<T> monitor : this.getEnginesActivititieMonitor()) {
				if (!monitor.isStopped()) {
					return false;
				}
			}

			return true;
		}

		return this.operationStatus == EtlOperationStatus.STOPPED;
	}

	@Override
	public boolean isFinished() {
		if (isNotInitialized()) {
			return false;
		}

		if (isParallelModeProcessing() && this.getEnginesActivititieMonitor() != null) {
			for (Engine<T> monitor : this.getEnginesActivititieMonitor()) {
				if (!monitor.isFinished()) {
					return false;
				}
			}

			return true;
		} else {
			return this.operationStatus == EtlOperationStatus.FINISHED;
		}
	}

	public synchronized void markTableOperationAsFinished(EtlItemConfiguration conf) throws DBException {

		logDebug("FINISHING OPERATION ON TABLE " + conf.getConfigCode().toUpperCase());

		TableOperationProgressInfo progressInfo = this.retrieveProgressInfo(conf);

		progressInfo.getProgressMeter().changeStatusToFinished();

		if (isResumable()) {
			OpenConnection conn = openDefaultConn(this);

			try {
				progressInfo.save(conn);
				conn.markAsSuccessifullyTerminated();
			} catch (DBException e) {
				throw new RuntimeException(e);
			} finally {
				conn.finalizeConnection(this);
			}
		}
	}

	public EtlConfiguration getEtlConfiguration() {
		return this.getProcessController().getRelatedEtlConf();
	}

	public List<EtlItemConfiguration> getEtlItemConfiguration() {
		return getEtlConfiguration().getEtlItemConfiguration();
	}

	public File generateTableProcessStatusFile_(AbstractTableConfiguration conf) {
		String operationId = this.getControllerId() + "_" + conf.getTableName();

		String fileName = generateOperationStatusFolder() + FileUtilities.getPathSeparator() + operationId;

		return new File(fileName);
	}

	public File generateOperationStatusFile() {
		return new File(generateOperationStatusFolder() + FileUtilities.getPathSeparator() + getControllerId());
	}

	public String generateOperationStatusFolder() {
		String rootFolder = getProcessController().getProcessInfo().generateProcessStatusFolder();

		String subFolder = "";

		if (operationConfig.getRelatedEtlConfig().isSupposedToRunInOrigin()) {
			subFolder = getOperationType().name().toLowerCase() + FileUtilities.getPathSeparator()
					+ getEtlConfiguration().getOriginAppLocationCode();
		} else if (operationConfig.getRelatedEtlConfig().isSupposedToHaveOriginAppCode()) {
			subFolder = getOperationType().name().toLowerCase() + FileUtilities.getPathSeparator()
					+ getEtlConfiguration().getOriginAppLocationCode();
		} else {
			subFolder = getOperationType().name().toLowerCase();
		}

		return rootFolder + FileUtilities.getPathSeparator() + subFolder;
	}

	public void markAsFinished() {
		logDebug("FINISHING OPERATION " + getControllerId());

		logDebug("WRITING OPERATION STATUS ON FILE [" + generateOperationStatusFile().getAbsolutePath() + "]");

		if (!this.progressInfo.isFinished())
			this.progressInfo.changeStatusToFinished();

		changeStatusToFinished();

		logInfo("OPERATION FINISHED!");
	}

	@Override
	public void onStart() {
		if (!generateOperationStatusFile().exists()) {
			if (this.progressInfo.getStartTime() == null) {
				this.progressInfo.setStartTime(DateAndTimeUtilities.getCurrentDate());
			}

			this.progressInfo.changeStatusToRunning();
		}

		if (this.progressInfo.getStartTime() == null) {
			this.progressInfo.setStartTime(DateAndTimeUtilities.getCurrentDate());
		}

		changeStatusToRunning();
	}

	@Override
	public void onSleep() {
	}

	@Override
	public void onStop() {
		if (lastException == null) {
			logWarn("THE PROCESS " + getControllerId().toUpperCase() + " WAS STOPPED!!!");
		} else {
			logErr("THE PROCESS " + getControllerId().toUpperCase() + " WAS STOPPED DUE ERROR!!!");

			lastException.printStackTrace();
		}

		this.processController.handleControllerFinalization(this);
	}

	@Override
	public void onFinish() {
		logDebug("FINISHING OPERATION " + getControllerId());

		this.processController.handleControllerFinalization(this);
	}

	@Override
	public void killSelfCreatedThreads() {
		if (selfTreadKilled)
			return;

		if (this.enginesActivititieMonitor != null) {
			for (Engine<T> monitor : this.getEnginesActivititieMonitor()) {
				ThreadPoolService.getInstance().terminateTread(logger, monitor.getEngineId(), monitor);
			}
		}

		selfTreadKilled = true;
	}

	@Override
	public int getWaitTimeToCheckStatus() {
		return this.getEtlConfiguration().getWaitTimeToCheckStatus();
	}

	public boolean mustRestartInTheEnd() {
		return this.getOperationConfig().mustRestartInTheEnd();
	}

	@JsonIgnore
	public EtlOperationType getOperationType() {
		return this.operationConfig.getOperationType();
	}

	public abstract TaskProcessor<T> initRelatedTaskProcessor(Engine<T> monitor, IntervalExtremeRecord limits,
			boolean runningInConcurrency);

	public abstract long getMinRecordId(Engine<? extends EtlDatabaseObject> engine);

	public abstract long getMaxRecordId(Engine<? extends EtlDatabaseObject> engine);

	public void refresh() {
	}

	public void requestStopDueError(Engine<T> monitor, Exception e) {
		logErr("Requesting stop due error", e);

		getProcessController().requestStop();
	}

	@Override
	public void requestStop() {
		if (stopRequested()) {
			return;
		}

		synchronized (LOCK) {
			if (stopRequested()) {
				return;
			}

			logTrace("Requesting stop of the operation...");

			if (isNotInitialized()) {
				logDebug("The operation was not initialized! Stopping now!");
				changeStatusToStopped();
				return;
			}

			this.stopRequested = true;

			if (this.getEnginesActivititieMonitor() != null) {
				for (Engine<T> engine : this.getEnginesActivititieMonitor()) {
					engine.requestStop();
				}
			}

			if (getChildren() != null) {
				logWarn("Requesting children to stop...");

				for (OperationController<? extends EtlDatabaseObject> child : getChildren()) {
					child.requestStop();
				}
			}

			boolean atLeastOneEngineIsRunning = false;

			if (this.getEnginesActivititieMonitor() != null) {
				for (Engine<T> engine : this.getEnginesActivititieMonitor()) {

					if (engine.isRunning()) {
						atLeastOneEngineIsRunning = true;
					}
				}
			}

			if (!atLeastOneEngineIsRunning) {
				logDebug("No engine is running! Stopping now as requested!");

				changeStatusToStopped();
			}
		}
	}

	public TableOperationProgressInfo retrieveProgressInfo(EtlItemConfiguration config) {
		if (progressInfo != null && utilities().listHasElement(progressInfo.getItemsProgressInfo())) {
			for (TableOperationProgressInfo item : progressInfo.getItemsProgressInfo()) {
				if (item.getEtlConfiguration().equals(config))
					return item;
			}
		}

		return null;
	}

	@JsonIgnore
	public OpenConnection openDefaultConn(BaseConfiguration opendFrom) {
		return getProcessController().openDefaultConn(opendFrom);
	}

	public OpenConnection openSrcConnection(BaseConfiguration opendFrom) throws DBException {
		return getProcessController().openConnection(opendFrom);
	}

	public OpenConnection tryToOpenDstConn(BaseConfiguration opendFrom) throws DBException {
		return getProcessController().tryToOpenDstConn(opendFrom);
	}

	public void logWarn(String msg) {
		this.processController.logWarn(msg);
	}

	public void logWarn(String msg, Object... arguments) {
		this.processController.logWarn(msg, arguments);
	}

	public void logTrace(String msg) {
		this.processController.logTrace(msg);
	}

	public void logTrace(String msg, Object... arguments) {
		this.processController.logTrace(msg, arguments);
	}

	@Override
	public void logWarn(String msg, long interval, boolean suppressIfAnyRecentLog) {
		this.processController.logWarn(msg, interval, suppressIfAnyRecentLog);
	}

	public void logErr(String msg, Exception e, Object... arguments) {
		this.processController.logErr(msg, e, arguments);
	}

	public void logErr(String msg) {
		this.processController.logErr(msg);
	}

	public void logErr(String msg, Exception e) {
		this.processController.logErr(msg, e);
	}

	public void logInfo(String msg, Object... arguments) {
		this.processController.logInfo(msg);
	}

	public void logInfo(String msg) {
		this.processController.logInfo(msg);
	}

	public void logDebug(String msg) {
		this.processController.logDebug(msg);
	}

	public void logDebug(String msg, Object... arguments) {
		this.processController.logDebug(msg, arguments);
	}

	public boolean isResumable() {
		return !getOperationConfig().isDoNotSaveOperationProgress();
	}

	public abstract boolean canBeRunInMultipleEngines();

	public int getQtyRecordsPerProcessing() {
		return this.getOperationConfig().getProcessingBatch();
	}

	public abstract void afterEtl(List<T> objs, Connection srcConn, Connection dstConn) throws DBException;

	public abstract AbstractEtlSearchParams<T> initMainSearchParams(ThreadRecordIntervalsManager<T> intervalsMgt,
			Engine<T> engine);

	public synchronized void finalize(Engine<T> engine) {
		this.addItemToFinalized(engine.getEtlItemConfiguration());
	}

	private synchronized void addItemToFinalized(EtlItemConfiguration item) {
		if (getFinalizedItems() == null) {
			this.setFinalizedItems(new ArrayList<>());
		}

		getFinalizedItems().add(item);

	}

	@Override
	public boolean isDisabled() {
		return this.getOperationConfig().isDisabled();
	}

}
