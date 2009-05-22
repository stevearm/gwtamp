package com.horsefire.gwtamp.client.records.values;

public class StringValue extends DataValue {

	private final String m_value;

	public StringValue(String value) {
		if (value == null) {
			m_value = "";
		} else {
			m_value = value;
		}
	}

	public String get() {
		return m_value;
	}

	@Override
	public String getJsonString() {
		return m_value;
	}
}
