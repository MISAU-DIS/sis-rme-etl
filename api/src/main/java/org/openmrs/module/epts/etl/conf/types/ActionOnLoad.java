package org.openmrs.module.epts.etl.conf.types;

public enum ActionOnLoad {
	NONE, UPDATE, PATCH_UPDATE;

	public boolean isPatchUpdate() {
		return this == PATCH_UPDATE;
	}

	public boolean isUpdate() {
		return this == UPDATE;
	}

	public boolean isNone() {
		return this == NONE;
	}
}
