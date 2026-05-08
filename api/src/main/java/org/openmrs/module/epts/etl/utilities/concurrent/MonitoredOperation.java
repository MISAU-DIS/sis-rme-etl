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
	
	TimeController getTimer();
	
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
	
	void logWarn(String msg);
	
	void logWarn(String msg, long interval);
	
	default void waitForOperationToStopStop(List<MonitoredOperation> operations) {
		if (!utilities.listHasElement(operations)) {
			return;
		}
		
		int maxIterations = 120; // 120 * 5s = 10 minutos
		
		for (int iteration = 0; iteration < maxIterations; iteration++) {
			boolean anyRunning = false;
			
			for (MonitoredOperation operation : operations) {
				if (!operation.isStopped()) {
					anyRunning = true;
					logWarn("STOP REQUESTED DUE ERROR BUT OPERATION IS STILL RUNNING: " + operation.getOperationId(), 120);
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
