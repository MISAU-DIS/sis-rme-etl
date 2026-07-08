package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.interfaces.EtlAdditionalDataSource;
import org.openmrs.module.epts.etl.conf.types.ActionOnEtlIssue;
import org.openmrs.module.epts.etl.model.base.EtlObject;

public class MissingRequiredTransformationObject extends EtlTransformationException {

	private EtlAdditionalDataSource ds;

	public MissingRequiredTransformationObject(EtlObject etlSrcObject, EtlAdditionalDataSource ds,
			ActionOnEtlIssue actionOnException) {

		super("Missing transformation object from databasource " + ds, etlSrcObject, actionOnException);

		this.ds = ds;
	}

	public EtlAdditionalDataSource getDs() {
		return ds;
	}

	private static final long serialVersionUID = -5753971111579580220L;

}
