package org.openmrs.module.epts.etl.etl.processor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlChildItemConfiguration;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.conf.types.EtlActionType;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriters;
import org.openmrs.module.epts.etl.connector.jdbc.JdbcConnectorRuntimeAdapters;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.controller.EtlController;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageAreaObject;
import org.openmrs.module.epts.etl.etl.processor.transformer.TransformationType;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.MissingRequiredTransformationObject;
import org.openmrs.module.epts.etl.exceptions.NoDstForGivenSrcException;
import org.openmrs.module.epts.etl.exceptions.ParentNotYetMigratedException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.processor.TaskProcessor;
import org.openmrs.module.epts.etl.utilities.db.conn.ConnectionKeepAlive;
import org.openmrs.module.epts.etl.utilities.db.conn.ConnectionKeepAliveManager;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class EtlProcessor extends TaskProcessor<EtlDatabaseObject> {

	private ConnectionKeepAliveManager keepAliveManager = ConnectionKeepAliveManager.getInstance();

	public EtlProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits, boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);
	}

	@Override
	public void transformAndLoad(List<EtlDatabaseObject> etlObjects, ConnectorRecordReader<?> reader,
			ConnectorRecordWriters writers) throws DBException {

		Connection srcConn = reader.unwrap(Connection.class);

		try {
			performTransformation(getEtlItemConfiguration(), etlObjects, null, LoadingType.PRINCIPAL, srcConn, writers);

			load(etlObjects, reader, writers);
		} catch (Exception e) {
			logWarn("Error ocurred on thread {} On Records {} [{}]", getProcessorId(), getLimits());

			logError(e.getLocalizedMessage(), e);

			getTaskResultInfo().setFatalException(e);
		}
	}

	@Override
	public void transform(List<EtlDatabaseObject> etlObjects, ConnectorRecordReader<?> reader,
			ConnectorRecordWriters writers) throws DBException {

		performTransformation(getEtlItemConfiguration(), etlObjects, null, LoadingType.PRINCIPAL,
				reader.unwrap(Connection.class), writers);
	}

	@Override
	public void load(List<EtlDatabaseObject> etlObjects, ConnectorRecordReader<?> reader,
			ConnectorRecordWriters writers) throws DBException {

		Connection srcConn = reader.unwrap(Connection.class);

		performLoading(getEtlItemConfiguration(), etlObjects, LoadingType.PRINCIPAL, reader, writers);

		EtlActionType action = getRelatedEtlOperationConfig().getAfterEtlActionType();

		if (action != null && !action.isUndefined()) {
			for (EtlDatabaseObject obj : etlObjects) {
				applyAfterEtlAction(obj, action, srcConn, null);
			}
		}
	}

	public EtlLoadHelper performTransformationAndLoading(EtlItemConfiguration etlItemConf,
			List<EtlDatabaseObject> etlObjects, EtlDatabaseObject parentMigratedRec, LoadingType loadingType,
			ConnectorRecordReader<?> reader, ConnectorRecordWriters writers) throws DBException {

		this.performTransformation(etlItemConf, etlObjects, parentMigratedRec, loadingType,
				reader.unwrap(Connection.class), writers);

		return performLoading(etlItemConf, etlObjects, loadingType, reader, writers);
	}

	EtlLoadHelper performTransformationAndLoadingJdbc(EtlItemConfiguration etlItemConf,
			List<EtlDatabaseObject> etlObjects, EtlDatabaseObject parentMigratedRec, LoadingType loadingType,
			Connection srcConn, Connection dstConn) throws DBException {

		ConnectorRecordWriters writers = etlItemConf.bindDestinationWriters(dstConn);
		this.performTransformation(etlItemConf, etlObjects, parentMigratedRec, loadingType, srcConn, writers);

		return performLoading(etlItemConf, etlObjects, loadingType, JdbcConnectorRuntimeAdapters.reader(srcConn),
				writers);
	}

	private void performTransformation(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
			EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn,
			ConnectorRecordWriters writers)
			throws DBException {

		this.debug("Starting transformation of {} {}", etlObjects.size(), etlItemConf.getSrcConf().getAlias());

		List<ConnectionKeepAlive> keepAlives = registerDestinationKeepAlives(writers);
		try {

			if (etlItemConf.canQuickTransform(this)) {
				logWarn("Performing quick tranformation of {} {}", etlObjects.size(),
						etlItemConf.getSrcConf().getTableAlias());

				for (EtlDatabaseObject srcRecord : etlObjects) {
					this.quickTransformRecord(srcRecord, srcConn,
							destinationConnection(writers, etlItemConf.getDstConf().get(0)));
				}

				logWarn("Quick tranformation done on {} {}", etlObjects.size(),
						etlItemConf.getSrcConf().getTableAlias());

			} else {

				for (EtlDatabaseObject srcRecord : etlObjects) {
					/*
					 * if (srcRecord.isTrackable() &&
					 * getRelatedEtlOperationConfig().hasActionAfterEtl() &&
					 * getRelatedEtlOperationConfig().getAfterEtlActionType().includeTracking()) {
					 * srcRecord.changeStatusToProcessing(srcConn); }
					 */

					if (!etlItemConf.getSrcConf().doNotUseAsDatasource()) {
						srcRecord.loadObjectIdData(etlItemConf.getSrcConf());
					}

					List<EtlDatabaseObject> avaliableSrcDs = parentMigratedRec != null
							? new ArrayList<>(parentMigratedRec.collectAllAvaliableSrcObjects())
							: new ArrayList<>();

					for (DstConf dstConf : etlItemConf.getDstConf()) {
						if (dstConf.isDisabled()) {
							logTrace("Skiping transformation of dstConf {} as it is disabled", dstConf);

							continue;
						}

						Connection dstConn = destinationConnection(writers, dstConf);
						List<EtlDatabaseObject> expansion = null;

						SrcConf srcConf = (SrcConf) srcRecord.getRelatedConfiguration();

						if (srcConf.hasExpansionDs()) {
							logTrace("Starting expation of record {} within the dstConf {}", srcRecord, dstConf);

							expansion = srcConf.getExpansionDataSource().expand(this, srcRecord, avaliableSrcDs, null,
									srcConn);
						} else {
							expansion = utilities.parseToList(srcRecord);
						}

						if (utilities.listHasElement(expansion)) {
							for (EtlDatabaseObject expanded : expansion) {
								if (srcConf.hasExpansionDs()) {
									logTrace(
											"Starting the transformation of record {} with expanstion {} within the dstConf {}",
											srcRecord, expanded, dstConf);
								} else {
									logTrace("Starting the transformation of record {} within the dstConf {}",
											srcRecord, dstConf);
								}

								this.transformRecord(srcRecord, expanded, parentMigratedRec, dstConf, srcConn, dstConn);

								if (srcConf.hasExpansionDs()) {
									logTrace(
											"Finished transformation of record {} with expanstion {} within the dstConf {}. Current transformed objects within the srcObject {}",
											srcRecord, expanded, dstConf, srcRecord);
								} else {
									logTrace(
											"Finished transformation of record {} within the dstConf {}. Current transformed objects within the srcObject {}",
											srcRecord, dstConf, srcRecord);
								}
							}
						} else {

							if (!getRelatedEtlConf().doNotWarnOnNoDstObjectFound()) {
								logWarn("Expansion of record result on empty list:" + srcRecord);
							} else {
								logDebug("Expansion of record result on empty list:" + srcRecord);
							}
						}
					}
				}
			}
		} finally {
			closeKeepAlives(keepAlives);
		}

		this.debug("Finished transformation of {} {}", etlObjects.size(), etlItemConf.getSrcConf().getAlias());
	}

	private List<ConnectionKeepAlive> registerDestinationKeepAlives(ConnectorRecordWriters writers) {
		List<ConnectionKeepAlive> keepAlives = new ArrayList<>();
		for (Connection connection : writers.unwrapAll(Connection.class)) {
			keepAlives.add(keepAliveManager.register(connection, new ReentrantLock(), this));
		}
		return keepAlives;
	}

	private void closeKeepAlives(List<ConnectionKeepAlive> keepAlives) {
		for (ConnectionKeepAlive keepAlive : keepAlives) {
			keepAlive.close();
		}
	}

	private Connection destinationConnection(ConnectorRecordWriters writers, DstConf destination) {
		try {
			return writers.get(destination).unwrap(Connection.class);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private Connection firstJdbcDestinationConnection(ConnectorRecordWriters writers) {
		List<Connection> connections = writers.unwrapAll(Connection.class);
		return connections.isEmpty() ? null : connections.get(0);
	}

	private EtlLoadHelper performLoading(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
			LoadingType loadingType, ConnectorRecordReader<?> reader, ConnectorRecordWriters writers)
			throws DBException, ParentNotYetMigratedException {
		Connection srcConn = reader.unwrap(Connection.class);
		EtlLoadHelper loadHelper = null;
		try {
			loadHelper = new EtlLoadHelper(this, etlItemConf, etlObjects, loadingType, etlItemConf.ignoreNoDstIssue());
		} catch (NoDstForGivenSrcException e) {
			tryToLogOrThrowNoDstForGivenSrcException(etlObjects, e, srcConn,
					firstJdbcDestinationConnection(writers));
		}
		if (loadHelper != null && loadHelper.hasDstConf()) {
			loadHelper.load(reader, writers);
			tryToPerfomeEtlOnChild(etlItemConf, loadHelper, reader, writers);
			logInfo("ETL OPERATION [" + etlItemConf.getConfigCode() + "] DONE ON " + etlObjects.size() + "' RECORDS");
		} else {
			String msg = "NO DST OBJECT WAS FOUND FOR ETL[" + etlItemConf.getConfigCode() + "] ON '" + etlObjects.size()
					+ "' RECORDS";
			if (!getRelatedEtlConf().doNotWarnOnNoDstObjectFound()) {
				logWarn(msg);
			} else {
				logDebug(msg);
			}
			tryToPerfomeEtlOnChild(etlItemConf, etlObjects, reader, writers);
		}
		return loadHelper;
	}

	private void quickTransformRecord(EtlDatabaseObject srcRecord, Connection srcConn, Connection dstConn)
			throws DBException {

		EtlDatabaseObject dstObject = this.getEtlItemConfiguration().getDstConf().get(0).createRecordInstance();

		dstObject.markAsNotCollactable();

		dstObject.copyFrom(srcRecord);

		dstObject.setEtlInfo(EtlInfo.fastCreateForRecordAlradyLoadedWithDstData(this, srcRecord, dstObject));

		srcRecord.addDestinationRecord(dstObject);
	}

	private void transformRecord(EtlDatabaseObject srcRecord, EtlDatabaseObject srcRecordExpansion,
			EtlDatabaseObject parentMigratedRec, DstConf dstConf, Connection srcConn, Connection dstConn)
			throws DBException {

		try {
			logDebug("Initializing the transformation process of record {} ", srcRecord);

			EtlDatabaseObject transitionalTransformedObject = dstConf.createRecordInstance();

			transitionalTransformedObject.markAsNotCollactable();

			transitionalTransformedObject
					.setEtlInfo(EtlInfo.initEtlRecord(this, srcRecord, transitionalTransformedObject));

			Set<EtlDatabaseObject> avaliableSrcObjects = dstConf.getTransformerInstance().collectSourceObjects(this,
					srcRecord, srcRecordExpansion, transitionalTransformedObject, parentMigratedRec, dstConf,
					TransformationType.PRINCIPAL, srcConn);

			if (transitionalTransformedObject.getEtlDefaultEtlException() != null) {
				throw (EtlExceptionImpl) transitionalTransformedObject.getEtlDefaultEtlException();
			}

			EtlDatabaseObject dstObject = null;

			if (utilities.setHasElement(avaliableSrcObjects) || dstConf.isDoNotUseSrcConfAsDataSource()) {
				if (dstConf.shouldBeProcessed(srcRecord, avaliableSrcObjects, srcConn, dstConn)) {

					dstObject = dstConf.getTransformerInstance().transform(this, srcRecord, avaliableSrcObjects,
							dstConf, parentMigratedRec, TransformationType.PRINCIPAL, srcConn, dstConn);

					if (dstObject != null) {
						srcRecord.addDestinationRecord(dstObject);

						logTrace("dstRecord " + srcRecord + " transforming to " + dstObject);
					} else {
						throw new EtlTransformationException("The srcObject could not be transformed", dstObject,
								getGeneralBehaviourOnEtlException());
					}
				}
			}

			logDebug("Transformation process of record {} is finalized! Transformed record -> {}", srcRecord,
					dstObject);

		} catch (MissingRequiredTransformationObject e) {
			tryToLogOrThrowException(srcRecord, dstConf, e);
		} catch (EtlTransformationException e) {
			tryToLogOrThrowException(srcRecord, dstConf, e);
		}
	}

	private void tryToLogOrThrowNoDstForGivenSrcException(List<EtlDatabaseObject> records, NoDstForGivenSrcException e,
			Connection srcConn, Connection dstConn) throws DBException {

		ActionOnEtlIssue defaultBehavior = this.getRelatedEtlConf().getDefaultExceptionBehavior();
		ActionOnEtlIssue exceptionBehavior = e.getAction();

		if (defaultBehavior.abort()) {
			throw e;
		}

		if (defaultBehavior.useExceptionBehavior()) {
			if (exceptionBehavior == null) {
				throw e;
			}

			if (exceptionBehavior.logging()) {
				for (EtlDatabaseObject record : records) {
					EtlStageAreaObject r = record.generateSrcStageRecord(srcConn, dstConn);
					r.setFieldValue("last_sync_try_err", utilities.garantirXCaracteres(e.getLocalizedMessage(), 499));
					r.save(r.getRelatedEtlTableConf(), srcConn);
				}
			} else {
				throw e;
			}
		}

	}

	private void tryToLogOrThrowException(EtlDatabaseObject record, DstConf mappingInfo, EtlTransformationException e) {
		ActionOnEtlIssue defaultBehavior = mappingInfo.getRelatedEtlConf().getDefaultExceptionBehavior();
		ActionOnEtlIssue exceptionBehavior = e.getAction();

		if (defaultBehavior.abort()) {
			logError("Error Happen while processing record {} \n\t -> {}", e, record, e.getLocalizedMessage());

			throw e;
		}

		if (defaultBehavior.logging()) {
			logWarn("Errror Happen while processing record {} \n\t -> {}", e, record, e.getLocalizedMessage());

			createDefaultFailedDstObject(record, mappingInfo, e);
		} else if (defaultBehavior.useExceptionBehavior()) {

			if (exceptionBehavior == null) {
				throw e;
			}

			if (exceptionBehavior.logging()) {
				logWarn("Error Happen while processing record {} \n\t -> {}", e, record, e.getLocalizedMessage());

				createDefaultFailedDstObject(record, mappingInfo, e);
			} else {
				throw e;
			}
		}
	}

	/**
	 * @param record
	 * @param mappingInfo
	 * @param e
	 */
	private void createDefaultFailedDstObject(EtlDatabaseObject record, DstConf mappingInfo,
			EtlTransformationException e) {

		if (!getRelatedEtlConf().doNotWarnOnNoDstObjectFound()) {
			logWarn("Issues found when transforming record " + record + ". The issue will be logged: "
					+ e.getLocalizedMessage());
		}

		EtlDatabaseObject dstObject = mappingInfo.createRecordInstance();

		dstObject.setEtlInfo(EtlInfo.initEtlRecord(this, record, dstObject));

		dstObject.getEtlInfo().setExceptionOnEtl(e);

		record.addDestinationRecord(dstObject);
	}

	private void tryToPerfomeEtlOnChild(EtlItemConfiguration itemConf, EtlLoadHelper loadHelper,
			ConnectorRecordReader<?> reader, ConnectorRecordWriters writers) throws DBException {

		tryToPerfomeEtlOnChild(itemConf,
				childItemConf -> loadHelper.getAllSuccedTransformedObjects(childItemConf.getRelatedParentDstConf()),
				reader, writers);
	}

	private void tryToPerfomeEtlOnChild(EtlItemConfiguration itemConf,
			Function<EtlChildItemConfiguration, List<EtlDatabaseObject>> objectsProvider,
			ConnectorRecordReader<?> reader, ConnectorRecordWriters writers) throws DBException {

		if (!itemConf.hasChildItemConf()) {
			return;
		}

		logDebug("Starting the load of child within the conf: " + itemConf.getConfigCode());

		for (EtlChildItemConfiguration childItemConf : itemConf.getChildItemConf()) {
			childItemConf.fullLoad(this.getRelatedEtlOperationConfig());

			List<EtlDatabaseObject> records = objectsProvider.apply(childItemConf);

			if (records == null || records.isEmpty()) {
				continue;
			}

			for (EtlDatabaseObject rec : records) {
				performeEtlOnChildItem(childItemConf, rec, reader, writers);
			}
		}
	}

	private void tryToPerfomeEtlOnChild(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
			ConnectorRecordReader<?> reader, ConnectorRecordWriters writers) throws DBException {

		tryToPerfomeEtlOnChild(etlItemConf, childItemConf -> etlObjects, reader, writers);
	}

	private void performeEtlOnChildItem(EtlChildItemConfiguration itemConf, EtlDatabaseObject transformedParent,
			ConnectorRecordReader<?> reader, ConnectorRecordWriters writers) throws DBException {
		Connection srcConn = reader.unwrap(Connection.class);
		Connection parentDstConn = transformedParent.getRelatedConfiguration() instanceof DstConf
				? destinationConnection(writers, (DstConf) transformedParent.getRelatedConfiguration())
				: null;
		Connection childDstConn = firstDestinationConnection(itemConf, writers);

		try {
			EtlDatabaseObject srcObject = transformedParent.isSrcObject() ? transformedParent
					: transformedParent.getEtlInfo().getRelatedSrcObject();

			transformedParent.loadObjectIdData();

			if (getRelatedEtlConf().verifyRecordAfterCreate()) {
				logWarn("Verifying the existence of parent record {} on destination db", transformedParent);

				transformedParent.validateIfRecordExistsOnDB(parentDstConn);

				logDebug("The existence of parent record {} on db was confirmed", transformedParent);
			}

			if (itemConf.shouldBeProcessed(srcObject, null, srcConn, childDstConn)) {

				List<EtlDatabaseObject> avaliableSrcObjects = transformedParent.isSrcObject() ? null
						: new ArrayList<>(transformedParent.getEtlInfo().getAvaliableSrcObjects());

				List<EtlDatabaseObject> etlObjects = itemConf.getSrcConf().searchRecords(this.getEngine(), srcObject,
						avaliableSrcObjects, srcConn);

				if (!etlObjects.isEmpty()) {
					for (EtlDatabaseObject obj : etlObjects) {
						srcObject.addChildObject(obj);
					}

					try {
						performTransformationAndLoading(itemConf, etlObjects,
								transformedParent.isDstObject() ? transformedParent : null, LoadingType.CHILD, reader,
								writers);
					} catch (Exception e) {
						logWarn("Error happened while perfoming etl within child {} with dstConf {}", itemConf,
								itemConf.getDstConf());

						throw e;
					}
				}
			}
		} catch (Exception e) {
			logWarn("Error happened while perfoming etl within child {} with dstConf {}", itemConf,
					itemConf.getDstConf());

			throw e;
		}
	}

	private Connection firstDestinationConnection(EtlItemConfiguration itemConf, ConnectorRecordWriters writers) {
		if (itemConf.getDstConf() == null) {
			return null;
		}
		for (DstConf destination : itemConf.getDstConf()) {
			if (!destination.isDisabled()) {
				Connection connection = destinationConnection(writers, destination);
				if (connection != null) {
					return connection;
				}
			}
		}
		return null;
	}

	@Override
	public AbstractEtlSearchParams<EtlDatabaseObject> getSearchParams() {
		return (EtlDatabaseObjectSearchParams) super.getSearchParams();
	}

	public DBConnectionInfo getDstConnInfo() {
		return this.getRelatedOperationController().getDstConnInfo();
	}

	public DBConnectionInfo getSrcConnInfo() {
		return this.getRelatedOperationController().getSrcConnInfo();
	}

	@Override
	public EtlController getRelatedOperationController() {
		return (EtlController) super.getRelatedOperationController();
	}

	private void applyAfterEtlAction(EtlDatabaseObject obj, EtlActionType action, Connection srcConn,
			Connection dstConn) throws DBException {

		if (action.isDelete()) {
			DatabaseObjectDAO.remove(obj, srcConn);
			return;
		}

		EtlStageAreaObject stageRecord = obj.generateProcessedRecord(srcConn, dstConn);

		if (action.moveToStageArea()) {
			moveToStageArea(obj, stageRecord, srcConn);
			return;
		}

		if (action.moveToStageAreaOnSuccess()) {
			if (stageRecord.isLoadedSuccessifuly()) {
				moveToStageArea(obj, stageRecord, srcConn);
			} else {
				trackFailedProcessingIfConfigured(obj, stageRecord, srcConn);
			}
			return;
		}

		if (action.moveToStageAreaOnNoError()) {
			if (stageRecord.hasNoError()) {
				moveToStageArea(obj, stageRecord, srcConn);
			} else {
				trackFailedProcessingIfConfigured(obj, stageRecord, srcConn);
			}
		}
	}

	private void moveToStageArea(EtlDatabaseObject sourceRecord, EtlStageAreaObject stageRecord, Connection srcConn)
			throws DBException {

		stageRecord.save(stageRecord.getRelatedConfiguration(), srcConn);

		DatabaseObjectDAO.remove(sourceRecord, srcConn);
	}

	private void trackFailedProcessingIfConfigured(EtlDatabaseObject sourceRecord, EtlStageAreaObject stageRecord,
			Connection srcConn) throws DBException {

		SrcConf srcConf = (SrcConf) sourceRecord.getRelatedConfiguration();

		if (srcConf.trackProcessingState()) {
			sourceRecord.trackProcessingState(stageRecord, srcConn);
		}
	}

	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(
			IntervalExtremeRecord limits) {
		ReloadRecordsWithDefaultParentProcessor p = new ReloadRecordsWithDefaultParentProcessor(
				(Engine<EtlDatabaseObject>) this.getEngine(), limits, false);

		p.setRelatedEtlProcessor(this);

		return p;
	}
}
