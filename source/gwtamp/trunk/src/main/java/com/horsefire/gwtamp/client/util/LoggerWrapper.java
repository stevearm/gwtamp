package com.horsefire.gwtamp.client.util;

public class LoggerWrapper implements Logger {

	private final String m_name;
	private final Logger m_parent;

	public LoggerWrapper(String name, Logger parentLogger) {
		m_name = name;
		m_parent = parentLogger;
	}

	private String prefixMessage(String message) {
		return m_name + " | " + message;
	}

	public void debug(String msg) {
		m_parent.debug(prefixMessage(msg));
	}

	public void debug(String format, Object arg) {
		m_parent.debug(prefixMessage(format), arg);
	}

	public void debug(String format, Object arg1, Object arg2) {
		m_parent.debug(prefixMessage(format), arg1, arg2);
	}

	public void debug(String format, Object[] argArray) {
		m_parent.debug(prefixMessage(format), argArray);
	}

	public void debug(String msg, Throwable t) {
		m_parent.debug(prefixMessage(msg), t);
	}

	public void error(String msg) {
		m_parent.error(prefixMessage(msg));
	}

	public void error(String format, Object arg) {
		m_parent.error(prefixMessage(format), arg);
	}

	public void error(String format, Object arg1, Object arg2) {
		m_parent.error(prefixMessage(format), arg1, arg2);
	}

	public void error(String format, Object[] argArray) {
		m_parent.error(prefixMessage(format), argArray);
	}

	public void error(String msg, Throwable t) {
		m_parent.error(prefixMessage(msg), t);
	}

	public String getName() {
		return m_name;
	}

	public void info(String msg) {
		m_parent.info(prefixMessage(msg));
	}

	public void info(String format, Object arg) {
		m_parent.info(prefixMessage(format), arg);
	}

	public void info(String format, Object arg1, Object arg2) {
		m_parent.info(prefixMessage(format), arg1, arg2);
	}

	public void info(String format, Object[] argArray) {
		m_parent.info(prefixMessage(format), argArray);
	}

	public void info(String msg, Throwable t) {
		m_parent.info(prefixMessage(msg), t);
	}

	public boolean isDebugEnabled() {
		return m_parent.isDebugEnabled();
	}

	public boolean isErrorEnabled() {
		return m_parent.isErrorEnabled();
	}

	public boolean isInfoEnabled() {
		return m_parent.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return m_parent.isTraceEnabled();
	}

	public boolean isWarnEnabled() {
		return m_parent.isWarnEnabled();
	}

	public void trace(String msg) {
		m_parent.trace(prefixMessage(msg));
	}

	public void trace(String format, Object arg) {
		m_parent.trace(prefixMessage(format), arg);
	}

	public void trace(String format, Object arg1, Object arg2) {
		m_parent.trace(prefixMessage(format), arg1, arg2);
	}

	public void trace(String format, Object[] argArray) {
		m_parent.trace(prefixMessage(format), argArray);
	}

	public void trace(String msg, Throwable t) {
		m_parent.trace(prefixMessage(msg), t);
	}

	public void warn(String msg) {
		m_parent.warn(prefixMessage(msg));
	}

	public void warn(String format, Object arg) {
		m_parent.warn(prefixMessage(format), arg);
	}

	public void warn(String format, Object[] argArray) {
		m_parent.warn(prefixMessage(format), argArray);
	}

	public void warn(String format, Object arg1, Object arg2) {
		m_parent.warn(prefixMessage(format), arg1, arg2);
	}

	public void warn(String msg, Throwable t) {
		m_parent.warn(prefixMessage(msg), t);
	}
}
