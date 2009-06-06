package com.horsefire.gwtamp.client;

import com.google.gwt.junit.client.GWTTestCase;

public abstract class AbstractGwtTestCase extends GWTTestCase {

	@Override
	public final String getModuleName() {
		return "com.horsefire.gwtamp.GwtAmp";
	}
}
