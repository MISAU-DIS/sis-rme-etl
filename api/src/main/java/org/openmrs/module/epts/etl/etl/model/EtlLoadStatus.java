package org.openmrs.module.epts.etl.etl.model;

public enum EtlLoadStatus {

	FULL_LOADED(4),

	PARTIALY_LOADED(3),

	NOT_LOADED(2),

	PROCESSING(1),

	NOT_PROCESSED(0),

	NOT_LOADED_DUE_ERRORS(-1);

	int status;

	EtlLoadStatus(int status) {
		this.status = status;
	}

	public int toInt() {
		return this.status;
	}

	public boolean isFullLoaded() {
		return this.equals(FULL_LOADED);
	}

	public boolean isPartialLoaded() {
		return this.equals(PARTIALY_LOADED);
	}

	public boolean isNotLoadedDueError() {
		return this.equals(NOT_LOADED_DUE_ERRORS);
	}

	public boolean isNotLoaded() {
		return this.equals(NOT_LOADED);
	}

	public boolean isNotProcessed() {
		return this.equals(NOT_PROCESSED);
	}

	public boolean isProcessing() {
		return this.equals(PROCESSING);
	}

}
