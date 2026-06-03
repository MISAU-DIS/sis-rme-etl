package org.openmrs.module.epts.etl.conf.types;

public enum DBOperationType {
	
	insert,
	delete,
	update;
	
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
