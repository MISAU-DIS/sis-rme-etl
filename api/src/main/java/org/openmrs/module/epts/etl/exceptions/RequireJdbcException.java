package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

public class RequireJdbcException extends EtlExceptionImpl {
	
	private static final long serialVersionUID = 1L;
	
	public RequireJdbcException() {
	}
	
	public RequireJdbcException(String msg) {
		super(msg);
	}
	
	public RequireJdbcException(String msg, EtlObject etlObject) {
		super(msg, etlObject, ActionOnEtlIssue.ABORT_PROCESS);
	}
	
	public RequireJdbcException(String msg, Exception e) {
		this(msg, e, null);
	}
	
	public RequireJdbcException(String msg, Exception e, EtlObject etlObject) {
		super(msg, e, etlObject, ActionOnEtlIssue.ABORT_PROCESS);
	}
	
	public RequireJdbcException(Exception e) {
		super(e);
	}
	
	public RequireJdbcException(Exception e, EtlObject etlObject) {
		super(e, etlObject, ActionOnEtlIssue.ABORT_PROCESS);
	}
	
}
