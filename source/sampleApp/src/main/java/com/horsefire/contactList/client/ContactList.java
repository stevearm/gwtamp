package com.horsefire.contactList.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.TabPanel;
import com.horsefire.gwtamp.client.GwtAmpRootPanel;
import com.horsefire.gwtamp.client.records.widgets.RecordAdmin;
import com.horsefire.gwtamp.client.rpc.RpcClient;
import com.horsefire.gwtamp.client.security.SecurityBlockerPanel;
import com.horsefire.gwtamp.client.security.SecurityEndpoint;
import com.horsefire.gwtamp.client.security.SecurityStatusWidget;
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

		SecurityEndpoint securityEndpoint = new SecurityEndpoint(
				new RpcClient());
		SecurityBlockerPanel securityWrapper = new SecurityBlockerPanel(
				securityEndpoint, tabPanel, new SecurityStatusWidget(
						securityEndpoint));

		GwtAmpRootPanel.initWidget(securityWrapper);
	}
}
