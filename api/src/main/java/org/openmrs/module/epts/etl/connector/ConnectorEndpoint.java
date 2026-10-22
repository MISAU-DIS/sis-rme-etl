package org.openmrs.module.epts.etl.connector;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Connector-neutral view of an external system. Implementations may adapt a
 * legacy configuration object while configuration migration is in progress.
 */
public interface ConnectorEndpoint {

	ConnectorType getType();

	String getUri();

	String getUserName();

	String getPassword();

	Set<ConnectorCapability> getCapabilities();

	default boolean supports(ConnectorCapability capability) {
		return getCapabilities().contains(capability);
	}

	default boolean supportsAll(Collection<ConnectorCapability> requiredCapabilities) {
		return getCapabilities().containsAll(requiredCapabilities);
	}

	default void requireCapabilities(Collection<ConnectorCapability> requiredCapabilities) {
		if (supportsAll(requiredCapabilities)) {
			return;
		}

		EnumSet<ConnectorCapability> missing = EnumSet.copyOf(requiredCapabilities);
		missing.removeAll(getCapabilities());
		throw new UnsupportedConnectorCapabilityException(getType(), Collections.unmodifiableSet(missing));
	}

	/** Gives migration code access to the native configuration when necessary. */
	<T> T unwrap(Class<T> nativeType);
}
