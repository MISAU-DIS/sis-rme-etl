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

public class FunctionTransformer extends AbstractEtlFieldTransformer {

	protected static final Map<String, FunctionTransformer> INSTANCES = new ConcurrentHashMap<>();

	private FunctionTransformerType type;

	private FunctionTransformer(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field, Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		super(parameters, relatedEtlTransformTarget, field, conn);
	}

	@Override
	protected void loadParameters(Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		super.loadParameters(conn);

		if (utilities.listHasElement(this.parameters)) {

			for (Object fieldData : this.parameters) {
				String[] mapping = fieldData.toString().split(":", 2);

				if (mapping.length != 2) {
					throw new EtlExceptionImpl(
							"Wrong format for conditional parameters within the tranformer " + getTransformerDsc()
									+ "\n" + "Each object param must be specified as paramName:paramValue");
				}

				String paramName = mapping[0];
				String paramValue = mapping[1];

				if (!utilities.stringHasValue(paramValue)) {
					throw new EtlExceptionImpl("The paramValue for parameter " + paramName
							+ " has no value on transformer:  " + getTransformerDsc());
				}

				if (paramName.equals("type")) {
					try {
						this.type = FunctionTransformerType.valueOf(paramValue);
					} catch (Exception e) {
						throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
								+ " on transformer:  " + getTransformerDsc());
					}
				}
			}
		}
	}

	public static FunctionTransformer getInstance(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field, Connection conn) {

		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);

		return INSTANCES.computeIfAbsent(key, k -> {
			try {
				return new FunctionTransformer(parameters, relatedEtlTransformTarget, field, conn);
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

			transformingInfo = this.input.getTransformerInstance().transform(processor, srcObject, transformedRecord,
					additionalSrcObjects, this.input, srcConn, dstConn);

			try {
				valueToTransform = transformingInfo.getTransformedValue();
			} catch (Exception e) {
				throw e;
			}

			Object result = evaluate(srcObject, valueToTransform);

			transformingInfo = new FieldTransformingInfo(field, result, null);

			transformingInfo
					.setLoadedWithDefaultValue(result != null && result.toString().equals(field.getValueToTransform()));
			return transformingInfo;
		} finally {
			traceTransformationFinalization(field);
		}
	}

	private Object evaluate(EtlDatabaseObject srcObject, Object readyValueToTransform) {
		return this.type.apply(readyValueToTransform);
	}
}
