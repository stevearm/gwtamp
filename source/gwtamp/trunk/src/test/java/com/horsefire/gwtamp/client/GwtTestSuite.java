package com.horsefire.gwtamp.client;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.gwt.junit.tools.GWTTestSuite;
import com.horsefire.gwtamp.client.records.values.GwtTestDateValue;

public class GwtTestSuite extends GWTTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Gwt tests for the GwtAMP module");
		suite.addTestSuite(GwtTestDateValue.class);
		return suite;
	}
}
