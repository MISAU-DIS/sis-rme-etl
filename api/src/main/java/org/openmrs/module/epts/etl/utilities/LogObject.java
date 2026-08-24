package org.openmrs.module.epts.etl.utilities;

import java.util.List;

public enum LogObject {
	CONF, PROCESSOR, CONN, DB_QUERY, DATA_SOURCE, TABLE_CONF, TRANSFORMATION;

	public boolean isExcluded(List<LogObject> exclusions) {
		return exclusions != null && exclusions.contains(this);
	}
}
