package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

public interface EtlException {
	
	String getLocalizedMessage();
	
	void printStackTrace();
	
	Throwable getException();
	
	ActionOnEtlIssue getAction();
	
	EtlObject getEtlObject();
	
}
