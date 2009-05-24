package com.horsefire.contactList.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.widgets.RecordAdmin;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialogImpl;

public class ContactList implements EntryPoint {

	public void onModuleLoad() {
		final DataSourceBundle dsBundle = new DataSourceBundle(
				new PleaseWaitDialogImpl());
		final TabPanel tabPanel = new TabPanel();
		tabPanel.add(new RecordAdmin(dsBundle.getContactDS()), "Contacts");
		tabPanel.add(new RecordAdmin(dsBundle.getPhoneNumberDS()),
				"Phone Numbers");
		tabPanel.selectTab(0);
		initWidget(tabPanel);
	}

	protected final void initWidget(Widget widget) {
		final RootPanel rootPanel = RootPanel.get("mainGui");
		rootPanel.add(widget);
	}
}
