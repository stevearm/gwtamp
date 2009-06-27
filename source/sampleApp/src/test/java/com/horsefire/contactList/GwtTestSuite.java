package com.horsefire.contactList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.gwt.junit.tools.GWTTestSuite;
import com.horsefire.contactList.client.SampleTestGwt;

public class GwtTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new GWTTestSuite();
		suite.addTestSuite(SampleTestGwt.class);
		return suite;
	}
}
