package org.openmrs.module.epts.etl.exceptions;

public class NoFieldWithFieldsMapping extends EtlConfException {
	
	private static final long serialVersionUID = 1L;
	
	public NoFieldWithFieldsMapping() {
		
		super("A FieldsMapping must have at least a srcFieldName or dstField");
	}
	
}
