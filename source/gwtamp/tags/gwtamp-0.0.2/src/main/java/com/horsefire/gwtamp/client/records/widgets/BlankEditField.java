package com.horsefire.gwtamp.client.records.widgets;

import com.google.gwt.user.client.ui.Label;
import com.horsefire.gwtamp.client.records.datasource.Change;

public class BlankEditField extends EditField {

	public BlankEditField(String key, String message) {
		super(key, null);
		initWidget(new Label(message));
	}

	@Override
	public Change getChange() {
		return null;
	}

	@Override
	public boolean isModified() {
		return false;
	}
}
