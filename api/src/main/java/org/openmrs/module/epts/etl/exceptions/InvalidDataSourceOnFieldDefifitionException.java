package org.openmrs.module.epts.etl.exceptions;

public class InvalidDataSourceOnFieldDefifitionException extends EtlConfException {

	private static final long serialVersionUID = 3807657803283143320L;

	public InvalidDataSourceOnFieldDefifitionException(String fieldNameDefinition, String dataSourceName) {
		super("Invalid datasource '" + dataSourceName + "' on field definition '" + fieldNameDefinition + "'");
	}

}
