package org.openmrs.module.epts.etl.exceptions;

import java.util.List;

import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public class NoDstForGivenSrcException extends EtlExceptionImpl {

	private static final long serialVersionUID = 1L;

	public NoDstForGivenSrcException(List<EtlDatabaseObject> srcObjects) {
		super("No dst was found for given src objects: " + srcObjects.get(0).getRelatedConfiguration().getAlias() + ":["
				+ retrieveObjectIds(srcObjects) + "]");
	}	

	private static String retrieveObjectIds(List<EtlDatabaseObject> srcObjects) {
		String str = "";

		for (EtlDatabaseObject o : srcObjects) {
			str += (str.isEmpty() ? "" : ",") + o.getObjectId().asSimpleValue();
		}

		return str;

	}
}
