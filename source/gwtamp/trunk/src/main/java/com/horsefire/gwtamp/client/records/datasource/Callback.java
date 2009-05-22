package com.horsefire.gwtamp.client.records.datasource;

import com.horsefire.gwtamp.client.util.Log;

public abstract class Callback {

	public void error(String message, Throwable e) {
		Log.error(message, e);
	}
}
