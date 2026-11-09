package org.openmrs.module.epts.etl.conf.destination;

import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.ChildTable;
import org.openmrs.module.epts.etl.conf.UniqueKeyInfo;
import org.openmrs.module.epts.etl.conf.interfaces.ParentTable;
import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;

/**
 * Connector-neutral description of the records accepted by one physical
 * destination variant.
 */
public interface DestinationDataConfiguration {

	String getName();

	UniqueKeyInfo getPrimaryKey();

	default List<UniqueKeyInfo> getUniqueKeys() {
		return java.util.Collections.emptyList();
	}

	default List<ParentTable> getParentRefInfo() {
		return java.util.Collections.emptyList();
	}

	default List<ChildTable> getChildRefInfo() {
		return java.util.Collections.emptyList();
	}

	default String getSharePkWith() {
		return null;
	}

	EtlDestinationConfiguration getParentDestination();

	void setParentDestination(EtlDestinationConfiguration parentDestination);

	Set<ConnectorWriteMode> getSupportedWriteModes();

	default boolean supports(ConnectorWriteMode mode) {
		return mode != null && getSupportedWriteModes().contains(mode);
	}
}
