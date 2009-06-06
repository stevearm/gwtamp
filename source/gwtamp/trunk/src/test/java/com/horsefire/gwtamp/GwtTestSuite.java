package com.horsefire.gwtamp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.gwt.junit.tools.GWTTestSuite;
import com.horsefire.gwtamp.client.records.values.GwtTestDateValue;

public class GwtTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new GWTTestSuite();
		suite.addTestSuite(GwtTestDateValue.class);
		return suite;
	}
}
