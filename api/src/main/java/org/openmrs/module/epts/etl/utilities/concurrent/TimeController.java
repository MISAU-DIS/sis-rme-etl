package org.openmrs.module.epts.etl.utilities.concurrent;

import java.io.Serializable;

import org.openmrs.module.epts.etl.exceptions.EtlExceptionImpl;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TimeController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DURACAO_IN_MINUTES = "MINUTES";
	
	public static final String DURACAO_IN_SECONDS = "SECONDS";
	
	public static final String DURACAO_IN_HOURS = "HOURS";
	
	public static final String DURACAO_IN_DAYS = "DAYS";
	
	private long startedAtNanos;
	
	private long accumulatedNanos;
	
	private boolean running;
	
	public TimeController() {
		this.accumulatedNanos = 0;
		this.running = false;
	}
	
	public TimeController(double elapsedTimeInSeconds) {
		this.accumulatedNanos = (long) (elapsedTimeInSeconds * 1_000_000_000L);
		this.running = false;
	}
	
	public static TimeController retrieveTimer(double elapsedTimeInSeconds) {
		return new TimeController(elapsedTimeInSeconds);
	}
	
	public synchronized void start() {
		if (running) {
			throw new ForbiddenOperationException("The timer is already running!!!");
		}
		
		this.startedAtNanos = System.nanoTime();
		this.running = true;
	}
	
	public synchronized void restart() {
		this.accumulatedNanos = 0;
		this.startedAtNanos = System.nanoTime();
		this.running = true;
	}
	
	public synchronized void stop() {
		if (running) {
			this.accumulatedNanos += System.nanoTime() - this.startedAtNanos;
			this.running = false;
		}
	}
	
	public synchronized double getCurrentTakenTime() {
		return getElapsedSeconds();
	}
	
	public synchronized double getElapsedSeconds() {
		return getElapsedNanos() / 1_000_000_000.0;
	}
	
	private long getElapsedNanos() {
		if (running) {
			return accumulatedNanos + (System.nanoTime() - startedAtNanos);
		}
		
		return accumulatedNanos;
	}
	
	public synchronized double getDuration(String durationType) {
		double totalInSeconds = getElapsedSeconds();
		
		switch (durationType) {
			case DURACAO_IN_SECONDS:
				return totalInSeconds;
			
			case DURACAO_IN_MINUTES:
				return totalInSeconds / 60;
			
			case DURACAO_IN_HOURS:
				return totalInSeconds / 3600;
			
			case DURACAO_IN_DAYS:
				return totalInSeconds / 86400;
			
			default:
				throw new EtlExceptionImpl("Unsupported durationType: '" + durationType + "'!");
		}
	}
	
	@JsonIgnore
	@Override
	public String toString() {
		return formatToHumanReadbleTime();
	}
	
	@JsonIgnore
	public synchronized String formatToHumanReadbleTime() {
		long totalSeconds = (long) getElapsedSeconds();
		
		long days = totalSeconds / 86400;
		long remaining = totalSeconds % 86400;
		
		long hours = remaining / 3600;
		remaining %= 3600;
		
		long minutes = remaining / 60;
		long seconds = remaining % 60;
		
		if (days > 0) {
			return days + "d " + pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
		}
		
		return pad(hours) + ":" + pad(minutes) + ":" + pad(seconds);
	}
	
	private String pad(long value) {
		return value < 10 ? "0" + value : String.valueOf(value);
	}
	
	public synchronized boolean isRunning() {
		return running;
	}
}
