package com.horsefire.gwtamp.client.records.values;

import junit.framework.TestCase;

public class SwitchBundleValueTest extends TestCase {

	// The first switch is the LSB, the second switch is the second LSB, so
	// defined switches move from right to left (LSB to MSB)
	// boolean 1001101 = 77 dec
	private static final long SWITCHES_LONG = 77;
	private static final boolean[] SWITCHES_BOOL = new boolean[] { true, false,
			true, true, false, false, true };

	private static String[] KEYS = new String[] { "first", "second", "third",
			"fourth", "fifth", "sixth", "seventh" };

	public void testParsing() {
		SwitchBundleValue value = new SwitchBundleValue(SWITCHES_LONG, KEYS);

		// Assert that it's parsing it right
		assertEquals("Parsed the wrong number of booleans",
				value.getValues().length, KEYS.length);
		for (int i = 0; i < 7; i++) {
			assertEquals("Parsed a switch value wrong for key=" + KEYS[i],
					SWITCHES_BOOL[i], value.getValue(KEYS[i]).getValue());
		}
	}
}
