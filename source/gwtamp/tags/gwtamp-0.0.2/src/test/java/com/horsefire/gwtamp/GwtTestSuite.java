package com.horsefire.gwtamp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.gwt.junit.tools.GWTTestSuite;

public class GwtTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new GWTTestSuite();
		suite.addTestSuite(com.horsefire.gwtamp.client.records.values.GwtTestDateValue.class);
		suite.addTestSuite(com.horsefire.gwtamp.client.rpc.RpcResponseParserTestGwt.class);
		suite.addTestSuite(com.horsefire.gwtamp.client.records.datasource.RecordParserTestGwt.class);
		return suite;
	}
}
