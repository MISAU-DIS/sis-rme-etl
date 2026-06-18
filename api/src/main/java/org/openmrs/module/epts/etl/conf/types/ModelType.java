package org.openmrs.module.epts.etl.conf.types;


public enum ModelType {
	// @formatter:off
	OPENMRS,
	OTHER;
	// @formatter:on
	
	public boolean isOpenMRS() {
		return this.equals(OPENMRS);
	}
}
