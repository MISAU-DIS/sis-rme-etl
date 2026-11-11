package org.openmrs.module.epts.etl.connector.capability.reader;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.epts.etl.conf.interfaces.EtlDataSource;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.ConnectorCapabilityProvider;
import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.aggregator.ConnectorRecordCapabilityProviderAggregator;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;

/** Readers resolved for one ETL worker, indexed by source configuration. */
public final class ConnectorRecordReadersAggregator implements ConnectorRecordCapabilityProviderAggregator {

	private final Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers;
	private final Map<EtlDataSource, ConnectorResource> resourcesByConfiguration;
	private final ConnectorRecordReader<? extends EtlDatabaseObject> primaryReader;

	public static ConnectorRecordReadersAggregator ofPrimary(
			ConnectorRecordReader<? extends EtlDatabaseObject> primaryReader) {
		return new ConnectorRecordReadersAggregator(primaryReader, Collections.emptyMap(), Collections.emptyMap());
	}

	public ConnectorRecordReadersAggregator(
			Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers,
			Map<EtlDataSource, ConnectorResource> resourcesByConfiguration) {
		this(null, readers, resourcesByConfiguration);
	}

	public ConnectorRecordReadersAggregator(ConnectorRecordReader<? extends EtlDatabaseObject> primaryReader,
			Map<ConnectorResource, ConnectorRecordReader<? extends EtlDatabaseObject>> readers,
			Map<EtlDataSource, ConnectorResource> resourcesByConfiguration) {

		this.primaryReader = primaryReader;
		this.readers = Collections.unmodifiableMap(new IdentityHashMap<>(readers));
		this.resourcesByConfiguration = Collections.unmodifiableMap(new IdentityHashMap<>(resourcesByConfiguration));
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

	@Override
	public <T extends EtlDatabaseObject> ConnectorRecordReader<T> get(EtlObjectConfiguration configuration) {
		return get(getResource(configuration));
	}

	public ConnectorResource getResource(EtlObjectConfiguration configuration) {
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

	@Override
	public List<ConnectorCapabilityProvider<?>> collectAvaliableCapabilities() {
		return new ArrayList<ConnectorCapabilityProvider<?>>(readers.values());
	}

	public Connection getEtlSrcConn() {
		return null;
	}
}
