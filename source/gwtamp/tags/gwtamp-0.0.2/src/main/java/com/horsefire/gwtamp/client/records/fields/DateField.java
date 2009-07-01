package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.DateValue;

public class DateField extends IntegerField {

	public DateField(String key, String title) {
		super(key, title);
	}

	@Override
	public DataValue createValue(JSONValue value) throws RecordParsingException {
		return new DateValue(parseOutLong(value));
	}

	@Override
	public DataValue createDefaultValue() {
		return new DateValue();
	}
}
