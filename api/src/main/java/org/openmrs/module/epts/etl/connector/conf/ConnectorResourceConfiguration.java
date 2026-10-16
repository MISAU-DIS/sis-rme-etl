package org.openmrs.module.epts.etl.connector.conf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Serializable configuration for a table, route, queue, topic or file. */
public class ConnectorResourceConfiguration {

	private String connector;
	private String name;
	private Map<String, Object> properties;

	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Object> getProperties() {
		return properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties == null ? null : new LinkedHashMap<>(properties);
	}

	public ConnectorResourceConfiguration copy() {
		ConnectorResourceConfiguration copy = new ConnectorResourceConfiguration();
		copy.setConnector(connector);
		copy.setName(name);
		copy.setProperties(properties);
		return copy;
	}

	@Override
	public String toString() {
		return "ConnectorResourceConfiguration[connector=" + connector + ", name=" + name + ", properties="
				+ getProperties().keySet() + "]";
	}
}

