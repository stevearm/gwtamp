package com.horsefire.gwtamp.client.records.values;

import com.google.gwt.user.client.Timer;
import com.horsefire.gwtamp.client.records.datasource.SingleRecordCallback;

public class BrokenLinkValue extends LinkValue {

	public static final BrokenLinkValue INSTANCE = new BrokenLinkValue();

	private BrokenLinkValue() {
		super(-1, null);
	}

	@Override
	public void getRecord(final SingleRecordCallback callback) {
		new Timer() {
			@Override
			public void run() {
				callback.error("No link id is defined for this link", null);
			}
		}.schedule(1);
	}
}
