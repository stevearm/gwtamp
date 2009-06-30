package com.horsefire.gwtamp.client.records.datasource;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.util.Log;

class RecordParser {

	static final String JSON_RECORD_KEY_DATA = "data";
	static final String JSON_RECORD_KEY_LINKS = "links";

	private final String m_recordTitleKey;
	private final List<DataField> m_dataFields;
	private final List<LinkField> m_linkFields;

	public RecordParser(String recordTitleKey, List<DataField> dataFields,
			List<LinkField> linkFields) {
		if (recordTitleKey == null || dataFields == null || linkFields == null) {
			throw new IllegalArgumentException("Cannot pass in nulls");
		}
		m_dataFields = dataFields;
		m_linkFields = linkFields;

		m_recordTitleKey = getTitleMatch(recordTitleKey);
	}

	private String getTitleMatch(String recordTitleKey) {
		if (recordTitleKey == null || recordTitleKey.isEmpty()) {
			throw new IllegalArgumentException(
					"recordTitleKey can not be empty");
		}
		for (DataField linkField : m_dataFields) {
			if (recordTitleKey.equals(linkField.getKey())) {
				return recordTitleKey;
			}
		}
		for (LinkField linkField : m_linkFields) {
			if (recordTitleKey.equals(linkField.getKey())) {
				return recordTitleKey;
			}
		}
		throw new IllegalArgumentException(
				"recordTitleKey must match one of the given fields");
	}

	public List<Record> parseRecords(JSONArray dataArray)
			throws RecordParsingException {
		List<Record> result = new ArrayList<Record>();
		for (int i = 0; i < dataArray.size(); i++) {
			final Record record = parseRecord(dataArray.get(i).isObject());
			if (record != null) {
				result.add(record);
			}
		}
		return result;
	}

	public Record parseRecord(JSONObject record) throws RecordParsingException {
		if (record == null) {
			Log.error("record is null");
			return null;
		}

		JSONValue value = record.get(Record.KEY_ID);
		if (value == null) {
			Log.error("id is null in response: " + record);
			return null;
		}
		final int id = (int) value.isNumber().doubleValue();

		value = record.get(JSON_RECORD_KEY_DATA);
		if (value == null || value.isObject() == null) {
			Log.error("data is null in response: " + record);
			return null;
		}
		final JSONObject dataObject = (JSONObject) value;

		value = record.get(JSON_RECORD_KEY_LINKS);
		if (value == null || value.isObject() == null) {
			Log.error("links is null in response: " + record);
			return null;
		}
		final JSONObject linksObject = (JSONObject) value;

		final Record parsedRecord = new Record(m_recordTitleKey);
		parsedRecord.setId(id);
		parseData(parsedRecord, dataObject);
		parseLinks(parsedRecord, linksObject);
		return parsedRecord;
	}

	private void parseData(Record record, JSONObject object)
			throws RecordParsingException {
		for (DataField field : m_dataFields) {
			final String key = field.getKey();
			final JSONValue givenJson = object.get(key);
			if (givenJson == null) {
				record.setDataValue(key, field.createDefaultValue());
			} else {
				record.setDataValue(key, field.createValue(givenJson));
			}
		}
	}

	private void parseLinks(Record record, JSONObject object)
			throws RecordParsingException {
		for (LinkField field : m_linkFields) {
			final String key = field.getKey();
			final JSONValue givenJson = object.get(key);
			if (givenJson == null) {
				record.setLinkId(key, field.createDefaultValue());
			} else {
				record.setLinkId(key, field.createValue(givenJson));
			}
		}
	}
}
