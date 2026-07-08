package org.openmrs.module.epts.etl.conf;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleCondition {

	private final String field;

	private final String operator;

	private final Set<String> values;

	private SimpleCondition(String field, String operator, Set<String> values) {
		this.field = normalize(field);
		this.operator = operator.trim().toLowerCase();
		this.values = values;
	}

	public static SimpleCondition parse(String condition) {

		if (condition == null || condition.trim().isEmpty()) {
			return null;
		}

		String normalized = condition.trim();

		// NOT IN
		Matcher notInMatcher = Pattern.compile("(?i)^(.+?)\\s+not\\s+in\\s*\\((.+)\\)$").matcher(normalized);

		if (notInMatcher.matches()) {
			return new SimpleCondition(notInMatcher.group(1), "not in", parseValues(notInMatcher.group(2)));
		}

		// IN
		Matcher inMatcher = Pattern.compile("(?i)^(.+?)\\s+in\\s*\\((.+)\\)$").matcher(normalized);

		if (inMatcher.matches()) {
			return new SimpleCondition(inMatcher.group(1), "in", parseValues(inMatcher.group(2)));
		}

		// =, !=, <>
		String[] operators = { "!=", "<>", "=" };

		for (String op : operators) {
			int index = normalized.indexOf(op);

			if (index > 0) {
				String left = normalized.substring(0, index).trim();
				String right = normalized.substring(index + op.length()).trim();

				if (!left.isEmpty() && !right.isEmpty()) {
					return new SimpleCondition(left, op, Collections.singleton(normalize(right)));
				}
			}
		}

		return null;
	}

	public boolean doesNotIntersectWith(SimpleCondition other) {

		if (other == null) {
			return false;
		}

		if (!this.field.equals(other.field)) {
			return false;
		}

		// A IN (...) vs A IN (...)
		if (this.isPositiveSet() && other.isPositiveSet()) {
			return Collections.disjoint(this.values, other.values);
		}

		// A = x vs A != x
		// A IN (x) vs A NOT IN (x)
		if (this.isPositiveSet() && other.isNegativeSet()) {
			return other.values.containsAll(this.values);
		}

		if (this.isNegativeSet() && other.isPositiveSet()) {
			return this.values.containsAll(other.values);
		}

		// A != x vs A != y
		// A NOT IN (...) vs A NOT IN (...)
		// Podem interceptar, porque há valores possíveis fora das duas listas.
		return false;
	}

	/**
	 * Backward compatible name.
	 */
	public boolean isMutuallyExclusiveWith(SimpleCondition other) {
		return doesNotIntersectWith(other);
	}

	private boolean isPositiveSet() {
		return "=".equals(operator) || "in".equals(operator);
	}

	private boolean isNegativeSet() {
		return "!=".equals(operator) || "<>".equals(operator) || "not in".equals(operator);
	}

	private static Set<String> parseValues(String valuesPart) {

		Set<String> values = new LinkedHashSet<>();

		if (valuesPart == null || valuesPart.isBlank()) {
			return values;
		}

		String[] parts = valuesPart.split(",");

		for (String part : parts) {
			values.add(normalize(part));
		}

		return values;
	}

	private static String normalize(String value) {

		if (value == null) {
			return null;
		}

		value = value.trim();

		if (value.startsWith("${") && value.endsWith("}")) {
			value = value.substring(2, value.length() - 1);
		}

		if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
			value = value.substring(1, value.length() - 1);
		}

		return value.trim();
	}
}