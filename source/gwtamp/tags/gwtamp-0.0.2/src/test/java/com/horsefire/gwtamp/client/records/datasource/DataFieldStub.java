package com.horsefire.gwtamp.client.records.datasource;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.values.DataValue;

public class DataFieldStub extends DataField {

	public static class DataValueStub extends DataValue {

		public String jsonString;

		public DataValueStub(String jsonString) {
			this.jsonString = jsonString;
		}

		@Override
		public String getJsonString() {
			return jsonString;
		}
	}

	public JSONValue providedJSON;
	public final DataValue returnedValue;
	public final DataValue defaultValue;

	public DataFieldStub(String key, String title, DataValue returnedValue,
			DataValue defaultValue) {
		super(key, title);
		this.returnedValue = returnedValue;
		this.defaultValue = defaultValue;
	}

	@Override
	public DataValue createValue(JSONValue value) {
		providedJSON = value;
		return returnedValue;
	}

	@Override
	public DataValue createDefaultValue() {
		return defaultValue;
	}
}
