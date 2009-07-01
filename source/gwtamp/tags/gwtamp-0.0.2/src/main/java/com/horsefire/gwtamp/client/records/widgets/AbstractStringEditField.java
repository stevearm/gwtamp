package com.horsefire.gwtamp.client.records.widgets;

import com.horsefire.gwtamp.client.records.datasource.Change;
import com.horsefire.gwtamp.client.records.values.StringValue;

public abstract class AbstractStringEditField extends EditField {

	private String m_currentValue = null;

	protected AbstractStringEditField(String key, StringValue value) {
		super(key, value);
		if (value != null) {
			m_currentValue = value.get();
		}
	}

	@Override
	public final Change getChange() {
		return new Change(getKey(), new StringValue(m_currentValue));
	}

	@Override
	public final boolean isModified() {
		return getInitialValue() == null
				|| !((StringValue) getInitialValue()).get().equals(
						m_currentValue);
	}

	protected final void setCurrentValue(String value) {
		m_currentValue = value;
		notifyObervers();
	}

	protected final String getCurrentValue() {
		return m_currentValue;
	}
}
