package com.horsefire.contactList.client;

import com.google.gwt.junit.client.GWTTestCase;

public abstract class AbstractGwtTestCase extends GWTTestCase {

	@Override
	public final String getModuleName() {
		return "com.horsefire.contactList.ContactList";
	}
}
