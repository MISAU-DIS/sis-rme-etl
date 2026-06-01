package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

public class EtlTransformationException extends EtlExceptionImpl {
	
	private static final long serialVersionUID = 1L;
	
	public EtlTransformationException(EtlObject etlSrcObject, ActionOnEtlIssue actionOnException) {
		super("An error occured transforming the object " + etlSrcObject, etlSrcObject, actionOnException);
	}
	
	public EtlTransformationException(String msg, EtlObject etlSrcObject, ActionOnEtlIssue actionOnException) {
		super(msg, etlSrcObject, actionOnException);
	}
	
	public EtlTransformationException(String msg, Exception e, EtlObject etlObject, ActionOnEtlIssue action) {
		super(msg, e, etlObject, action);
	}
}
