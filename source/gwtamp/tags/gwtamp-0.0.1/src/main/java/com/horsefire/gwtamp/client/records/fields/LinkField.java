package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.values.LinkValue;

public class LinkField extends Field {

	private final DataSource m_dataSource;

	public LinkField(String key, String title, DataSource dataSource) {
		super(key, title);
		m_dataSource = dataSource;
	}

	public final DataSource getDataSource() {
		return m_dataSource;
	}

	public final LinkValue createValue(JSONValue value) {
		if (value == null) {
			return null;
		}
		return new LinkValue(Integer.parseInt(value.toString()), m_dataSource);
	}
}
