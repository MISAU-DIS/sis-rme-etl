package org.openmrs.module.epts.etl.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

/** Readers resolved for one ETL worker, indexed by source configuration. */
public final class ConnectorRecordReaders {

	private final Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers;
	private final Map<EtlDataSource, ConnectorResource> resourcesByConfiguration;
	private final ConnectorRecordReader<? extends EtlDatabaseObject> primaryReader;

	public ConnectorRecordReaders(
			Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers,
			Map<EtlDataSource, ConnectorResource> resourcesByConfiguration) {
		this(null, readers, resourcesByConfiguration);
	}

	public ConnectorRecordReaders(ConnectorRecordReader<? extends EtlDatabaseObject> primaryReader,
			Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers,
			Map<EtlDataSource, ConnectorResource> resourcesByConfiguration) {
		this.primaryReader = primaryReader;
		this.readers = Collections.unmodifiableMap(new IdentityHashMap<>(readers));
		this.resourcesByConfiguration = Collections
				.unmodifiableMap(new IdentityHashMap<>(resourcesByConfiguration));
	}

	@SuppressWarnings("unchecked")
	public <T extends EtlDatabaseObject> ConnectorRecordReader<T> getPrimary() {
		if (primaryReader == null) {
			throw new IllegalStateException("No primary reader was registered");
		}
		return (ConnectorRecordReader<T>) primaryReader;
	}

	@SuppressWarnings("unchecked")
	public <T extends EtlDatabaseObject> ConnectorRecordReader<T> get(ConnectorResource resource) {
		ConnectorRecordReader<?> reader = readers.get(resource);
		if (reader == null) {
			throw new IllegalArgumentException("No reader registered for source resource " + resource);
		}
		return (ConnectorRecordReader<T>) reader;
	}

	public <T extends EtlDatabaseObject> ConnectorRecordReader<T> get(EtlDataSource configuration) {
		return get(getResource(configuration));
	}

	public ConnectorResource getResource(EtlDataSource configuration) {
		ConnectorResource resource = resourcesByConfiguration.get(configuration);
		if (resource == null) {
			throw new IllegalArgumentException("No source resource registered for " + configuration);
		}
		return resource;
	}

	public List<ConnectorRecordReader<? extends EtlDatabaseObject>> getAll() {
		List<ConnectorRecordReader<? extends EtlDatabaseObject>> all = new ArrayList<>(readers.values());
		if (primaryReader != null && !containsIdentity(all, primaryReader)) {
			all.add(primaryReader);
		}
		return Collections.unmodifiableList(all);
	}

	public <N> List<N> unwrapAll(Class<N> nativeType) {
		List<N> values = new ArrayList<>();
		for (ConnectorRecordReader<?> reader : getAll()) {
			N value;
			try {
				value = reader.unwrap(nativeType);
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
}
