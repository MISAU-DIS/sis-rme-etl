package org.openmrs.module.epts.etl.connector;

import java.util.Set;

/** Raised during validation when an operation requires unsupported behaviour. */
public class UnsupportedConnectorCapabilityException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public UnsupportedConnectorCapabilityException(ConnectorType connectorType,
			Set<ConnectorCapability> missingCapabilities) {
		super("Connector " + connectorType + " does not support required capabilities " + missingCapabilities);
	}
}
