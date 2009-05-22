package com.horsefire.gwtamp.client.records.values;

import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.datasource.SingleRecordCallback;

public class LinkValue extends Value {

	private final int m_id;
	private final DataSource m_dataSource;

	public LinkValue(int id, DataSource dataSource) {
		m_id = id;
		m_dataSource = dataSource;
	}

	public int getId() {
		return m_id;
	}

	public void getRecord(SingleRecordCallback callback) {
		m_dataSource.getRecord(m_id, callback);
	}

	@Override
	public String getJsonString() {
		return Integer.toString(m_id);
	}
}
