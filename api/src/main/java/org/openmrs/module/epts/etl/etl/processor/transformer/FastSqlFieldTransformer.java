package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.EtlConfiguration;
import org.openmrs.module.epts.etl.conf.datasource.QueryDataSourceConfig;
import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EmptyTransformedValueException;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Field transformer that retrieves the destination field value by executing a
 * SQL query against the source database.
 * <p>
 * The SQL query is executed through a {@link QueryDataSourceConfig}, which
 * loads the result as a temporary data source during the ETL execution. The
 * query must return at least one column; only the value of the first column of
 * the first returned row will be used as the destination field value.
 * </p>
 * <p>
 * The SQL query is executed lazily: the {@link QueryDataSourceConfig} is
 * created and fully loaded only once, during the first invocation of the
 * transformer. Subsequent transformations reuse the previously loaded
 * configuration.
 * </p>
 * <p>
 * If the query returns no result or the resulting value is {@code null}, the
 * transformer behaves as follows:
 * <ul>
 * <li>If the destination field defines a default value, the transformer returns
 * {@code null} so that the default value can be applied later by the ETL
 * processing pipeline.</li>
 * <li>If no default value is defined, an {@link EmptyTransformedValueException}
 * is thrown.</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * FastSqlFieldTransformer("SELECT uuid()")
 * </pre>
 * 
 * In this example the transformer executes the SQL query and assigns the
 * resulting UUID value to the destination field.
 */
public class FastSqlFieldTransformer extends AbstractEtlFieldTransformer {

	private static final Map<String, FastSqlFieldTransformer> INSTANCES = new ConcurrentHashMap<>();

	private static final Object LOCK = new Object();

	private String sqlQuery;

	private String name;

	private ActionOnEtlIssue onMultipleSrcObjectsFound;

	private volatile QueryDataSourceConfig dataSourceConfig;

	public FastSqlFieldTransformer(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field, Connection conn) {

		super(parameters, relatedEtlTransformTarget, field);

		this.retrieveSqlQueryFromParameters(parameters);

		try {
			this.tryToLoadDumpScriptContentToFieldAndValidate("sqlQuery",
					relatedEtlTransformTarget != null
							? relatedEtlTransformTarget.retrieveAllAvailableTemplateParameters()
							: null,
					conn);
		} catch (DBException e) {
			throw new EtlConfException(e);
		}
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public static FastSqlFieldTransformer getInstance(List<Object> parameters,
			EtlTransformTarget relatedEtlTransformTarget, TransformableField field, Connection conn) {

		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);

		return INSTANCES.computeIfAbsent(key,
				k -> new FastSqlFieldTransformer(parameters, relatedEtlTransformTarget, field, conn));
	}

	private void retrieveSqlQueryFromParameters(List<Object> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			throw new EtlExceptionImpl("A FastSqlFieldTransformer needs a sqlQuery as parameter.\n"
					+ "ex: org.openmrs.module.epts.etl.etl.processor.transformer.FastSqlFieldTransformer(select uuid())");
		}

		if (parameters.size() == 1) {
			this.sqlQuery = parameters.get(0).toString();

			return;
		}

		for (Object fieldData : parameters) {
			String[] mapping = fieldData.toString().split(":", 2);

			if (mapping.length != 2) {
				throw new EtlExceptionImpl("Wrong format for conditional parameters within the tranformer "
						+ getTransformerDsc() + "\n" + "Each object param must be specified as paramName:paramValue");
			}

			String paramName = mapping[0];
			String paramValue = mapping[1];

			if (!utilities.stringHasValue(paramValue)) {
				throw new EtlExceptionImpl("The paramValue for parameter " + paramName
						+ " has no value on transformer:  " + getTransformerDsc());
			}

			if (paramName.equals("query")) {
				this.sqlQuery = paramValue;
			} else if (paramName.equals("name")) {
				this.name = paramValue;
			} else if (paramName.equals("onMultipleSrcObjectsFound")) {
				try {
					this.onMultipleSrcObjectsFound = ActionOnEtlIssue.valueOf(paramValue);
				} catch (Exception e) {
					throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
							+ " on transformer:  " + getTransformerDsc());
				}

			} else {
				throw new ForbiddenOperationException(
						"Unsupported parameter " + paramName + " on transformer:  " + getTransformerDsc());
			}
		}
	}

	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {

		if (this.dataSourceConfig == null) {
			synchronized (LOCK) {
				if (this.dataSourceConfig == null) {

					EtlConfiguration relatedEtlConfiguration = field.getTransformationTargetObject()
							.getSrcConf() != null
									? field.getTransformationTargetObject().getSrcConf().getRelatedEtlConf()
									: field.getTransformationTargetObject().getRelatedEtlConf();

					QueryDataSourceConfig conf = new QueryDataSourceConfig(this.sqlQuery,
							field.getTransformationTargetObject().getSrcConf());

					conf.setRelatedEtlConf(relatedEtlConfiguration);

					conf.setName(this.name);

					conf.setOnMultipleSrcObjectsFound(this.onMultipleSrcObjectsFound);

					conf.fullLoad(hasOverrideConnection() ? getOverrideConnection() : srcConn);

					this.dataSourceConfig = conf;
				}
			}
		}

		EtlDatabaseObject srcObj = this.dataSourceConfig.loadRelatedSrcObject(processor, srcObject, transformedRecord,
				additionalSrcObjects, hasOverrideConnection() ? getOverrideConnection() : srcConn);

		Object dstValue = null;

		if (srcObj != null && srcObj.getFields() != null && !srcObj.getFields().isEmpty()) {

			dstValue = srcObj.getFields().get(0).getValue();
		}

		if (dstValue != null) {
			return new FieldTransformingInfo(field, dstValue, null);
		}

		if (field.getDefaultValue() == null) {
			if (this.getOnNullTransformedvalue() != null && this.getOnNullTransformedvalue().setToNull()) {
				return new FieldTransformingInfo(field, null, null);
			}

			EtlDatabaseObject obj = utilities.listHasElement(additionalSrcObjects) ? additionalSrcObjects.get(0) : null;

			throw new EmptyTransformedValueException(obj,
					field.getSrcField() != null ? field.getSrcField() : field.getDstField(), this,
					ActionOnEtlIssue.ABORT_PROCESS);
		}

		return null;
	}

}
