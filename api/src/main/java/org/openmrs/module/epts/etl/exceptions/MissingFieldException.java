package org.openmrs.module.epts.etl.exceptions;

import org.openmrs.module.epts.etl.model.pojo.generic.EtlDatabaseObjectConfiguration;

public class MissingFieldException extends ForbiddenOperationException {

	private static final long serialVersionUID = -4799473775930078338L;

	public MissingFieldException(String fieldName, EtlDatabaseObjectConfiguration tabConf) {
		this(fieldName, tabConf, true);
	}

	public MissingFieldException(String fieldName, String objectName) {
		this(fieldName, objectName, true);
	}

	private MissingFieldException(String fieldName, Object object, boolean b) {
		super("The field '" + fieldName + "' could not be found within "
				+ (object != null ? " the object " + object : "Aknown Object"));
	}
}
