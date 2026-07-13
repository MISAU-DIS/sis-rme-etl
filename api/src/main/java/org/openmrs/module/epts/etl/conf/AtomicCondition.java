package org.openmrs.module.epts.etl.conf;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openmrs.module.epts.etl.conf.interfaces.ConditionExpressionAdapter;
import org.openmrs.module.epts.etl.exceptions.InvalidAtomicConditionException;

public class AtomicCondition {

	private final String field;

	private final String operator;

	private final Set<String> values;

	private final String originalCondition;
	private final boolean expressionBased;

	private static final List<ConditionExpressionAdapter> EXPRESSION_ADAPTERS = Arrays
			.asList(new EtlConfCheckConditionAdapter());

	private AtomicCondition(String field, String operator, Set<String> values, String originalCondition,
			boolean expressionBased) {

		this.field = normalize(field);
		this.operator = operator.trim().toLowerCase();
		this.values = values;

		this.originalCondition = originalCondition;
		this.expressionBased = expressionBased;
	}

	public static AtomicCondition field(String field, String operator, Set<String> values, String originalCondition) {

		return new AtomicCondition(field, operator, values, originalCondition, false);
	}

	public static AtomicCondition expression(String expressionKey, String operator, Set<String> values,
			String originalCondition) {

		return new AtomicCondition(expressionKey, operator, values, originalCondition, true);
	}

	public boolean isExpressionBased() {
		return expressionBased;
	}

	public String getOriginalCondition() {
		return originalCondition;
	}

	public static AtomicCondition parse(String condition) throws InvalidAtomicConditionException {

		if (condition == null || condition.trim().isEmpty()) {
			return null;
		}

		String normalized = condition.trim();

		/*
		 * 1. Expressão isolada:
		 *
		 * ETL_CONF_CHECK(...)
		 */
		ConditionExpressionAdapter directAdapter = findExpressionAdapter(normalized);

		if (directAdapter != null) {
			return directAdapter.toAtomicCondition(normalized, null, Collections.emptySet());
		}

		/*
		 * 2. NOT IN
		 */
		Matcher notInMatcher = Pattern.compile("(?i)^(.+?)\\s+not\\s+in\\s*\\((.+)\\)$").matcher(normalized);

		if (notInMatcher.matches()) {
			return createCondition(notInMatcher.group(1), "not in", parseValues(notInMatcher.group(2)), condition);
		}

		/*
		 * 3. IN
		 */
		Matcher inMatcher = Pattern.compile("(?i)^(.+?)\\s+in\\s*\\((.+)\\)$").matcher(normalized);

		if (inMatcher.matches()) {
			return createCondition(inMatcher.group(1), "in", parseValues(inMatcher.group(2)), condition);
		}

		/*
		 * A ordem é importante: operadores maiores primeiro.
		 */
		String[] operators = { ">=", "<=", "!=", "<>", "=", ">", "<" };

		OperatorLocation operatorLocation = findTopLevelOperator(normalized, operators);

		if (operatorLocation == null) {
			return null;
		}

		String left = normalized.substring(0, operatorLocation.index).trim();

		String right = normalized.substring(operatorLocation.index + operatorLocation.operator.length()).trim();

		if (left.isEmpty() || right.isEmpty()) {
			throw new InvalidAtomicConditionException();
		}

		return createCondition(left, operatorLocation.operator, Collections.singleton(normalize(right)), condition);
	}

	private boolean rangesDoNotIntersect(AtomicCondition other) {

		NumericRange thisRange = NumericRange.from(this);
		NumericRange otherRange = NumericRange.from(other);

		/*
		 * this termina antes de other começar
		 */
		if (thisRange.upperBound != null && otherRange.lowerBound != null) {

			int comparison = thisRange.upperBound.compareTo(otherRange.lowerBound);

			if (comparison < 0) {
				return true;
			}

			if (comparison == 0 && (!thisRange.upperInclusive || !otherRange.lowerInclusive)) {
				return true;
			}
		}

		/*
		 * other termina antes de this começar
		 */
		if (otherRange.upperBound != null && thisRange.lowerBound != null) {

			int comparison = otherRange.upperBound.compareTo(thisRange.lowerBound);

			if (comparison < 0) {
				return true;
			}

			if (comparison == 0 && (!otherRange.upperInclusive || !thisRange.lowerInclusive)) {
				return true;
			}
		}

		return false;
	}

	public boolean doesNotIntersectWith(AtomicCondition other) {

		if (other == null || !Objects.equals(this.field, other.field)) {
			return false;
		}

		if (this.isPositiveSet() && other.isPositiveSet()) {
			return Collections.disjoint(this.values, other.values);
		}

		if (this.isPositiveSet() && other.isNegativeSet()) {
			return other.values.containsAll(this.values);
		}

		if (this.isNegativeSet() && other.isPositiveSet()) {
			return this.values.containsAll(other.values);
		}

		if (this.isRangeOperator() && other.isRangeOperator()) {
			return rangesDoNotIntersect(other);
		}

		if (this.isEquality() && other.isRangeOperator()) {
			return !other.acceptsNumericValue(this.singleNumericValue());
		}

		if (this.isRangeOperator() && other.isEquality()) {
			return !this.acceptsNumericValue(other.singleNumericValue());
		}

		return false;
	}

	private boolean isEquality() {
		return "=".equals(operator) && values.size() == 1;
	}

	private BigDecimal singleNumericValue() {
		return new BigDecimal(values.iterator().next());
	}

	private boolean acceptsNumericValue(BigDecimal value) {

		BigDecimal boundary = singleNumericValue();
		int comparison = value.compareTo(boundary);

		switch (operator) {
		case ">":
			return comparison > 0;
		case ">=":
			return comparison >= 0;
		case "<":
			return comparison < 0;
		case "<=":
			return comparison <= 0;
		case "=":
			return comparison == 0;
		default:
			return true;
		}
	}

	/**
	 * Backward compatible name.
	 */
	public boolean isMutuallyExclusiveWith(AtomicCondition other) {
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

	private static ConditionExpressionAdapter findExpressionAdapter(String expression) {

		for (ConditionExpressionAdapter adapter : EXPRESSION_ADAPTERS) {
			if (adapter.supports(expression)) {
				return adapter;
			}
		}

		return null;
	}

	private static AtomicCondition createCondition(String left, String operator, Set<String> values,
			String originalCondition) {

		ConditionExpressionAdapter adapter = findExpressionAdapter(left);

		if (adapter != null) {
			return adapter.toAtomicCondition(left, operator, values);
		}

		return AtomicCondition.field(left, operator, values, originalCondition);
	}

	private static OperatorLocation findTopLevelOperator(String condition, String[] operators) {

		int parenthesesLevel = 0;
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;

		for (int i = 0; i < condition.length(); i++) {

			char c = condition.charAt(i);

			if (c == '\'' && !inDoubleQuote) {
				inSingleQuote = !inSingleQuote;
				continue;
			}

			if (c == '"' && !inSingleQuote) {
				inDoubleQuote = !inDoubleQuote;
				continue;
			}

			if (inSingleQuote || inDoubleQuote) {
				continue;
			}

			if (c == '(') {
				parenthesesLevel++;
				continue;
			}

			if (c == ')') {
				parenthesesLevel--;
				continue;
			}

			if (parenthesesLevel != 0) {
				continue;
			}

			for (String operator : operators) {
				if (condition.startsWith(operator, i)) {
					return new OperatorLocation(i, operator);
				}
			}
		}

		return null;
	}

	private boolean isRangeOperator() {
		return ">".equals(operator) || ">=".equals(operator) || "<".equals(operator) || "<=".equals(operator);
	}

	private static class OperatorLocation {

		private final int index;
		private final String operator;

		private OperatorLocation(int index, String operator) {
			this.index = index;
			this.operator = operator;
		}
	}

	private static class NumericRange {

		private BigDecimal lowerBound;
		private boolean lowerInclusive;

		private BigDecimal upperBound;
		private boolean upperInclusive;

		private static NumericRange from(AtomicCondition condition) {

			if (condition == null || condition.values == null || condition.values.size() != 1) {

				throw new IllegalArgumentException("A range condition must contain exactly one value.");
			}

			BigDecimal value;

			try {
				value = new BigDecimal(condition.values.iterator().next());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Range value is not numeric: " + condition.values, e);
			}

			NumericRange range = new NumericRange();

			switch (condition.operator) {

			case ">":
				range.lowerBound = value;
				range.lowerInclusive = false;
				break;

			case ">=":
				range.lowerBound = value;
				range.lowerInclusive = true;
				break;

			case "<":
				range.upperBound = value;
				range.upperInclusive = false;
				break;

			case "<=":
				range.upperBound = value;
				range.upperInclusive = true;
				break;

			case "=":
				range.lowerBound = value;
				range.lowerInclusive = true;

				range.upperBound = value;
				range.upperInclusive = true;
				break;

			default:
				throw new IllegalArgumentException("Unsupported range operator: " + condition.operator);
			}

			return range;
		}
	}
}