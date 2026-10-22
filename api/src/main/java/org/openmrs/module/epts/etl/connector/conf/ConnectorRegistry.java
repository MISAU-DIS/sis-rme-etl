package org.openmrs.module.epts.etl.connector.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.connector.ConnectorDirection;
import org.openmrs.module.epts.etl.connector.ConnectorEndpoint;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;
import org.openmrs.module.epts.etl.utilities.db.conn.DBConnectionInfo;

/** Immutable, per-configuration registry of resolved connector endpoints. */
public final class ConnectorRegistry {

	public static final String LEGACY_SOURCE_CONNECTOR_ID = "legacy-src";
	public static final String LEGACY_DESTINATION_CONNECTOR_ID = "legacy-dst";

	private static final CommonUtilities UTILITIES = CommonUtilities.getInstance();

	private final Map<String, ConnectorEndpoint> endpoints;
	private final List<String> configuredIds;

	public ConnectorRegistry(List<ConnectorEndpointConfiguration> configurations, DBConnectionInfo legacySource,
			DBConnectionInfo legacyDestination) {

		Map<String, ConnectorEndpoint> resolved = new LinkedHashMap<>();
		List<String> explicitIds = new ArrayList<>();

		if (configurations != null) {
			for (ConnectorEndpointConfiguration configuration : configurations) {
				if (configuration == null || !UTILITIES.stringHasValue(configuration.getId())) {
					throw new IllegalArgumentException("Every connector must define a non-empty id");
				}

				String id = configuration.getId();

				if (isLegacyId(id)) {
					throw new IllegalArgumentException(
							"Connector id '" + id + "' is reserved for legacy compatibility");
				}

				if (resolved.put(id, ConnectorConfigurationResolver.resolveEndpoint(configuration, null)) != null) {
					throw new IllegalArgumentException("Connector id '" + id + "' is duplicated");
				}

				explicitIds.add(id);
			}
		}

		if (legacySource != null) {
			resolved.put(LEGACY_SOURCE_CONNECTOR_ID,
					ConnectorConfigurationResolver.resolveEndpoint(null, legacySource));
		}

		if (legacyDestination != null) {
			resolved.put(LEGACY_DESTINATION_CONNECTOR_ID,
					ConnectorConfigurationResolver.resolveEndpoint(null, legacyDestination));
		}

		this.endpoints = Collections.unmodifiableMap(resolved);
		this.configuredIds = Collections.unmodifiableList(explicitIds);
	}

	public Map<String, ConnectorEndpoint> getEndpoints() {
		return endpoints;
	}

	public ConnectorEndpoint get(String id) {
		ConnectorEndpoint endpoint = endpoints.get(id);
		if (endpoint == null) {
			throw new IllegalArgumentException("Unknown connector id '" + id + "'");
		}
		return endpoint;
	}

	public ConnectorEndpoint select(String requestedId, String defaultId, ConnectorDirection direction,
			boolean preferLegacy) {

		String selectedId = requestedId;

		if (!UTILITIES.stringHasValue(selectedId)) {
			selectedId = defaultId;
		}

		if (!UTILITIES.stringHasValue(selectedId) && preferLegacy) {
			String legacyId = legacyId(direction);
			if (endpoints.containsKey(legacyId))
				selectedId = legacyId;
		}

		if (!UTILITIES.stringHasValue(selectedId) && configuredIds.size() == 1) {
			selectedId = configuredIds.get(0);
		}

		if (!UTILITIES.stringHasValue(selectedId)) {
			String legacyId = legacyId(direction);
			if (configuredIds.isEmpty() && endpoints.containsKey(legacyId))
				selectedId = legacyId;
		}

		if (!UTILITIES.stringHasValue(selectedId)) {
			throw new IllegalArgumentException("Connector selection is ambiguous for " + direction
					+ "; specify the resource connector or a default connector");
		}

		return ConnectorConfigurationResolver.requireDirectionCapability(get(selectedId), direction);
	}

	private static boolean isLegacyId(String id) {
		return LEGACY_SOURCE_CONNECTOR_ID.equals(id) || LEGACY_DESTINATION_CONNECTOR_ID.equals(id);
	}

	private static String legacyId(ConnectorDirection direction) {
		return direction == ConnectorDirection.SOURCE ? LEGACY_SOURCE_CONNECTOR_ID : LEGACY_DESTINATION_CONNECTOR_ID;
	}
}
