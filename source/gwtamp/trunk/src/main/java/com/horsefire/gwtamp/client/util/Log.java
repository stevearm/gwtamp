package com.horsefire.gwtamp.client.util;

public final class Log {

	private static Logger s_logger;

	private static Logger getLogger() {
		if (s_logger == null) {
			s_logger = LogFactory.getLogger("DEPRECATED");
		}
		return s_logger;
	}

	public static void debug(String message) {
		debug(message, null);
	}

	public static void debug(String message, Throwable e) {
		getLogger().debug(message, e);
	}

	public static void error(String message) {
		error(message, null);
	}

	public static void error(String message, Throwable e) {
		getLogger().error(message, e);
	}
}
