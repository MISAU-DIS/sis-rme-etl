package org.openmrs.module.epts.etl.conf.datasource;

import java.util.List;

public class PreparedQueryInfo {
	
	private final String query;
	
	private final List<Object> parameters;
	
	public PreparedQueryInfo(String query, List<Object> parameters) {
		this.query = query;
		this.parameters = parameters;
	}
	
	public String getQuery() {
		return query;
	}
	
	public List<Object> getParameters() {
		return parameters;
	}
	
	public Object[] getParametersAsArray() {
		return this.getParameters() != null ? this.getParameters().toArray() : null;
	}
}
