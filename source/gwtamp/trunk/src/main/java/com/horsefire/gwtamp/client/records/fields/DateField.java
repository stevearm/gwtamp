package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.DateValue;
import com.horsefire.gwtamp.client.records.values.IntegerValue;

public class DateField extends IntegerField {

	public DateField(String key, boolean userVisible, String title) {
		super(key, userVisible, title);
	}

	@Override
	public DataValue createValue(JSONValue value) {
		IntegerValue dataValue = (IntegerValue) super.createValue(value);
		return new DateValue(dataValue.get());
	}

}
