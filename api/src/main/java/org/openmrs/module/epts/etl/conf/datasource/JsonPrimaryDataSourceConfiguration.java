package org.openmrs.module.epts.etl.conf.datasource;

import java.util.List;

import org.openmrs.module.epts.etl.model.Field;

/** Logical JSON records, independently of the connector transporting them. */
public class JsonPrimaryDataSourceConfiguration extends AbstractRecordPrimaryDataSourceConfiguration {

	private List<Field> fields;

	@Override
	public List<Field> getFields() {
		return fields;
	}

	@Override
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@Override
	protected String getSourceTypeName() {
		return "jsonDataSource";
	}
}
