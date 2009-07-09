package com.horsefire.gwtamp.client.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This is supposed to be replicating the functionality of
 * org.slf4j.LoggerFactory, which supposedly implements org.slf4j.ILoggerFactory
 * or something.
 */
public final class LogFactory {

	private static Logger s_rootLogger = new JsonLogger();
	private static final Map<String, Logger> s_namedLoggers = new HashMap<String, Logger>();

	private LogFactory() {
	}

	public static Logger getLogger(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		if (name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) {
			return s_rootLogger;
		}

		Logger namedLogger = s_namedLoggers.get(name);
		if (namedLogger == null) {
			namedLogger = new LoggerWrapper(name, s_rootLogger);
			s_namedLoggers.put(name, namedLogger);
		}
		return namedLogger;
	}

	public static void setBaseLogger(Logger baseLogger) {
		s_rootLogger = baseLogger;
		s_namedLoggers.clear();
	}
}