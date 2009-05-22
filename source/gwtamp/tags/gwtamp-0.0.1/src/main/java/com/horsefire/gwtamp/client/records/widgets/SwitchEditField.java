package com.horsefire.gwtamp.client.records.widgets;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.horsefire.gwtamp.client.records.values.SwitchSubValue;

public class SwitchEditField extends Composite {

	private final String m_key;
	private final CheckBox m_checkbox;

	public SwitchEditField(SwitchSubValue value) {
		m_key = value.getKey();
		m_checkbox = new CheckBox();
		m_checkbox.setChecked(value.getValue());
		initWidget(m_checkbox);
	}

	public SwitchSubValue getValue() {
		return new SwitchSubValue(m_key, m_checkbox.isChecked());
	}
}
