package org.openmrs.module.epts.etl.conf.datasource;

import org.openmrs.module.epts.etl.connector.conf.ConnectorResourceConfiguration;

/** Physical source resource and the rules used to locate its records. */
public class SourceResourceConfiguration extends ConnectorResourceConfiguration {

	private JsonRecordLayout layout;

	private String recordPath;

	public JsonRecordLayout getLayout() {
		return layout;
	}

	public void setLayout(JsonRecordLayout layout) {
		this.layout = layout;
	}

	public String getRecordPath() {
		return recordPath;
	}

	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}

	public JsonRecordLayout jsonLayout() {
		return layout == null ? JsonRecordLayout.JSON_LINES : layout;
	}

	public String normalizedRecordPath() {
		return recordPath == null || recordPath.trim().isEmpty() ? "/" : recordPath.trim();
	}
}
