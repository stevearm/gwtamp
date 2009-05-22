package com.horsefire.gwtamp.client.records.fields;

public abstract class Field {

	private final String m_key;
	private final String m_title;

	protected Field(String key, String title) {
		m_key = key;
		m_title = title;
	}

	public final String getKey() {
		return m_key;
	}

	public final String getTitle() {
		return m_title;
	}
}
