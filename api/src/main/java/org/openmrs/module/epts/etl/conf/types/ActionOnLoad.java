package org.openmrs.module.epts.etl.conf.types;

public enum ActionOnLoad {
	UPDATE, PATCH_UPDATE;

	public boolean isPatchUpdate() {
		return this == PATCH_UPDATE;
	}
}
