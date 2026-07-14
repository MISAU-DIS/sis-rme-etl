package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.Field;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Default field transformer responsible for retrieving the value of a field
 * directly from the configured source data objects.
 * <p>
 * This transformer resolves the destination field value by locating the
 * corresponding source object based on the configured data source alias and
 * retrieving the field value from that object.
 * </p>
 * <p>
 * The field name is resolved using two naming strategies:
 * <ul>
 * <li>snake_case</li>
 * <li>camelCase</li>
 * </ul>
 * The transformer first attempts to retrieve the field using the snake_case
 * representation. If that fails, it falls back to camelCase.
 * </p>
 * <p>
 * If no matching data source is found among the provided source objects, an
 * {@link EtlTransformationException} is thrown.
 * </p>
 * <p>
 * This transformer acts as the base mechanism for retrieving field values from
 * source data sets and is commonly used internally by other transformers.
 * </p>
 */
public class DefaultFieldTransformer extends AbstractEtlFieldTransformer {

	private static DefaultFieldTransformer INSTANCE;

	private DefaultFieldTransformer(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field) {
		super(parameters, relatedEtlTransformTarget, field);
	}

	public static DefaultFieldTransformer getInstance(List<Object> parameters,
			EtlTransformTarget relatedEtlTransformTarget, TransformableField field, Connection conn) {

		if (INSTANCE == null) {
			INSTANCE = new DefaultFieldTransformer(parameters, relatedEtlTransformTarget, field);
		}

		return INSTANCE;
	}

	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects,
			final TransformableField field, Connection srcConn, Connection dstConn)
			throws DBException, EtlTransformationException {

		List<EtlDatabaseObject> availableObjects = new ArrayList<>();

		if (additionalSrcObjects != null) {
			availableObjects.addAll(additionalSrcObjects);
		}

		availableObjects.addAll(retrievePreviousDestinationRecordsUsableAsDataSource(srcObject, transformedRecord));

		FieldTransformingInfo transformingInfo = tryResolveFromAvailableObjects(field, availableObjects);

		if (transformingInfo != null) {
			return transformingInfo;
		}

		return handleValueNotFound(srcObject, transformedRecord, field);
	}

	private List<EtlDatabaseObject> retrievePreviousDestinationRecordsUsableAsDataSource(EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord) {

		List<EtlDatabaseObject> result = new ArrayList<>();

		if (srcObject == null || srcObject.getDestinationObjects() == null
				|| srcObject.getDestinationObjects().isEmpty() || transformedRecord == null
				|| transformedRecord.getRelatedConfiguration() == null) {
			return result;
		}

		if (transformedRecord.getRelatedConfiguration() instanceof EtlTransformTarget
				&& ((EtlTransformTarget) transformedRecord.getRelatedConfiguration()).hasPreviousDataSourceTargets()) {
			EtlTransformTarget targetConf = (EtlTransformTarget) transformedRecord.getRelatedConfiguration();

			List<EtlTransformTarget> previousDataSourceTargets = targetConf.retrievePreviousDataSourceTargets();

			for (EtlDatabaseObject dstObj : srcObject.getDestinationObjects()) {

				if (dstObj == null || dstObj.getRelatedConfiguration() == null) {
					continue;
				}

				for (EtlTransformTarget previousTarget : previousDataSourceTargets) {

					if (dstObj.getRelatedConfiguration() == previousTarget
							|| dstObj.getRelatedConfiguration().getAlias().equals(previousTarget.getAlias()) || dstObj
									.getRelatedConfiguration().getObjectName().equals(previousTarget.getObjectName())) {

						result.add(dstObj);
						break;
					}
				}
			}
		}

		return result;
	}

	private FieldTransformingInfo tryResolveFromAvailableObjects(TransformableField field,
			List<EtlDatabaseObject> availableObjects) {

		if (availableObjects == null || availableObjects.isEmpty()) {
			return null;
		}

		for (EtlDatabaseObject obj : availableObjects) {

			if (obj == null || obj.getRelatedConfiguration() == null) {
				continue;
			}

			if (field.getDataSourceName() != null
					&& !field.getDataSourceName().equals(obj.getRelatedConfiguration().getAlias())) {
				continue;
			}

			FieldValueResolution resolved = tryResolveFieldValue(obj, field);

			if (resolved == null) {
				continue;
			}

			Field srcField = resolved.field;
			Object value = resolved.value;

			if (srcField.getTransformingInfo() != null && srcField.getTransformingInfo().getTransformedValue() != null
					&& srcField.getTransformingInfo().getTransformedValue().equals(value)) {
				return srcField.getTransformingInfo();
			}

			return new FieldTransformingInfo(field, value, (EtlDataSource) obj.getRelatedConfiguration());
		}

		return null;
	}

	private FieldValueResolution tryResolveFieldValue(EtlDatabaseObject obj, TransformableField field) {

		String originalName = field.getName();
		String snakeName = utilities.parsetoSnakeCase(originalName);
		String camelName = utilities.parsetoCamelCase(originalName);

		String[] candidateNames = { originalName, snakeName, camelName };

		for (String candidate : candidateNames) {
			try {
				Field srcField = obj.getField(candidate);
				Object value = obj.getFieldValue(candidate);

				return new FieldValueResolution(srcField, value);

			} catch (ForbiddenOperationException e) {
			}
		}

		return null;
	}

	private FieldTransformingInfo handleValueNotFound(EtlDatabaseObject srcObject, EtlDatabaseObject transformedRecord,
			TransformableField field) throws EtlTransformationException {

		String objectName = transformedRecord != null && transformedRecord.getRelatedConfiguration() != null
				? transformedRecord.getRelatedConfiguration().getObjectName()
				: null;

		String fieldName = objectName != null ? objectName + "(" + field.getDstField() + ")"
				: "'" + field.getDstField() + "'";

		String srcField = field.getDataSourceName() != null ? field.getDataSourceName() + "." : "";

		srcField += field.hasSrcField() ? field.getSrcField().split("@")[0] : "";

		String srcMessage = "the available source objects or previous destination records";

		if (!srcField.isEmpty())
			srcMessage = srcField;

		String msg = "The field " + fieldName + " could not be resolved from " + srcMessage
				+ ". The transformation would produce a null value, but this field is not configured to accept null values. "
				+ "Configure an explicit mapping, allow null values, or ensure that a previous destination record is available as a data source.";

		if (field.nullValueBehavior().markRecordAsFailed()) {
			throw new EtlTransformationException(msg, srcObject, ActionOnEtlIssue.LOG);
		}

		if (field.nullValueBehavior().abort()) {
			throw new EtlTransformationException(msg, srcObject, ActionOnEtlIssue.ABORT_PROCESS);
		}

		if (field.nullValueBehavior().ignore()) {
			return null;
		}

		return new FieldTransformingInfo(field, null, null);
	}

	private static class FieldValueResolution {

		private final Field field;

		private final Object value;

		private FieldValueResolution(Field field, Object value) {
			this.field = field;
			this.value = value;
		}
	}
}
