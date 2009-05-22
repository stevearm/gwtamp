package com.horsefire.gwtamp.client.util;

import java.util.Date;
import java.util.List;

public abstract class AbstractLog {

	public static final int SEVERITY_DEBUG = 10;
	public static final int SEVERITY_ERROR = 50;

	public static final EmptyLog EMPTY_LOG = new EmptyLog();
	private static AbstractLog s_instance = EMPTY_LOG;

	public static void set(AbstractLog log) {
		s_instance = log;
	}

	public static AbstractLog get() {
		return s_instance;
	}

	protected class LogMessage {
		public final String date;
		public final int severity;
		public final String message;
		public Throwable exception;

		public LogMessage(final int sev, final String msg, final Throwable e) {
			date = new Date().toGMTString();
			severity = sev;
			message = msg;
			exception = e;
		}
	}

	public abstract void clearMessages();

	public abstract List<LogMessage> getMessages();

	public abstract void log(int severity, String message);

	public abstract void log(int severity, String message, Throwable exception);
}
