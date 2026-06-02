package org.openmrs.module.epts.etl.utilities.concurrent;

import java.util.List;

import org.openmrs.module.epts.etl.conf.types.EtlOperationStatus;
import org.openmrs.module.epts.etl.utilities.CommonUtilities;

/**
 * Representa uma operacao monitorada
 * 
 * @author JPBOANE
 */
public interface MonitoredOperation extends Runnable {
	
	static final CommonUtilities utilities = CommonUtilities.getInstance();
	
	TimeController getTotalTimer();
	
	TimeController getPauseTimer();
	
	TimeController getProcessingTimer();
	
	EtlOperationStatus getOperationStatus();
	
	void setOperationStatus(EtlOperationStatus status);
	
	void requestStop();
	
	boolean stopRequested();
	
	int getWaitTimeToCheckStatus();
	
	String getOperationId();
	
	default boolean isNotInitialized() {
		return this.getOperationStatus().notInitialized();
	}
	
	default boolean isRunning() {
		return this.getOperationStatus().running();
	}
	
	default boolean isStopped() {
		return this.getOperationStatus().stopped();
	}
	
	default boolean isStopping() {
		return this.getOperationStatus().stopping();
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
		this.setOperationStatus(EtlOperationStatus.RUNNING);
	}
	
	default void changeStatusToStopped() {
		this.setOperationStatus(EtlOperationStatus.STOPPED);
	}
	
	default void changeStatusToStopping() {
		this.setOperationStatus(EtlOperationStatus.STOPPING);
	}
	
	default void changeStatusToFinished() {
		this.setOperationStatus(EtlOperationStatus.FINISHED);
	}
	
	default void changeStatusToPaused() {
		this.setOperationStatus(EtlOperationStatus.PAUSED);
	}
	
	default void changeStatusToSleeping() {
		this.setOperationStatus(EtlOperationStatus.SLEEPING);
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
	
	void logWarn(String msg);
	
	void logWarn(String msg, long interval, boolean suppressIfAnyRecentLog);
	
	default void waitForOperationToStopStop(List<MonitoredOperation> operations) {
		if (!utilities.listHasElement(operations)) {
			return;
		}
		
		int maxIterations = 120;
		
		for (int iteration = 0; iteration < maxIterations; iteration++) {
			boolean anyRunning = false;
			
			for (MonitoredOperation operation : operations) {
				if (!operation.isStopped()) {
					anyRunning = true;
					logWarn("STOP REQUESTED DUE ERROR BUT OPERATION IS STILL RUNNING: " + operation.getOperationId(), 120,
					    false);
				}
			}
			
			if (!anyRunning) {
				return;
			}
			
			TimeCountDown.sleep(5);
		}
		
		logWarn("Timeout while waiting for engines to stop.");
	}
	
}
