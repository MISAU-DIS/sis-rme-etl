package org.openmrs.module.epts.etl.exceptions;

public class InvalidDataSourceOnFieldDefifitionException extends EtlConfException {

	private static final long serialVersionUID = 3807657803283143320L;

	private String fieldNameDefinition;
	private String dataSourceName;

	public InvalidDataSourceOnFieldDefifitionException(String fieldNameDefinition, String dataSourceName) {
		super("Invalid datasource '" + dataSourceName + "' on field definition '" + fieldNameDefinition + "'");

		this.fieldNameDefinition = fieldNameDefinition;
		this.dataSourceName = dataSourceName;
	}

	public String getFieldNameDefinition() {
		return fieldNameDefinition;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}
}
