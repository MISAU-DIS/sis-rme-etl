package org.openmrs.module.epts.etl.conf.interfaces;

public enum DataSourceSide {
	// @formatter:off
	THIS_IS_DATA_SOURCE, 
	OTHER_IS_DATA_SOURCE, 
	NONE_IS_DATA_SOURCE;
	// @formatter:on

	public boolean thisIsDataSource() {
		return this == THIS_IS_DATA_SOURCE;
	}

	public boolean otherIsDataSource() {
		return this == OTHER_IS_DATA_SOURCE;
	}

	public boolean noneIsDataSource() {
		return this == NONE_IS_DATA_SOURCE;
	}
}
