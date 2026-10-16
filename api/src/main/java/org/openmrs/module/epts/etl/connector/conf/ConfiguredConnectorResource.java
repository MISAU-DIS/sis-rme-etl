package org.openmrs.module.epts.etl.connector.conf;

import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.connector.ConnectorResource;

/** Neutral resource representation not tied to a relational table. */
final class ConfiguredConnectorResource implements ConnectorResource {

	private final ConnectorResourceConfiguration configuration;
	private final ConnectorDirection direction;
	private final ConnectorEndpoint endpoint;

	ConfiguredConnectorResource(ConnectorResourceConfiguration configuration, ConnectorDirection direction,
			ConnectorEndpoint endpoint) {
		this.configuration = configuration;
		this.direction = direction;
		this.endpoint = endpoint;
	}

	@Override
	public String getName() {
		return configuration.getName();
	}

	@Override
	public ConnectorDirection getDirection() {
		return direction;
	}

	@Override
	public ConnectorEndpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public <T> T unwrap(Class<T> nativeType) {
		if (nativeType != null && nativeType.isInstance(configuration)) {
			return nativeType.cast(configuration);
		}
		throw new IllegalArgumentException(
				"Configured resource cannot be unwrapped as " + (nativeType == null ? "null" : nativeType.getName()));
	}

	@Override
	public String toString() {
		return "ConfiguredConnectorResource[name=" + getName() + ", direction=" + direction + ", endpoint=" + endpoint
				+ "]";
	}
}
