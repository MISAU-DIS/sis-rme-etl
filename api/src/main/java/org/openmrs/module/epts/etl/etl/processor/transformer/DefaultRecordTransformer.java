package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openmrs.module.epts.etl.conf.datasource.PrimaryDataSourceConfiguration;
import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.connector.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldsMappingException;
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
	public EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDestinationConfiguration dstConf, EtlDatabaseObject migratedDstParent,
			TransformationType transformationType, ConnectorRecordReader<?> reader, ConnectorRecordWriter<?> writer)
			throws DBException, EtlTransformationException {

		Set<EtlDatabaseObject> srcObjects = collectSourceObjects(processor, srcObject, null, null, migratedDstParent,
				dstConf, transformationType, reader, writer);

		try {

			if (srcObjects == null || srcObjects.isEmpty()) {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}

		return transform(processor, srcObject, srcObjects, dstConf, migratedDstParent, transformationType, reader,
				writer);
	}

	@Override
	public EtlDatabaseObject transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			Set<EtlDatabaseObject> collectedSrcObjects, EtlDestinationConfiguration dstConf,
			EtlDatabaseObject migratedDstParent, TransformationType transformationType, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer) throws DBException, EtlTransformationException {
		Connection dstConn = unwrapConnection(writer);

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

		if (utilities.setHasElement(collectedSrcObjects)) {
			transformationRecords.addAll(collectedSrcObjects);
		}

		collectToSrcObjects(transformedRec, transformationRecords);

		transformedRec.getEtlInfo().setTransformationSrcObject(transformationRecords);

		applyFieldTransformations(processor, srcObject, transformedRec, transformationRecords, reader, writer, dstConn);

		resolvePrimaryKeyAndParent(processor, srcObject, transformedRec, migratedDstParent, transformationRecords,
				reader, writer, dstConn);

		if (transformationType.onDemand()) {
			transformedRec.setUuid(UUID.randomUUID().toString());
		}

		processor.logTrace("Record " + srcObject + " transformed to " + transformedRec);

		return transformedRec;
	}

	private void resolvePrimaryKeyAndParent(EtlProcessor processor, EtlDatabaseObject srcRecord,
			EtlDatabaseObject transformedRec, EtlDatabaseObject migratedDstParent,
			Set<EtlDatabaseObject> availableSrcObjs, ConnectorRecordReader<?> reader, ConnectorRecordWriter<?> writer,
			Connection dstConn) throws EtlTransformationException, DBException {

		EtlDestinationConfiguration dstConf = (EtlDestinationConfiguration) transformedRec.getRelatedConfiguration();

		transformedRec.loadObjectIdData(dstConf);
		transformedRec.loadUniqueKeyValues();

		Field pkField = resolvePrimaryKeyField(transformedRec);

		FieldsMapping pkMapping = null;

		try {
			pkMapping = dstConf.getMappingUsingDstField(pkField.getName());

		} catch (FieldsMappingException e) {
			pkMapping = FieldsMapping.fastCreate(dstConf, pkField.getName(), dstConn);

			pkMapping.tryToLoadTransformer(dstConf);
		}

		List<EtlDatabaseObject> availableSrcList = availableSrcObjs != null ? new ArrayList<>(availableSrcObjs)
				: new ArrayList<>();

		resolvePrimaryKey(processor, srcRecord, transformedRec, dstConf, pkField, pkMapping, migratedDstParent,
				availableSrcList, reader, writer, dstConn);

		if (migratedDstParent != null) {
			resolveParentForeignKey(transformedRec, dstConf, migratedDstParent, dstConn);
		}
	}

	private Field resolvePrimaryKeyField(EtlDatabaseObject transformedRec) throws EtlTransformationException {

		if (transformedRec.getObjectId() == null || transformedRec.getObjectId().asSimpleKey() == null) {
			throw new EtlTransformationException(
					"Unable to resolve primary key. ObjectId is not loaded for " + transformedRec, null,
					ActionOnEtlIssue.ABORT_PROCESS);
		}

		String pkName = transformedRec.getObjectId().asSimpleKey().getName();

		Field pkField = transformedRec.getField(pkName);

		if (pkField == null) {
			throw new EtlTransformationException(
					"Primary key field '" + pkName + "' not found on transformed record " + transformedRec, null,
					ActionOnEtlIssue.ABORT_PROCESS);
		}

		return pkField;
	}

	private boolean resolvePrimaryKey(EtlProcessor processor, EtlDatabaseObject srcRecord,
			EtlDatabaseObject transformedRec, EtlDestinationConfiguration dstConf, Field pkField,
			FieldsMapping pkMapping, EtlDatabaseObject migratedDstParent, List<EtlDatabaseObject> availableSrcObjs,
			ConnectorRecordReader<?> reader, ConnectorRecordWriter<?> writer, Connection dstConn)
			throws EtlTransformationException, DBException {

		if (!dstConf.useSharedPKKey()) {
			return resolveNonSharedPrimaryKey(processor, srcRecord, transformedRec, dstConf, pkField, pkMapping,
					availableSrcObjs, reader, writer, dstConn);
		}

		return resolveSharedPrimaryKey(processor, srcRecord, transformedRec, dstConf, pkField, pkMapping,
				migratedDstParent, availableSrcObjs, reader, writer, dstConn);
	}

	private boolean resolveNonSharedPrimaryKey(EtlProcessor processor, EtlDatabaseObject srcRecord,
			EtlDatabaseObject transformedRec, EtlDestinationConfiguration dstConf, Field pkField,
			FieldsMapping pkMapping, List<EtlDatabaseObject> availableSrcObjs, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer, Connection dstConn) throws EtlTransformationException, DBException {

		if (!pkMapping.useDefaultTransformer()) {
			FieldTransformingInfo info = pkMapping.transform(processor, srcRecord, transformedRec, availableSrcObjs,
					reader, writer);

			if (info.getTransformedValue() != null) {
				transformedRec.setFieldValue(pkField.getName(), info.getTransformedValue());
				pkField.setTransformingInfo(info);

				transformedRec.loadObjectIdData();

				return true;
			}
		}

		if (dstConf.useManualGeneratedObjectId() && !dstConf.getRelatedEtlConf().isDoNotTransformsPrimaryKeys()) {
			Object nextId = dstConf.retrieveNextRecordId(dstConn);

			transformedRec.getObjectId().asSimpleKey().setValue(nextId);
			transformedRec.setFieldValue(pkField.getName(), nextId);

			transformedRec.loadObjectIdData();

			return true;
		}

		return true;
	}

	private boolean resolveSharedPrimaryKey(EtlProcessor processor, EtlDatabaseObject srcRecord,
			EtlDatabaseObject transformedRec, EtlDestinationConfiguration dstConf, Field pkField,
			FieldsMapping pkMapping, EtlDatabaseObject migratedDstParent, List<EtlDatabaseObject> availableSrcObjs,
			ConnectorRecordReader<?> reader, ConnectorRecordWriter<?> writer, Connection dstConn)
			throws EtlTransformationException, DBException {

		if (migratedDstParent != null && pkMapping.useDefaultTransformer()) {
			return false;
		}

		FieldTransformingInfo info;

		if (sourceAndDestinationHaveSameObjectName(srcRecord, transformedRec)) {

			srcRecord.loadObjectIdData();

			Object srcPkValue = srcRecord.getObjectId().asSimpleKey().getValue();

			pkField.setValue(srcPkValue);
			transformedRec.setFieldValue(pkField.getName(), srcPkValue);

			info = new FieldTransformingInfo(pkMapping, srcPkValue,
					(EtlDataSource) srcRecord.getRelatedConfiguration());

		} else {
			info = pkMapping.transform(processor, srcRecord, transformedRec, availableSrcObjs, reader, writer);

			transformedRec.setFieldValue(pkField.getName(), info.getTransformedValue());
		}

		pkField.setTransformingInfo(info);

		return true;
	}

	private boolean sourceAndDestinationHaveSameObjectName(EtlDatabaseObject srcRecord,
			EtlDatabaseObject transformedRec) {

		if (srcRecord == null || transformedRec == null || srcRecord.getRelatedConfiguration() == null
				|| transformedRec.getRelatedConfiguration() == null) {
			return false;
		}

		return srcRecord.getRelatedConfiguration().getObjectName()
				.equals(transformedRec.getRelatedConfiguration().getObjectName());
	}

	private void resolveParentForeignKey(EtlDatabaseObject transformedRec, EtlDestinationConfiguration dstConf,
			EtlDatabaseObject migratedDstParent, Connection conn) throws EtlTransformationException {

		if (dstConf.hasParentRefInfo()) {

			for (ParentTable refInfo : dstConf.getParentRefInfo()) {

				if (!refInfo.getTableName().equals(migratedDstParent.getRelatedConfiguration().getObjectName())) {
					continue;
				}

				Field fkField = transformedRec.getField(refInfo);

				if (fkField == null) {
					throw new EtlTransformationException("Foreign key field for parent '" + refInfo.getTableName()
							+ "' not found on transformed record.", null, ActionOnEtlIssue.ABORT_PROCESS);
				}

				FieldsMapping fkMapping = null;

				try {
					fkMapping = dstConf.getMappingUsingDstField(fkField.getName());

					if (fkMapping == null) {
						throw new EtlTransformationException(
								"No mapping found for foreign key field '" + fkField.getName() + "'.", null,
								ActionOnEtlIssue.ABORT_PROCESS);
					}

				} catch (FieldsMappingException e) {

					try {
						fkMapping = FieldsMapping.fastCreate(dstConf, fkField.getName(),
								migratedDstParent.getRelatedConfiguration().getAlias(),
								refInfo.getParentColumnOnSimpleMapping(), conn);
					} catch (DBException e1) {
						throw new EtlExceptionImpl(e);
					}
				}

				Object currentValue = transformedRec.getFieldValue(fkField.getName());

				if (currentValue != null && !fkMapping.overridable()) {
					return;
				}

				Object parentPkValue = migratedDstParent.getObjectId().asSimpleNumericValue();

				FieldTransformingInfo info = new FieldTransformingInfo(fkMapping, parentPkValue,
						(EtlDataSource) migratedDstParent.getRelatedConfiguration());

				fkField.setTransformingInfo(info);

				transformedRec.setFieldValue(fkField.getName(), info.getTransformedValue());

				return;
			}
		}
	}

	public Set<EtlDatabaseObject> collectSourceObjects(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject srcObjectExpansion, EtlDatabaseObject dstObject, EtlDatabaseObject migratedDstParent,
			EtlDestinationConfiguration dstConf, TransformationType transformationType, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer) throws DBException {

		Set<EtlDatabaseObject> result = new LinkedHashSet<>();

		collectToSrcObjects(srcObject, result);
		collectToSrcObjects(srcObjectExpansion, result);
		collectToSrcObjects(dstObject, result);
		collectToSrcObjects(migratedDstParent, result);
		collectToSrcObjectsFromExtraDataSources(processor, result, srcObject, dstObject, transformationType, writer);

		return result;

	}

	private void applyFieldTransformations(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRec, Set<EtlDatabaseObject> srcObjects, ConnectorRecordReader<?> reader,
			ConnectorRecordWriter<?> writer, Connection dstConn) throws DBException {

		EtlDestinationConfiguration dstConf = (EtlDestinationConfiguration) transformedRec.getRelatedConfiguration();

		for (FieldsMapping fieldsMapping : dstConf.getAllMapping()) {

			if (!fieldsMapping.getDstField().equals(dstConf.getPrimaryKey().asSimpleKey().getName())) {

				if (fieldsMapping.shouldBeProcessed(srcObject, srcObjects, reader, writer)) {
					dstConf.getRelatedEtlConf().trace("Transforming field " + fieldsMapping);

					fieldsMapping.getTransformerInstance().performFieldTransformation(processor, srcObject,
							transformedRec, new ArrayList<>(srcObjects), fieldsMapping, reader, writer);

					dstConf.getRelatedEtlConf().trace("Field " + fieldsMapping + " Transformed to: "
							+ transformedRec.getFieldValue(fieldsMapping.getDstField()));
				}

			}
		}
	}

	private Connection unwrapConnection(ConnectorRecordWriter<?> writer) {

		if (writer == null) {
			return null;
		}

		try {
			return writer.unwrap(Connection.class);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private void collectToSrcObjects(EtlDatabaseObject toCollectFrom, Set<EtlDatabaseObject> srcObjects) {

		if (toCollectFrom != null && toCollectFrom.collactable()) {
			Set<EtlDatabaseObject> avaliable = toCollectFrom.collectAllAvaliableSrcObjects();

			if (utilities.setHasElement(avaliable)) {
				srcObjects.addAll(avaliable);
			}
		}
	}

	private void collectToSrcObjectsFromExtraDataSources(EtlProcessor processor, Set<EtlDatabaseObject> srcObjects,
			EtlDatabaseObject srcObject, EtlDatabaseObject dstObject, TransformationType transformationType,
			ConnectorRecordWriter<?> writer) throws DBException {

		if (srcObject.getRelatedConfiguration() instanceof PrimaryDataSourceConfiguration) {
			PrimaryDataSourceConfiguration srcConf = (PrimaryDataSourceConfiguration) srcObject
					.getRelatedConfiguration();

			if (!srcConf.doNotUseAsDatasource()) {

				for (EtlAdditionalDataSource ds : srcConf.getParentSource().getAvailableAdditionalDataSources()) {
					ConnectorRecordReader<?> reader = processor.getRecordReader(ds);
					List<EtlDatabaseObject> avaliableObjects = srcObjects.stream()
							.collect(java.util.stream.Collectors.toList());

					if (!ds.shouldBeProcessed(srcObject, srcObjects, reader, writer)) {
						continue;
					}

					EtlDatabaseObject relatedSrcObject = ds.loadRelatedSrcObject(processor, srcObject, dstObject,
							avaliableObjects, reader);

					if (relatedSrcObject == null) {

						/*
						 * If the transformation is not principal, then mean the record is being
						 * transformed as parent of other record. So we force the tranformation
						 */
						if (ds.isRequired() && transformationType.isPrincipal()) {
							throw new MissingRequiredTransformationObject(srcObject, ds,
									srcConf.getParentSource().getMissingRequiredObjectBehavior());
						} else if (!transformationType.isPrincipal()) {
							relatedSrcObject = ds.newInstance();
							relatedSrcObject.setRelatedConfiguration(ds);
						}
					}

					collectToSrcObjects(relatedSrcObject, srcObjects);
				}
			}
		}
	}

}
