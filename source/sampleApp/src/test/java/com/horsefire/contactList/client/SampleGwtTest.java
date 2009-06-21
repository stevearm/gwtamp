package com.horsefire.contactList.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialog;
import com.horsefire.gwtamp.client.widgets.PleaseWaitDialogImpl;

public class SampleGwtTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.horsefire.contactList.ContactList";
	}

	public void testPleaseWaitDialog() {
		final PleaseWaitDialog dialog = new PleaseWaitDialogImpl();
	}
}
