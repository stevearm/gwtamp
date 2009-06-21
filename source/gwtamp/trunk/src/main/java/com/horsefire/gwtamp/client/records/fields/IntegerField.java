package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.IntegerValue;

public class IntegerField extends NumberField {

	public IntegerField(String key, String title) {
		super(key, title);
	}

	protected final long parseOutLong(JSONValue value)
			throws RecordParsingException {
		if (value == null || value.isNumber() == null) {
			throw new RecordParsingException("Field '" + getKey()
					+ "' must be an integer");
		}
		return (long) value.isNumber().doubleValue();
	}

	@Override
	public DataValue createValue(JSONValue value) throws RecordParsingException {
		return new IntegerValue(parseOutLong(value));
	}

	@Override
	public DataValue createDefaultValue() {
		return new IntegerValue();
	}
}
