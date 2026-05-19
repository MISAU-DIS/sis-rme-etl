package org.openmrs.module.epts.etl.etl.processor.transformer;

public enum DateAndTimeFormat {
	
	YEAR_FORMAT("yyyy"),
	DAY_FORMAT("dd"),
	MONTH_FORMAT("MM"),
	HOUR_FORMAT("HH"),
	SECOND_FORMAT("ss"),
	MINUTE_FORMAT("mm"),
	MILLISECOND_FORMAT("SSS"),
	DD_MM_YYYY_FORMAT("dd-MM-yyyy"),
	YYYY_MM_DD("yyyy-MM-dd"),
	ORACLE_DATE_TIME_FORMAT("dd-MM-yyyy HH24:MI:SS"),
	DATE_TIME_FORMAT("dd-MM-yyyy HH:mm:ss"),
	ORACLE_MINUTE_FORMAT("MI"),
	ORACLE_HOUR_FORMAT("HH24");
	
	private final String format;
	
	DateAndTimeFormat(String format) {
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
	
	public static DateAndTimeFormat resolve(String value) {
		
		if (value == null || value.isBlank()) {
			return null;
		}
		
		value = value.trim();
		
		for (DateAndTimeFormat f : values()) {
			
			if (f.name().equalsIgnoreCase(value)) {
				return f;
			}
			
			if (f.getFormat().equalsIgnoreCase(value)) {
				return f;
			}
		}
		
		return DateAndTimeFormat.valueOf(value);
	}
	
}
