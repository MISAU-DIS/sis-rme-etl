package org.openmrs.module.epts.etl.connector.conf;

import java.util.Collections;
import java.util.Set;

import org.openmrs.module.epts.etl.connector.ConnectorCapability;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorType;

/** Endpoint representation used before a concrete connector is installed. */
final class ConfiguredConnectorEndpoint implements ConnectorEndpoint {

	private final ConnectorEndpointConfiguration configuration;

	ConfiguredConnectorEndpoint(ConnectorEndpointConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public ConnectorType getType() {
		return configuration.getType();
	}

	@Override
	public String getUri() {
		return configuration.getUri();
	}

	@Override
	public String getUserName() {
		return configuration.getUserName();
	}

	@Override
	public String getPassword() {
		return configuration.getPassword();
	}

	@Override
	public Set<ConnectorCapability> getCapabilities() {
		if (configuration.getType() == ConnectorType.FILE) {
			return java.util.EnumSet.of(ConnectorCapability.READ, ConnectorCapability.BATCH_READ,
					ConnectorCapability.WRITE, ConnectorCapability.BATCH_WRITE);
		}
		return Collections.emptySet();
	}

	@Override
	public <T> T unwrap(Class<T> nativeType) {
		if (nativeType != null && nativeType.isInstance(configuration)) {
			return nativeType.cast(configuration);
		}
		throw new IllegalArgumentException(
				"Configured endpoint cannot be unwrapped as " + (nativeType == null ? "null" : nativeType.getName()));
	}

	@Override
	public String toString() {
		return configuration.toString();
	}
}
