package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Field transformer that explicitly returns {@code null} as the transformed
 * value.
 *
 * <p>
 * This transformer is used when a destination field must be intentionally
 * mapped to {@code null}. It does not read values from source objects, does not
 * execute any lookup, and does not require parameters.
 * </p>
 *
 * <p>
 * This transformer is useful when the configuration must explicitly communicate
 * that a destination field should be cleared or left empty, instead of relying
 * on missing mappings or implicit default behavior.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * {
 *    "dstField":"date_stopped",
 *    "transformer":"NULL_VALUE_TRANSFORMER()"
 * }
 * </pre>
 *
 * <p>
 * Note that the field must be configured to accept null values. If the
 * destination field does not allow null values, the ETL engine may apply the
 * configured null-handling behavior, such as marking the record as failed or
 * aborting the process.
 * </p>
 */
public class NullValueTransformer extends AbstractEtlFieldTransformer {

	private static final Map<String, NullValueTransformer> INSTANCES = new ConcurrentHashMap<>();

	private NullValueTransformer(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field) {

		super(parameters, relatedEtlTransformTarget, field);
	}

	public static NullValueTransformer getInstance(List<Object> parameters,
			EtlTransformTarget relatedEtlTransformTarget, TransformableField field, Connection conn) {

		if (utilities.listHasElement(parameters)) {
			throw new EtlConfException("No parameters are allowed for " + NullValueTransformer.class.getName());
		}

		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);

		return INSTANCES.computeIfAbsent(key,
				k -> new NullValueTransformer(parameters, relatedEtlTransformTarget, field));
	}

	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {

		traceTransformationInitialization(field);
		try {
			return new FieldTransformingInfo(field, null, null);
		} finally {
			traceTransformationFinalization(field);
		}
	}
}
