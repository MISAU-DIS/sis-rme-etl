package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;
import org.openmrs.module.epts.etl.conf.interfaces.TransformableField;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.etl.processor.EtlProcessor;
import org.openmrs.module.epts.etl.exceptions.EmptyTransformedValueException;
import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.FieldsMappingException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public class StringTranformerElements {

	private static CommonUtilities utilities = CommonUtilities.getInstance();

	private Object valueToTransform;

	private String function;

	private List<FieldsMapping> params;

	private StringTranformerElements nextElements;

	private FieldsMapping auxMapping;

	private Method method;

	private boolean fullLoaded;

	private StringTranformer relatedTransformer;

	StringTranformerElements(StringTranformer relatedTransformer) {
		this.relatedTransformer = relatedTransformer;
	}

	public Object getValueToTransform() {
		return valueToTransform;
	}

	public FieldsMapping getAuxMapping() {
		return auxMapping;
	}

	public void setValueToTransform(Object valueToTransform) throws FieldAvaliableInMultipleDataSources, DBException {
		this.valueToTransform = valueToTransform;
	}

	public ActionOnEtlIssue getNullOperandBehavior() {
		return this.relatedTransformer.getNullOperandBehavior();
	}

	private synchronized void fullLoad(Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		if (!fullLoaded) {
			if (this.valueToTransform != null) {
				try {
					this.auxMapping = FieldsMapping.fastCreate(this.relatedTransformer.getRelatedEtlTransformTarget(),
							this.valueToTransform.toString(), "anknown_field", conn);

					if (!this.auxMapping.hasDataSourceName()) {
						this.auxMapping = FieldsMapping.createSimpleFieldsMapping(
								this.relatedTransformer.getRelatedEtlTransformTarget(), "anknown_field",
								this.valueToTransform, conn);
					}
				} catch (Exception e) {
					throw e;
				}

				fullLoaded = true;
			}
		}
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public List<FieldsMapping> getParams() {
		return params;
	}

	public void setParams(List<FieldsMapping> params) {
		this.params = params;
	}

	public StringTranformerElements getNextElements() {
		return nextElements;
	}

	public void setNextElements(StringTranformerElements nextElements) {
		this.nextElements = nextElements;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		buildString(sb, 0);
		return sb.toString();
	}

	private void buildString(StringBuilder sb, int level) {

		for (int i = 0; i < level; i++) {
			sb.append("\t");
		}

		sb.append("valueToTransform=").append(valueToTransform).append(", function=").append(function)
				.append(", params=").append(params).append("\n");

		if (nextElements != null) {
			nextElements.buildString(sb, level + 1);
		}
	}

	public void init(Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		resolveBestMethod();
	}

	private void resolveBestMethod() {

		String methodName = this.function;
		int paramCount = utilities.arraySize(params);

		Method[] methods = String.class.getMethods();

		for (Method m : methods) {
			if (m.getName().equals(methodName) && m.getParameterCount() == paramCount) {
				this.method = m;

				return;
			}
		}

		throw new RuntimeException("No matching method found: " + methodName + " with " + paramCount + " params");
	}

	public Object evaluate(EtlProcessor processor, EtlDatabaseObject srcObject, EtlDatabaseObject transformedRecord,
			List<EtlDatabaseObject> additionalSrcObjects, TransformableField field, Connection srcConn,
			Connection dstConn) throws Exception {

		StringTranformerElements element = this;

		if (element.getFunction() != null) {
			List<FieldsMapping> params = element.getParams();

			Class<?>[] paramTypes = method.getParameterTypes();
			Object[] methodParams = new Object[paramTypes.length];

			for (int i = 0; i < paramTypes.length; i++) {
				FieldTransformingInfo rawValue = params.get(i).getTransformerInstance().transform(processor, srcObject,
						transformedRecord, additionalSrcObjects, params.get(i), srcConn, dstConn);

				methodParams[i] = convertToType(rawValue.getTransformedValue(), paramTypes[i]);
			}

			Object valueToTransform;

			try {
				valueToTransform = this.getAuxMapping().getTransformerInstance().transform(processor, srcObject,
						transformedRecord, additionalSrcObjects, auxMapping, srcConn, dstConn).getTransformedValue();
			} catch (EmptyTransformedValueException e) {
				if (this.getNullOperandBehavior().useEmptyString()) {
					valueToTransform = "";
				} else
					throw e;
			}

			Object currentValue = "";

			try {
				currentValue = method.invoke(valueToTransform.toString(), methodParams);
			} catch (Exception e) {
				if (e.getCause() instanceof NullPointerException) {
					currentValue = valueToTransform;

					field.getTransformationTargetObject().getRelatedEtlConf().warn(
							"NullPointerException found while executing transformation within {} ",
							this.relatedTransformer);
				} else
					throw e;
			}

			if (element.getNextElements() != null) {
				element.getNextElements().setValueToTransform(currentValue);
				element.getNextElements().fullLoad(srcConn);

				return element.getNextElements().evaluate(processor, srcObject, transformedRecord, additionalSrcObjects,
						element.getNextElements().getAuxMapping(), srcConn, dstConn);
			}

			return currentValue;
		}

		return element.getValueToTransform();
	}

	private Object convertToType(Object value, Class<?> targetType) {

		if (value == null)
			return null;

		String str = value.toString();

		if (targetType == String.class) {
			return str;
		}

		if (targetType == int.class || targetType == Integer.class) {
			return Integer.parseInt(str);
		}

		if (targetType == long.class || targetType == Long.class) {
			return Long.parseLong(str);
		}

		if (targetType == double.class || targetType == Double.class) {
			return Double.parseDouble(str);
		}

		if (targetType == boolean.class || targetType == Boolean.class) {
			return Boolean.parseBoolean(str);
		}

		if (targetType == char.class || targetType == Character.class) {
			return str.charAt(0);
		}

		return value;
	}

	public static StringTranformerElements buildChain(Object value, String remaining,
			StringTranformer relatedTransformer, Connection conn)
			throws FieldAvaliableInMultipleDataSources, DBException {

		StringTranformerElements element = new StringTranformerElements(relatedTransformer);

		element.setValueToTransform(value);
		element.fullLoad(conn);

		if (remaining == null || remaining.isBlank()) {
			return element;
		}

		remaining = remaining.trim();

		if (!remaining.startsWith(".")) {
			throw new EtlExceptionImpl("Invalid string transformer chain: " + remaining);
		}

		int methodNameStart = 1;
		int openingParenthesis = remaining.indexOf('(', methodNameStart);

		if (openingParenthesis < 0) {
			throw new EtlExceptionImpl("Missing opening parenthesis in transformer chain: " + remaining);
		}

		String methodName = remaining.substring(methodNameStart, openingParenthesis).trim();

		if (!isValidIdentifier(methodName)) {
			throw new EtlExceptionImpl("Invalid string transformer method: " + methodName);
		}

		int closingParenthesis = findMatchingClosingParenthesis(remaining, openingParenthesis);

		if (closingParenthesis < 0) {
			throw new EtlExceptionImpl(
					"Missing closing parenthesis for method [" + methodName + "] in expression: " + remaining);
		}

		String argsStr = remaining.substring(openingParenthesis + 1, closingParenthesis);

		String next = remaining.substring(closingParenthesis + 1).trim();

		element.setFunction(methodName);

		List<FieldsMapping> params = new ArrayList<>();

		if (!argsStr.isBlank()) {
			for (String arg : splitArguments(argsStr)) {
				params.add(parseArgument(arg.trim(), relatedTransformer.getRelatedEtlTransformTarget(), conn));
			}
		}

		element.setParams(params);
		element.init(conn);

		if (!next.isBlank()) {
			element.setNextElements(buildChain(null, next, relatedTransformer, conn));
		}

		return element;
	}

	public static int findMatchingClosingParenthesis(String expression, int openingParenthesisIndex) {

		int depth = 0;
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		boolean escaped = false;

		for (int i = openingParenthesisIndex; i < expression.length(); i++) {

			char current = expression.charAt(i);

			if (escaped) {
				escaped = false;
				continue;
			}

			if (current == '\\') {
				escaped = true;
				continue;
			}

			if (current == '\'' && !inDoubleQuote) {
				inSingleQuote = !inSingleQuote;
				continue;
			}

			if (current == '"' && !inSingleQuote) {
				inDoubleQuote = !inDoubleQuote;
				continue;
			}

			if (inSingleQuote || inDoubleQuote) {
				continue;
			}

			if (current == '(') {
				depth++;
			} else if (current == ')') {
				depth--;

				if (depth == 0) {
					return i;
				}
			}
		}

		return -1;
	}

	private static boolean isValidIdentifier(String value) {

		if (value == null || value.isBlank()) {
			return false;
		}

		return value.matches("[a-zA-Z_][a-zA-Z0-9_]*");
	}

	private static String[] splitArguments(String argsStr) {

		if (argsStr == null || argsStr.isBlank()) {
			return new String[0];
		}

		List<String> arguments = new ArrayList<>();

		int depth = 0;
		int argumentStart = 0;

		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		boolean escaped = false;

		for (int i = 0; i < argsStr.length(); i++) {

			char current = argsStr.charAt(i);

			if (escaped) {
				escaped = false;
				continue;
			}

			if (current == '\\') {
				escaped = true;
				continue;
			}

			if (current == '\'' && !inDoubleQuote) {
				inSingleQuote = !inSingleQuote;
				continue;
			}

			if (current == '"' && !inSingleQuote) {
				inDoubleQuote = !inDoubleQuote;
				continue;
			}

			if (inSingleQuote || inDoubleQuote) {
				continue;
			}

			switch (current) {
			case '(':
				depth++;
				break;

			case ')':
				if (depth > 0) {
					depth--;
				}
				break;

			case ',':
				if (depth == 0) {
					arguments.add(argsStr.substring(argumentStart, i).trim());

					argumentStart = i + 1;
				}
				break;

			default:
				break;
			}
		}

		arguments.add(argsStr.substring(argumentStart).trim());

		return arguments.toArray(new String[0]);
	}

	private static FieldsMapping parseArgument(String arg, EtlTransformTarget dstConf, Connection conn)
			throws FieldAvaliableInMultipleDataSources, DBException {

		if (FieldsMapping.isTransformerExpression(dstConf.getRelatedEtlConf(), arg)) {
			return FieldsMapping.fastCreateWithTransformer(dstConf, "anknown_field", arg, conn);
		}

		arg = arg.trim();

		Object argInstance = arg;

		// Strings com aspas
		if ((arg.startsWith("\"") && arg.endsWith("\"")) || (arg.startsWith("'") && arg.endsWith("'"))) {
			argInstance = arg.substring(1, arg.length() - 1);
		}

		// Integer
		if (arg.matches("-?\\d+")) {
			argInstance = Integer.parseInt(arg);
		}

		// Double
		if (arg.matches("-?\\d+\\.\\d+")) {
			argInstance = Double.parseDouble(arg);
		}

		FieldsMapping map = null;

		if (argInstance instanceof String) {
			try {
				map = FieldsMapping.fastCreate(dstConf, arg, conn);

				if (!map.hasDataSourceName()) {
					map = null;
				}
			} catch (FieldsMappingException e) {
			}
		}

		if (map == null) {
			map = FieldsMapping.createSimpleFieldsMapping(dstConf, "anknown_field", argInstance, conn);
		}

		return map;
	}

}
