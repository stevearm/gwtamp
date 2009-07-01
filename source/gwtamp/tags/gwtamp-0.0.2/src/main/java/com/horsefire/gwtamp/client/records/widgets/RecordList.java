package com.horsefire.gwtamp.client.records.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.datasource.MultipleRecordCallback;
import com.horsefire.gwtamp.client.records.datasource.DataSource.ChangeObserver;

public class RecordList extends Composite {

	public static interface RecordSelectionObserver {
		void recordSelected(Record record);
	}

	private final Collection<RecordSelectionObserver> m_selectionObservers = new ArrayList<RecordSelectionObserver>();
	private final ListBox m_listBox;
	private final DataSource m_dataSource;

	private Map<String, Record> m_recordsCache;

	public RecordList(final DataSource dataSource) {
		m_dataSource = dataSource;

		m_listBox = new ListBox();
		m_listBox.setVisibleItemCount(20);
		final ScrollPanel scroller = new ScrollPanel(m_listBox);
		initWidget(scroller);

		// Listen to library and update list accordingly
		m_dataSource.addObserver(new ChangeObserver() {
			public void dataSourceChange() {
				refreshList();
			}
		});

		// Listen to list and notify observers on selection change
		m_listBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				Record selectedRecord = m_recordsCache.get(m_listBox
						.getValue(m_listBox.getSelectedIndex()));
				for (RecordSelectionObserver o : m_selectionObservers) {
					o.recordSelected(selectedRecord);
				}
			}
		});
	}

	public final void refreshList() {
		m_recordsCache = null;
		m_dataSource.getRecords(new MultipleRecordCallback() {
			public void gotRecords(Collection<Record> records) {
				m_listBox.clear();
				m_recordsCache = new HashMap<String, Record>();
				for (Record singleRecord : records) {
					String id = Integer.toString(singleRecord.getId());
					m_recordsCache.put(id, singleRecord);
					m_listBox.addItem(singleRecord.getRecordTitle(), id);
				}
			}
		});
	}

	public final void addSelectionObserver(RecordSelectionObserver o) {
		m_selectionObservers.add(o);
	}

	public final void removeSelectionObserver(RecordSelectionObserver o) {
		m_selectionObservers.remove(o);
	}

	public final Record getSelectedRecord() {
		return m_recordsCache.get(m_listBox.getValue(m_listBox
				.getSelectedIndex()));
	}

	@Override
	protected void onLoad() {
		refreshList();
	}
}
