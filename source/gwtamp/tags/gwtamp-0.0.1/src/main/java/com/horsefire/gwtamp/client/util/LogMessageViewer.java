package com.horsefire.gwtamp.client.util;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.util.AbstractLog.LogMessage;

public class LogMessageViewer extends DialogBox {

	private final ListBox m_listBox;

	public LogMessageViewer() {
		setText("Error log");

		final FlexTable table = new FlexTable();
		setWidget(table);

		m_listBox = new ListBox();
		m_listBox.setVisibleItemCount(10);
		table.setWidget(0, 0, m_listBox);
		table.getFlexCellFormatter().setColSpan(0, 0, 2);

		table.setWidget(1, 0, new Button("Clear log", new ClickListener() {
			public void onClick(Widget sender) {
				AbstractLog.get().clearMessages();
				refreshList();
			}
		}));
		table.setWidget(1, 1, new Button("Close", new ClickListener() {
			public void onClick(Widget sender) {
				LogMessageViewer.this.hide();
			}
		}));
	}

	private void refreshList() {
		m_listBox.clear();
		for (LogMessage message : AbstractLog.get().getMessages()) {
			String string = message.date + " - " + message.message;
			if (message.exception != null) {
				string = string + " - " + message.exception.getMessage();
			}
			m_listBox.addItem(string);
		}
	}

	public void show() {
		refreshList();
		super.show();
	}
}
