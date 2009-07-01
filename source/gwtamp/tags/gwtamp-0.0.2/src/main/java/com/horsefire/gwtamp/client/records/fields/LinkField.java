package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.values.BrokenLinkValue;
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

	public final LinkValue createValue(JSONValue value)
			throws RecordParsingException {
		if (value == null || value.isNumber() == null) {
			throw new RecordParsingException("Error parsing LinkId for "
					+ getKey() + ". Fields must be an integer value");
		}
		return new LinkValue((int) value.isNumber().doubleValue(), m_dataSource);
	}

	public final LinkValue createDefaultValue() {
		return BrokenLinkValue.INSTANCE;
	}
}
