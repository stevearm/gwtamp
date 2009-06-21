package com.horsefire.gwtamp.client.records.values;

public class IntegerValue extends DataValue {

	public static final long DEFAULT_VALUE = 0;

	private final long m_value;

	public IntegerValue() {
		m_value = DEFAULT_VALUE;
	}

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
