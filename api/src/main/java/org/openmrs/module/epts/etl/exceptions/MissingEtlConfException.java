package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

public class MissingEtlConfException extends EtlExceptionImpl {
	
	private static final long serialVersionUID = 1L;
	
	public MissingEtlConfException() {
	}
	
	public MissingEtlConfException(String msg) {
		super(msg);
	}
	
	public MissingEtlConfException(String msg, EtlObject etlObject) {
		super(msg, etlObject, ActionOnEtlIssue.ABORT_PROCESS);
	}
	
	public MissingEtlConfException(String msg, Exception e) {
		this(msg, e, null);
	}
	
	public MissingEtlConfException(String msg, Exception e, EtlObject etlObject) {
		super(msg, e, etlObject, ActionOnEtlIssue.ABORT_PROCESS);
	}
	
	public MissingEtlConfException(Exception e) {
		super(e);
	}
	
	public MissingEtlConfException(Exception e, EtlObject etlObject) {
		super(e, etlObject, ActionOnEtlIssue.ABORT_PROCESS);
	}
	
}
