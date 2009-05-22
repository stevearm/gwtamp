package com.horsefire.gwtamp.client.records.widgets;

import com.horsefire.gwtamp.client.records.datasource.Change;
import com.horsefire.gwtamp.client.records.values.IntegerValue;

public abstract class AbstractIntegerEditField extends EditField {

	private long m_currentValue = 0;

	protected AbstractIntegerEditField(String key, IntegerValue value) {
		super(key, value);
		if (value != null) {
			m_currentValue = value.get();
		}
	}

	@Override
	public final Change getChange() {
		return new Change(getKey(), new IntegerValue(m_currentValue));
	}

	@Override
	public final boolean isModified() {
		return getInitialValue() == null
				|| ((IntegerValue) getInitialValue()).get() == m_currentValue;
	}

	protected final void setCurrentValue(long value) {
		m_currentValue = value;
		notifyObervers();
	}

	protected final long getCurrentValue() {
		return m_currentValue;
	}
}
