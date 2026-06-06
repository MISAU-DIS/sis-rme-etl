package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

/**
 * This exception is throwen when some forbiden operation is performed
 * 
 * @author JP. Boane
 * @version 1.0 07/12/2012
 */
public class ForbiddenOperationException extends EtlExceptionImpl {
	
	private static final long serialVersionUID = 1414389901658588032L;
	
	private Object currentObject;
	
	public ForbiddenOperationException() {
	}
	
	public ForbiddenOperationException(String msg) {
		super(msg);
	}
	
	public ForbiddenOperationException(String msg, Object currentObject) {
		super(msg);
		
		this.currentObject = currentObject;
	}
	
	public ForbiddenOperationException(String msg, Exception exception) {
		super(msg, exception);
	}
	
	public ForbiddenOperationException(Exception exception) {
		super(exception);
	}
	
	public Object getCurrentObject() {
		return currentObject;
	}
	
	public void setCurrentObject(Object currentObject) {
		this.currentObject = currentObject;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public Throwable getException() {
		return this;
	}
	
	@Override
	public ActionOnEtlIssue getAction() {
		return ActionOnEtlIssue.ABORT_PROCESS;
	}
	
	@Override
	public EtlObject getEtlObject() {
		return null;
	}
}
