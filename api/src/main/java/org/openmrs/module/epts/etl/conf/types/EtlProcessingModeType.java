package org.openmrs.module.epts.etl.conf.types;

public enum EtlProcessingModeType {
	// @formatter:off

	SERIAL,
	PARALLEL;

	// @formatter:on

	public boolean isSerial() {
		return this.equals(SERIAL);
	}

	public boolean isParallel() {
		return this.equals(PARALLEL);
	}
}
