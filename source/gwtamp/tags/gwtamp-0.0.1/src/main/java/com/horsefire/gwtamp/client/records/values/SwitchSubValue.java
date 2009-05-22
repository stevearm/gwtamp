package com.horsefire.gwtamp.client.records.values;

public final class SwitchSubValue {

	private final String m_key;
	private final boolean m_value;

	public SwitchSubValue(String key, boolean value) {
		m_key = key;
		m_value = value;
	}

	public String getKey() {
		return m_key;
	}

	public boolean getValue() {
		return m_value;
	}
}
