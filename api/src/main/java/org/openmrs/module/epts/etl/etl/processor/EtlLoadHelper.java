package org.openmrs.module.epts.etl.etl.processor;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlOperationConfig;
import org.openmrs.module.epts.etl.conf.JdbcTableDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;
import org.openmrs.module.epts.etl.conf.types.EtlActionType;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.reader.ConnectorRecordReadersAggregator;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorRecordWritersAggregator;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorWriteMode;
import org.openmrs.module.epts.etl.connector.capability.writer.ConnectorWriteRequest;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.etl.controller.EtlController;
import org.openmrs.module.epts.etl.etl.model.EtlStatus;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.etl.processor.transformer.TransformationType;
import org.openmrs.module.epts.etl.exceptions.EtlException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.MissingParentException;
import org.openmrs.module.epts.etl.exceptions.NoDstForGivenSrcException;
import org.openmrs.module.epts.etl.exceptions.ParentNotYetMigratedException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationItemResult;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlOperationResultHeader;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;
import org.openmrs.module.epts.etl.utilities.db.conn.InconsistentStateException;
import org.openmrs.module.epts.etl.utilities.io.FileUtilities;

public class EtlLoadHelper {

	protected static CommonUtilities utilities = CommonUtilities.getInstance();

	private List<EtlDatabaseObject> srcObjects;

	private EtlProcessor processor;

	private LoadingType loadingType;
	private EtlItemConfiguration etlItemConf;

	private List<EtlDestinationConfiguration> dstConf;

	public EtlLoadHelper(EtlProcessor processor, EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> srcObjects,
			LoadingType loadingType, boolean ignoreNoDstIssue) {
		this.processor = processor;
		this.etlItemConf = etlItemConf;
		this.srcObjects = srcObjects;
		this.loadingType = loadingType;

		this.dstConf = new ArrayList<>();

		for (EtlDatabaseObject obj : srcObjects) {

			if (obj.hasDestinationRecords()) {

				for (EtlDatabaseObject dst : obj.getDestinationObjects()) {

					if (!this.dstConf.contains((EtlDestinationConfiguration) dst.getRelatedConfiguration())) {
						this.dstConf.add((EtlDestinationConfiguration) dst.getRelatedConfiguration());
					}
				}
			}
		}

		if (utilities.listHasNoElement(this.dstConf) && !ignoreNoDstIssue) {
			throw new NoDstForGivenSrcException(etlItemConf, srcObjects);
		}
	}

	public EtlItemConfiguration getEtlItemConf() {
		return etlItemConf;
	}

	public List<EtlDestinationConfiguration> getDstConf() {
		return dstConf;
	}

	public EtlProcessor getProcessor() {
		return this.processor;
	}

	public Engine<? extends EtlDatabaseObject> getEngine() {
		return getProcessor().getEngine();
	}

	public EtlController getController() {
		return (EtlController) getEngine().getRelatedOperationController();
	}

	public EtlOperationConfig getEtlOperationConfig() {
		return getController().getOperationConfig();
	}

	public boolean isPrincipalLoading() {
		return this.loadingType.isPrincipal();
	}

	public List<EtlDatabaseObject> getSrcObjects() {
		return srcObjects;
	}

	public void load(ConnectorRecordReadersAggregator readers, ConnectorRecordWritersAggregator writers)
			throws ParentNotYetMigratedException, DBException {

		if (this.hasDstConf()) {

			for (EtlDestinationConfiguration dst : this.getDstConf()) {

				try {
					load(dst, readers, writers);
				} catch (Exception e) {
					logWarn("Error happened while perfoming load within etl {} with dstConf {}", dst.getParentConf(),
							dst);

					throw e;
				}

				if (!dst.getRelatedEtlConf().getGeneralBehaviourOnEtlException().log()) {
					EtlDatabaseObject err = this.retrieveFirstDstRecordWithUnresolvedError(dst);

					if (err != null) {
						logError("Found issues loading object {} {}",
								(EtlExceptionImpl) err.getEtlDefaultEtlException(), err,
								((EtlExceptionImpl) err.getEtlDefaultEtlException()).getLocalizedMessage());

						logWarn("Aborting operation");

						return;
					}
				}
			}
		}

		for (EtlDatabaseObject obj : getAllTransformedObjects()) {
			EtlInfo info = obj.getEtlInfo();

			if (info.hasExceptionOnEtl()) {
				info.markAsFailed();
			} else {
				info.markAsSuccess();
			}
		}

		boolean writeOperation = getEtlOperationConfig().writeOperationHistory();

		if (writeOperation) {
			logInfo("Registering StageArea persistence within {}", generateAvaliableEtl());

			getEngine().registerStageAreaPersistence(getProcessor(), getSrcObjects(), readers, writers);

			logDebug("StageArea persistence registered!");
		}
	}

	private Set<EtlItemConfiguration> generateAvaliableEtl() {
		Set<EtlItemConfiguration> itemConf = new HashSet<>();

		for (EtlDestinationConfiguration dst : this.getDstConf()) {
			itemConf.add(dst.getParentConf());
		}

		return itemConf;
	}

	private EtlDatabaseObject retrieveFirstDstRecordWithUnresolvedError(EtlDestinationConfiguration dst) {

		for (EtlDatabaseObject r : this.getSrcObjects()) {
			EtlDatabaseObject dstObject = r.retriveDestinationRecord(dst);

			if (dstObject != null && dstObject.getEtlInfo().hasExceptionOnEtl()) {

				if (isIgnorableException(dst, dstObject.getEtlInfo().getExceptionOnEtl())) {
					logWarn("\n\t-> {} \n\t\t-> While processing record {} \n\t\t\t-> The issue will be logged!",
							((EtlExceptionImpl) dstObject.getEtlInfo().getExceptionOnEtl()).getSuperLocalizedMessage(),
							dstObject);

					continue;
				}

				return dstObject;
			}
		}

		return null;
	}

	private boolean isIgnorableException(EtlDestinationConfiguration dstConf, EtlException e) {
		boolean isInconsistency = e instanceof InconsistentStateException;

		Throwable cause = e.getException();

		DBException dbException = null;

		while (cause != null) {

			if (cause instanceof DBException) {
				dbException = (DBException) cause;

				break;
			} else {
				cause = cause.getCause();
			}
		}

		isInconsistency = isInconsistency || dbException != null && dbException.isInconsistentDataException();

		boolean isMarkRecordAsFailedBehaviour = dstConf.getInconsistencyBehavior().logging();

		if (isInconsistency && isMarkRecordAsFailedBehaviour) {
			return true;
		} else {
			return e instanceof EtlTransformationException;
		}
	}

	private void load(EtlDestinationConfiguration dstConf, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws ParentNotYetMigratedException, DBException {
		loadToConnector(dstConf, readers, writers);
	}

	private void loadToConnector(EtlDestinationConfiguration dstConf, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws ParentNotYetMigratedException, DBException {

		if (dstConf.getDestinationDataConfiguration() instanceof JdbcTableDestinationConfiguration) {
			this.loadToDb(dstConf, readers, writers);
		} else {
			writeToConnector(dstConf, writers);
		}
	}

	private void writeToConnector(EtlDestinationConfiguration dstConf, ConnectorRecordWritersAggregator writers)
			throws DBException {
		List<EtlDatabaseObject> records = this.getAllTransformedObjects(dstConf, null);

		for (EtlDatabaseObject record : records) {
			record.loadObjectIdData();
			record.getEtlInfo().markAsReady();
		}

		List<EtlDatabaseObject> ready = this.getAllReadyTransformedObjects(dstConf);

		ConnectorRecordWriter<EtlDatabaseObject> writer = writers.get(dstConf);

		this.loadAndAddResult(writer.write(new ConnectorWriteRequest<>(ConnectorWriteMode.INSERT, ready)), dstConf);
	}

	private void loadToDb(EtlDestinationConfiguration dstConf, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws ParentNotYetMigratedException, DBException {

		if (!dstConf.isFullLoaded()) {
			dstConf.fullLoad();
		}

		this.beforeLoadToDb(dstConf, readers, writers);

		this.onLoadToDb(dstConf, writers);

		this.afterLoadToDb(dstConf, readers, writers);

	}

	/**
	 * @param srcConn
	 * @param dstConn
	 * @param config
	 * @param processedRecords
	 * @throws ParentNotYetMigratedException
	 * @throws DBException
	 */
	public void afterLoadToDb(EtlDestinationConfiguration dstConf, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws ParentNotYetMigratedException, DBException {

		if (getActionType().isCreate() || getActionType().isUpdate()) {

			for (EtlDatabaseObject loadRec : this.getAllReadyTransformedObjects(dstConf)) {

				if (loadRec.getEtlInfo().hasParentsWithDefaultValues()) {
					getEngine().registerDefaultParentPersistence(getProcessor(), loadRec.getEtlInfo());
				}

				if (loadRec.getEtlDefaultEtlException() == null) {
					loadRec.getEtlInfo().markAsSuccess();
				} else {
					loadRec.getEtlInfo().markAsFailed();
				}

				if (loadRec.getEtlInfo().isInSuccessStatus() && getEtlConfiguration().verifyRecordAfterCreate()) {
					logDebug("Verifying record after load: {}", loadRec);

					loadRec.validateIfRecordExistsOnDB(
							writers.get(loadRec.getRelatedConfiguration()).unwrap(Connection.class));
				}
			}
		}
	}

	void tryToAddToResult(EtlOperationItemResult<EtlDatabaseObject> resultItem) {

		if (isPrincipalLoading()) {
			getProcessor().getTaskResultInfo().addOrUpdate(resultItem);
		}
	}

	void loadAndAddResult(EtlOperationResultHeader<EtlDatabaseObject> result, EtlDestinationConfiguration dstConf) {

		if (isPrincipalLoading()) {

			if (result != null) {
				getProcessor().getTaskResultInfo().addAllFromOtherResult(result);
			}
		}
	}

	/**
	 * @param dstConn
	 * @param config
	 * @param objects
	 * @throws DBException
	 * @throws ForbiddenOperationException
	 */
	public void onLoadToDb(EtlDestinationConfiguration dstConf, ConnectorRecordWritersAggregator writers)
			throws DBException, ForbiddenOperationException {

		List<EtlDatabaseObject> objects = this.getAllReadyTransformedObjects(dstConf);

		ConnectorRecordWriter<EtlDatabaseObject> writer = writers.get(dstConf);
		Connection dstConn = writer.unwrap(Connection.class);

		JdbcTableDestinationConfiguration tabConf = dstConf.getTableDataDestination();

		if (getActionType().isCreate()) {
			logDebug("Starting the insertion of " + objects.size() + " " + tabConf.getTableName() + " on db...");

			tabConf.setUseMysqlInsertIgnore(this.getEtlOperationConfig().useseMysqlInsertIgnore());

			List<EtlDatabaseObject> recordToUpdate = new ArrayList<>();
			List<EtlDatabaseObject> recordToCreate = new ArrayList<>();

			if (tabConf.hasActionWhenRecordIdIsSet()) {

				for (EtlDatabaseObject obj : objects) {

					if (obj.hasValuedObjectId()) {

						if (obj.hasSharedPkObj()) {
							// We want to prevent situation where the sharedObject is persisted but the
							// child object is not yet persisted

							EtlDatabaseObject recordOnDB = DatabaseObjectDAO.getByOid(tabConf, obj.getObjectId(),
									dstConn);

							if (recordOnDB == null) {
								recordToCreate.add(obj);
								continue;
							}
						}

						recordToUpdate.add(obj);
					} else {
						recordToCreate.add(obj);
					}
				}
			} else {
				recordToCreate = objects;
			}

			this.loadAndAddResult(writer.write(new ConnectorWriteRequest<>(ConnectorWriteMode.INSERT, recordToCreate)),
					dstConf);

			logDebug(recordToCreate.size() + " " + tabConf.getTableName() + "  inserted on db!");

			if (!recordToUpdate.isEmpty()) {
				this.loadAndAddResult(
						writer.write(new ConnectorWriteRequest<>(ConnectorWriteMode.UPDATE, recordToUpdate)), dstConf);
				logDebug(recordToUpdate.size() + " " + tabConf.getTableName() + "  updated on db!");
			}

		} else if (getActionType().isUpdate()) {
			logDebug("Starting the update of " + objects.size() + " " + tabConf.getTableName() + " on db...");

			this.loadAndAddResult(writer.write(new ConnectorWriteRequest<>(ConnectorWriteMode.UPDATE_ALL, objects)),
					dstConf);
			logDebug(objects.size() + " " + tabConf.getTableName() + " updated from db!");

		} else if (getActionType().isDelete()) {
			logDebug("Starting the deletion of " + objects.size() + " " + tabConf.getTableName() + " on db...");

			this.loadAndAddResult(writer.write(new ConnectorWriteRequest<>(ConnectorWriteMode.DELETE_ALL, objects)),
					dstConf);

			logDebug(objects.size() + " " + tabConf.getTableName() + "  deleted on db!");

		} else {
			throw new ForbiddenOperationException("Unsupported operation " + getActionType() + " on ETL");
		}
	}

	public List<EtlDatabaseObject> getAllSuccedTransformedObjects(EtlDestinationConfiguration dstConf) {
		return this.getAllTransformedObjects(dstConf, EtlStatus.SUCCESS);
	}

	private List<EtlDatabaseObject> getAllReadyTransformedObjects(EtlDestinationConfiguration dstConf) {
		return getAllTransformedObjects(dstConf, EtlStatus.READY);
	}

	public List<EtlDatabaseObject> getAllTransformedObjects(EtlDestinationConfiguration dstConf) {
		return getAllTransformedObjects(dstConf, null);
	}

	public List<EtlDatabaseObject> getAllTransformedObjects() {
		List<EtlDatabaseObject> allOfDst = new ArrayList<>();

		for (EtlDatabaseObject obj : this.getSrcObjects()) {
			if (!obj.hasDestinationRecords())
				continue;

			allOfDst.addAll(obj.getDestinationObjects());
		}

		return allOfDst;
	}

	public List<EtlDatabaseObject> getAllTransformedObjects(EtlDestinationConfiguration dstConf, EtlStatus status) {
		List<EtlDatabaseObject> allOfDst = new ArrayList<>();

		for (EtlDatabaseObject srcObject : this.getSrcObjects()) {
			EtlDatabaseObject dstObject = srcObject.retriveDestinationRecord(dstConf);

			if (dstObject != null && (status == null || dstObject.getEtlInfo().getStatus().equals(status))) {
				allOfDst.add(dstObject);
			}
		}

		return allOfDst;
	}

	/**
	 * @param srcConn
	 * @param dstConn
	 * @param objects
	 * @param processedRecords
	 * @throws DBException
	 * @throws ParentNotYetMigratedException
	 * @throws MissingParentException
	 */
	public void beforeLoadToDb(EtlDestinationConfiguration dstConf, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers)
			throws DBException, ParentNotYetMigratedException, MissingParentException {

		List<EtlDatabaseObject> toLoad = this.getAllTransformedObjects(dstConf);

		this.logDebug("Preparing the load of " + toLoad.size());

		if (dstConf.getRelatedEtlConf().getRelationshipResolutionStrategy().skip()) {
			return;
		}

		for (EtlDatabaseObject obj : toLoad) {

			if (obj.getEtlInfo().hasExceptionOnEtl())
				continue;

			EtlInfo etlInfo = obj.getEtlInfo();

			this.logTrace("Preparing the load of dstRecord " + etlInfo.getTransformedObject());

			if (getActionType().isCreate() || getActionType().isUpdate()) {
				etlInfo.loadDstParentInfo(readers, writers);

				String errorMsg = "Found inconsistences on dstRecord " + etlInfo.getTransformedObject()
						+ " but all were resolved!";

				if (!etlInfo.getResultItem().hasUnresolvedInconsistences()) {
					etlInfo.setStatus(EtlStatus.READY);

					if (etlInfo.getResultItem().hasInconsistences()) {
						this.logTrace(errorMsg);
					}
				} else {
					InconsistentStateException e = new InconsistentStateException(
							etlInfo.getResultItem().getInconsistenceInfo());

					if (getEtlConfiguration().getDefaultInconsistencyBehavior().abortProcess()) {
						logError("Error while processing record: " + obj, e);

						throw e;
					} else {
						etlInfo.setExceptionOnEtl(e);
						etlInfo.setStatus(EtlStatus.FAIL);
					}
				}
			} else {
				etlInfo.setStatus(EtlStatus.READY);
			}

			tryToAddToResult(etlInfo.getResultItem());

		}

	}

	private EtlConfiguration getEtlConfiguration() {
		return getProcessor().getRelatedEtlConfiguration();
	}

	/**
	 * @return
	 */
	public EtlActionType getActionType() {
		return getEtlOperationConfig().getActionType();
	}

	public void loadToFile(EtlDestinationConfiguration dstConf) throws ParentNotYetMigratedException, DBException {
		List<EtlDatabaseObject> toLoad = this.getAllTransformedObjects(dstConf);

		for (EtlDatabaseObject obj : toLoad) {
			obj.loadObjectIdData();
			obj.getEtlInfo().markAsReady();
		}

		List<EtlDatabaseObject> objs = this.getAllReadyTransformedObjects(dstConf);

		String dataFile = getEngine().getDataDir().getAbsolutePath() + File.separator + objs.get(0).generateTableName();

		String data = null;

		if (getEngine().isJsonDst()) {
			data = utilities.parseToJSON(objs);

			dataFile += ".json";
		} else if (getEngine().isCsvDst()) {
			dataFile += ".csv";

			data = utilities.parseToCSVWithoutHeader(objs, dstConf.getExcludedFields(),
					dstConf.getCsvDataDestination().getDelimiter());
		} else if (getEngine().isDumpDst()) {
			dataFile += ".sql";

			data = TableConfiguration.generateInsertDump(objs);
		}

		synchronized (getEngine()) {
			boolean includeHeader = FileUtilities.isEmpty(new File(dataFile)) && getEngine().isCsvDst();

			if (includeHeader) {
				FileUtilities.write(dataFile, utilities.generateCsvHeader(objs.get(0), dstConf.getExcludedFields(),
						dstConf.getCsvDataDestination().getDelimiter()));
			}

			FileUtilities.write(dataFile, data);
		}

		getProcessor().getTaskResultInfo().addSuccessfulRecords(objs);
	}

	void logTrace(String msg) {
		getProcessor().logTrace(msg);
	}

	void logDebug(String msg) {
		getProcessor().logDebug(msg);
	}

	void logDebug(String msg, Object... arguments) {
		getProcessor().logDebug(msg, arguments);
	}

	void logInfo(String msg) {
		getProcessor().logInfo(msg);
	}

	void logInfo(String msg, Object... arguments) {
		getProcessor().logInfo(msg, arguments);
	}

	void logWarn(String msg) {
		getProcessor().logWarn(msg);
	}

	void logWarn(String msg, Object... arguments) {
		getProcessor().logWarn(msg, arguments);
	}

	void logError(String msg, Exception e) {
		getProcessor().logError(msg, e);
	}

	void logError(String msg, Exception e, Object... arguments) {
		getProcessor().logError(msg, e, arguments);
	}

	public static void performeParentLoading(EtlDatabaseObject srcObject, ConnectorRecordReadersAggregator readers,
			ConnectorRecordWritersAggregator writers) throws ParentNotYetMigratedException, DBException {

		EtlInfo etlInfo = srcObject.getDestinationObjects().get(0).getEtlInfo();

		TableConfiguration relatedTable = srcObject.getRelatedConfiguration().retrieveRelatedTableConf();
		String sourceDescription = relatedTable != null ? relatedTable.getFullTableDescription()
				: srcObject.getRelatedConfiguration().getObjectName();
		String msg = "Initializing the load of parent record [" + sourceDescription + srcObject + "]";

		String tree = "";

		EtlInfo parent = srcObject.getEtlInfo();

		while (parent != null) {

			if (tree.isEmpty()) {
				tree = parent.getTransformedObject().toString();
			} else {
				tree = tree + " <<<< " + parent.getTransformedObject().toString();
			}

			parent = parent.getParentEtlInfo();
		}

		msg += " Tree Info: [" + tree + "]";

		etlInfo.getProcessor().logTrace(msg);

		new EtlLoadHelper(etlInfo.getProcessor(), etlInfo.getPrimaryDataSource().getParentItemConfiguration(),
				utilities.parseToList(srcObject), LoadingType.PARENT, false)
				.load(etlInfo.getDstConf(), readers, writers);
	}

	public static EtlLoadHelper fastLoadRecord(EtlProcessor processor, EtlDatabaseObject srcRecord,
			EtlDestinationConfiguration dstConf, TransformationType transformationType, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer) throws ParentNotYetMigratedException, DBException {

		EtlItemConfiguration conf = dstConf.getParentConf();

		return processor.performTransformationAndLoading(conf, utilities.parseToList(srcRecord), null,
				LoadingType.PARENT, (ConnectorRecordReadersAggregator) reader.getAggregator(),
				(ConnectorRecordWritersAggregator) writer.getAggregator());
	}

	public boolean hasDstConf() {
		return utilities.listHasElement(this.getDstConf());
	}
}
