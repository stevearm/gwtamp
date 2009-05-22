package com.horsefire.gwtamp.client.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class mocks out the logging interface so there aren't null pointer
 * exceptions, but doesn't log anything anywhere
 * 
 * @author steve
 * 
 */
public class EmptyLog extends AbstractLog {

	private static final List<LogMessage> EMPTY_LIST = new ArrayList<LogMessage>(
			0);

	@Override
	public void clearMessages() {
	}

	@Override
	public List<LogMessage> getMessages() {
		return EMPTY_LIST;
	}

	@Override
	public void log(int severity, String message) {
	}

	@Override
	public void log(int severity, String message, Throwable exception) {
	}

}
