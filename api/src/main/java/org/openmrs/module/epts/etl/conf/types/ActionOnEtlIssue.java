package org.openmrs.module.epts.etl.conf.types;

import org.openmrs.module.epts.etl.utilities.CommonUtilities;

public enum ActionOnEtlIssue {
	
	ABORT_PROCESS,
	LOG,
	IGNORE,
	USE_LAST,
	USE_FIRST,
	ALLOW,
	SET_TO_NULL,
	MARK_RECORD_AS_FAILED,
	CREATE_ON_DST,
	USE_INPUT,
	USE_DEFAULT;
	
	public static final CommonUtilities utilities = CommonUtilities.getInstance();
	
	public static final ActionOnEtlIssue[] ON_MULTIPLE_DATA_SOURCE_FOUND = { ABORT_PROCESS, USE_LAST, USE_FIRST };
	
	public static final ActionOnEtlIssue[] ON_NULL = { ABORT_PROCESS, MARK_RECORD_AS_FAILED, ALLOW };
	
	public static final ActionOnEtlIssue[] ON_INCONSISTENCY = { ABORT_PROCESS, MARK_RECORD_AS_FAILED, SET_TO_NULL };
	
	public static final ActionOnEtlIssue[] ON_MISSING_FAST_SRC_PARENT = { ABORT_PROCESS, MARK_RECORD_AS_FAILED,
	        CREATE_ON_DST };
	
	public static final ActionOnEtlIssue[] ON_MISSING_MAPPING = { ABORT_PROCESS, MARK_RECORD_AS_FAILED, USE_INPUT, IGNORE };
	
	public boolean useInputOnMissingMapping() {
		return this.equals(USE_INPUT);
	}
	
	public boolean ignoreOnMissingMapping() {
		return this.equals(IGNORE);
	}
	
	public boolean useDefaultOnMissingMapping() {
		return this.equals(USE_DEFAULT);
	}
	
	public boolean createOnDst() {
		return this.equals(CREATE_ON_DST);
	}
	
	public boolean setToNull() {
		return this.equals(SET_TO_NULL);
	}
	
	public boolean log() {
		return this.equals(LOG);
	}
	
	public boolean abort() {
		return this.equals(ABORT_PROCESS);
	}
	
	public boolean ignore() {
		return this.equals(IGNORE);
	}
	
	public boolean useLast() {
		return this.equals(USE_LAST);
	}
	
	public boolean abortProcess() {
		return this.equals(ABORT_PROCESS);
	}
	
	public boolean useFirst() {
		return this.equals(USE_FIRST);
	}
	
	public boolean allow() {
		return this.equals(ALLOW);
	}
	
	public boolean markRecordAsFailed() {
		return this.equals(MARK_RECORD_AS_FAILED);
	}
	
	public boolean allowedOnMultipleDataSourceFound() {
		return utilities.existOnArray(ON_MULTIPLE_DATA_SOURCE_FOUND, this);
	}
	
	public boolean allowedOnNull() {
		return utilities.existOnArray(ON_NULL, this);
	}
	
	public boolean allowedOnInconsisteny() {
		return utilities.existOnArray(ON_INCONSISTENCY, this);
	}
	
	public boolean allowedOnMissingFastSrcParent() {
		return utilities.existOnArray(ON_MISSING_FAST_SRC_PARENT, this);
	}
	
	public boolean allowedOnMissingMapping() {
		return utilities.existOnArray(ON_MISSING_MAPPING, this);
	}
}
