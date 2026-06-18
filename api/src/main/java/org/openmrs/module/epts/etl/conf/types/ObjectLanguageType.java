package org.openmrs.module.epts.etl.conf.types;

public enum ObjectLanguageType {
	// @formatter:off

	groovy,
	java;
	
	// @formatter:on

	public boolean isJava() {
		return this.equals(java);
	}
	
	public boolean isGroovy() {
		return this.equals(groovy);
	}
	
}
