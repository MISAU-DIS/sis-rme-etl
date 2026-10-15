package org.openmrs.module.epts.etl.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Writers resolved for one ETL execution, indexed by their destination resource. */
public final class ConnectorRecordWriters {

	private final Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers;
	private final Map<DstConf, ConnectorResource> resourcesByConfiguration;

	public ConnectorRecordWriters(
			Map<ConnectorResource, ConnectorRecordWriter<? extends EtlDatabaseObject>> writers,
			Map<DstConf, ConnectorResource> resourcesByConfiguration) {
		this.writers = Collections.unmodifiableMap(new IdentityHashMap<>(writers));
		this.resourcesByConfiguration = Collections.unmodifiableMap(new IdentityHashMap<>(resourcesByConfiguration));
	}

	@SuppressWarnings("unchecked")
	public <T extends EtlDatabaseObject> ConnectorRecordWriter<T> get(ConnectorResource resource) {
		ConnectorRecordWriter<?> writer = writers.get(resource);
		if (writer == null) {
			throw new IllegalArgumentException("No writer registered for destination resource " + resource);
		}
		return (ConnectorRecordWriter<T>) writer;
	}

	public <T extends EtlDatabaseObject> ConnectorRecordWriter<T> get(DstConf configuration) {
		ConnectorResource resource = resourcesByConfiguration.get(configuration);
		if (resource == null) {
			throw new IllegalArgumentException("No destination resource registered for " + configuration);
		}
		return get(resource);
	}

	public ConnectorResource getResource(DstConf configuration) {
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
			N value = writer.unwrap(nativeType);
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
			N value = writer.unwrap(nativeType);
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
}
