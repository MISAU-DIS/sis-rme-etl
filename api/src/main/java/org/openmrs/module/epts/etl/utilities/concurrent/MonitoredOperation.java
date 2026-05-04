package org.openmrs.module.epts.etl.utilities.concurrent;

import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;

/**
 * Representa uma operacao monitorada
 * 
 * @author JPBOANE
 */
public interface MonitoredOperation extends Runnable {
	
	TimeController getTimer();
	
	EtlOperationStatus getOperationStatus();
	
	void setOperationStatus(EtlOperationStatus status);
	
	void requestStop();
	
	boolean stopRequested();
	
	default boolean isNotInitialized() {
		return this.getOperationStatus().notInitialized();
	}
	
	default boolean isRunning() {
		return this.getOperationStatus().running();
	}
	
	default boolean isStopped() {
		return this.getOperationStatus().stopped();
	}
	
	default boolean isFinished() {
		return this.getOperationStatus().finished();
	}
	
	default boolean isPaused() {
		return this.getOperationStatus().paused();
	}
	
	default boolean isSleeping() {
		return this.getOperationStatus().slepping();
	}
	
	default void changeStatusToRunning() {
		this.setOperationStatus(EtlOperationStatus.STATUS_RUNNING);
	}
	
	default void changeStatusToStopped() {
		this.setOperationStatus(EtlOperationStatus.STATUS_STOPPED);
	}
	
	default void changeStatusToFinished() {
		this.setOperationStatus(EtlOperationStatus.STATUS_FINISHED);
	}
	
	default void changeStatusToPaused() {
		this.setOperationStatus(EtlOperationStatus.STATUS_PAUSED);
	}
	
	default void changeStatusToSleeping() {
		this.setOperationStatus(EtlOperationStatus.STATUS_SLEEPING);
	}
	
	default void onStart() {
		changeStatusToStopped();
	}
	
	default void onSleep() {
		changeStatusToSleeping();
	}
	
	default void onStop() {
		changeStatusToStopped();
	}
	
	default void onFinish() {
		changeStatusToFinished();
	}
	
	public abstract int getWaitTimeToCheckStatus();
	
}
