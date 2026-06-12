package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.conf.datasource.SrcConf;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.MissingRequiredTransformationObject;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.EtlInfo;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class DefaultRecordTransformer implements EtlRecordTransformer {
	
	private static CommonUtilities utilities = CommonUtilities.getInstance();
	
	private static final DefaultRecordTransformer INSTANCE = new DefaultRecordTransformer();
	
	private DefaultRecordTransformer() {
	}
	
	public static DefaultRecordTransformer getInstance() {
		return INSTANCE;
	}
	
	@Override
	public EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject, DstConf dstConf,
	        EtlDatabaseObject migratedDstParent, TransformationType transformationType, Connection srcConn,
	        Connection dstConn) throws DBException, EtlTransformationException {
		
		List<EtlDatabaseObject> srcObjects = collectSourceObjects(processor, srcObject, null, migratedDstParent, dstConf,
		    transformationType, srcConn);
		
		try {
			if (srcObjects == null || srcObjects.isEmpty()) {
				return null;
			}
		}
		catch (Exception e) {
			throw e;
		}
		
		return transform(processor, srcObject, srcObjects, dstConf, migratedDstParent, transformationType, srcConn, dstConn);
	}
	
	@Override
	public EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject,
	        List<EtlDatabaseObject> collectedSrcObjects, DstConf dstConf, EtlDatabaseObject migratedDstParent,
	        TransformationType transformationType, Connection srcConn, Connection dstConn)
	        throws DBException, EtlTransformationException {
		
		if (dstConf.isDisabled()) {
			throw new EtlExceptionImpl("Attempt to tranform to disabled dstConf");
		}
		
		if (srcObject == null) {
			throw new EtlTransformationException("SrcObject cannot be null", null, ActionOnEtlIssue.ABORT_PROCESS);
		}
		
		processor.logTrace("Transforming dstRecord " + srcObject);
		
		EtlDatabaseObject transformedRec = dstConf.createRecordInstance();
		
		transformedRec.setEtlInfo(EtlInfo.initEtlRecord(processor, srcObject, transformedRec));
		
		Set<EtlDatabaseObject> transformationRecords = new LinkedHashSet<>();
		
		if (utilities.listHasElement(collectedSrcObjects)) {
			transformationRecords.addAll(collectedSrcObjects);
		}
		
		collectToSrcObjects(transformedRec, transformationRecords);
		
		transformedRec.getEtlInfo().setTransformationSrcObject(transformationRecords);
		
		applyFieldTransformations(processor, srcObject, transformedRec, transformationRecords, srcConn, dstConn);
		
		resolvePrimaryKeyAndParent(processor, srcObject, transformedRec, migratedDstParent, transformationRecords, srcConn,
		    dstConn);
		
		if (transformationType.onDemand()) {
			transformedRec.setUuid(UUID.randomUUID().toString());
		}
		
		processor.logTrace("Record " + srcObject + " transformed to " + transformedRec);
		
		return transformedRec;
	}
	
	private void resolvePrimaryKeyAndParent(EtlProcessor processor, EtlDatabaseObject srcRecord,
	        EtlDatabaseObject transformedRec, EtlDatabaseObject migratedDstParent, Set<EtlDatabaseObject> avaliableSrcObjs,
	        Connection srcConn, Connection dstConn) throws EtlTransformationException, DBException {
		
		DstConf dstConf = (DstConf) transformedRec.getRelatedConfiguration();
		
		transformedRec.loadObjectIdData(dstConf);
		transformedRec.loadUniqueKeyValues();
		
		if (!dstConf.useSharedPKKey()) {
			if (dstConf.useManualGeneratedObjectId() && !dstConf.getRelatedEtlConf().isDoNotTransformsPrimaryKeys()) {
				transformedRec.getObjectId().asSimpleKey().setValue(dstConf.retriveNextRecordId(processor));
			}
		} else {
			Field pk = transformedRec.getField(transformedRec.getObjectId().asSimpleKey().getName());
			
			FieldsMapping pkMapping = dstConf.getMappingUsingDstField(pk.getName());
			FieldTransformingInfo fi = null;
			
			if (migratedDstParent == null) {
				
				//We are assumning that the src and dst structure are the same
				if (srcRecord.getRelatedConfiguration().getObjectName()
				        .equals(transformedRec.getRelatedConfiguration().getObjectName())) {
					
					srcRecord.loadObjectIdData();
					
					pk.setValue(srcRecord.getObjectId().asSimpleKey().getValue());
					
					fi = new FieldTransformingInfo(pkMapping, pk.getValue(),
					        (EtlDataSource) srcRecord.getRelatedConfiguration());
					
				} else {
					fi = pkMapping.transform(processor, srcRecord, transformedRec, new ArrayList<>(avaliableSrcObjs),
					    srcConn, dstConn);
					
					transformedRec.setFieldValue(pk.getName(), fi.getTransformedValue());
				}
				
				pk.setTransformingInfo(fi);
			}
		}
		
		//Force the related child field to be mapped to the dstPK
		if (migratedDstParent != null) {
			for (ParentTable refInfo : dstConf.getParentRefInfo()) {
				if (refInfo.getTableName().equals(migratedDstParent.getRelatedConfiguration().getObjectName())) {
					Field fk = transformedRec.getField(refInfo);
					
					FieldsMapping definedMapping = dstConf.getMappingUsingDstField(fk.getName());
					
					if (transformedRec.getFieldValue(fk.getName()) == null || definedMapping.overridable()) {
						FieldsMapping f = ((DstConf) transformedRec.getRelatedConfiguration())
						        .getMappingUsingDstField(fk.getName());
						
						fk.setTransformingInfo(
						    new FieldTransformingInfo(f, migratedDstParent.getObjectId().asSimpleNumericValue(),
						            (EtlDataSource) migratedDstParent.getRelatedConfiguration()));
						
						transformedRec.setFieldValue(fk.getName(), fk.getTransformingInfo().getTransformedValue());
						
						break;
					}
				}
			}
		}
	}
	
	public List<EtlDatabaseObject> collectSourceObjects(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject dstObject, EtlDatabaseObject migratedDstParent, DstConf dstConf,
	        TransformationType transformationType, Connection srcConn) throws DBException {
		
		try {
			Set<EtlDatabaseObject> result = new LinkedHashSet<>();
			
			collectToSrcObjects(srcObject, result);
			collectToSrcObjects(dstObject, result);
			collectToSrcObjectsFromExtraDataSources(processor, result, srcObject, dstObject, transformationType, srcConn);
			
			return new ArrayList<>(result);
		}
		catch (MissingRequiredTransformationObject e) {
			return null;
		}
	}
	
	private void applyFieldTransformations(EtlProcessor processor, EtlDatabaseObject srcObject,
	        EtlDatabaseObject transformedRec, Set<EtlDatabaseObject> srcObjects, Connection srcConn, Connection dstConn)
	        throws DBException {
		
		DstConf dstConf = (DstConf) transformedRec.getRelatedConfiguration();
		
		for (FieldsMapping fieldsMapping : dstConf.getAllMapping()) {
			if (!fieldsMapping.getName().equals(dstConf.getPrimaryKey().asSimpleKey().getName())) {
				dstConf.getRelatedEtlConf().logTrace("Transforming field " + fieldsMapping);
				
				fieldsMapping.getTransformerInstance().performFieldTransformation(processor, srcObject, transformedRec,
				    new ArrayList<>(srcObjects), fieldsMapping, srcConn, dstConn);
				
				dstConf.getRelatedEtlConf().logTrace("Field " + fieldsMapping + " Transformed to: "
				        + transformedRec.getFieldValue(fieldsMapping.getDstField()));
				
			}
		}
	}
	
	private void collectToSrcObjects(EtlDatabaseObject toCollectFrom, Set<EtlDatabaseObject> srcObjects) {
		if (toCollectFrom != null) {
			Set<EtlDatabaseObject> avaliable = toCollectFrom.collectAllAvaliableSrcObjects();
			
			if (utilities.setHasElement(avaliable)) {
				srcObjects.addAll(avaliable);
			}
		}
	}
	
	private void collectToSrcObjectsFromExtraDataSources(EtlProcessor processor, Set<EtlDatabaseObject> srcObjects,
	        EtlDatabaseObject srcObject, EtlDatabaseObject dstObject, TransformationType transformationType,
	        Connection srcConn) throws DBException {
		
		if (srcObject.getRelatedConfiguration() instanceof SrcConf) {
			SrcConf srcConf = (SrcConf) srcObject.getRelatedConfiguration();
			
			for (EtlAdditionalDataSource mappingInfo : srcConf.getAvaliableExtraDataSource()) {
				
				List<EtlDatabaseObject> avaliableObjects = mappingInfo.allowMultipleSrcObjectsForLoading()
				        ? srcObjects.stream().toList()
				        : utilities.parseToList(srcObject);
				
				EtlDatabaseObject relatedSrcObject = mappingInfo.loadRelatedSrcObject(processor, srcObject, dstObject,
				    avaliableObjects, srcConn);
				
				if (relatedSrcObject == null) {
					
					/*
					 * If the transformation is not principal, then mean the record is being transformed as parent of other record. So we force the tranformation
					 */
					if (mappingInfo.isRequired() && transformationType.isPrincipal()) {
						throw new MissingRequiredTransformationObject();
					} else if (!transformationType.isPrincipal()) {
						relatedSrcObject = mappingInfo.newInstance();
						relatedSrcObject.setRelatedConfiguration(mappingInfo);
					}
				}
				
				collectToSrcObjects(relatedSrcObject, srcObjects);
			}
		}
	}
}
