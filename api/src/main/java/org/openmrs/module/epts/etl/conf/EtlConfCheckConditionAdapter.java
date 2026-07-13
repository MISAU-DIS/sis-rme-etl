package org.openmrs.module.epts.etl.conf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.epts.etl.conf.interfaces.ConditionExpressionAdapter;
import org.openmrs.module.epts.etl.conf.types.EtlConfCheckType;

public class EtlConfCheckConditionAdapter implements ConditionExpressionAdapter {

	private static final Pattern PATTERN = Pattern.compile("(?i)^ETL_CONF_CHECK\\s*\\((.*)\\)$");

	@Override
	public boolean supports(String expression) {
		return expression != null && PATTERN.matcher(expression.trim()).matches();
	}

	@Override
	public AtomicCondition toAtomicCondition(String expression, String externalOperator, Set<String> externalValues) {

		EtlConfCheckExpression parsed = parseExpression(expression);

		String confName = normalize(parsed.getConfName());
		EtlConfCheckType operation = parsed.getOperation();

		if (operation == EtlConfCheckType.EXISTS) {
			return AtomicCondition.expression("ETL_CONF_CHECK:EXISTS:" + confName, "=", Collections.singleton("true"),
					expression);
		}

		if (operation == EtlConfCheckType.DOES_NOT_EXIST) {
			return AtomicCondition.expression("ETL_CONF_CHECK:EXISTS:" + confName, "=", Collections.singleton("false"),
					expression);
		}

		String expressionKey = buildCanonicalKey(parsed);

		// Exemplo:
		// ETL_CONF_CHECK(...COUNT_FIELDS) >= 10
		if (externalOperator != null) {
			return AtomicCondition.expression(expressionKey, externalOperator, externalValues, expression);
		}

		/*
		 * Uma expressão isolada é considerada verdadeira.
		 */
		return AtomicCondition.expression(expressionKey, "=", Collections.singleton("true"), expression);
	}

	private String buildCanonicalKey(EtlConfCheckExpression expression) {

		StringBuilder key = new StringBuilder();

		key.append("ETL_CONF_CHECK:").append(expression.getOperation()).append(":")
				.append(normalize(expression.getConfName()));

		if (expression.getField() != null) {
			key.append(":").append(normalize(expression.getField()));
		}

		return key.toString();
	}

	private EtlConfCheckExpression parseExpression(String expression) {

		Matcher matcher = PATTERN.matcher(expression.trim());

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid ETL_CONF_CHECK expression: " + expression);
		}

		Map<String, String> parameters = parseExpressionParameters(matcher.group(1));

		EtlConfCheckExpression result = new EtlConfCheckExpression();

		result.setConfName(firstNonNull(parameters.get("conf_name"), parameters.get("confName")));

		result.setOperation(EtlConfCheckType.valueOf(parameters.get("operation").trim().toUpperCase()));

		result.setField(parameters.get("field"));

		return result;
	}

	private Map<String, String> parseExpressionParameters(String content) {

		Map<String, String> result = new LinkedHashMap<>();

		for (String parameter : content.split("\\s*,\\s*")) {

			String[] pair = parameter.split("\\s*:\\s*", 2);

			if (pair.length != 2) {
				throw new IllegalArgumentException("Invalid expression parameter: " + parameter);
			}

			result.put(pair[0].trim(), normalize(pair[1]));
		}

		return result;
	}

	private String firstNonNull(String... values) {
		for (String value : values) {
			if (value != null) {
				return value;
			}
		}

		return null;
	}

	private String normalize(String value) {
		return value == null ? null : value.trim().toLowerCase();
	}
}