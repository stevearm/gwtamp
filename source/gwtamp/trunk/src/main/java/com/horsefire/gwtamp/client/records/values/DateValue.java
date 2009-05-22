package com.horsefire.gwtamp.client.records.values;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.horsefire.gwtamp.client.util.Log;

/**
 * This deals with dates like Unix. The GWT Date.getTime() returns milliseconds
 * since the epoch, while Unix timestamps use seconds. This uses seconds.
 */
public class DateValue extends IntegerValue {

	private static final int MILLIS_PER_SECOND = 1000;

	// Can't initialize this right here, because it's null the first time it's
	// accessed (only happens in web mode)
	private static DateTimeFormat s_format = null;

	private static DateTimeFormat getFormat() {
		if (s_format == null) {
			s_format = DateTimeFormat.getFormat("M/dd/yyyy");
		}
		return s_format;
	}

	public static DateValue parse(String value) {
		if (value == null || value.isEmpty()) {
			return new DateValue(new Date().getTime() / MILLIS_PER_SECOND);
		}
		try {
			Date parse = getFormat().parse(value);
			return new DateValue(parse.getTime() / MILLIS_PER_SECOND);
		} catch (IllegalArgumentException e) {
			Log.debug("Can't parse string into date", e);
		}
		return null;
	}

	public DateValue(long value) {
		super(value);
	}

	@Override
	public String getUserString() {
		return getFormat().format(new Date(get() * MILLIS_PER_SECOND));
	}
}
