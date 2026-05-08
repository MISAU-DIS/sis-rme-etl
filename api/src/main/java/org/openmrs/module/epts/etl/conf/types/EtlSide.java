package org.openmrs.module.epts.etl.conf.types;

public enum EtlSide {
	
	SRC,
	DST;
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
