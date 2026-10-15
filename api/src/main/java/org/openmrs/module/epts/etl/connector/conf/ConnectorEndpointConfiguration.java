package org.openmrs.module.epts.etl.connector.conf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.epts.etl.connector.ConnectorType;

/** Serializable, connector-neutral endpoint configuration. */
public class ConnectorEndpointConfiguration {

	private String id;
	private ConnectorType type;
	private String uri;
	private String userName;
	private String password;
	private Map<String, Object> properties;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ConnectorType getType() {
		return type != null ? type : ConnectorType.JDBC;
	}

	public void setType(ConnectorType type) {
		this.type = type;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, Object> getProperties() {
		return properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties == null ? null : new LinkedHashMap<>(properties);
	}

	public boolean hasProperty(String name) {
		return getProperties().containsKey(name);
	}

	public Object getProperty(String name) {
		return getProperties().get(name);
	}

	@Override
	public String toString() {
		return "ConnectorEndpointConfiguration[id=" + id + ", type=" + getType() + ", uri=" + uri + ", userName="
				+ userName + ", properties=" + getProperties().keySet() + "]";
	}
}

