package org.openmrs.module.epts.etl.conf.types;

public enum EtlSide {
	// @formatter:off

	SRC,
	DST;

	// @formatter:on
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
