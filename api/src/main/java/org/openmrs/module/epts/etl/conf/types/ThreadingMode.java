package org.openmrs.module.epts.etl.conf.types;

public enum ThreadingMode {
	// @formatter:off

	MULTI,
	MULTITHREAD,
	MULTI_THREAD,
	SINGLE,
	SINGLETHREAD,
	SINGLE_THREAD;
	
	// @formatter:on

	public boolean isMultiThread() {
		return this.equals(MULTI) || this.equals(MULTITHREAD) || this.equals(MULTI_THREAD);
	}

	public boolean isSingleThread() {
		return this.equals(SINGLE) || this.equals(SINGLETHREAD) || this.equals(SINGLE_THREAD);
	}
}
