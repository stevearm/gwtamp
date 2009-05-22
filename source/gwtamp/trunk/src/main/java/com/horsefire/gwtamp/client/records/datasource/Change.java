package com.horsefire.gwtamp.client.records.datasource;

import com.horsefire.gwtamp.client.records.values.Value;

public class Change {

	public final String key;
	public final Value value;

	public Change(String key, Value value) {
		this.key = key;
		this.value = value;
	}
}
