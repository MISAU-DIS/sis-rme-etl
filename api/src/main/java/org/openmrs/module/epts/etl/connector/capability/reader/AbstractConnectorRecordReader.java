package org.openmrs.module.epts.etl.connector.capability.reader;

import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordReader;
import org.openmrs.module.epts.etl.connector.capability.aggregator.ConnectorRecordCapabilityProviderAggregator;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public abstract class AbstractConnectorRecordReader<T extends EtlDatabaseObject> implements ConnectorRecordReader<T> {

	private ConnectorRecordReadersAggregator readersAggregator;

	@Override
	public ConnectorRecordCapabilityProviderAggregator getAggregator() {
		return this.readersAggregator;
	}

	@Override
	public void setAggregator(ConnectorRecordCapabilityProviderAggregator aggregator) {

		if (aggregator instanceof ConnectorRecordReadersAggregator) {
			this.readersAggregator = (ConnectorRecordReadersAggregator) aggregator;
		} else {
			throw new EtlConfException("Only ConnectorRecordReadersAggregator is accepted for ConnectorRecordReader");
		}
	}
}