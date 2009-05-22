package com.horsefire.gwtamp.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;

public class ClientLog extends AbstractLog {

	private static final int MAX_MESSAGES = 30;

	private List<LogMessage> m_logMessages = new ArrayList<LogMessage>();

	public List<LogMessage> getMessages() {
		return new ArrayList<LogMessage>(m_logMessages);
	}

	public void clearMessages() {
		m_logMessages.clear();
	}

	private void addMessage(LogMessage message) {
		if (m_logMessages.size() >= 30) {
			m_logMessages.remove(0);
		}
		m_logMessages.add(message);
	}

	@Override
	public void log(int severity, String message) {
		log(severity, message, null);
	}

	@Override
	public void log(int severity, String message, Throwable exception) {
		addMessage(new LogMessage(severity, message, exception));
		if (severity >= AbstractLog.SEVERITY_ERROR) {
			Window.alert(message);
		}
	}
}
