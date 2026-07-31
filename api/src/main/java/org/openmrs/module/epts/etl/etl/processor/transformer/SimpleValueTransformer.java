package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.InvalidDataSourceOnFieldDefifitionException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Field transformer that performs direct value assignment without applying any
 * additional transformation logic.
 * <p>
 * The transformer simply resolves any dynamic parameters present in the source
 * value and assigns the resulting value to the destination field.
 * </p>
 * <p>
 * This transformer is used as the default transformer when no explicit
 * transformer is defined for a field mapping.
 * </p>
 * <p>
 * Before returning the value, any dynamic parameters contained in the source
 * value are resolved using
 * {@link EtlFieldTransformer#tryToReplaceParametersOnSrcValue}.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * valueToTransform = "@patient_uuid"
 * </pre>
 * 
 * If {@code patient_uuid = "1234-5678"}, the transformer will assign
 * {@code "1234-5678"} directly to the destination field.
 */
public class SimpleValueTransformer extends AbstractEtlFieldTransformer {

	protected static final Map<String, SimpleValueTransformer> INSTANCES = new ConcurrentHashMap<>();

	private SimpleValueTransformer(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		super(parameters, relatedEtlTransformTarget, field, conn);
	}

	public static SimpleValueTransformer getInstance(List<Object> parameters,
			EtlTransformTarget relatedEtlTransformTarget, TransformableField field, Connection conn) {

		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);

		return INSTANCES.computeIfAbsent(key, k -> {
			try {
				return new SimpleValueTransformer(parameters, relatedEtlTransformTarget, field, conn);
			} catch (DBException e) {
				throw new EtlExceptionImpl(e);
			}
		});

	}

	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {

		traceTransformationInitialization(field);

		try {
			Object valueToTransform = null;
			FieldTransformingInfo transformingInfo = null;

			if (this.hasInput()) {
				transformingInfo = this.input.getTransformerInstance().transform(processor, srcObject,
						transformedRecord, additionalSrcObjects, this.input, srcConn, dstConn);

				try {
					valueToTransform = transformingInfo.getTransformedValue();
				} catch (Exception e) {
					throw e;
				}

			} else {
				valueToTransform = field.getValueToTransform();
			}
			if (valueToTransform == null) {
				return null;
			}

			Object result = EtlFieldTransformer
					.tryToReplaceParametersOnSrcValue(field.getTransformationTargetObject() != null
							? field.getTransformationTargetObject().getRelatedEtlConf()
							: null, additionalSrcObjects, valueToTransform);

			transformingInfo = new FieldTransformingInfo(field, result, null);

			transformingInfo
					.setLoadedWithDefaultValue(result != null && result.toString().equals(field.getValueToTransform()));
			return transformingInfo;
		} finally {
			traceTransformationFinalization(field);
		}
	}
}
