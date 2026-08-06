package org.openmrs.module.epts.etl.etl.model;

public enum LoadingType {

	/**
	 * The principal or main loading
	 */
	PRINCIPAL,

	/**
	 * The inner loading, usually for configured child loading
	 */
	CHILD,

	/**
	 * The inner loading, usually for parent loading
	 */
	PARENT;

	public boolean isPrincipal() {
		return this.equals(PRINCIPAL);
	}

	public boolean isParent() {
		return this.equals(PARENT);
	}

	public boolean isChild() {
		return this.equals(CHILD);
	}
}
