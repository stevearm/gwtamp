package com.horsefire.gwtamp.client.records.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.RecordParsingException;
import com.horsefire.gwtamp.client.records.fields.DataField;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.records.values.LinkValue;
import com.horsefire.gwtamp.client.rpc.RpcCallback;
import com.horsefire.gwtamp.client.rpc.RpcClient;
import com.horsefire.gwtamp.client.rpc.RpcResponse;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialog;

public abstract class DataSource {

	public static final String BASE_URL = "api/";

	public static interface ChangeObserver {
		void dataSourceChange();
	}

	private static final String PARAM_OPERATION_TYPE = "_operationType";
	private static final String VALUE_FETCH = "fetch";
	private static final String VALUE_UPDATE = "update";
	private static final String VALUE_ADD = "add";
	private static final String VALUE_REMOVE = "remove";

	private final Set<ChangeObserver> m_observers = new HashSet<ChangeObserver>();
	private final String m_name;
	private final List<DataField> m_dataFields;
	private final List<LinkField> m_linkFields;
	private final PleaseWaitDialog m_pleaseWaitDialog;
	private final RecordParser m_recordParser;
	private final RpcClient m_rpcClient;

	protected DataSource(String name, List<DataField> dataFields,
			List<LinkField> linkFields, String keyForNameField,
			PleaseWaitDialog waitDialog, RpcClient rpcClient) {
		this(name, dataFields, linkFields, new RecordParser(keyForNameField,
				dataFields, linkFields), waitDialog, rpcClient);
	}

	DataSource(String name, List<DataField> dataFields,
			List<LinkField> linkFields, RecordParser parser,
			PleaseWaitDialog waitDialog, RpcClient rpcClient) {
		if (name == null || dataFields == null || linkFields == null) {
			throw new IllegalArgumentException("Cannot pass in nulls");
		}
		m_name = name;
		m_dataFields = dataFields;
		m_linkFields = linkFields;
		m_recordParser = parser;
		m_pleaseWaitDialog = waitDialog;
		m_rpcClient = rpcClient;
	}

	public final String getName() {
		return m_name;
	}

	private String getUrl() {
		return GWT.getHostPageBaseURL() + BASE_URL + m_name + ".php";
	}

	public final void getRecord(final int id,
			final SingleRecordCallback callback) {
		m_pleaseWaitDialog.show("Getting record");
		final String url = getUrl() + '?' + PARAM_OPERATION_TYPE + '='
				+ VALUE_FETCH + "&id=" + id;

		m_rpcClient.doGet(url, new RpcCallback() {
			public void response(RpcResponse response) {
				if (response.getResponseCode() == RpcResponse.STATUS_SUCCESS) {
					JSONArray data = response.getData();
					if (data.size() == 0) {
						callback.error("No records found", null);
					} else if (data.size() > 1) {
						callback.error("Somehow got multiple records", null);
					} else {
						try {
							List<Record> records = m_recordParser
									.parseRecords(data);
							callback.gotRecord(records.get(0));
						} catch (RecordParsingException e) {
							callback.error("Unreadable record", e);
						}
					}
				} else {
					callback.error("Error refreshing records", null);
				}
				m_pleaseWaitDialog.hide();
			}
		});
	}

	public final void getRecords(final MultipleRecordCallback callback) {
		final String url = getUrl() + '?' + PARAM_OPERATION_TYPE + '='
				+ VALUE_FETCH;

		m_pleaseWaitDialog.show("Getting records");
		m_rpcClient.doGet(url, new RpcCallback() {
			public void response(RpcResponse response) {
				if (response.getResponseCode() != RpcResponse.STATUS_SUCCESS) {
					callback.error("Error refreshing records", null);
				} else {
					try {
						List<Record> records = m_recordParser
								.parseRecords(response.getData());
						callback.gotRecords(records);
					} catch (RecordParsingException e) {
						callback.error("Unreadable records", e);
					}
				}
				m_pleaseWaitDialog.hide();
			}
		});
	}

	public final void addRecord(Collection<Change> values,
			SingleRecordCallback callback) {
		m_pleaseWaitDialog.show("Adding record");
		final String url = getUrl() + "?" + PARAM_OPERATION_TYPE + "="
				+ VALUE_ADD;
		addSaveRecord(url, values, callback);
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
		Map<String, String> postBody = new HashMap<String, String>();
		for (Change change : values) {
			postBody.put(change.key, change.value.getJsonString());
		}
		m_rpcClient.doPost(url, postBody, new RpcCallback() {
			public void response(RpcResponse response) {
				if (response.getResponseCode() != RpcResponse.STATUS_SUCCESS) {
					callback.error("Error saving record: ("
							+ response.getResponseCode() + ") "
							+ response.getMessage(), null);
				} else {
					JSONArray data = response.getData();
					if (data.size() == 0) {
						callback.error("No records found", null);
					} else if (data.size() > 1) {
						callback.error("Somehow got multiple records", null);
					} else {
						try {
							List<Record> records = m_recordParser
									.parseRecords(data);
							callback.gotRecord(records.get(0));
							notifyObservers();
						} catch (RecordParsingException e) {
							callback.error("Unreadable record", e);
						}
					}
				}
				m_pleaseWaitDialog.hide();
			}
		});
	}

	public final void deleteRecord(final Record record,
			final PlainCallback callback) {
		final String url = getUrl() + "?" + PARAM_OPERATION_TYPE + "="
				+ VALUE_REMOVE + "&" + Record.KEY_ID + "=" + record.getId();
		m_pleaseWaitDialog.show("Deleting record");
		m_rpcClient.doGet(url, new RpcCallback() {
			public void response(RpcResponse response) {
				if (response.getResponseCode() != RpcResponse.STATUS_SUCCESS) {
					callback.error("Error deleting record", null);
				} else {
					callback.success();
					notifyObservers();
				}
				m_pleaseWaitDialog.hide();
			}
		});
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
