package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.model.base.EtlObject;

public class MissingParameterOnEtlTransformationException extends EtlTransformationException {
	
	private static final long serialVersionUID = 1L;
	
	public MissingParameterOnEtlTransformationException(EtlObject etlSrcObject, ActionOnEtlException actionOnException) {
		super("An error occured transforming the object " + etlSrcObject, etlSrcObject, actionOnException);
	}
	
	public MissingParameterOnEtlTransformationException(String msg, EtlObject etlSrcObject,
	    ActionOnEtlException actionOnException) {
		super(msg, etlSrcObject, actionOnException);
	}
	
	public MissingParameterOnEtlTransformationException(String msg, Exception e, EtlObject etlObject,
	    ActionOnEtlException action) {
		super(msg, e, etlObject, action);
	}
}
