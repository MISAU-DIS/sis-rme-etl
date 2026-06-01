package org.openmrs.module.epts.etl.etl.processor;

import java.sql.Connection;
import java.util.List;
import java.util.function.Function;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.EtlItemConfiguration;
import org.openmrs.module.epts.etl.engine.AbstractEtlSearchParams;
import org.openmrs.module.epts.etl.engine.Engine;
import org.openmrs.module.epts.etl.engine.TaskProcessor;
import org.openmrs.module.epts.etl.engine.record_intervals_manager.IntervalExtremeRecord;
import org.openmrs.module.epts.etl.etl.controller.EtlController;
import org.openmrs.module.epts.etl.etl.model.EtlDatabaseObjectSearchParams;
import org.openmrs.module.epts.etl.etl.model.EtlLoadHelper;
import org.openmrs.module.epts.etl.etl.model.LoadingType;
import org.openmrs.module.epts.etl.etl.processor.transformer.TransformationType;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Represents a generic processor for ETL operation
 * 
 * @author jpboane
 */
public class EtlProcessor extends TaskProcessor<EtlDatabaseObject> {
	
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
	public void performeEtl(List<EtlDatabaseObject> etlObjects, Connection srcConn, Connection dstConn) throws DBException {
		try {
			perform(this.getEtlItemConfiguration(), etlObjects, null, LoadingType.PRINCIPAL, srcConn, dstConn);
		}
		catch (Exception e) {
			logWarn("Error ocurred on thread " + getProcessorId() + " On Records [" + getLimits() + "]... \n");
			logError(e.getLocalizedMessage());
			logError(this.getEngine().getEngineId() + ": " + e.getMessage());
			
			getTaskResultInfo().setFatalException(e);
		}
	}
	
	public EtlLoadHelper perform(EtlItemConfiguration etlItemConf, List<EtlDatabaseObject> etlObjects,
	        EtlDatabaseObject parentMigratedRec, LoadingType loadingType, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		for (EtlDatabaseObject record : etlObjects) {
			EtlDatabaseObject srcRecord = (EtlDatabaseObject) record;
			srcRecord.loadObjectIdData(etlItemConf.getSrcConf());
			
			for (DstConf mappingInfo : etlItemConf.getDstConf()) {
				if (mappingInfo.isDisabled()) {
					continue;
				}
				
				try {
					if (mappingInfo.checkIfSrcObjectCanBeLoaded(srcRecord)) {
						EtlDatabaseObject dstObject = mappingInfo.getTransformerInstance().transform(this, srcRecord,
						    mappingInfo, parentMigratedRec, TransformationType.PRINCIPAL, srcConn, dstConn);
						
						if (dstObject != null) {
							record.addDestinationRecord(dstObject);
							
							logTrace("dstRecord " + srcRecord + " transforming to " + dstObject);
						} else {
							dstObject = mappingInfo.createRecordInstance();
							dstObject.setEtlInfo(EtlInfo.initEtlRecord(this, record, dstObject));
							dstObject.getEtlInfo().setExceptionOnEtl(new EtlExceptionImpl(
							        "Not generated because one or more required related src object was not found!"));
							
							record.addDestinationRecord(dstObject);
							
							logTrace("The dstRecord " + srcRecord + " could not be transformed");
						}
					}
				}
				catch (EtlTransformationException e) {
					if (getRelatedEtlConfiguration().getGeneralBehaviourOnEtlException().log()
					        || getRelatedEtlConfiguration().getGeneralBehaviourOnEtlException().markRecordAsFailed()) {
						EtlDatabaseObject dstObject = mappingInfo.createRecordInstance();
						
						dstObject.setEtlInfo(EtlInfo.initEtlRecord(this, dstObject, dstObject));
						
						dstObject.getEtlInfo().setExceptionOnEtl(e);
						
						record.addDestinationRecord(dstObject);
					} else {
						throw e;
					}
				}
			}
		}
		
		logDebug("Initializing the loading of " + etlObjects.size() + " " + etlItemConf.getSrcConf().getFullTableName());
		
		EtlLoadHelper loadHelper = null;
		
		loadHelper = new EtlLoadHelper(this, etlObjects, loadingType, etlItemConf.ignoreNoDstIssue());
		
		if (loadHelper.hasDstConf()) {
			loadHelper.load(srcConn, dstConn);
			
			tryToPerfomeEtlOnChild(etlItemConf, loadHelper, srcConn, dstConn);
			
			logInfo("ETL OPERATION [" + etlItemConf.getConfigCode() + "] DONE ON " + etlObjects.size() + "' RECORDS");
		} else {
			logWarn("NO DST OBJECT WAS FOUND FOR ETL[" + etlItemConf.getConfigCode() + "] ON '" + etlObjects.size()
			        + "' RECORDS");
			
			tryToPerfomeEtlOnChild(etlItemConf, etlObjects, srcConn, dstConn);
		}
		
		return loadHelper;
	}
	
	private void tryToPerfomeEtlOnChild(EtlItemConfiguration itemConf, EtlLoadHelper loadHelper, Connection srcConn,
	        Connection dstConn) throws DBException {
		
		tryToPerfomeEtlOnChild(itemConf,
		    childItemConf -> loadHelper.getAllSuccedTransformedObjects(childItemConf.getRelatedParentDstConf()), srcConn,
		    dstConn);
	}
	
	private void tryToPerfomeEtlOnChild(EtlItemConfiguration itemConf,
	        Function<EtlItemConfiguration, List<EtlDatabaseObject>> objectsProvider, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		if (!itemConf.hasChildItemConf()) {
			return;
		}
		
		for (EtlItemConfiguration childItemConf : itemConf.getChildItemConf()) {
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
	
	private void performeEtlOnChildItem(EtlItemConfiguration itemConf, EtlDatabaseObject transformedParent,
	        Connection srcConn, Connection dstConn) throws DBException {
		
		EtlDatabaseObject srcObject = transformedParent.isSrcObject() ? transformedParent
		        : transformedParent.getEtlInfo().getRelatedSrcObject();
		
		List<EtlDatabaseObject> avaliableSrcObjects = transformedParent.isSrcObject() ? null
		        : transformedParent.getEtlInfo().getAvaliableSrcObjects();
		
		List<EtlDatabaseObject> etlObjects = itemConf.getSrcConf().searchRecords(this.getEngine(), srcObject,
		    avaliableSrcObjects, srcConn);
		
		if (!etlObjects.isEmpty()) {
			perform(itemConf, etlObjects, transformedParent.isDstObject() ? transformedParent : null, LoadingType.INNER,
			    srcConn, dstConn);
		}
	}
	
	@Override
	public TaskProcessor<EtlDatabaseObject> initReloadRecordsWithDefaultParentsTaskProcessor(IntervalExtremeRecord limits) {
		ReloadRecordsWithDefaultParentProcessor p = new ReloadRecordsWithDefaultParentProcessor(
		        (Engine<EtlDatabaseObject>) this.getEngine(), limits, false);
		
		p.setRelatedEtlProcessor(this);
		
		//return null;
		
		return p;
	}
}
