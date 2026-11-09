package org.openmrs.module.epts.etl.conf.destination;

import java.util.Collections;
import java.util.Set;

import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Common logical record schema for non-JDBC destination variants. */
public abstract class AbstractRecordDestinationConfiguration implements DestinationDataConfiguration {

	@JsonIgnore
	private transient EtlDestinationConfiguration parentDestination;

	@Override
	public EtlDestinationConfiguration getParentDestination() {
		return parentDestination;
	}

	@Override
	public void setParentDestination(EtlDestinationConfiguration parentDestination) {
		this.parentDestination = parentDestination;
	}

	@Override
	public Set<ConnectorWriteMode> getSupportedWriteModes() {
		return Collections.singleton(ConnectorWriteMode.INSERT);
	}
}
