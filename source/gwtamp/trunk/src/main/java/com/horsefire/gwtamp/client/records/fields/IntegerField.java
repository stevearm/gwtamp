package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.IntegerValue;

public class IntegerField extends NumberField {

	public IntegerField(String key, boolean userVisible, String title) {
		super(key, userVisible, title);
	}

	@Override
	public DataValue createValue(JSONValue value) {
		return new IntegerValue((long) value.isNumber().doubleValue());
	}
}
