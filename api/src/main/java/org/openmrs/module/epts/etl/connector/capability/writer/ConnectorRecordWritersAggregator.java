package org.openmrs.module.epts.etl.connector.capability.writer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.destination.EtlDestinationConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.ConnectorCapabilityProvider;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.aggregator.ConnectorRecordCapabilityProviderAggregator;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;

/**
 * Writers resolved for one ETL execution, indexed by their destination
 * resource.
 */
public final class ConnectorRecordWritersAggregator implements ConnectorRecordCapabilityProviderAggregator {

	private final Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers;
	private final Map<EtlDestinationConfiguration, ConnectorResource> resourcesByConfiguration;

	public ConnectorRecordWritersAggregator(
			Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers,
			Map<EtlDestinationConfiguration, ConnectorResource> resourcesByConfiguration) {
		this.writers = Collections.unmodifiableMap(new IdentityHashMap<>(writers));
		this.resourcesByConfiguration = Collections.unmodifiableMap(new IdentityHashMap<>(resourcesByConfiguration));
		loadAggregatorToAvaliableCapabilities();
	}

	@SuppressWarnings("unchecked")
	public <T extends EtlDatabaseObject> ConnectorRecordWriter<T> get(ConnectorResource resource) {
		ConnectorRecordWriter<?> writer = writers.get(resource);

		if (writer == null) {
			throw new IllegalArgumentException("No writer registered for destination resource " + resource);
		}
		return (ConnectorRecordWriter<T>) writer;
	}

	public <T extends EtlDatabaseObject> ConnectorRecordWriter<T> get(EtlObjectConfiguration configuration) {
		ConnectorResource resource = resourcesByConfiguration.get(configuration);

		if (resource == null) {
			throw new IllegalArgumentException("No destination resource registered for " + configuration);
		}

		return get(resource);
	}

	public ConnectorResource getResource(EtlObjectConfiguration configuration) {
		ConnectorResource resource = resourcesByConfiguration.get(configuration);

		if (resource == null) {
			throw new IllegalArgumentException("No destination resource registered for " + configuration);
		}
		return resource;
	}

	public List<ConnectorRecordWriter<? extends EtlDatabaseObject>> getAll() {
		return Collections.unmodifiableList(new ArrayList<>(writers.values()));
	}

	public <N> N unwrap(ConnectorResource resource, Class<N> nativeType) {
		return get(resource).unwrap(nativeType);
	}

	/** Returns the native value only when all writers share that same instance. */
	public <N> N unwrapShared(Class<N> nativeType) {
		N shared = null;

		for (ConnectorRecordWriter<?> writer : writers.values()) {
			N value;

			try {
				value = writer.unwrap(nativeType);
			} catch (IllegalArgumentException e) {
				continue;
			}

			if (shared != null && shared != value) {
				throw new IllegalStateException("Destination writers do not share one " + nativeType.getName());
			}
			shared = value;
		}
		return shared;
	}

	public <N> List<N> unwrapAll(Class<N> nativeType) {
		List<N> values = new ArrayList<>();

		for (ConnectorRecordWriter<?> writer : writers.values()) {
			N value;

			try {
				value = writer.unwrap(nativeType);
			} catch (IllegalArgumentException e) {
				continue;
			}

			if (!containsIdentity(values, value)) {
				values.add(value);
			}
		}
		return Collections.unmodifiableList(values);
	}

	private boolean containsIdentity(List<?> values, Object candidate) {

		for (Object value : values) {

			if (value == candidate) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<ConnectorCapabilityProvider<?>> collectAvaliableCapabilities() {
		return new ArrayList<ConnectorCapabilityProvider<?>>(writers.values());
	}
}
