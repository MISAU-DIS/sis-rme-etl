package org.openmrs.module.epts.etl.connector;

/** Raised when the execution layer has no implementation for a connector type. */
public class UnsupportedConnectorTypeException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public UnsupportedConnectorTypeException(ConnectorType type, ConnectorDirection direction) {
		super("No " + direction.name().toLowerCase() + " execution adapter is available for connector type " + type);
	}
}
