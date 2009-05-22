package com.horsefire.gwtamp.client.records.values;

public class IntegerValue extends DataValue {

	private final long m_value;

	public IntegerValue(long value) {
		m_value = value;
	}

	public long get() {
		return m_value;
	}

	@Override
	public String getJsonString() {
		return Long.toString(m_value);
	}
}
