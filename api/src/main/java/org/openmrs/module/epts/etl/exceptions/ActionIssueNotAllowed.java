package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;

public class ActionIssueNotAllowed extends EtlExceptionImpl {

	private static final long serialVersionUID = 1L;

	public ActionIssueNotAllowed(String field, ActionOnEtlIssue action, ActionOnEtlIssue[] allowed) {
		super("The action for field " + field + " = " + action + " is not allowed. Please use one of the following: "
				+ allowed);
	}

}
