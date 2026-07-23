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
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.TaskProcessor;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.controller.EtlController;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.etl.model.EtlLoadHelper;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.etl.model.stage.EtlStageAreaObject;
import org.openmrs.module.epts.etl.etl.processor.transformer.TransformationType;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.MissingRequiredTransformationObject;
import org.openmrs.module.epts.etl.exceptions.NoDstForGivenSrcException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.model.pojo.generic.DatabaseObjectDAO;
import org.openmrs.module.epts.etl.utilities.db.conn.ConnectionKeepAlive;
import org.openmrs.module.epts.etl.utilities.db.conn.ConnectionKeepAliveManager;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Represents a generic processor for ETL operation
 * 
 * @author jpboane
 */
public class EtlProcessor extends TaskProcessor<EtlDatabaseObject> {

	private ReentrantLock dstConnectionLock = new ReentrantLock();
	private ConnectionKeepAliveManager keepAliveManager = ConnectionKeepAliveManager.getInstance();

	public EtlProcessor(Engine<EtlDatabaseObject> monitor, IntervalExtremeRecord limits, boolean runningInConcurrency) {
		super(monitor, limits, runningInConcurrency);
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

	@Override
	public void performeEtl(List<EtlDatabaseObject> etlObjects, Connection srcConn, Connection dstConn)
			throws DBException {
		try {
			perform(this.getEtlItemConfiguration(), etlObjects, null, LoadingType.PRINCIPAL, srcConn, dstConn);

			EtlActionType action = getRelatedEtlOperationConfig().getAfterEtlActionType();

			if (action == null || action.isUndefined()) {
				return;
			}

			for (EtlDatabaseObject obj : etlObjects) {
				applyAfterEtlAction(obj, action, srcConn, dstConn);
			}
		} catch (Exception e) {
			logWarn("Error ocurred on thread " + getProcessorId() + " On Records [" + getLimits() + "]... \n");
			logError(e.getLocalizedMessage(), e);

			getTaskResultInfo().setFatalException(e);
		}
	}

	private void applyAfterEtlAction(EtlDatabaseObject obj, EtlActionType action, Connection srcConn,
			Connection dstConn) throws DBException {

		if (action.isDelete()) {
			DatabaseObjectDAO.remove(obj, srcConn);
			return;
		}

		EtlStageAreaObject stageRecord = obj.generateProcessedRecord(srcConn, dstConn);

		boolean successful = stageRecord.hasNoError();

		if (action.moveToStageArea()) {
			moveToStageArea(obj, stageRecord, srcConn);
			return;
		}

		if (action.moveToStageAreaOnSuccess()) {

			if (successful) {
				moveToStageArea(obj, stageRecord, srcConn);
				DatabaseObjectDAO.remove(obj, srcConn);
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

	public EtlLoadHelper perform(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
			EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn, Connection dstConn)
			throws DBException {

		ReentrantLock dstConnLock = new ReentrantLock();

		try (ConnectionKeepAlive keepAlive = keepAliveManager.register(dstConn, dstConnLock, this)) {

			for (EtlDatabaseObject srcRecord : etlObjects) {
				logTrace("Initializing the transformation process of record {} ", srcRecord);

				if (srcRecord.isTrackable() && getRelatedEtlOperationConfig().hasActionAfterEtl()
						&& getRelatedEtlOperationConfig().getAfterEtlActionType().includeTracking()) {
					srcRecord.changeStatusToProcessing(srcConn);
				}

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
							// dstConnectionLock.lock();

							try {

								if (srcConf.hasExpansionDs()) {
									logTrace(
											"Starting the transformation of record {} with expanstion {} within the dstConf {}",
											srcRecord, expanded, dstConf);
								} else {
									logTrace("Starting the transformation of record {} within the dstConf {}",
											srcRecord, dstConf);
								}

								transform(srcRecord, expanded, parentMigratedRec, dstConf, srcConn, dstConn);

								if (srcConf.hasExpansionDs()) {
									logTrace(
											"Finished transformation of record {} with expanstion {} within the dstConf {}. Current transformed objects within the srcObject {}",
											srcRecord, expanded, dstConf, srcRecord);
								} else {
									logTrace(
											"Finished transformation of record {} within the dstConf {}. Current transformed objects within the srcObject {}",
											srcRecord, dstConf, srcRecord);
								}
							} finally {
								// dstConnectionLock.unlock();
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

		logDebug(
				"Initializing the loading of " + etlObjects.size() + " " + etlItemConf.getSrcConf().getFullTableName());

		EtlLoadHelper loadHelper = null;

		try {
			loadHelper = new EtlLoadHelper(this, etlItemConf, etlObjects, loadingType, etlItemConf.ignoreNoDstIssue());
		} catch (NoDstForGivenSrcException e) {
			tryToLogOrThrowNoDstForGivenSrcException(etlObjects, e, srcConn, dstConn);
		}

		if (loadHelper.hasDstConf()) {
			loadHelper.load(srcConn, dstConn);

			tryToPerfomeEtlOnChild(etlItemConf, loadHelper, srcConn, dstConn);

			logInfo("ETL OPERATION [" + etlItemConf.getConfigCode() + "] DONE ON " + etlObjects.size() + "' RECORDS");
		} else {

			String msg = "NO DST OBJECT WAS FOUND FOR ETL[" + etlItemConf.getConfigCode() + "] ON '" + etlObjects.size()
					+ "' RECORDS";

			if (!getRelatedEtlConf().doNotWarnOnNoDstObjectFound()) {
				logWarn(msg);
			} else {
				logDebug(msg);
			}

			tryToPerfomeEtlOnChild(etlItemConf, etlObjects, srcConn, dstConn);
		}

		return loadHelper;
	}

	private void transform(EtlDatabaseObject srcRecord, EtlDatabaseObject srcRecordExpansion,
			EtlDatabaseObject parentMigratedRec, DstConf dstConf, Connection srcConn, Connection dstConn)
			throws DBException {

		try {
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

			if (utilities.setHasElement(avaliableSrcObjects) || dstConf.isDoNotUseSrcConfAsDataSource()) {
				if (dstConf.shouldBeProcessed(srcRecord, avaliableSrcObjects, srcConn, dstConn)) {

					EtlDatabaseObject dstObject = dstConf.getTransformerInstance().transform(this, srcRecord,
							avaliableSrcObjects, dstConf, parentMigratedRec, TransformationType.PRINCIPAL, srcConn,
							dstConn);

					if (dstObject != null) {
						srcRecord.addDestinationRecord(dstObject);

						logTrace("dstRecord " + srcRecord + " transforming to " + dstObject);
					} else {
						throw new EtlTransformationException("The srcObject could not be transformed", dstObject,
								getGeneralBehaviourOnEtlException());
					}
				}
			}
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
			throw e;
		}

		if (defaultBehavior.logging()) {
			createDefaultFailedDstObject(record, mappingInfo, e);
		} else if (defaultBehavior.useExceptionBehavior()) {
			if (exceptionBehavior == null) {
				throw e;
			}

			if (exceptionBehavior.logging()) {
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

	private void tryToPerfomeEtlOnChild(EtlItemConfiguration itemConf, EtlLoadHelper loadHelper, Connection srcConn,
			Connection dstConn) throws DBException {

		tryToPerfomeEtlOnChild(itemConf,
				childItemConf -> loadHelper.getAllSuccedTransformedObjects(childItemConf.getRelatedParentDstConf()),
				srcConn, dstConn);
	}

	private void tryToPerfomeEtlOnChild(EtlItemConfiguration itemConf,
			Function<EtlChildItemConfiguration, List<EtlDatabaseObject>> objectsProvider, Connection srcConn,
			Connection dstConn) throws DBException {

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
				performeEtlOnChildItem(childItemConf, rec, srcConn, dstConn);
			}
		}
	}

	private void tryToPerfomeEtlOnChild(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
			Connection srcConn, Connection dstConn) throws DBException {

		tryToPerfomeEtlOnChild(etlItemConf, childItemConf -> etlObjects, srcConn, dstConn);
	}

	private void performeEtlOnChildItem(EtlChildItemConfiguration itemConf, EtlDatabaseObject transformedParent,
			Connection srcConn, Connection dstConn) throws DBException {

		EtlDatabaseObject srcObject = transformedParent.isSrcObject() ? transformedParent
				: transformedParent.getEtlInfo().getRelatedSrcObject();

		if (itemConf.shouldBeProcessed(srcObject, null, srcConn, dstConn)) {

			List<EtlDatabaseObject> avaliableSrcObjects = transformedParent.isSrcObject() ? null
					: new ArrayList<>(transformedParent.getEtlInfo().getAvaliableSrcObjects());

			List<EtlDatabaseObject> etlObjects = itemConf.getSrcConf().searchRecords(this.getEngine(), srcObject,
					avaliableSrcObjects, srcConn);

			if (!etlObjects.isEmpty()) {
				for (EtlDatabaseObject obj : etlObjects) {
					srcObject.addChildObject(obj);
				}

				perform(itemConf, etlObjects, transformedParent.isDstObject() ? transformedParent : null,
						LoadingType.INNER, srcConn, dstConn);
			}
		}
	}

	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(
			IntervalExtremeRecord limits) {
		ReloadRecordsWithDefaultParentProcessor p = new ReloadRecordsWithDefaultParentProcessor(
				(Engine<EtlDatabaseObject>) this.getEngine(), limits, false);

		p.setRelatedEtlProcessor(this);

		return null;

		// return p;
	}

}
