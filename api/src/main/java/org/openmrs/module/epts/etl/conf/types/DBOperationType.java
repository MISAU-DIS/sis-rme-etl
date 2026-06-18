package org.openmrs.module.epts.etl.conf.types;

public enum DBOperationType {
	// @formatter:off
	insert,
	delete,
	update;
	// @formatter:on

	public boolean isInsert() {
		return this == insert;
	}

	public boolean isDelete() {
		return this == delete;
	}

	public boolean isUpdate() {
		return this == update;
	}
}
