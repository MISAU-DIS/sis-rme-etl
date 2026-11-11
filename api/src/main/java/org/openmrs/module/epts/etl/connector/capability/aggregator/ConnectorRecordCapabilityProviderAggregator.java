package org.openmrs.module.epts.etl.connector.capability.aggregator;

import java.util.List;

import org.openmrs.module.epts.etl.conf.interfaces.BaseConfiguration;
import org.openmrs.module.epts.etl.connector.ConnectorResource;
import org.openmrs.module.epts.etl.connector.capability.ConnectorCapabilityProvider;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;
import org.openmrs.module.epts.etl.model.pojo.generic.EtlObjectConfiguration;

public interface ConnectorRecordCapabilityProviderAggregator {

	<T extends EtlDatabaseObject> ConnectorCapabilityProvider<T> get(ConnectorResource resource);

	List<ConnectorCapabilityProvider<?>> collectAvaliableCapabilities();

	default <T extends EtlDatabaseObject> ConnectorCapabilityProvider<T> get(EtlObjectConfiguration configuration) {
		return this.get(configuration.getResolvedResource());
	}

	default void finalizeAvaliableCapabilities(BaseConfiguration openedFrom) {
		List<ConnectorCapabilityProvider<?>> capabilities = this.collectAvaliableCapabilities();

		if (capabilities != null) {

			for (ConnectorCapabilityProvider<?> c : capabilities) {
				c.finalizeCapability(openedFrom);
			}
		}
	}

	default void markAvaliableCapabilitiesAsSuccessifullyTerminected() {
		List<ConnectorCapabilityProvider<?>> capabilities = this.collectAvaliableCapabilities();

		if (capabilities != null) {

			for (ConnectorCapabilityProvider<?> c : capabilities) {
				c.markAsSuccessifullyTerminected();
			}
		}
	}

	default void loadAggregatorToAvaliableCapabilities() {
		List<ConnectorCapabilityProvider<?>> capabilities = this.collectAvaliableCapabilities();

		if (capabilities != null) {

			for (ConnectorCapabilityProvider<?> c : capabilities) {
				c.setAggregator(this);
			}
		}
	}
}
