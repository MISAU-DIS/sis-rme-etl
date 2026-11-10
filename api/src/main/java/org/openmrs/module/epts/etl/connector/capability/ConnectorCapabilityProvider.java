package org.openmrs.module.epts.etl.connector.capability;

import org.openmrs.module.epts.etl.connector.capability.aggregator.ConnectorRecordCapabilityProviderAggregator;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public interface ConnectorCapabilityProvider<T extends EtlDatabaseObject> {
	ConnectorRecordCapabilityProviderAggregator getAggregator();

	void setAggregator(ConnectorRecordCapabilityProviderAggregator aggregator);
}
