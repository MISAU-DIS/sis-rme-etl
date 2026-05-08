package org.openmrs.module.epts.etl.conf.types;

public enum EtlOperationStatus {
	
	STATUS_NOT_INITIALIZED(0),
	STATUS_RUNNING(1),
	STATUS_PAUSED(2),
	STATUS_STOPPED(3),
	STATUS_SLEEPING(4),
	STATUS_FINISHING(5),
	STATUS_FINISHED(6),
	STATUS_STOPPED_DUE_ERROR(7);
	
	int status;
	
	EtlOperationStatus(int status) {
		this.status = status;
	}
	
	public boolean notInitialized() {
		return this.equals(STATUS_NOT_INITIALIZED);
	}
	
	public boolean running() {
		return this.equals(STATUS_RUNNING);
	}
	
	public boolean paused() {
		return this.equals(STATUS_PAUSED);
	}
	
	public boolean stopped() {
		return this.equals(STATUS_STOPPED);
	}
	
	public boolean slepping() {
		return this.equals(STATUS_SLEEPING);
	}
	
	public boolean finished() {
		return this.equals(STATUS_FINISHED);
	}
	
	public boolean stoppedDueError() {
		return this.equals(STATUS_STOPPED_DUE_ERROR);
	}
	
}
