package com.horsefire.gwtamp.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.horsefire.gwtamp.client.rpc.RpcCallback;
import com.horsefire.gwtamp.client.rpc.RpcClient;
import com.horsefire.gwtamp.client.rpc.RpcResponse;

public class JsonLogger implements Logger {

	private static enum LogLevel {
		TRACE, DEBUG, INFO, WARN, ERROR
	}

	private static final String URL = "log.php";
	public static final String LOGGER_KEY_LOG_TO_SERVER = "logToServer";
	public static final String LOGGER_KEY_LOG_LEVEL = "logLevel";
	public static final String LOGGER_LEVEL_TRACE = "trace";
	public static final String LOGGER_LEVEL_DEBUG = "debug";
	public static final String LOGGER_LEVEL_INFO = "info";
	public static final String LOGGER_LEVEL_WARN = "warn";
	public static final String LOGGER_LEVEL_ERROR = "error";
	public static final String LOGGER_PARAM_MESSAGE = "message";

	private RpcClient m_rpcClient;
	private boolean m_logToServer;
	private LogLevel m_logLevel;

	public JsonLogger() {
		this(new RpcClient(), true, false, LogLevel.TRACE);
	}

	public JsonLogger(RpcClient rpcClient, boolean logToServer,
			LogLevel logLevel) {
		this(rpcClient, false, logToServer, logLevel);
	}

	JsonLogger(RpcClient rpcClient, boolean initFromServer,
			boolean logToServer, LogLevel logLevel) {
		m_rpcClient = rpcClient;
		m_logToServer = logToServer;
		m_logLevel = logLevel;

		if (initFromServer) {
			m_rpcClient.doGet(URL, new RpcCallback() {
				public void response(RpcResponse response) {
					if (response.getResponseCode() != RpcResponse.STATUS_SUCCESS) {
						Window
								.alert("Cannot initialize logging system. Got response ("
										+ response.getResponseCode()
										+ "): "
										+ response.getMessage());
					} else {
						JSONArray data = response.getData();
						JSONObject object = data.get(0).isObject();
						m_logLevel = LogLevel.valueOf(object.get(
								LOGGER_KEY_LOG_LEVEL).isString().stringValue());
					}
				}
			});
		}
	}

	private void log(LogLevel debugLevel, String message, Throwable e) {
		// If we're in Hosted Mode, just print the log to the console
		GWT.log(debugLevel + message, e);

		if (GWT.isScript()) {
			Window.alert(debugLevel + " " + " " + message);
		} else {
		}
	}

	private String format(String format, Object... args) {
		StringBuilder result = new StringBuilder(format);
		for (Object arg : args) {
			result.append(" | ").append(arg);
		}
		return result.toString();
	}

	public void debug(String msg) {
		debug(msg, (Throwable) null);
	}

	public void debug(String format, Object arg) {
		debug(format(format, arg));
	}

	public void debug(String format, Object arg1, Object arg2) {
		debug(format(format, arg1, arg2));
	}

	public void debug(String format, Object[] argArray) {
		debug(format(format, argArray));
	}

	public void debug(String msg, Throwable t) {
		log(LogLevel.DEBUG, msg, t);
	}

	public void error(String msg) {
		error(msg, (Throwable) null);
	}

	public void error(String format, Object arg) {
		error(format(format, arg));
	}

	public void error(String format, Object arg1, Object arg2) {
		error(format(format, arg1, arg2));
	}

	public void error(String format, Object[] argArray) {
		error(format(format, argArray));
	}

	public void error(String msg, Throwable t) {
		log(LogLevel.ERROR, msg, t);
	}

	public String getName() {
		return Logger.ROOT_LOGGER_NAME;
	}

	public void info(String msg) {
		info(msg, (Throwable) null);
	}

	public void info(String format, Object arg) {
		info(format(format, arg));
	}

	public void info(String format, Object arg1, Object arg2) {
		info(format(format, arg1, arg2));
	}

	public void info(String format, Object[] argArray) {
		info(format(format, argArray));
	}

	public void info(String msg, Throwable t) {
		log(LogLevel.INFO, msg, t);
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isErrorEnabled() {
		return true;
	}

	public boolean isInfoEnabled() {
		return true;
	}

	public boolean isTraceEnabled() {
		return true;
	}

	public boolean isWarnEnabled() {
		return true;
	}

	public void trace(String msg) {
		trace(msg, (Throwable) null);
	}

	public void trace(String format, Object arg) {
		trace(format(format, arg));
	}

	public void trace(String format, Object arg1, Object arg2) {
		trace(format(format, arg1, arg2));
	}

	public void trace(String format, Object[] argArray) {
		trace(format(format, argArray));
	}

	public void trace(String msg, Throwable t) {
		log(LogLevel.TRACE, msg, t);
	}

	public void warn(String msg) {
		warn(msg, (Throwable) null);
	}

	public void warn(String format, Object arg) {
		warn(format(format, arg));
	}

	public void warn(String format, Object[] argArray) {
		warn(format(format, argArray));
	}

	public void warn(String format, Object arg1, Object arg2) {
		warn(format(format, arg1, arg2));
	}

	public void warn(String msg, Throwable t) {
		log(LogLevel.WARN, msg, t);
	}

}
