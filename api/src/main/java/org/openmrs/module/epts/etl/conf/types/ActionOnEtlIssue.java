package org.openmrs.module.epts.etl.conf.types;

import org.openmrs.module.epts.etl.exceptions.ActionIssueNotAllowed;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;

public enum ActionOnEtlIssue {

	USE_LAST, USE_FIRST, SET_TO_NULL, CREATE_ON_DST, USE_INPUT, USE_DEFAULT, LOG, IGNORE, MARK_RECORD_AS_FAILED,
	USE_EXCEPTION_BEHAVIOR, ABORT_PROCESS;

	public static final CommonUtilities utilities = CommonUtilities.getInstance();

	public static final ActionOnEtlIssue[] COMPARABLE = { LOG, IGNORE, MARK_RECORD_AS_FAILED, ABORT_PROCESS };

	public static final ActionOnEtlIssue[] ALLOWED_ON_ETL_CONFIGURATION = { LOG, IGNORE, MARK_RECORD_AS_FAILED,
			ABORT_PROCESS, USE_EXCEPTION_BEHAVIOR };

	public static final ActionOnEtlIssue[] ON_MULTIPLE_DATA_SOURCE_FOUND = { ABORT_PROCESS, USE_LAST, USE_FIRST };

	public static final ActionOnEtlIssue[] ON_NULL = { ABORT_PROCESS, MARK_RECORD_AS_FAILED, IGNORE };

	public static final ActionOnEtlIssue[] ON_INCONSISTENCY = { ABORT_PROCESS, MARK_RECORD_AS_FAILED, SET_TO_NULL };

	public static final ActionOnEtlIssue[] ON_MISSING_FAST_SRC_PARENT = { ABORT_PROCESS, MARK_RECORD_AS_FAILED,
			CREATE_ON_DST };

	public static final ActionOnEtlIssue[] ON_MISSING_MAPPING = { ABORT_PROCESS, MARK_RECORD_AS_FAILED, USE_INPUT,
			IGNORE };

	public static final ActionOnEtlIssue[] ON_MISSING_FIELDS_MAPPING = { ABORT_PROCESS, IGNORE, USE_DEFAULT };

	public static final ActionOnEtlIssue[] ON_MISSING_REQUIRED_OBJECT = { ABORT_PROCESS, MARK_RECORD_AS_FAILED };

	public static final ActionOnEtlIssue[] IN_SAME_LEVEL = { USE_DEFAULT, USE_INPUT, SET_TO_NULL, USE_LAST, USE_FIRST,
			CREATE_ON_DST };

	public static final ActionOnEtlIssue[] LOGING = { MARK_RECORD_AS_FAILED, LOG };

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

	public boolean markRecordAsFailed() {
		return this.equals(MARK_RECORD_AS_FAILED);
	}

	public boolean useExceptionBehavior() {
		return this.equals(USE_EXCEPTION_BEHAVIOR);
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

	public boolean allowedOnMissingFieldsMapping() {
		return utilities.existOnArray(ON_MISSING_FIELDS_MAPPING, this);
	}

	public boolean comparable(ActionOnEtlIssue otherAction) {
		return utilities.existOnArray(COMPARABLE, this) && utilities.existOnArray(COMPARABLE, otherAction);
	}

	public boolean allowedOnEtlConfiguration() {
		return utilities.existOnArray(ALLOWED_ON_ETL_CONFIGURATION, this);
	}

	public boolean logging() {
		return utilities.existOnArray(LOGING, this);
	}

	public void validateAllowedOnEtlConfiguration(String field) throws EtlConfException {
		validate(field, ALLOWED_ON_ETL_CONFIGURATION);
	}

	public void validateOnMultipleDataSourceFound(String field) throws EtlConfException {
		validate(field, ON_MULTIPLE_DATA_SOURCE_FOUND);
	}

	public void validateOnNull(String field) throws EtlConfException {
		validate(field, ON_NULL);
	}

	public void validateOnInconsistency(String field) throws EtlConfException {
		validate(field, ON_INCONSISTENCY);
	}

	public void validateOnMissingFastSrcParent(String field) throws EtlConfException {
		validate(field, ON_MISSING_FAST_SRC_PARENT);
	}

	public void validateOnMissingMapping(String field) throws EtlConfException {
		validate(field, ON_MISSING_MAPPING);
	}

	public void validateOnMissingFieldsMapping(String field) throws EtlConfException {
		validate(field, ON_MISSING_FIELDS_MAPPING);
	}

	public void validateOnMissingRequiredObject(String field) throws EtlConfException {
		validate(field, ON_MISSING_REQUIRED_OBJECT);
	}

	private void validate(String field, ActionOnEtlIssue[] against) throws EtlConfException {
		if (!utilities.existOnArray(against, this)) {
			throw new ActionIssueNotAllowed(field, this, against);
		}
	}
}
