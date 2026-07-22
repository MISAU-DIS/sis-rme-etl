package org.openmrs.module.epts.etl.etl.processor.transformer;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.controller.conf.tablemapping.FieldsMapping;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
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

	private DstConf relatedDstConf;

	private boolean fullLoaded;

	StringTranformerElements(DstConf relatedDstConf) {
		this.relatedDstConf = relatedDstConf;
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

	private synchronized void fullLoad(Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {
		if (!fullLoaded) {
			if (this.valueToTransform != null) {
				try {
					this.auxMapping = FieldsMapping.fastCreate(this.relatedDstConf, this.valueToTransform.toString(),
							conn);

					if (!this.auxMapping.hasDataSourceName()) {
						this.auxMapping = FieldsMapping.createSimpleFieldsMapping(this.relatedDstConf, "anknown_field",
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

	public Object evaluate(List<EtlDatabaseObject> additionalSrcObjects, Connection conn) throws Exception {

		StringTranformerElements element = this;

		if (element.getFunction() != null) {
			List<FieldsMapping> params = element.getParams();

			Class<?>[] paramTypes = method.getParameterTypes();
			Object[] methodParams = new Object[paramTypes.length];

			for (int i = 0; i < paramTypes.length; i++) {
				FieldTransformingInfo rawValue = params.get(i).getTransformerInstance().transform(null, null, null,
						additionalSrcObjects, params.get(i), conn, conn);

				methodParams[i] = convertToType(rawValue.getTransformedValue(), paramTypes[i]);
			}

			Object valueToTransform = this.getAuxMapping().getTransformerInstance()
					.transform(null, null, null, additionalSrcObjects, auxMapping, conn, conn).getTransformedValue();

			Object currentValue = method.invoke(valueToTransform.toString(), methodParams);

			if (element.getNextElements() != null) {
				element.getNextElements().setValueToTransform(currentValue);
				element.getNextElements().fullLoad(conn);

				return element.getNextElements().evaluate(additionalSrcObjects, conn);
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

	public static StringTranformerElements buildChain(Object value, String remaining, DstConf relatedDstConf,
			Connection conn) throws FieldAvaliableInMultipleDataSources, DBException {

		StringTranformerElements element = new StringTranformerElements(relatedDstConf);
		element.setValueToTransform(value);
		element.fullLoad(conn);

		if (remaining == null || remaining.isBlank()) {
			return null;
		}

		Pattern pattern = Pattern.compile("^\\.(\\w+)\\(([^)]*)\\)(.*)$");
		Matcher matcher = pattern.matcher(remaining);

		if (!matcher.find()) {
			return element;
		}

		String methodName = matcher.group(1);
		String argsStr = matcher.group(2);
		String next = matcher.group(3);

		element.setFunction(methodName);

		List<FieldsMapping> params = new ArrayList<>();

		if (!argsStr.isBlank()) {
			String[] args = splitArguments(argsStr);
			for (String arg : args) {
				params.add(parseArgument(arg, relatedDstConf, conn));
			}
		}

		element.setParams(params);

		element.init(conn);

		element.setNextElements(buildChain(null, next, relatedDstConf, conn));

		return element;
	}

	private static String[] splitArguments(String args) {

		List<String> result = new ArrayList<>();

		StringBuilder current = new StringBuilder();
		boolean inQuotes = false;

		for (char c : args.toCharArray()) {

			if (c == '"' || c == '\'') {
				inQuotes = !inQuotes;
			}

			if (c == ',' && !inQuotes) {
				result.add(current.toString().trim());
				current.setLength(0);
				continue;
			}

			current.append(c);
		}

		if (!current.isEmpty()) {
			result.add(current.toString().trim());
		}

		return result.toArray(new String[0]);
	}

	private static FieldsMapping parseArgument(String arg, DstConf dstConf, Connection conn)
			throws FieldAvaliableInMultipleDataSources, DBException {

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
			map = FieldsMapping.fastCreate(dstConf, arg, conn);

			if (!map.hasDataSourceName()) {
				map = null;
			}
		}

		if (map == null) {
			map = FieldsMapping.createSimpleFieldsMapping(dstConf, "anknown_field", argInstance, conn);
		}

		return map;
	}

}
