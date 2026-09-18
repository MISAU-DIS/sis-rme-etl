
package org.openmrs.module.epts.etl.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.openmrs.module.epts.etl.conf.AbstractBaseConfiguration;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.OperationProgressInfo;
import org.openmrs.module.epts.etl.model.ProcessProgressInfo;
import org.openmrs.module.epts.etl.monitor.EtlMonitor;
import org.openmrs.module.epts.etl.monitor.registry.EtlMonitorRegistry;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.ThreadPoolService;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeController;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeCountDown;
import org.openmrs.module.epts.etl.utilities.db.DBUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.OpenConnection;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.openmrs.module.epts.etl.engine.EtlProgressMeter;
import org.openmrs.module.epts.etl.monitor.model.EtlMonitorSnapshot;

/**
 * The controller of the whole synchronization process.
 *
 * <p>
 * This class uses {@link OperationController} to execute the individual
 * synchronization operations that compose an ETL process.
 * </p>
 *
 * <p>
 * A {@link ProcessController} represents one concrete execution of an ETL
 * configuration. Each process controller owns one {@link EtlMonitor}, which
 * exposes the runtime state of that execution to the monitoring layer.
 * </p>
 *
 * <p>
 * The monitor does not execute or control the ETL. The ProcessController
 * remains the source of truth for the execution itself.
 * </p>
 *
 * @author jpboane
 */
public class ProcessController extends AbstractBaseConfiguration
		implements Controller, ControllerStarter {

	private static final EtlLogger LOG =
			EtlLogger.getLogger(ProcessController.class);

	private EtlConfiguration relatedEtlConf;

	private EtlOperationStatus operationStatus;

	private List<OperationController<? extends EtlDatabaseObject>> operationsControllers;

	private String controllerId;

	private ProcessProgressInfo progressInfo;

	private static CommonUtilities utilities =
			CommonUtilities.getInstance();

	private boolean progressInfoLoaded;

	private ProcessStarter starter;

	private boolean finalized;

	protected boolean selfTreadKilled;

	private ProcessInfo processInfo;

	private EtlDatabaseObject schemaInfoSrc;

	private static final Object LOCK = new Object();

	private boolean stopRequested;

	/**
	 * Monitor associated with this concrete ETL execution.
	 *
	 * <p>
	 * The monitor is only a representation of runtime state. It does not
	 * execute, stop or otherwise control the ETL process.
	 * </p>
	 */
	private EtlMonitor monitor;

	public ProcessController() {
		this.progressInfo = new ProcessProgressInfo(this);
	}

	public ProcessController(
			ProcessStarter starter,
			EtlConfiguration configuration) throws DBException {

		this();

		this.starter = starter;

		init(configuration);
	}

	/**
	 * Resolve o status agregado do processo a partir dos status dos
	 * engines reais.
	 *
	 * A prioridade é dada aos estados que representam uma execução ainda
	 * ativa ou uma interrupção/erro.
	 */
	private EtlOperationStatus resolveMonitorStatus(
			EtlOperationStatus currentStatus,
			EtlOperationStatus engineStatus) {

		if (engineStatus == null) {
			return currentStatus;
		}

		/*
		 * Se algum engine ainda está executando, o processo está RUNNING.
		 */
		if (engineStatus.running()) {
			return EtlOperationStatus.RUNNING;
		}

		/*
		 * STOPPING tem prioridade sobre estados passivos.
		 */
		if (engineStatus == EtlOperationStatus.STOPPING) {
			return EtlOperationStatus.STOPPING;
		}

		/*
		 * Se houve erro/paragem, preservamos esse estado.
		 */
		if (engineStatus == EtlOperationStatus.STOPPED_DUE_ERROR) {
			return EtlOperationStatus.STOPPED_DUE_ERROR;
		}

		/*
		 * Se já existe uma execução em andamento, não devemos sobrescrevê-la
		 * com FINISHED/STOPPED de outro engine que terminou antes.
		 */
		if (currentStatus != null && currentStatus.running()) {
			return currentStatus;
		}

		return engineStatus;
	}

	/**
	 * Cria uma fotografia do estado atual do processo ETL.
	 *
	 * O método não calcula o progresso diretamente a partir de operações
	 * artificiais. Ele lê os EtlProgressMeter dos Engine reais que estão
	 * executando a operação.
	 *
	 * Quando existem vários engines/operações em execução, os valores são
	 * agregados:
	 *
	 *     total     = soma dos totais dos engines
	 *     processed = soma dos processados
	 *     remaining = total - processed
	 *     progress  = processed / total * 100
	 *
	 * Os tempos e datas são obtidos dos próprios progress meters.
	 *
	 * @return snapshot atual do processo ETL
	 */
	public EtlMonitorSnapshot createMonitorSnapshot() {

		EtlMonitorSnapshot snapshot = new EtlMonitorSnapshot();

		snapshot.setOperationId(this.getControllerId());

		int total = 0;
		int processed = 0;

		Date startTime = null;
		Date finishTime = null;

		EtlOperationStatus status = this.getOperationStatus();

		/*
		 * Percorre todas as operações controladas pelo processo.
		 */
		if (this.getOperationsControllers() != null) {

			for (OperationController<? extends EtlDatabaseObject> operation
					: this.getOperationsControllers()) {

				if (operation == null) {
					continue;
				}

				/*
				 * Cada OperationController pode possuir vários engines,
				 * principalmente quando a execução é paralela.
				 */
				List<? extends Engine<? extends EtlDatabaseObject>> engines =
						operation.getEnginesActivititieMonitor();

				if (engines == null) {
					continue;
				}

				for (Engine<? extends EtlDatabaseObject> engine : engines) {

					if (engine == null) {
						continue;
					}

					EtlProgressMeter meter = engine.getProgressMeter();

					if (meter == null) {
						continue;
					}

					/*
					 * O EtlProgressMeter é a fonte real dos dados de progresso.
					 */
					total += meter.getTotal();
					processed += meter.getProcessed();

					/*
					 * Usa a data mais antiga encontrada como início
					 * da execução.
					 */
					Date engineStartTime = meter.getStartTime();

					if (engineStartTime != null &&
							(startTime == null ||
									engineStartTime.before(startTime))) {

						startTime = engineStartTime;
					}

					/*
					 * Usa a data mais recente encontrada como término.
					 */
					Date engineFinishTime = meter.getFinishTime();

					if (engineFinishTime != null &&
							(finishTime == null ||
									engineFinishTime.after(finishTime))) {

						finishTime = engineFinishTime;
					}

					/*
					 * Se algum engine ainda estiver executando, o processo
					 * não deve ser apresentado como terminado.
					 */
					if (meter.getStatus() != null) {
						status = resolveMonitorStatus(status, meter.getStatus());
					}
				}
			}
		}

		/*
		 * Evita que valores inconsistentes de algum engine produzam
		 * processed > total.
		 */
		if (processed > total) {
			processed = total;
		}

		int remaining = total - processed;

		double progress = 0;

		if (total > 0) {
			progress = ((double) processed / (double) total) * 100.0;
		}

		/*
		 * Preenche o snapshot.
		 */
		snapshot.setStatus(status);
		snapshot.setTotal(total);
		snapshot.setProcessed(processed);
		snapshot.setRemaining(remaining);
		snapshot.setProgress(progress);
		snapshot.setStartTime(startTime);
		snapshot.setFinishTime(finishTime);

		return snapshot;
	}

	public boolean isStopRequested() {
		return stopRequested;
	}

	public void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}

	@Override
	public EtlOperationStatus getOperationStatus() {
		return this.operationStatus;
	}

	@Override
	public void setOperationStatus(EtlOperationStatus status) {
		this.operationStatus = status;
	}

	public void setSchemaInfoSrc(EtlDatabaseObject schemaInfoSrc) {
		this.schemaInfoSrc = schemaInfoSrc;
	}

	@JsonIgnore
	public EtlDatabaseObject getSchemaInfoSrc() {
		return schemaInfoSrc;
	}

	@JsonIgnore
	public List<OperationController<? extends EtlDatabaseObject>>
	getOperationsControllers() {

		return operationsControllers;
	}

	public ProcessProgressInfo getProgressInfo() {
		return progressInfo;
	}

	public ProcessInfo getProcessInfo() {
		return processInfo;
	}

	/**
	 * Returns the monitor associated with this ETL execution.
	 *
	 * @return monitor for the current process
	 */
	@JsonIgnore
	public EtlMonitor getMonitor() {

		if (monitor != null) {
			monitor.updateSnapshot(createMonitorSnapshot());
		}

		return monitor;
	}

	public OperationProgressInfo initOperationProgressMeter(
			OperationController<? extends EtlDatabaseObject> operationController,
			Connection conn) throws DBException {

		return this.progressInfo
				.initAndAddProgressMeterToList(operationController, conn);
	}

	public void init(File syncCongigurationFile) throws DBException {
		try {
			init(EtlConfiguration.loadFromFile(syncCongigurationFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initializes this ETL process controller.
	 *
	 * <p>
	 * The process identifier is generated first because the identifier is
	 * also used as the unique identifier of the runtime monitor.
	 * </p>
	 */
	public void init(EtlConfiguration configuration) throws DBException {

		this.relatedEtlConf = configuration;

		this.relatedEtlConf.setRelatedController(this);

		this.processInfo = new ProcessInfo(getRelatedEtlConf());

		/*
		 * Generate the identifier of this concrete ETL execution.
		 */
		this.controllerId = configuration.generateProcessId();

		/*
		 * Create the monitor for this execution.
		 *
		 * The monitor is associated with the ProcessController itself,
		 * rather than with a separate execution context.
		 */
		this.monitor = new EtlMonitor(this.controllerId);

		/*
		 * Register the monitor so that the monitoring layer can find
		 * the execution by its process identifier.
		 */
		EtlMonitorRegistry.getInstance().register(this.monitor);

		this.operationStatus = EtlOperationStatus.NOT_INITIALIZED;

		this.operationsControllers = new ArrayList<>();

		OpenConnection conn = openConnection(this);

		try {

			for (EtlOperationConfig operation : configuration.getOperations()) {

				List<OperationController<? extends EtlDatabaseObject>>
						controller =
						operation.generateRelatedController(
								this,
								operation.getRelatedEtlConf()
										.getOriginAppLocationCode(),
								conn);

				this.operationsControllers.addAll(controller);
			}

			this.progressInfoLoaded = true;

			conn.markAsSuccessifullyTerminated();

		} finally {
			conn.finalizeConnection(this);
		}
	}

	public void setFinalized(boolean finalized) {
		this.finalized = finalized;
	}

	public boolean isFinalized() {
		return finalized;
	}

	public void handleFinalization() {
		setFinalized(true);

		getRelatedEtlConf().finalizeAllApps();
	}

	@Override
	public void handleControllerFinalization(Controller c) {

		c.killSelfCreatedThreads();

		List<OperationController<? extends EtlDatabaseObject>>
				nextOperation =
				((OperationController<? extends EtlDatabaseObject>) c)
						.getChildren();

		logDebug("TRY TO INIT NEXT OPERATION");

		/*
		 * Remember, if one of multiple child is disabled, then all other
		 * children are disabled.
		 */
		while (nextOperation != null
				&& !nextOperation.isEmpty()
				&& nextOperation.get(0)
				.getOperationConfig()
				.isDisabled()) {

			nextOperation = nextOperation.get(0).getChildren();
		}

		if (nextOperation != null) {

			if (!stopRequested()) {

				for (OperationController<? extends EtlDatabaseObject>
						controller : nextOperation) {

					logDebug(
							"STARTING NEXT OPERATION "
									+ controller.getControllerId());

					ExecutorService executor =
							ThreadPoolService.getInstance()
									.createNewThreadPoolExecutor(
											controller.getControllerId());

					executor.execute(controller);
				}

			} else {

				String nextOperations = "[";

				for (OperationController<? extends EtlDatabaseObject>
						controller : nextOperation) {

					nextOperations +=
							controller.getControllerId() + ";";
				}

				nextOperations += "]";

				logWarn(
						"THE OPERATION "
								+ nextOperations.toUpperCase()
								+ "NESTED COULD NOT BE INITIALIZED "
								+ "BECAUSE THERE WAS A STOP REQUEST!!!");
			}

		} else {

			logWarn(
					"THERE IS NO MORE OPERATION TO EXECUTE..."
							+ " FINALIZING PROCESS... "
							+ this.getControllerId());
		}

		getRelatedEtlConf().finalizeAllApps();
	}

	public OpenConnection openDefaultConn(BaseConfiguration opendFrom) {

		try {
			return getRelatedEtlConf()
					.getSrcConnInfo()
					.openConnection(opendFrom);

		} catch (DBException e) {
			throw new EtlExceptionImpl(e);
		}
	}

	@JsonIgnore
	public DBConnectionInfo getDstConnInfo() {
		return getRelatedEtlConf().getDstConnInfo();
	}

	@Override
	@JsonIgnore
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
		return this.isStopRequested()
				|| generateStopRequestFile().exists();
	}

	@Override
	public boolean isDisabled() {
		return this.getRelatedEtlConf().isDisabled();
	}

	public File generateStopRequestFile() {
		return new File(
				getRelatedEtlConf().getEtlRootDirectory()
						+ "/process_status/stop_requested.info");
	}

	@Override
	public boolean isStopped() {

		if (isNotInitialized())
			return false;

		if (utilities.listHasElement(this.operationsControllers)) {

			for (OperationController<? extends EtlDatabaseObject>
					controller : this.operationsControllers) {

				if (controller.getOperationConfig().isDisabled()) {
					continue;

				} else if (!controller.isStopped()
						&& !controller.isFinished()) {

					return false;

				} else {

					List<OperationController<? extends EtlDatabaseObject>>
							children = controller.getChildren();

					while (children != null) {

						List<OperationController<? extends EtlDatabaseObject>>
								grandChildren = null;

						for (OperationController<? extends EtlDatabaseObject>
								child : children) {

							if (!child.isStopped()
									&& !child.isFinished()) {

								return false;
							}

							if (child.getChildren() != null) {

								if (grandChildren == null)
									grandChildren = new ArrayList<>();

								for (OperationController<? extends EtlDatabaseObject>
										childOfChild :
										child.getChildren()) {

									grandChildren.add(childOfChild);
								}
							}
						}

						children = grandChildren;
					}
				}
			}

			return true;
		}

		return this.operationStatus == EtlOperationStatus.STOPPED;
	}

	@Override
	public boolean isFinished() {

		if (Controller.super.isStopped()) {
			return true;
		}

		if (Controller.super.isFinished()) {
			return true;
		}

		if (utilities.listHasElement(this.operationsControllers)) {

			for (OperationController<? extends EtlDatabaseObject>
					controller : this.operationsControllers) {

				if (controller.getOperationConfig().isDisabled()) {
					continue;

				} else if (!controller.isFinished()) {

					return false;

				} else {

					List<OperationController<? extends EtlDatabaseObject>>
							children = controller.getChildren();

					while (children != null) {

						List<OperationController<? extends EtlDatabaseObject>>
								grandChildren = null;

						for (OperationController<? extends EtlDatabaseObject>
								child : children) {

							if (!child.isFinished()
									&& !child.getOperationConfig()
									.isDisabled()) {

								return false;
							}

							if (child.getChildren() != null) {

								if (grandChildren == null)
									grandChildren = new ArrayList<>();

								for (OperationController<? extends EtlDatabaseObject>
										childOfChild :
										child.getChildren()) {

									grandChildren.add(childOfChild);
								}
							}
						}

						children = grandChildren;
					}
				}
			}

			return true;
		}

		return this.operationStatus == EtlOperationStatus.FINISHED;
	}

	@Override
	public void requestStop() {

		if (isStopping()) {
			logWarn("Stop Already requested!!!");
			return;
		}

		logWarn("Requesting Stop");

		synchronized (LOCK) {

			if (isStopping()) {
				return;
			}

			changeStatusToStopping();

			setStopRequested(true);

			/*
			 * Stop request file was previously written here.
			 * The current implementation uses the in-memory flag
			 * and the existing stop-request file mechanism.
			 */

			if (isNotInitialized()) {

				logWarn(
						"Process not initialized, the stopping now!");

				changeStatusToStopped();

			} else if (utilities.listHasElement(
					this.operationsControllers)) {

				logWarn(
						"Requesting stop of Operation Controllers...");

				for (OperationController<? extends EtlDatabaseObject>
						controller : this.operationsControllers) {

					if (!controller.stopRequested()) {
						controller.requestStop();
					}
				}
			}
		}
	}

	@Override
	public void run() {

		tryToRemoveOldStopRequested();

		if (stopRequested()) {

			logWarn(
					"THE PROCESS COULD NOT BE INITIALIZED "
							+ "DUE STOP REQUESTED!!!!");

			changeStatusToStopped();

			return;
		}

		boolean wasPreviouslyFinished =
				processIsAlreadyFinished();

		if (wasPreviouslyFinished
				&& (!canBeReRun()
				|| !reRunConditionsAreSatisfied())) {

			logWarn(
					"THE PROCESS "
							+ getControllerId().toUpperCase()
							+ " WAS ALREADY FINISHED!!!");

			onFinish();

		} else {

			OpenConnection conn = null;

			try {

				if (wasPreviouslyFinished) {
					performePreReRunActions();
				}

				conn = openDefaultConn(this);

				initOperationsControllers(conn);

				conn.markAsSuccessifullyTerminated();

			} catch (DBException e) {

				throw new RuntimeException(e);

			} finally {

				if (conn != null) {
					conn.finalizeConnection(this);
				}
			}

			changeStatusToRunning();

			boolean running = true;

			while (running) {

				TimeCountDown.sleep(
						getWaitTimeToCheckStatus());

				LOG.warn(
						("The process "
								+ getControllerId()
								+ " is still running...")
								.toUpperCase(),
						60 * 5,
						true);

				if (this.isFinished()) {

					this.markAsFinished();

					this.onFinish();

					running = false;

				} else if (this.isStopped()) {

					running = false;

					this.onStop();

				} else if (stopRequested()
						&& !isStopping()) {

					requestStop();
				}
			}
		}
	}

	private void performePreReRunActions()
			throws DBException {

		FileUtilities.removeFile(
				this.getProcessInfo()
						.generateProcessStatusFile());

		FileUtilities.removeFile(
				this.getProcessInfo()
						.generateProcessStatusFolder());

		OpenConnection conn = openConnection(this);

		try {

			this.progressInfo =
					new ProcessProgressInfo(this);

			for (OperationController<? extends EtlDatabaseObject>
					controller : this.operationsControllers) {

				controller.resetProgressInfo(conn);
			}

			FileUtilities.removeFile(
					this.getProcessInfo()
							.generateProcessStatusFile());

			FileUtilities.removeFile(
					this.getProcessInfo()
							.generateProcessStatusFolder());

			conn.markAsSuccessifullyTerminated();

		} catch (DBException e) {

			throw new RuntimeException(e);

		} finally {

			conn.finalizeConnection(this);
		}
	}

	/**
	 * Check if the conditions for this process to be re-run
	 * are satisfied.
	 *
	 * @return true if the re-run conditions are satisfied
	 */
	public boolean reRunConditionsAreSatisfied() {

		if (!canBeReRun())
			return false;

		ProcessInfo processInfoOnDB =
				this.processInfo.tryToLoadFromFile();

		return !this.processInfo.equals(
				processInfoOnDB);
	}

	private boolean canBeReRun() {
		return getRelatedEtlConf().reRunable();
	}

	public boolean isDBReSyncProcess() {
		return getRelatedEtlConf().isDBReSyncProcess();
	}

	public boolean isDBQuickExportProcess() {
		return getRelatedEtlConf().isDBQuickExportProcess();
	}

	public boolean isDBQuickLoadProcess() {
		return getRelatedEtlConf().isDBQuickLoadProcess();
	}

	private void tryToRemoveOldStopRequested() {

		File file = generateStopRequestFile();

		if (file.exists())
			file.delete();
	}

	public void initOperationsControllers(Connection conn)
			throws DBException {

		for (OperationController<? extends EtlDatabaseObject>
				controller : this.operationsControllers) {

			this.tryToInitController(controller);
		}
	}

	private void tryToInitController(
			OperationController<? extends EtlDatabaseObject> controller) {

		if (!controller.getOperationConfig().isDisabled()) {

			ExecutorService executor =
					ThreadPoolService.getInstance()
							.createNewThreadPoolExecutor(
									controller.getControllerId());

			executor.execute(controller);

		} else if (controller.hasChild()) {

			for (OperationController<? extends EtlDatabaseObject>
					child : controller.getChildren()) {

				this.tryToInitController(child);
			}
		}
	}

	@Override
	public void onStart() {
		logInfo("STARTING PROCESS");
	}

	@Override
	public void onSleep() {
	}

	@Override
	public void onStop() {

		logWarn(
				"THE PROCESS "
						+ getControllerId().toUpperCase()
						+ " WAS STOPPED!!!");

		FileUtilities.removeFile(
				generateStopRequestFile().getAbsolutePath());

		this.starter.handleControllerFinalization(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onFinish() {

		markAsFinished();

		if (getRelatedEtlConf().hasFinalizer()) {

			Class[] parameterTypes = {
					ProcessController.class
			};

			try {

				Constructor<? extends ProcessFinalizer> a =
						getRelatedEtlConf()
								.getFinalizer()
								.getFinalizerClazz()
								.getConstructor(parameterTypes);

				ProcessFinalizer finalizer =
						a.newInstance(this);

				finalizer.performeFinalizationTasks();

			} catch (Exception e) {

				throw new ForbiddenOperationException(e);
			}
		}

		starter.handleControllerFinalization(this);
	}

	@Override
	public void killSelfCreatedThreads() {

		if (selfTreadKilled)
			return;

		if (this.operationsControllers != null) {

			for (OperationController<? extends EtlDatabaseObject>
					operationController :
					this.operationsControllers) {

				operationController.killSelfCreatedThreads();

				ThreadPoolService.getInstance()
						.terminateTread(
								LOG,
								operationController.getControllerId(),
								operationController);
			}
		}

		selfTreadKilled = true;
	}

	@Override
	public void markAsFinished() {

		logDebug("FINISHING PROCESS...");

		if (!this.processInfo
				.generateProcessStatusFile()
				.exists()) {

			logDebug(
					"FINISHING PROCESS... WRITING PROCESS STATUS ON FILE ["
							+ this.processInfo
							.generateProcessStatusFile()
							.getAbsolutePath()
							+ "]");

			this.processInfo.save();

			logDebug("FILE WROTE");
		}

		changeStatusToFinished();

		logInfo("THE PROCESS IS FINISHED...");
	}

	@Override
	@JsonIgnore
	public String toString() {
		return this.controllerId;
	}

	@JsonIgnore
	public boolean processIsAlreadyFinished() {

		for (OperationController<? extends EtlDatabaseObject>
				controller : this.operationsControllers) {

			if (!controller.operationIsAlreadyFinished()
					|| !controller.childOperationsAreAlreadyFinished()) {

				return false;
			}
		}

		return true;
	}

	@Override
	public int getWaitTimeToCheckStatus() {
		return this.getRelatedEtlConf()
				.getWaitTimeToCheckStatus();
	}

	@JsonIgnore
	public String getControllerId() {
		return this.controllerId;
	}

	@Override
	public String getOperationId() {
		return this.getControllerId();
	}

	public void logDebug(String msg) {
		LOG.debug(msg);
	}

	public void logDebug(
			String msg,
			Object... argments) {

		LOG.debug(msg, argments);
	}

	public void logInfo(String msg) {
		LOG.info(msg);
	}

	public void logInfo(
			String msg,
			Object... argments) {

		LOG.info(msg, argments);
	}

	public void logWarn(String msg) {
		LOG.warn(msg);
	}

	public void logTrace(String msg) {
		LOG.trace(msg);
	}

	public void logTrace(
			String msg,
			Object... argments) {

		LOG.trace(msg, argments);
	}

	public void logWarn(
			String msg,
			long interval,
			boolean suppressIfAnyRecentLog) {

		LOG.warn(
				msg,
				interval,
				suppressIfAnyRecentLog);
	}

	public void logWarn(
			String msg,
			Object... argments) {

		LOG.warn(msg, argments);
	}

	public void logErr(
			String msg,
			Exception e) {

		LOG.err(msg, e);
	}

	public void logErr(
			String msg,
			Exception e,
			Object... arguments) {

		LOG.err(msg, e, arguments);
	}

	public boolean isProgressInfoLoaded() {
		return progressInfoLoaded;
	}

	public static <T extends EtlDatabaseObject>
	ProcessController retrieveRunningThread(
			EtlConfiguration configuration) {

		String controllerId =
				configuration.generateProcessId();

		for (Thread t : Thread.getAllStackTraces().keySet()) {

			if (t.getName().equals(controllerId)) {
				t.getState();
				t.getThreadGroup();
				t.isAlive();
			}
		}

		return null;
	}

	public OpenConnection openConnection(
			BaseConfiguration opendFrom)
			throws DBException {

		OpenConnection conn =
				openDefaultConn(opendFrom);

		if (getRelatedEtlConf()
				.doNotResolveRelationship()) {

			DBUtilities.disableForegnKeyChecks(conn);
		}

		return conn;
	}

	public OpenConnection tryToOpenMainConnection(
			BaseConfiguration opendFrom)
			throws DBException {

		OpenConnection conn =
				getRelatedEtlConf()
						.openMainConn(opendFrom);

		if (getRelatedEtlConf()
				.doNotResolveRelationship()) {

			DBUtilities.disableForegnKeyChecks(conn);
		}

		return conn;
	}

	public OpenConnection tryToOpenDstConn(
			BaseConfiguration opendFrom)
			throws DBException {

		OpenConnection conn = null;

		if (getRelatedEtlConf()
				.hasDstConnInfo()) {

			conn = getDstConnInfo()
					.openConnection(opendFrom);

			if (getRelatedEtlConf()
					.doNotResolveRelationship()) {

				DBUtilities.disableForegnKeyChecks(conn);
			}
		}

		return conn;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return this.relatedEtlConf;
	}

	@Override
	public void logErr(
			String msg,
			Throwable throwable) {

		LOG.err(msg, throwable);
	}
}