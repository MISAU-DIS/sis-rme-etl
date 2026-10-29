package org.openmrs.module.epts.etl.conf.datasource;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;

/** Policies that belong to source processing rather than to its physical form. */
public class SourceProcessingPolicy {

	private Boolean limitToOneResult;

	private Boolean trackProcessingState;

	private ActionOnEtlIssue missingRequiredObjectBehavior;

	public Boolean getLimitToOneResult() {
		return limitToOneResult;
	}

	public void setLimitToOneResult(Boolean limitToOneResult) {
		this.limitToOneResult = limitToOneResult;
	}

	public Boolean getTrackProcessingState() {
		return trackProcessingState;
	}

	public void setTrackProcessingState(Boolean trackProcessingState) {
		this.trackProcessingState = trackProcessingState;
	}

	public ActionOnEtlIssue getMissingRequiredObjectBehavior() {
		return missingRequiredObjectBehavior;
	}

	public void setMissingRequiredObjectBehavior(ActionOnEtlIssue missingRequiredObjectBehavior) {
		this.missingRequiredObjectBehavior = missingRequiredObjectBehavior;
	}
}
