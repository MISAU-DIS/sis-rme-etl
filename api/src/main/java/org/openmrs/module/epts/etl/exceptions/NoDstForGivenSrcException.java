package org.openmrs.module.epts.etl.exceptions;
	
public class NoDstForGivenSrcException extends EtlExceptionImpl {
	
	private static final long serialVersionUID = 1L;
	
	public NoDstForGivenSrcException() {
		super("No dst was found for given src objects!");
	}
}
