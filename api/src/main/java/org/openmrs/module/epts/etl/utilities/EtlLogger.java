package org.openmrs.module.epts.etl.utilities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.openmrs.module.epts.etl.exceptions.ForbiddenOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class EtlLogger {

	private static final CommonUtilities utilities = CommonUtilities.getInstance();

	private final Map<String, AtomicLong> lastLogByKey = new ConcurrentHashMap<>();

	private final AtomicLong lastEffectiveLogTimeMillis = new AtomicLong(-1);

	private final Logger logger;

	private final Level level;

	private volatile boolean blocked;

	public <T> EtlLogger(Class<T> clazz) {
		this(LoggerFactory.getLogger(clazz));
	}

	public EtlLogger(Logger logger) {
		this.logger = logger;
		this.level = determineLogLevel();
		this.blocked = false;
	}

	public static <T> EtlLogger getLogger(Class<T> clazz) {
		return new EtlLogger(clazz);
	}

	public double getElapsedTimeSinceLastEffectiveLogInSeconds() {
		long last = lastEffectiveLogTimeMillis.get();

		if (last < 0) {
			return -1;
		}

		return (System.currentTimeMillis() - last) / 1000.0;
	}

	public void warn(String msg, double logIntervalInSeconds, boolean suppressIfAnyRecentLog) {
		warn(msg, msg, logIntervalInSeconds, suppressIfAnyRecentLog);
	}

	public void warn(String msg, Object... arguments) {
		log(Level.WARN, msg, null, arguments);
	}

	public void warn(String key, String msg, double logIntervalInSeconds, boolean suppressIfAnyRecentLog) {

		if (!isEnabled(Level.WARN)) {
			return;
		}

		long now = System.currentTimeMillis();
		long intervalMillis = (long) (logIntervalInSeconds * 1000);

		AtomicLong lastLogTime = lastLogByKey.computeIfAbsent(key, k -> new AtomicLong(-1));

		while (true) {

			long lastForThisKey = lastLogTime.get();

			// 1. suprimir se esta mesma mensagem foi exibida recentemente
			if (lastForThisKey > 0 && now - lastForThisKey < intervalMillis) {
				return;
			}

			// 2. opcionalmente suprimir se QUALQUER log foi exibido recentemente
			if (suppressIfAnyRecentLog) {
				long lastGlobalLog = lastEffectiveLogTimeMillis.get();

				if (lastGlobalLog > 0 && now - lastGlobalLog < intervalMillis) {
					return;
				}
			}

			if (lastLogTime.compareAndSet(lastForThisKey, now)) {
				warn(msg);
				return;
			}
		}
	}

	public void warn(String msg) {
		log(Level.WARN, msg, null);
	}

	public void info(String msg) {
		log(Level.INFO, msg, null);
	}

	public void error(String msg) {
		log(Level.ERROR, msg, null);
	}

	public void err(String msg, Exception e) {
		log(Level.ERROR, msg, e);
	}

	public void debug(String msg) {
		log(Level.DEBUG, msg, null);
	}

	public void trace(String msg) {
		log(Level.TRACE, msg, null);
	}

	public void err(String msg, Throwable throwable, Object... arguments) {
		log(Level.ERROR, msg, throwable, arguments);
	}

	public void info(String msg, Object... arguments) {
		log(Level.INFO, msg, null, arguments);
	}

	public void trace(String msg, Object... arguments) {
		log(Level.TRACE, msg, null, arguments);
	}

	public void debug(String msg, Object... arguments) {
		log(Level.DEBUG, msg, null, arguments);
	}

	private boolean log(Level msgLevel, String msg, Throwable throwable) {
		Object[] arguments = null;

		return log(msgLevel, msg, throwable, arguments);
	}

	private boolean log(Level msgLevel, String msg, Throwable throwable, Object... arguments) {

		if (!isEnabled(msgLevel)) {
			return false;
		}

		String finalMsg = putAdditionalInfoOnLog(msg);

		switch (msgLevel) {
		case ERROR:
			if (throwable != null) {
				logger.error(finalMsg, throwable, arguments);
			} else {
				logger.error(finalMsg, arguments);
			}
			break;

		case WARN:
			logger.warn(finalMsg, arguments);
			break;

		case INFO:
			logger.info(finalMsg, arguments);
			break;

		case DEBUG:
			logger.debug(finalMsg, arguments);
			break;

		case TRACE:
			logger.trace(finalMsg, arguments);
			break;

		default:
			return false;
		}

		lastEffectiveLogTimeMillis.set(System.currentTimeMillis());

		return true;
	}

	private boolean isEnabled(Level msgLevel) {

		if (blocked) {
			return false;
		}

		if (msgLevel.compareTo(level) > 0) {
			return false;
		}

		switch (msgLevel) {
		case ERROR:
			return logger.isErrorEnabled();

		case WARN:
			return logger.isWarnEnabled();

		case INFO:
			return logger.isInfoEnabled();

		case DEBUG:
			return logger.isDebugEnabled();

		case TRACE:
			return logger.isTraceEnabled();

		default:
			return false;
		}
	}

	public static Level determineLogLevel() {
		String log = System.getProperty("log.level");

		if (!utilities.stringHasValue(log)) {
			return Level.INFO;
		}

		try {
			return Level.valueOf(log.trim().toUpperCase());
		} catch (Exception e) {
			throw new ForbiddenOperationException("Unsupported Log Level [" + log + "]");
		}
	}

	String putAdditionalInfoOnLog(String msg) {
		return msg;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void block() {
		this.blocked = true;
	}

	public void unblock() {
		this.blocked = false;
	}

	public Level getLevel() {
		return level;
	}

	public Logger getLogger() {
		return logger;
	}
}
