package com.horsefire.gwtamp.client.records.widgets;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.Composite;
import com.horsefire.gwtamp.client.records.datasource.Change;
import com.horsefire.gwtamp.client.records.values.Value;

public abstract class EditField extends Composite {

	public static interface EditFieldObserver {
		void valueChanged(EditField editField);
	}

	private final String m_key;
	private final Value m_value;

	private final Collection<EditFieldObserver> m_observers = new ArrayList<EditFieldObserver>();

	protected EditField(String key, Value value) {
		m_key = key;
		m_value = value;
	}

	public final String getKey() {
		return m_key;
	}

	protected final Value getInitialValue() {
		return m_value;
	}

	public abstract boolean isModified();

	public abstract Change getChange();

	public final void addObserver(EditFieldObserver o) {
		m_observers.add(o);
	}

	public final void removeObserver(EditFieldObserver o) {
		m_observers.remove(o);
	}

	protected final void notifyObervers() {
		for (EditFieldObserver o : m_observers) {
			o.valueChanged(this);
		}
	}
}
