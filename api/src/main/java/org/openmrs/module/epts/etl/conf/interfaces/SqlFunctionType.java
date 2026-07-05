package org.openmrs.module.epts.etl.conf.interfaces;

import java.util.regex.Pattern;

public enum SqlFunctionType {

	COUNT, MAX, MIN, timestampdiff, UNKOWN;

	public boolean isCount() {
		return this.equals(COUNT);
	}

	public static SqlFunctionType determine(String token) {

		if (token == null || token.isBlank()) {
			return SqlFunctionType.UNKOWN;
		}

		if (containsSqlFunction(token, "count")) {
			return SqlFunctionType.COUNT;
		}

		if (containsSqlFunction(token, "max")) {
			return SqlFunctionType.MAX;
		}

		if (containsSqlFunction(token, "min")) {
			return SqlFunctionType.MIN;
		}

		if (containsSqlFunction(token, "timestampdiff")) {
			return SqlFunctionType.timestampdiff;
		}

		return SqlFunctionType.UNKOWN;
	}

	private static boolean containsSqlFunction(String token, String functionName) {
		String regex = "(?i)(?<![a-zA-Z0-9_])" + Pattern.quote(functionName) + "\\s*\\(";

		return Pattern.compile(regex).matcher(token).find();
	}

	public boolean isUnknown() {
		return this == UNKOWN;
	}
}
