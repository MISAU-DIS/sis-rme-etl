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

	public boolean isDst() {
		return this == DST;
	}

	public boolean isSrc() {
		return this == SRC;
	}

	public boolean isDestination() {
		return isDst();
	}

	public boolean isSource() {
		return isSrc();
	}
}
