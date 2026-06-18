package org.openmrs.module.epts.etl.conf.types;

public enum ConditionClauseScope {
	// @formatter:off
	JOIN_CLAUSE,
	WHERE_CLAUSE;
	// @formatter:on
	public boolean isJoinClause() {
		return this.equals(JOIN_CLAUSE);
	}

	public boolean isWhereClause() {
		return this.equals(WHERE_CLAUSE);
	}

}
