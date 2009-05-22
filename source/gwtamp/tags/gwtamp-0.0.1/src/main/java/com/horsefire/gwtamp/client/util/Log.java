package com.horsefire.gwtamp.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public final class Log {

	public static void debug(String message) {
		debug(message, null);
	}

	public static void debug(String message, Throwable e) {
		GWT.log(message, e);
	}

	public static void error(String message) {
		error(message, null);
	}

	public static void error(String message, Throwable e) {
		if (GWT.isScript()) {
			Window.alert(message);
		} else {
			// If we're in Hosted Mode, just throw it to the console
			GWT.log(message, e);
		}
	}
}
