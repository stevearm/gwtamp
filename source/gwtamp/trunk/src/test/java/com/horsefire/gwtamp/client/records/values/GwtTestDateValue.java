package com.horsefire.gwtamp.client.records.values;

import com.horsefire.gwtamp.client.AbstractGwtTestCase;

public class GwtTestDateValue extends AbstractGwtTestCase {

	public void testParse() {
		final String date = "4/20/2009";
		final String seconds = "1240200000";
		final int secondsInt = 1240200000;
		DateValue dateValue = DateValue.parse(date);
		assertEquals("Didn't convert string date into seconds properly",
				1240200000, dateValue.get());
		assertEquals("Didn't convert string date into json string properly",
				seconds, dateValue.getJsonString());
		dateValue = new DateValue(secondsInt);
		assertEquals("Didn't convert seconds into proper date", date, dateValue
				.getUserString());
		assertEquals("Didn't convert string date into json string properly",
				seconds, dateValue.getJsonString());
	}
}
