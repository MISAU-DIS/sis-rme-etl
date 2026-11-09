package org.openmrs.module.epts.etl.conf.destination;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openmrs.module.epts.etl.conf.PrimaryKey;
import org.openmrs.module.epts.etl.connector.ConnectorWriteMode;
import org.openmrs.module.epts.etl.model.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Common logical record schema for non-JDBC destination variants. */
public abstract class AbstractRecordDestinationConfiguration implements DestinationDataConfiguration {

	private String name;
	private List<Field> fields;
	private PrimaryKey primaryKey;

	@JsonIgnore
	private transient EtlDestinationConfiguration parentDestination;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@Override
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
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
		return Collections.singleton(ConnectorWriteMode.INSERT);
	}
}
