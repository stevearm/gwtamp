package com.horsefire.gwtamp.client.records.values;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestDateValue extends GWTTestCase {

	public void testParse() {
		DateValue dateValue = DateValue.parse("4/20/2009");
		assertEquals("Didn't convert string date into seconds properly",
				1240200000, dateValue.get());
		assertEquals("Didn't convert string date into json string properly",
				"1240200000", dateValue.getJsonString());
		dateValue = new DateValue(1240200000);
		assertEquals("Didn't convert seconds into proper date", "4/20/2009",
				dateValue.getUserString());
		assertEquals("Didn't convert string date into json string properly",
				"1240200000", dateValue.getJsonString());
	}

	public void testFailure() {
		fail();
	}

	@Override
	public String getModuleName() {
		return "com.speedyserve.gwtamp.GwtAmp";
	}
}
