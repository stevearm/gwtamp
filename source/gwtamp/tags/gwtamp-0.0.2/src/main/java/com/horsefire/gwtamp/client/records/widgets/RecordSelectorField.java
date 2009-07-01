package com.horsefire.gwtamp.client.records.widgets;

import java.util.Collection;

import com.google.gwt.user.client.ui.ListBox;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.datasource.Change;
import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.datasource.MultipleRecordCallback;
import com.horsefire.gwtamp.client.records.fields.LinkField;
import com.horsefire.gwtamp.client.records.values.LinkValue;

public class RecordSelectorField extends EditField {

	private final ListBox m_listBox;
	private final DataSource m_dataSource;

	public RecordSelectorField(LinkField field, final LinkValue value) {
		super(field.getKey(), value);
		m_dataSource = field.getDataSource();
		final LinkField m_field = field;
		m_listBox = new ListBox();
		initWidget(m_listBox);

		m_listBox.setEnabled(false);
		m_listBox.addItem("Loading...");
		m_listBox.setSelectedIndex(0);

		m_field.getDataSource().getRecords(new MultipleRecordCallback() {
			@Override
			public void gotRecords(Collection<Record> records) {
				m_listBox.clear();
				m_listBox.setEnabled(true);
				int selected = 0;
				m_listBox.addItem("<none>", "-1");
				int i = 1;
				for (Record record : records) {
					m_listBox.addItem(record.getRecordTitle(), Integer
							.toString(record.getId()));
					if (value != null && value.getId() == record.getId()) {
						selected = i;
					}
					i++;
				}
				m_listBox.setSelectedIndex(selected);
			}

			@Override
			public void error(String message, Throwable e) {
				m_listBox.clear();
				m_listBox.addItem("Error loading records");
				m_listBox.setSelectedIndex(0);
				super.error(message, e);
			}
		});
	}

	@Override
	public Change getChange() {
		int id = Integer.parseInt(m_listBox.getValue(m_listBox
				.getSelectedIndex()));
		return new Change(getKey(), new LinkValue(id, m_dataSource));
	}

	@Override
	public boolean isModified() {
		// FIXME This isn't implemented
		return true;
	}
}
