package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.values.DataValue;

public abstract class DataField extends Field {

	protected DataField(String key, String title) {
		super(key, title);
	}

	@Deprecated
	public final boolean isUserVisible() {
		return true;
	}

	/**
	 * @param value
	 *            Source data to parse from
	 * @return Parsed value, never null
	 * @throws RecordParsingException
	 *             if value can't be parsed properly
	 */
	public abstract DataValue createValue(JSONValue value)
			throws RecordParsingException;

	public abstract DataValue createDefaultValue();
}
