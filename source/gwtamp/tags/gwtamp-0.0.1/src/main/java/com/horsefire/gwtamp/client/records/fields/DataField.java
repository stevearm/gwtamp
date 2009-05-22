package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.values.DataValue;

public abstract class DataField extends Field {

	private final boolean m_userVisible;

	protected DataField(String key, boolean userVisible, String title) {
		super(key, title);
		m_userVisible = userVisible;
	}

	public final boolean isUserVisible() {
		return m_userVisible;
	}

	public abstract DataValue createValue(JSONValue value);
}
