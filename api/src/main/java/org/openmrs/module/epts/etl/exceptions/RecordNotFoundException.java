package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.conf.interfaces.TableConfiguration;

public class RecordNotFoundException extends EtlExceptionImpl {
	
	private static final long serialVersionUID = 1L;
	
	public RecordNotFoundException(TableConfiguration tabConf, Object recordId) {
		super("Record Not Found:  " + tabConf.getFullTableDescription() + "(" + recordId + ")");
	}
	
}
