package org.openmrs.module.epts.etl.conf;

public class SimpleCondition {

	private final String field;
	private final String operator;
	private final String value;

	private SimpleCondition(String field, String operator, String value) {
		this.field = normalize(field);
		this.operator = operator.trim();
		this.value = normalize(value);
	}

	public static SimpleCondition parse(String condition) {

		if (condition == null || condition.trim().isEmpty()) {
			return null;
		}

		String normalized = condition.trim();

		String[] operators = { "!=", "<>", "=" };

		for (String op : operators) {
			int index = normalized.indexOf(op);

			if (index > 0) {
				String left = normalized.substring(0, index).trim();
				String right = normalized.substring(index + op.length()).trim();

				if (!left.isEmpty() && !right.isEmpty()) {
					return new SimpleCondition(left, op, right);
				}
			}
		}

		return null;
	}

	public boolean isMutuallyExclusiveWith(SimpleCondition other) {

		if (other == null) {
			return false;
		}

		if (!this.field.equals(other.field)) {
			return false;
		}

		if (this.value.equals(other.value)) {

			if (isEqualsOperator(this.operator) && isNotEqualsOperator(other.operator)) {
				return true;
			}

			if (isNotEqualsOperator(this.operator) && isEqualsOperator(other.operator)) {
				return true;
			}
		}

		if (this.isNullCheck() && other.isNullCheck()) {

			if (isEqualsOperator(this.operator) && isNotEqualsOperator(other.operator)) {
				return true;
			}

			if (isNotEqualsOperator(this.operator) && isEqualsOperator(other.operator)) {
				return true;
			}
		}

		return false;
	}

	private boolean isNullCheck() {
		return "null".equalsIgnoreCase(this.value);
	}

	private static boolean isEqualsOperator(String op) {
		return "=".equals(op);
	}

	private static boolean isNotEqualsOperator(String op) {
		return "!=".equals(op) || "<>".equals(op);
	}

	private static String normalize(String value) {
		if (value == null) {
			return null;
		}

		value = value.trim();

		if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
			value = value.substring(1, value.length() - 1);
		}

		return value.trim();
	}
}