package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.interfaces.EtlTransformTarget;

public class IncompleteFieldsMappingException extends EtlConfException {

	private static final long serialVersionUID = 1505624913800886849L;

	public IncompleteFieldsMappingException(EtlTransformTarget conf) {
		super("Incomplete mapping configuration within targetConf: " + conf);
	}

}