package org.openmrs.module.epts.etl.conf.types;

public enum EtlOperationStatus {
	
	//@// @formatter:off
	NOT_INITIALIZED(0),
	RUNNING(1),
	PAUSED(2),
	STOPPED(3),
	SLEEPING(4),
	FINISHING(5),
	FINISHED(6),
	STOPPED_DUE_ERROR(7),
	STOPPING(8);
	
	
	// @formatter:on
	int status;
	
	EtlOperationStatus(int status) {
		this.status = status;
	}
	
	public String getDsc() {
		return this.name();
	}
	
	public boolean notInitialized() {
		return this.equals(NOT_INITIALIZED);
	}
	
	public boolean running() {
		return this.equals(RUNNING);
	}
	
	public boolean paused() {
		return this.equals(PAUSED);
	}
	
	public boolean stopped() {
		return this.equals(STOPPED);
	}
	
	public boolean stopping() {
		return this.equals(STOPPING);
	}
	
	public boolean slepping() {
		return this.equals(SLEEPING);
	}
	
	public boolean finished() {
		return this.equals(FINISHED);
	}
	
	public boolean stoppedDueError() {
		return this.equals(STOPPED_DUE_ERROR);
	}
	
}
