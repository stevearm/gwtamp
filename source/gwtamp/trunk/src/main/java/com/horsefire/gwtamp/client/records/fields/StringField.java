package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.StringValue;

public class StringField extends DataField {

	public static final int MAX_LENGTH_DEFAULT = 20;

	private final int m_maxLength;

	public StringField(String key, String title) {
		this(key, title, MAX_LENGTH_DEFAULT);
	}

	public StringField(String key, String title, int maxLength) {
		super(key, title);
		m_maxLength = maxLength;
	}

	public int getMaxLength() {
		return m_maxLength;
	}

	@Override
	public DataValue createValue(JSONValue value) throws RecordParsingException {
		if (value == null || value.isString() == null) {
			throw new RecordParsingException("Field '" + getKey()
					+ "' must be a string");
		}
		return new StringValue(value.isString().stringValue());
	}

	@Override
	public DataValue createDefaultValue() {
		return new StringValue(null);
	}
}
