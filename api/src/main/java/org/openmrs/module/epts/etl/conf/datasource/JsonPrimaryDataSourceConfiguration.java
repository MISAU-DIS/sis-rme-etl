package org.openmrs.module.epts.etl.conf.datasource;

import java.util.List;

import org.openmrs.module.epts.etl.model.Field;

/** Logical JSON records, independently of the connector transporting them. */
public class JsonPrimaryDataSourceConfiguration extends AbstractRecordPrimaryDataSourceConfiguration {

	private String recordPath;

	private JsonRecordLayout layout = JsonRecordLayout.JSON_LINES;

	private List<Field> fields;

	public String getRecordPath() {
		return recordPath;
	}

	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}

	public JsonRecordLayout getLayout() {
		return layout;
	}

	public void setLayout(JsonRecordLayout layout) {
		this.layout = layout;
	}

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
		return "json";
	}
}
