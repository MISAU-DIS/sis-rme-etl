package org.openmrs.module.epts.etl.conf.interfaces;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.epts.etl.conf.EtlConfCheckEvaluator;
import org.openmrs.module.epts.etl.conf.EtlConfCheckExpression;
import org.openmrs.module.epts.etl.conf.types.EtlConfCheckType;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.exceptions.EtlTransformationException;
import org.openmrs.module.epts.etl.exceptions.FieldAvaliableInMultipleDataSources;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.openmrs.module.epts.etl.exceptions.InvalidDataSourceOnFieldDefifitionException;
import org.openmrs.module.epts.etl.exceptions.MissingParameterOnEtlTransformationException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.utilities.db.conn.DBException;

public interface ConditionalEtlElement extends EtlDataConfiguration {

	String getCondition();

	default Boolean hasCondition() {
		return utilities.stringHasValue(this.getCondition());
	}

	default Boolean shouldBeProcessed(EtlDatabaseObject srcObject, Set<EtlDatabaseObject> avaliableSrcObjects,
			Connection srcConn, Connection dstConn) throws DBException {

		if (this.hasCondition()) {
			String condition = this.getCondition();

			condition = condition.replaceAll("(?i)\\s+or\\s+", "||");
			condition = condition.replaceAll("(?i)\\s+and\\s+", "&&");

			String[] orConditions = condition.split("\\|\\|");

			for (String orCond : orConditions) {

				boolean andResult = true;

				String[] andConditions = orCond.split("&&");

				for (String andCond : andConditions) {

					andCond = andCond.trim();

					boolean result;

					if (isEtlConfCheckExpression(andCond)) {
						result = evaluateEtlConfCheck(andCond);
					} else {
						result = evaluateCondition(srcObject, avaliableSrcObjects, andCond, srcConn, dstConn);
					}

					if (!result) {
						andResult = false;
						break;
					}
				}

				if (andResult) {
					return true;
				}
			}

			return false;

		} else {
			return true;
		}

	}

	default boolean isEtlConfCheckExpression(String condition) {
		return condition != null && condition.trim().matches("(?i)^ETL_CONF_CHECK\\s*\\(.*\\)$");
	}

	default boolean evaluateEtlConfCheck(String expressionText) throws EtlTransformationException, DBException {

		EtlConfCheckExpression expression = parseEtlConfCheckExpression(expressionText);

		Object result = new EtlConfCheckEvaluator().evaluate(expression);

		if (!(result instanceof Boolean)) {
			throw new EtlConfException("ETL_CONF_CHECK must return a boolean value when used as a condition. "
					+ "Expression: " + expressionText + ", returned: " + result);
		}

		return (Boolean) result;
	}

	default EtlConfCheckExpression parseEtlConfCheckExpression(String expressionText) {

		String text = expressionText.trim();

		Pattern pattern = Pattern.compile("(?i)^ETL_CONF_CHECK\\s*\\((.*)\\)$");

		Matcher matcher = pattern.matcher(text);

		if (!matcher.find()) {
			throw new EtlConfException("Invalid ETL_CONF_CHECK expression: " + expressionText);
		}

		String body = matcher.group(1).trim();

		Map<String, String> params = parseEtlConfCheckParams(body);

		String confName = params.get("conf_name");
		String operation = params.get("operation");
		String field = params.get("field");

		if (confName == null || confName.isBlank()) {
			throw new EtlConfException("ETL_CONF_CHECK requires parameter 'confName'.");
		}

		if (operation == null || operation.isBlank()) {
			throw new EtlConfException("ETL_CONF_CHECK requires parameter 'operation'.");
		}

		EtlConfCheckExpression expression = new EtlConfCheckExpression();

		expression.setConfName(confName);
		expression.setOperation(EtlConfCheckType.valueOf(operation.trim().toUpperCase()));
		expression.setField(field);

		expression.init(this);

		return expression;
	}

	default Map<String, String> parseEtlConfCheckParams(String body) {

		Map<String, String> params = new LinkedHashMap<>();

		if (body == null || body.isBlank()) {
			return params;
		}

		String[] parts = body.split("\\s*,\\s*");

		for (String part : parts) {

			String[] keyValue = part.split("\\s*:\\s*", 2);

			if (keyValue.length != 2) {
				throw new EtlConfException("Invalid ETL_CONF_CHECK parameter: " + part);
			}

			params.put(keyValue[0].trim(), stripQuotes(keyValue[1].trim()));
		}

		return params;
	}

	default Boolean evaluateCondition(EtlDatabaseObject obj, Set<EtlDatabaseObject> avaliableSrcObjects,
			String condition, Connection srcConn, Connection dstConn)
			throws FieldAvaliableInMultipleDataSources, DBException {

		List<EtlDatabaseObject> list = utilities.setHasElement(avaliableSrcObjects)
				? new ArrayList<>(avaliableSrcObjects)
				: utilities.parseToList(obj);

		try {
			condition = org.openmrs.module.epts.etl.utilities.db.conn.SQLUtilities
					.ensureDataSourceElementsReplaced(condition, null, list, this.getRelatedEtlConf(), dstConn);
		} catch (MissingParameterOnEtlTransformationException | InvalidDataSourceOnFieldDefifitionException e) {

			if (e instanceof InvalidDataSourceOnFieldDefifitionException) {
				throw e;
			}

			return false;
		}

		// NOT IN
		if (condition.matches("(?i).+\\s+not\\s+in\\s*\\(.+\\)")) {
			return evaluateIn(obj, condition, true);
		}

		// IN
		if (condition.matches("(?i).+\\s+in\\s*\\(.+\\)")) {
			return evaluateIn(obj, condition, false);
		}

		// LIKE
		if (condition.matches("(?i).+\\s+like\\s+.+")) {
			return evaluateLike(obj, condition);
		}

		// operadores de comparação
		String operator = null;

		if (condition.contains(">="))
			operator = ">=";
		else if (condition.contains("<="))
			operator = "<=";
		else if (condition.contains("!="))
			operator = "!=";
		else if (condition.contains(">"))
			operator = ">";
		else if (condition.contains("<"))
			operator = "<";
		else if (condition.contains("="))
			operator = "=";

		if (operator == null) {
			throw new IllegalArgumentException("Unsupported condition: " + condition);
		}

		String[] parts = condition.split("\\Q" + operator + "\\E");

		String field = parts[0].trim();
		String expected = stripQuotes(parts[1].trim());

		Object value;

		try {
			value = obj.getFieldValue(field);
		} catch (ForbiddenOperationException e) {
			value = field;
		}

		if (value == null) {
			value = "null";
		}

		String actual = value.toString();

		switch (operator) {

		case "=":
			return actual.equals(expected);

		case "!=":
			return !actual.equals(expected);

		case ">":
			return compare(actual, expected) > 0;

		case "<":
			return compare(actual, expected) < 0;

		case ">=":
			return compare(actual, expected) >= 0;

		case "<=":
			return compare(actual, expected) <= 0;

		default:
			throw new IllegalArgumentException("Unsupported operator");
		}
	}

	default int compare(String a, String b) {

		try {
			Double da = Double.valueOf(a);
			Double db = Double.valueOf(b);

			return da.compareTo(db);

		} catch (NumberFormatException e) {
			return a.compareTo(b);
		}
	}

	default Boolean evaluateIn(EtlDatabaseObject obj, String condition, boolean negated) {

		String[] parts = condition.split(negated ? "(?i)not\\s+in" : "(?i)in");

		String field = parts[0].trim();
		String valuesPart = parts[1].trim();

		valuesPart = valuesPart.replaceAll("[()]", "");

		Object value = null;

		try {
			value = obj.getFieldValue(field);
		} catch (ForbiddenOperationException e) {
		}

		if (value == null) {
			value = field;
		}

		String actual = value.toString();

		boolean found = false;

		for (String v : valuesPart.split(",")) {

			if (actual.equals(stripQuotes(v.trim()))) {
				found = true;
				break;
			}
		}

		return negated ? !found : found;
	}

	default Boolean evaluateLike(EtlDatabaseObject obj, String condition) {

		String[] parts = condition.split("(?i)like");

		String field = parts[0].trim();
		String pattern = stripQuotes(parts[1].trim());

		Object value = obj.getFieldValue(field);

		if (value == null) {
			value = field;
		}

		String actual = value.toString();

		String regex = pattern.replace("%", ".*").replace("_", ".");

		return actual.matches(regex);
	}

	default String stripQuotes(String s) {
		return s.replaceAll("^['\"]|['\"]$", "");
	}

}
