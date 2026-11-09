package org.openmrs.module.epts.etl.conf.destination;

import java.util.EnumSet;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.DstConf;
import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** JDBC table destination variant backed by the current {@link DstConf}. */
public class JdbcTableDestinationConfiguration extends DstConf implements DestinationDataConfiguration {

	@JsonIgnore
	private transient EtlDestinationConfiguration parentDestination;

	public JdbcTableDestinationConfiguration() {
	}

	public JdbcTableDestinationConfiguration(String tableName) {
		super(tableName);
	}

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
		return EnumSet.allOf(ConnectorWriteMode.class);
	}
}
