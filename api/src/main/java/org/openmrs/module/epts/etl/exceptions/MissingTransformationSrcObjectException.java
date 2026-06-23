package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

public class MissingTransformationSrcObjectException extends EtlTransformationException {

	private static final long serialVersionUID = 1L;

	public MissingTransformationSrcObjectException(EtlObject etlSrcObject, ActionOnEtlIssue actionOnException) {
		super("Missing one or more required traansformation objects while transforming object: " + etlSrcObject,
				etlSrcObject, actionOnException);
	}

	public MissingTransformationSrcObjectException(String msg, EtlObject etlSrcObject,
			ActionOnEtlIssue actionOnException) {
		super(msg, etlSrcObject, actionOnException);
	}

	public MissingTransformationSrcObjectException(String msg, Exception e, EtlObject etlObject,
			ActionOnEtlIssue action) {
		super(msg, e, etlObject, action);
	}
}
