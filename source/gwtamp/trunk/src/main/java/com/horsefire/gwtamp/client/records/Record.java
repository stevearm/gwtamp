package com.horsefire.gwtamp.client.records;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.LinkValue;

public final class Record {

	public static final String KEY_ID = "id";

	private int m_id;
	private final Map<String, DataValue> m_data;
	private final Map<String, LinkValue> m_links;

	private String m_titleKey = null;;

	public Record() {
		m_data = new HashMap<String, DataValue>();
		m_links = new HashMap<String, LinkValue>();
	}

	public Record(String preferredTitleKey) {
		this();
		setPreferredTitleKey(preferredTitleKey);
	}

	public final void setPreferredTitleKey(String key) {
		m_titleKey = key;
	}

	public final Set<String> getDataKeys() {
		return m_data.keySet();
	}

	public final DataValue getDataValue(String key) {
		return m_data.get(key);
	}

	public final void setDataValue(String key, DataValue value) {
		m_data.put(key, value);
	}

	public final Set<String> getLinkKeys() {
		return m_links.keySet();
	}

	public final LinkValue getLinkId(String key) {
		return m_links.get(key);
	}

	public final void setLinkId(String key, LinkValue id) {
		m_links.put(key, id);
	}

	public final int getId() {
		return m_id;
	}

	public final void setId(int id) {
		m_id = id;
	}

	public final String getRecordTitle() {
		if (m_titleKey != null) {
			DataValue dataValue = getDataValue(m_titleKey);
			if (dataValue != null) {
				return dataValue.getUserString();
			}
		}
		return Integer.toString(m_id);
	}
}
