package org.openmrs.module.epts.etl.processor;

import java.sql.Connection;
import java.util.List;

import org.openmrs.module.epts.etl.conf.AbstractEtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.datasource.JdbcTablePrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlActionType;
import org.openmrs.module.epts.etl.conf.types.EtlDstType;
import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;
import org.openmrs.module.epts.etl.conf.types.EtlOperationType;
import org.openmrs.module.epts.etl.connector.ConnectorReadRequest;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReaders;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.ConnectorType;
import org.openmrs.module.epts.etl.controller.OperationController;
import org.openmrs.module.epts.etl.databasemodelgeneration.processor.DatabaseModelGenerationProcessor;
import org.openmrs.module.epts.etl.databasepreparation.controller.DatabasePreparationProcessor;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.base.EtlObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.EtlLogger;
import org.openmrs.module.epts.etl.utilities.concurrent.MonitoredOperation;
import org.openmrs.module.epts.etl.utilities.concurrent.TimeController;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Represent a task Processor. A task processor performs the task which will end
 * up producing or consuming the the etl info.
 * <p>
 * There are several kinds of engines that performes diferents kind of
 * operations. All the avaliable operations are listed in
 * {@link EtlOperationType} enum
 * 
 * @author jpboane
 */
public abstract class TaskProcessor<T extends EtlDatabaseObject> extends AbstractEtlDataConfiguration
		implements MonitoredOperation {

	private static final EtlLogger LOG = EtlLogger.getLogger(TaskProcessor.class);

	public static CommonUtilities utilities = CommonUtilities.getInstance();

	protected Engine<T> monitor;

	private String processorId;

	protected IntervalExtremeRecord limits;

	protected boolean runningInConcurrency;

	protected EtlOperationResultHeader<T> taskResultInfo;

	private transient ConnectorRecordReaders recordReaders;

	protected EtlOperationStatus operationStatus;

	public TaskProcessor(Engine<T> monitr, IntervalExtremeRecord limits, boolean runningInConcurrency) {
		this.monitor = monitr;
		this.limits = limits;
		this.runningInConcurrency = runningInConcurrency;
		this.taskResultInfo = new EtlOperationResultHeader<>(limits);
		this.operationStatus = EtlOperationStatus.NOT_INITIALIZED;
	}

	@Override
	public EtlConfiguration getRelatedEtlConf() {
		return monitor.getRelatedEtlConf();
	}

	@Override
	public String getOperationId() {
		return getProcessorId();
	}

	@Override
	public EtlOperationStatus getOperationStatus() {
		return operationStatus;
	}

	@Override
	public void setOperationStatus(EtlOperationStatus operationStatus) {
		this.operationStatus = operationStatus;
	}

	public EtlOperationResultHeader<T> getTaskResultInfo() {
		return taskResultInfo;
	}

	public void setTaskResultInfo(EtlOperationResultHeader<T> taskResultInfo) {
		this.taskResultInfo = taskResultInfo;
	}

	public void resetForTransactionRetry() {
		this.taskResultInfo = new EtlOperationResultHeader<>(limits);
		this.operationStatus = EtlOperationStatus.NOT_INITIALIZED;
	}

	public boolean isRunningInConcurrency() {
		return runningInConcurrency;
	}

	public void setRunningInConcurrency(boolean runningInConcurrency) {
		this.runningInConcurrency = runningInConcurrency;
	}

	public IntervalExtremeRecord getLimits() {
		return limits;
	}

	public Engine<T> getEngine() {
		return monitor;
	}

	public String getProcessorId() {
		return processorId;
	}

	public void setProcessorId(String processorId) {
		this.processorId = processorId;
	}

	public OperationController<T> getRelatedOperationController() {
		return this.monitor.getController();
	}

	public EtlOperationConfig getRelatedEtlOperationConfig() {
		return getRelatedOperationController().getOperationConfig();
	}

	public EtlConfiguration getRelatedEtlConfiguration() {
		return getRelatedOperationController().getEtlConfiguration();
	}

	public EtlItemConfiguration getEtlItemConfiguration() {
		return monitor.getEtlItemConfiguration();
	}

	public String getMainSrcTableName() {
		return getPrimaryDataSource().getObjectName();
	}

	public JdbcTablePrimaryDataSourceConfiguration getJdbcPrimaryDataSource() {
		return monitor.getJdbcPrimaryDataSource();
	}

	public PrimaryDataSourceConfiguration getPrimaryDataSource() {
		return monitor.getPrimaryDataSource();
	}

	public AbstractEtlSearchParams<T> getSearchParams() {
		return getEngine().getSearchParams();
	}

	/** Creates the writer scope appropriate for this processor. */
	public ConnectorRecordWriters createRecordWriters() throws DBException {
		return getEtlItemConfiguration().createDestinationWriters(getEngine());
	}

	/** Creates the reader appropriate for this processor. */
	public ConnectorRecordReader<T> createRecordReader(ConnectorRecordWriters writers) throws DBException {
		return getEtlItemConfiguration().createSourceReader(getSearchParams(), getEngine(), writers);
	}

	/** Creates and attaches the complete reader scope for this processor. */
	public ConnectorRecordReaders createRecordReaders(ConnectorRecordWriters writers) throws DBException {
		ConnectorRecordReaders readers = getEtlItemConfiguration().createSourceReaders(getSearchParams(), getEngine(),
				writers);
		setRecordReaders(readers);
		return readers;
	}

	public ConnectorRecordReaders getRecordReaders() {
		return recordReaders;
	}

	public void setRecordReaders(ConnectorRecordReaders recordReaders) {
		this.recordReaders = recordReaders;
	}

	/** Resolves the reader associated with a source visible to this processor. */
	public ConnectorRecordReader<? extends EtlDatabaseObject> getRecordReader(EtlDataSource dataSource) {

		if (recordReaders == null) {
			throw new IllegalStateException("Connector reader scope has not been initialized for this processor");
		}
		return recordReaders.get(dataSource);
	}

	public void extractTransformAndLoad(boolean useMultiThreadSearch, ConnectorRecordReaders readers,
			ConnectorRecordWriters writers) throws DBException {
		ConnectorRecordReader<T> reader = readers.getPrimary();

		if (this.getRelatedEtlOperationConfig().isDisableMultithreadingSearch()
				|| this.getRelatedEtlOperationConfig().getParallelProcessingStrategy().isRangePartitioning()
				|| this.getRelatedEtlOperationConfig().getProcessingBatch() == 1) {
			useMultiThreadSearch = false;
		}

		String threads = useMultiThreadSearch ? " USING " + utilities.getAvailableProcessors() + " THREADS"
				: " USING SINGLE THREAD";

		if (getLimits() != null) {
			logWarn("SERCHING NEXT RECORDS FOR LIMITS " + getLimits() + threads);
		} else {
			logInfo("SERCHING NEXT RECORDS " + threads);
		}
		List<T> records = null;

		if (this instanceof DatabasePreparationProcessor || this instanceof DatabaseModelGenerationProcessor) {
			records = this.getSearchParams().search(getLimits(), null, null, reader.unwrap(Connection.class), null);
		} else {
			records = reader.read(new ConnectorReadRequest<>(this.getLimits(), null, null, useMultiThreadSearch));
		}

		logDebug("SERCH NEXT MIGRATION RECORDS FOR ETL '" + this.getEtlItemConfiguration().getConfigCode()
				+ "' ON SOURCE '" + getPrimaryDataSource().getObjectName() + "' FINISHED. FOUND: '"
				+ utilities.arraySize(records) + "' RECORDS.");

		if (utilities.listHasElement(records)) {
			transformAndLoadExtractedRecords(records, readers, writers);
		} else {
			logDebug("NO SRC RECORD FOUND FOR ETL!");
		}

	}

	/**
	 * Processes records that were already extracted by the engine. This is used by
	 * result-partitioning strategies, where extraction must happen exactly once.
	 */
	public void transformAndLoadExtractedRecords(List<T> records, ConnectorRecordReaders readers,
			ConnectorRecordWriters writers) throws DBException {

		prepareExtractedRecords(records, writers);

		logWarn("PERFORMING " + getRelatedOperationController().getOperationType().name().toLowerCase() + " OF '"
				+ records.size() + "' RECORDS OF SOURCE '" + this.getPrimaryDataSource().getAlias() + "'");

		if (getActionType().isDelete()) {
			performeDelete(records, writers);
		} else {
			transformAndLoad(records, readers, writers);
		}

		logWarn("TASK {} ON {} RECORDS OF TABLE {} DONE ",
				getRelatedOperationController().getOperationType().name().toLowerCase(), " OF '" + records.size(),
				this.getPrimaryDataSource().getAlias());

	}

	/** Prepares and transforms records already extracted by the engine. */
	@SuppressWarnings("unchecked")
	public void transformExtractedRecords(List<T> records, ConnectorRecordReaders readers,
			ConnectorRecordWriters writers) throws DBException {

		prepareExtractedRecords(records, writers);

		transform((List<EtlDatabaseObject>) (List<?>) records, readers, writers);
	}

	/** Loads records previously transformed by one or more processors. */
	@SuppressWarnings("unchecked")
	public void loadTransformedRecords(List<T> records, ConnectorRecordReaders readers, ConnectorRecordWriters writers)
			throws DBException {

		getTaskResultInfo().setProcessedRecords((List<EtlDatabaseObject>) (List<?>) records);

		load((List<EtlDatabaseObject>) (List<?>) records, readers, writers);
	}

	public void load(List<EtlDatabaseObject> etlObjects, ConnectorRecordReaders readers, ConnectorRecordWriters writers)
			throws DBException {
		throw new ForbiddenOperationException("This processor does not support an independent load phase");
	}

	public void transformAndLoad(List<T> records, ConnectorRecordReaders readers, ConnectorRecordWriters writers)
			throws DBException {
		throw new ForbiddenOperationException("This processor does not support transform and load");
	}

	public void transform(List<EtlDatabaseObject> records, ConnectorRecordReaders readers,
			ConnectorRecordWriters writers) throws DBException {

		throw new ForbiddenOperationException("This processor does not support an independent transform phase");
	}

	private void prepareExtractedRecords(List<T> records, ConnectorRecordWriters writers) throws DBException {

		if (!utilities.listHasElement(records)) {
			return;
		}

		beforeSync(records);

		getTaskResultInfo().addProcessedRecords(records);
	}

	private void performeDelete(List<T> records, ConnectorRecordWriters writers) throws DBException {
		logDebug("Starting the deletion of " + records.size() + " " + getJdbcPrimaryDataSource().getTableName()
				+ " on db...");

		this.loadAndAddResult(TaskProcessorConnectorSupport.delete(this, records, writers));

		logDebug(records.size() + " " + getJdbcPrimaryDataSource().getTableName() + "  deleted on db!");

	}

	void loadAndAddResult(EtlOperationResultHeader<T> result) {

		if (result != null) {
			this.getTaskResultInfo().addAllFromOtherResult(result);
		}
	}

	public EtlActionType getActionType() {
		return getRelatedEtlOperationConfig().getActionType();
	}

	public EtlDstType determineDstType(DstConf dstConf) {
		ConnectorResource resource = dstConf.getResolvedResource();

		if (resource == null) {
			throw new IllegalStateException("Destination resource has not been initialized for " + dstConf);
		}
		ConnectorType connectorType = resource.getEndpoint().getType();

		if (connectorType == ConnectorType.FILE) {
			return EtlDstType.json;
		}

		if (connectorType != ConnectorType.JDBC) {
			throw new ForbiddenOperationException(
					"Connector type '" + connectorType + "' does not use the legacy EtlDstType classification");
		}

		if (!getEtlItemConfiguration().usesLegacyDestinationResource(dstConf)) {
			return EtlDstType.db;
		}

		return determineLegacyDstType(dstConf);
	}

	private EtlDstType determineLegacyDstType(DstConf dstConf) {
		EtlDstType dstType = dstConf.getDstType();

		if (dstType == null) {
			dstType = dstConf.getParentConf().requireJdbcPrimaryDataSource().getDstType();
		}

		if (dstType == null) {
			dstType = getEngine().getGlobalDstType();
		}

		return dstType;
	}

	private void beforeSync(List<T> records) {
		if (!(getPrimaryDataSource() instanceof TableConfiguration))
			return;

		for (EtlObject rec : records) {

			if (rec instanceof EtlDatabaseObject) {
				((EtlDatabaseObject) rec).loadObjectIdData((TableConfiguration) getPrimaryDataSource());
			}
		}
	}

	@Override
	public String toString() {
		return getProcessorId() + " Limits [" + getSearchParams().getThreadRecordIntervalsManager() + "]";
	}

	public void logError(String msg, Exception e) {
		LOG.err(msg, e);
	}

	public void logError(String msg, Exception e, Object... arguments) {
		LOG.err(msg, e, arguments);
	}

	public void logError(String msg, Throwable throwable) {
		LOG.err(msg, throwable);
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

	public void logTrace(String msg) {
		LOG.trace(msg);
	}

	public void logTrace(String msg, Object... arguments) {
		LOG.trace(msg, arguments);
	}

	public void logWarn(String msg) {
		LOG.warn(msg);
	}

	public void logWarn(String msg, Object... arguments) {
		LOG.warn(msg, arguments);
	}

	@Override
	public void logWarn(String msg, long interval, boolean suppressIfAnyRecentLog) {
		LOG.warn(msg, interval, suppressIfAnyRecentLog);
	}

	public Connection findPrimaryJdbcConnection(ConnectorRecordReaders readers) {
		return findPrimaryJdbcConnection(getEtlItemConfiguration(), readers);
	}

	public Connection findPrimaryJdbcConnection(EtlItemConfiguration itemConf, ConnectorRecordReaders readers) {
		return this.findJdbcConnection(findPrimaryReader(itemConf, readers));
	}

	public Connection findJdbcConnection(ConnectorRecordReader<?> reader) {

		try {
			return reader.unwrap(Connection.class);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public ConnectorRecordReader<?> findPrimaryReader(EtlItemConfiguration itemConf, ConnectorRecordReaders readers) {

		try {
			return readers.get(itemConf.getPrimaryDataSource());
		} catch (IllegalArgumentException e) {

			if (itemConf == getEtlItemConfiguration()) {
				return readers.getPrimary();
			}
			throw e;
		}
	}

	public Connection requirePrimaryJdbcConnection(ConnectorRecordReaders readers, String operation) {
		Connection connection = findPrimaryJdbcConnection(readers);

		if (connection == null) {
			throw new IllegalStateException("A JDBC source connection is required for " + operation);
		}

		return connection;
	}

	public boolean writeOperationHistory() {
		return getRelatedEtlOperationConfig().writeOperationHistory();
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {

		if (obj == null || !(obj instanceof TaskProcessor)) {
			return false;
		}

		TaskProcessor<T> e = (TaskProcessor<T>) obj;

		return this.getProcessorId().equals(e.getProcessorId());
	}

	@Override
	public EtlDataConfiguration getParentConf() {
		return null;
	}

	@Override
	public void tryToReplacePlaceholders(EtlDatabaseObject schemaInfoSrc) {
	}

	@Override
	public TimeController getTotalTimer() {
		throw new ForbiddenOperationException("Unimplemented method!");
	}

	@Override
	public TimeController getPauseTimer() {
		throw new ForbiddenOperationException("Unimplemented method!");
	}

	@Override
	public TimeController getProcessingTimer() {
		throw new ForbiddenOperationException("Unimplemented method!");
	}

	@Override
	public void requestStop() {
		throw new ForbiddenOperationException("Unimplemented method!");
	}

	@Override
	public boolean stopRequested() {
		throw new ForbiddenOperationException("Unimplemented method!");
	}

	@Override
	public int getWaitTimeToCheckStatus() {
		throw new ForbiddenOperationException("Unimplemented method!");
	}

	@Override
	public void run() {
		throw new ForbiddenOperationException("Unimplemented method!");
	}

	public abstract TaskProcessor<T> initReloadRecordsWithDefaultParentsTaskProcessor(IntervalExtremeRecord limits);

}
