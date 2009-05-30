package com.horsefire.gwtamp.client.records.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.LinkValue;
import com.horsefire.gwtamp.client.util.Log;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialog;

public abstract class DataSource {

	public static final String BASE_URL = "api/";

	public static interface ChangeObserver {
		void dataSourceChange();
	}

	private class ResponseBean {
		public int responseCode;
		public String message;
		public Collection<Record> records;
	}

	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	private static final String CONTENT_TYPE = "Content-type";

	private static final String PARAM_OPERATION_TYPE = "_operationType";
	private static final String VALUE_FETCH = "fetch";
	private static final String VALUE_UPDATE = "update";
	private static final String VALUE_ADD = "add";
	private static final String VALUE_REMOVE = "remove";

	private static final String JSON_RECORD_KEY_DATA = "data";
	private static final String JSON_RECORD_KEY_LINKS = "links";

	private final ResponseParser m_responseParser = new ResponseParser();
	private final Set<ChangeObserver> m_observers = new HashSet<ChangeObserver>();
	private final String m_name;
	private final List<DataField> m_dataFields;
	private final List<LinkField> m_linkFields;
	private final String m_recordTitleKey;
	private final PleaseWaitDialog m_pleaseWaitDialog;

	protected DataSource(String name, List<DataField> dataFields,
			List<LinkField> linkFields, String recordTitleKey,
			PleaseWaitDialog waitDialog) {
		m_name = name;
		if (dataFields == null) {
			m_dataFields = new ArrayList<DataField>();
		} else {
			m_dataFields = dataFields;
		}
		if (linkFields == null) {
			m_linkFields = new ArrayList<LinkField>();
		} else {
			m_linkFields = linkFields;
		}
		m_recordTitleKey = recordTitleKey;
		m_pleaseWaitDialog = waitDialog;
	}
	
	public String getName() {
		return m_name;
	}

	private String getUrl() {
		return GWT.getHostPageBaseURL() + BASE_URL + m_name + ".php";
	}

	public final void getRecord(final int id,
			final SingleRecordCallback callback) {
		final String url = getUrl() + '?' + PARAM_OPERATION_TYPE + '='
				+ VALUE_FETCH + "&id=" + id;

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		final RequestCallback requestCallback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				callback.error("Error refreshing records", exception);
			}

			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != 200) {
					callback.error("Got a " + response.getStatusCode()
							+ " response getting record " + id, null);
				} else {
					ResponseBean responseBean = getResponse(response.getText());
					if (responseBean != null && responseBean.records != null) {
						if (responseBean.records.size() == 0) {
							callback.gotRecord(null);
						} else if (responseBean.records.size() == 1) {
							callback.gotRecord(responseBean.records.iterator()
									.next());
						} else {
							callback.error("Somehow got multiple cards for id="
									+ id, null);
						}
					} else {
						callback.error("Couldn't parse records", null);
					}
				}
			}
		};
		try {
			builder.sendRequest("", requestCallback);
		} catch (RequestException e) {
			callback.error("Problem sending request to refresh records", e);
		}
	}

	public final void getRecords(final MultipleRecordCallback callback) {
		final String url = getUrl() + '?' + PARAM_OPERATION_TYPE + '='
				+ VALUE_FETCH;

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		final RequestCallback requestCallback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				callback.error("Error refreshing records", exception);
			}

			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != 200) {
					callback.error("Got a " + response.getStatusCode()
							+ " response getting records", null);
				} else {
					ResponseBean responseBean = getResponse(response.getText());
					if (responseBean != null && responseBean.records != null) {
						callback.gotRecords(responseBean.records);
					} else {
						callback.error("Couldn't parse records", null);
					}
				}
			}
		};
		try {
			builder.sendRequest("", requestCallback);
		} catch (RequestException e) {
			callback.error("Problem sending request to refresh records", e);
		}
	}

	private Record parseRecord(JSONObject record) {
		if (record == null) {
			Log.error("record is null");
			return null;
		}
		Record parsedRecord = createRecord();

		JSONValue value = record.get(Record.KEY_ID);
		if (value == null) {
			Log.error("id is null in response: " + record);
			return null;
		}
		parsedRecord.setId(Integer.parseInt(value.isNumber().toString()));

		value = record.get(JSON_RECORD_KEY_DATA);
		if (value == null) {
			Log.error("data is null in response: " + record);
			return null;
		}
		parseData(parsedRecord, value.isObject());

		value = record.get(JSON_RECORD_KEY_LINKS);
		if (value == null) {
			Log.error("links is null in response: " + record);
			return null;
		}
		parseLinks(parsedRecord, value.isObject());

		return parsedRecord;
	}

	private void parseData(Record record, JSONObject object) {
		for (DataField field : m_dataFields) {
			JSONValue jsonValue = object.get(field.getKey());
			if (jsonValue != null) {
				DataValue value = field.createValue(jsonValue);
				if (value != null) {
					record.setDataValue(field.getKey(), value);
				}
			}
		}
	}

	private void parseLinks(Record record, JSONObject object) {
		for (LinkField field : m_linkFields) {
			JSONValue jsonValue = object.get(field.getKey());
			if (jsonValue != null) {
				LinkValue value = field.createValue(jsonValue);
				if (value != null) {
					record.setLinkId(field.getKey(), value);
				}
			}
		}
	}

	private ResponseBean getResponse(String response) {
		ResponseParser.ResponseBean jsonResponseBean = m_responseParser
				.getResponse(response);
		if (jsonResponseBean == null) {
			return null;
		}

		ResponseBean responseBean = new ResponseBean();
		responseBean.responseCode = jsonResponseBean.responseCode;
		responseBean.message = jsonResponseBean.message;

		if (responseBean.responseCode == 0) {
			JSONValue tempValue = jsonResponseBean.dataValue;
			if (tempValue.isArray() == null) {
				Log.error("Response code was 0 but 'data' wasn't an array");
				return null;
			}
			responseBean.records = new ArrayList<Record>();
			JSONArray dataArray = tempValue.isArray();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONValue value = dataArray.get(i);
				if (value.isObject() == null) {
					Log.error("Data item isn't an object: " + value);
				} else {
					Record record = parseRecord(value.isObject());
					if (record != null) {
						responseBean.records.add(record);
					}
				}
			}
		}

		return responseBean;
	}

	public final void saveRecord(final Record record,
			final Collection<Change> values, final SingleRecordCallback callback) {
		m_pleaseWaitDialog.show("Saving record");
		final String url = getUrl() + "?" + PARAM_OPERATION_TYPE + "="
				+ VALUE_UPDATE + "&" + Record.KEY_ID + "=" + record.getId();
		addSaveRecord(url, values, callback);
	}

	private void addSaveRecord(final String url,
			final Collection<Change> values, final SingleRecordCallback callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		final RequestCallback requestCallback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				m_pleaseWaitDialog.hide();
				callback.error("Error adding/saving record", exception);
			}

			public void onResponseReceived(Request request, Response response) {
				m_pleaseWaitDialog.hide();
				if (response.getStatusCode() != 200) {
					callback.error("Got a " + response.getStatusCode()
							+ " response adding/saving record", null);
					return;
				}
				ResponseBean responseBean = getResponse(response.getText());
				if (responseBean != null && responseBean.records != null
						&& responseBean.records.size() > 0) {
					callback.gotRecord(responseBean.records.iterator().next());
					notifyObservers();
				} else {
					callback.error("Can't decode add/save response: "
							+ response.getText(), null);
				}
			}
		};
		StringBuilder postBody = new StringBuilder();
		boolean first = true;
		for (Change change : values) {
			if (change != null) {
				if (first) {
					first = false;
				} else {
					postBody.append('&');
				}
				postBody.append(URL.encode(change.key)).append('=').append(
						URL.encode(change.value.getJsonString()));
			}
		}
		try {
			builder.setHeader(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
			builder.sendRequest(postBody.toString(), requestCallback);
		} catch (RequestException e) {
			m_pleaseWaitDialog.hide();
			callback.error("Problem sending request to refresh records", e);
			notifyObservers();
		}
	}

	public final void deleteRecord(final Record record,
			final PlainCallback callback) {
		final String url = getUrl() + "?" + PARAM_OPERATION_TYPE + "="
				+ VALUE_REMOVE + "&" + Record.KEY_ID + "=" + record.getId();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		final RequestCallback requestCallback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				m_pleaseWaitDialog.hide();
				callback.error("Error deleting record", exception);
			}

			public void onResponseReceived(Request request, Response response) {
				m_pleaseWaitDialog.hide();
				if (response.getStatusCode() == 200) {
					callback.success();
					notifyObservers();
				} else {
					callback.error("Got a " + response.getStatusCode()
							+ " response deleting record", null);
				}
			}
		};
		try {
			m_pleaseWaitDialog.show("Deleting record");
			builder.setHeader(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);
			builder.sendRequest("", requestCallback);
		} catch (RequestException e) {
			m_pleaseWaitDialog.hide();
			callback.error("Problem sending request to refresh records", e);
			notifyObservers();
		}
	}

	public final void addRecord(Collection<Change> values,
			SingleRecordCallback callback) {
		m_pleaseWaitDialog.show("Adding record");
		final String url = getUrl() + "?" + PARAM_OPERATION_TYPE + "="
				+ VALUE_ADD;
		addSaveRecord(url, values, callback);
	}

	public final List<DataField> getDataFields() {
		return m_dataFields;
	}

	public final List<LinkField> getLinkFields() {
		return m_linkFields;
	}

	public final void addObserver(ChangeObserver o) {
		m_observers.add(o);
	}

	public final void removeObserver(ChangeObserver o) {
		m_observers.remove(o);
	}

	protected final void notifyObservers() {
		for (ChangeObserver o : m_observers) {
			o.dataSourceChange();
		}
	}

	protected final Record createRecord() {
		return new Record(m_recordTitleKey);
	}

	public final void getRecordsLinkedToRecord(final String linkKey,
			final Record linkedRecord, final MultipleRecordCallback callback) {
		getRecordsLinkedToRecord(linkKey, linkedRecord.getId(), callback);
	}

	public final void getRecordsLinkedToRecord(final String linkKey,
			final int linkId, final MultipleRecordCallback callback) {
		final MultipleRecordCallback internalCallback = new MultipleRecordCallback() {
			public void gotRecords(Collection<Record> records) {
				Collection<Record> matchingRecords = new ArrayList<Record>();
				for (Record record : records) {
					LinkValue link = record.getLinkId(linkKey);
					if (link != null && link.getId() == linkId) {
						matchingRecords.add(record);
					}
				}
				callback.gotRecords(matchingRecords);
			}
		};
		getRecords(internalCallback);
	}
}
