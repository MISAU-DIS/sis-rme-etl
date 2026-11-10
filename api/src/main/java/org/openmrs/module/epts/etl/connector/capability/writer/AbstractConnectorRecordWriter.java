package org.openmrs.module.epts.etl.connector.capability.writer;

import org.openmrs.module.epts.etl.connector.capability.ConnectorRecordWriter;
import org.openmrs.module.epts.etl.connector.capability.aggregator.ConnectorRecordCapabilityProviderAggregator;
import org.openmrs.module.epts.etl.exceptions.EtlConfException;
import org.openmrs.module.epts.etl.model.EtlDatabaseObject;

public abstract class AbstractConnectorRecordWriter<T extends EtlDatabaseObject> implements ConnectorRecordWriter<T> {

	private ConnectorRecordWritersAggregator writersAggregator;

	@Override
	public ConnectorRecordCapabilityProviderAggregator getAggregator() {
		return this.writersAggregator;
	}

	@Override
	public void setAggregator(ConnectorRecordCapabilityProviderAggregator aggregator) {

		if (aggregator instanceof ConnectorRecordWritersAggregator) {
			this.writersAggregator = (ConnectorRecordWritersAggregator) aggregator;
		} else {
			throw new EtlConfException("Only ConnectorRecordReadersAggregator is accepted for ConnectorRecordWriter");
		}
	}
}
