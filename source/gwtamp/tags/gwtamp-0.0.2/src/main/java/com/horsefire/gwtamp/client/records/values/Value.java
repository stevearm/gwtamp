package com.horsefire.gwtamp.client.records.values;

public abstract class Value {

	public abstract String getJsonString();

	public String getUserString() {
		return getJsonString();
	}

	@Override
	public final String toString() {
		return "Value object: " + getUserString();
	}
}
