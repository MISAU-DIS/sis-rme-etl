package org.openmrs.module.epts.etl.exceptions;

public class InvalidAtomicConditionException extends EtlConfException {
	private static final long serialVersionUID = 1L;

	public InvalidAtomicConditionException() {
	}

	public InvalidAtomicConditionException(String msg) {
		super(msg);
	}

	public InvalidAtomicConditionException(String msg, Exception e) {
		super(msg, e);
	}
}
