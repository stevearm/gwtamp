package com.horsefire.gwtamp.client.records.widgets;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.Record;
import com.horsefire.gwtamp.client.records.datasource.DataSource;
import com.horsefire.gwtamp.client.records.datasource.PlainCallback;
import com.horsefire.gwtamp.client.records.widgets.RecordList.RecordSelectionObserver;

public class RecordAdmin extends Composite {

	private final DataSource m_dataSource;
	private final String m_displayField;

	public RecordAdmin(DataSource dataSource) {
		this(dataSource, null);
	}

	public RecordAdmin(DataSource dataSource, String preferredDisplayField) {
		m_dataSource = dataSource;
		m_displayField = preferredDisplayField;

		final HorizontalPanel outerRow = new HorizontalPanel();
		initWidget(outerRow);

		final VerticalPanel sidebar = new VerticalPanel();
		outerRow.add(sidebar);

		final HorizontalPanel buttonRow = new HorizontalPanel();
		sidebar.add(buttonRow);

		final RecordList recordList = new RecordList(m_dataSource);
		sidebar.add(recordList);

		final EditForm editForm = new EditForm(m_dataSource, true);
		outerRow.add(editForm);

		recordList.addSelectionObserver(new RecordSelectionObserver() {
			public void recordSelected(Record record) {
				editForm.setRecord(record);
			}
		});

		buttonRow.add(new Button("Refresh", new ClickListener() {
			public void onClick(Widget sender) {
				recordList.refreshList();
				editForm.setRecord(null);
			}
		}));
		buttonRow.add(new Button("+", new ClickListener() {
			public void onClick(Widget sender) {
				editForm.setRecord(null);
			}
		}));
		buttonRow.add(new Button("-", new ClickListener() {
			public void onClick(Widget sender) {
				Record selectedRecord = recordList.getSelectedRecord();
				if (selectedRecord == null) {
					Window.alert("You must select a record to delete it");
				} else {
					m_dataSource.deleteRecord(selectedRecord,
							new PlainCallback() {
								@Override
								public void success() {
								}
							});
				}
			}
		}));
	}
}
