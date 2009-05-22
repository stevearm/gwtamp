package com.horsefire.gwtamp.client.records.fields;

public final class SingleSwitchSubField {

	private final String m_key;
	private final String m_title;

	public SingleSwitchSubField(String key, String title) {
		m_key = key;
		m_title = title;
	}

	public String getKey() {
		return m_key;
	}

	public String getTitle() {
		return m_title;
	}
}
