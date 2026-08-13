package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.InvalidDataSourceOnFieldDefifitionException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

/**
 * Field transformer that evaluates string expressions using standard
 * {@link String} methods.
 *
 * <p>
 * The transformer evaluates a string expression supplied through the required
 * <b>input</b> parameter. Expressions may contain:
 * </p>
 * <ul>
 * <li>constant string values;</li>
 * <li>fields from available data sources;</li>
 * <li>dynamic ETL parameters;</li>
 * <li>nested field transformers;</li>
 * <li>chained {@link String} method invocations.</li>
 * </ul>
 *
 * <p>
 * Transformer syntax:
 * </p>
 *
 * <pre>
 * STRING_TRANSFORMER(
 *     input:expression,
 *     null_operand_behavior:behavior
 * )
 * </pre>
 *
 * <p>
 * The expression supplied in the <b>input</b> parameter follows the format:
 * </p>
 *
 * <pre>
 * (value)
 *     .method1(arg1, arg2, ...)
 *     .method2(...)
 *     .methodN(...)
 * </pre>
 *
 * <p>
 * where:
 * </p>
 * <ul>
 * <li><b>value</b> is the initial string value;</li>
 * <li><b>methodX</b> is a method defined in {@link String};</li>
 * <li><b>argX</b> represents optional method arguments.</li>
 * </ul>
 *
 * <p>
 * Before invoking each method, the transformer:
 * </p>
 * <ul>
 * <li>resolves fields from the available data sources;</li>
 * <li>resolves dynamic ETL parameters;</li>
 * <li>executes nested transformers;</li>
 * <li>converts method arguments to the parameter types expected by the target
 * {@link String} method.</li>
 * </ul>
 *
 * <p>
 * Expressions are evaluated from left to right, where the result of each method
 * invocation becomes the input of the next method in the chain.
 * </p>
 *
 * <p>
 * The optional <b>null_operand_behavior</b> parameter controls how the
 * transformer behaves when the initial value or any method argument evaluates
 * to {@code null}. Supported behaviors are:
 * </p>
 * <ul>
 * <li><b>ABORT_PROCESS</b> – throws an {@link EtlTransformationException};</li>
 * <li><b>USE_EMPTY_STRING</b> – replaces the null operand with an empty string
 * and continues evaluating the expression;</li>
 * <li><b>RETURN_NULL</b> – immediately returns {@code null}. The returned value
 * is then handled according to the mapping's configured
 * <code>nullValueBehavior</code>.</li>
 * </ul>
 *
 * <p>
 * Example expressions:
 * </p>
 *
 * <pre>
 * STRING_TRANSFORMER(
 *     input:(John).toUpperCase()
 * )
 *
 * STRING_TRANSFORMER(
 *     input:(hello world).substring(0,5).toUpperCase()
 * )
 *
 * STRING_TRANSFORMER(
 *     input:(person_name_src_ds.given_name)
 *         .concat(FUNCTION_TRANSFORMER(type:SPACE))
 *         .concat(person_name_src_ds.family_name)
 * )
 * </pre>
 *
 * <p>
 * Method invocation is performed dynamically using Java reflection.
 * </p>
 *
 * <p>
 * If the expression is invalid, a method cannot be resolved, an argument cannot
 * be converted to the required type, or the configured
 * <b>null_operand_behavior</b> requires the transformation to fail, an
 * {@link EtlTransformationException} is raised.
 * </p>
 */
public class StringTranformer extends AbstractEtlFieldTransformer {

	protected static final Map<String, StringTranformer> INSTANCES = new ConcurrentHashMap<>();

	private ActionOnEtlIssue nullOperandBehavior;

	private StringTranformerElements transformerElements;

	public StringTranformer(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field, Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		super(parameters, relatedEtlTransformTarget, field, conn);

		loadTransformerElements(conn);
	}

	public ActionOnEtlIssue getNullOperandBehavior() {
		return nullOperandBehavior;
	}

	@Override
	protected void loadParameters(Connection conn)
			throws InvalidDataSourceOnFieldDefifitionException, FieldAvaliableInMultipleDataSources, DBException {

		if (utilities.listHasElement(this.parameters)) {
			if (this.parameters.size() == 1) {
				this.setInputExpression(this.parameters.get(0).toString());
			} else {
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

					if (paramName.equals("null_operand_behavior")) {
						try {
							this.nullOperandBehavior = ActionOnEtlIssue.valueOf(paramValue);
						} catch (Exception e) {
							throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
									+ " on transformer:  " + getTransformerDsc());
						}
					} else if (paramName.equals("input")) {
						try {
							this.setInputExpression(paramValue);
						} catch (Exception e) {
							throw new EtlExceptionImpl("Unsupported value paramValue for parameter " + paramName
									+ " on transformer:  " + getTransformerDsc());
						}
					}
				}
			}
		}
	}

	private void loadTransformerElements(Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		String expr = this.getInputExpression();

		if (expr == null || expr.isBlank() || !expr.startsWith("(")) {
			throw new EtlExceptionImpl("Invalid input expression: " + expr);
		}

		int firstClose = StringTranformerElements.findMatchingClosingParenthesis(expr, 0);

		if (firstClose < 0) {
			throw new EtlExceptionImpl("Unbalanced parentheses in expression: " + expr);
		}

		String initialValue = expr.substring(1, firstClose).trim();

		String remaining = expr.substring(firstClose + 1).trim();

		this.transformerElements = StringTranformerElements.buildChain(initialValue, remaining, this, conn);

		if (this.getNullOperandBehavior() == null) {
			setOnNullTransformedvalue(this.getGeneralBehaviourOnEtlException());
		}

		logTrace("StringTranformer elements loaded: {}", this.transformerElements);
	}

	public static String buildCacheKey(String transformationString) {
		return transformationString;
	}

	public static StringTranformer getInstance(List<Object> parameters, EtlTransformTarget relatedEtlTransformTarget,
			TransformableField field, Connection conn) {
		String key = buildCacheKey(relatedEtlTransformTarget, field, parameters);

		return INSTANCES.computeIfAbsent(key, k -> {
			try {
				return new StringTranformer(parameters, relatedEtlTransformTarget, field, conn);
			} catch (FieldAvaliableInMultipleDataSources | DBException e) {
				throw new EtlExceptionImpl(e);
			}
		});
	}

	@Override
	public FieldTransformingInfo transform(EtlProcessor processor, EtlDatabaseObject srcObject,
			EtlDatabaseObject transformedRecord, List<EtlDatabaseObject> additionalSrcObjects, TransformableField field,
			Connection srcConn, Connection dstConn) throws DBException, EtlTransformationException {

		traceTransformationInitialization(field);

		if (additionalSrcObjects == null || additionalSrcObjects.isEmpty()) {
			throw new EtlTransformationException("StringTransformer requires at least one source object.", null,
					srcObject, ActionOnEtlIssue.ABORT_PROCESS);
		}

		try {

			Object result = this.transformerElements.evaluate(processor, srcObject, transformedRecord,
					additionalSrcObjects, field, srcConn, dstConn);

			FieldTransformingInfo transformingInfo = new FieldTransformingInfo(field, result, null);

			transformingInfo.setLoadedWithDefaultValue(true);

			return transformingInfo;

		} catch (Exception e) {

			throw new EtlTransformationException("Failed to evaluate string expression: " + field.getValueToTransform(),
					e, srcObject, ActionOnEtlIssue.ABORT_PROCESS);
		} finally {
			traceTransformationFinalization(field);
		}
	}

}
