package org.openmrs.module.epts.etl.utilities;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmrs.module.epts.etl.Main;
import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class EtlLogger {
	
	private final Map<String, Date> lastLogDates = new ConcurrentHashMap<>();
	
	static CommonUtilities utilities = CommonUtilities.getInstance();
	
	Logger logger = LoggerFactory.getLogger(Main.class);
	
	private Level level_;
	
	private boolean blocked;
	
	public <T> EtlLogger(Class<T> clazz) {
		this.logger = LoggerFactory.getLogger(clazz);
		this.level_ = determineLogLevel();
		
		this.blocked = false;
	}
	
	public <T> EtlLogger(Logger logger) {
		this.logger = logger;
		this.level_ = determineLogLevel();
		
		this.blocked = false;
	}
	
	public static <T> EtlLogger getLogger(Class<T> clazz) {
		return new EtlLogger(clazz);
	}
	
	public Level getLevel_() {
		return level_;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public static Level determineLogLevel() {
		String log = System.getProperty("log.level");
		
		if (!utilities.stringHasValue(log))
			return Level.INFO;
		
		if (log.equals("DEBUG"))
			return Level.DEBUG;
		if (log.equals("INFO"))
			return Level.INFO;
		if (log.equals("WARN"))
			return Level.WARN;
		if (log.equals("ERROR"))
			return Level.ERROR;
		if (log.equals("TRACE"))
			return Level.TRACE;
		
		throw new ForbiddenOperationException("Unsupported Log Level [" + log + "]");
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	/**
	 * Inteligent logwarn. It only logs if the elapsed time (in seconds) after the last log is
	 * greater that #logInterval
	 * 
	 * @param msg the message to log
	 * @param logInterval the max log interval permited fore repited logs
	 */
	public void warn(String msg, double logInterval) {
		
		synchronized (msg) {
			
		}
		
		Date now = utilities.getCurrentDate();
		
		Date lastDate = lastLogDates.get(msg);
		
		if (lastDate == null) {
			
			warn(msg);
			lastLogDates.put(msg, now);
			
			return;
		}
		
		double elapsedTime = DateAndTimeUtilities.dateDiff(now, lastDate, DateAndTimeUtilities.SECOND_FORMAT);
		
		if (elapsedTime >= logInterval) {
			
			warn(msg);
			
			lastLogDates.put(msg, now);
		}
	}
	
	public void warn(String msg) {
		if (Level.WARN.compareTo(level_) <= 0) {
			msg = putAdditionalInfoOnLog(msg);
			
			logger.warn(msg);
		}
	}
	
	public void info(String msg) {
		if (Level.INFO.compareTo(level_) <= 0) {
			msg = putAdditionalInfoOnLog(msg);
			
			logger.info(msg);
		}
	}
	
	public void error(String msg) {
		if (Level.ERROR.compareTo(level_) <= 0) {
			
			msg = putAdditionalInfoOnLog(msg);
			
			logger.error(msg);
		}
	}
	
	public void debug(String msg) {
		if (Level.DEBUG.compareTo(level_) <= 0) {
			
			msg = putAdditionalInfoOnLog(msg);
			
			logger.debug(msg);
		}
	}
	
	public void trace(String msg) {
		if (Level.TRACE.compareTo(level_) <= 0) {
			
			msg = putAdditionalInfoOnLog(msg);
			
			logger.trace(msg);
		}
	}
	
	String putAdditionalInfoOnLog(String msg) {
		return msg;
	}
	
}
