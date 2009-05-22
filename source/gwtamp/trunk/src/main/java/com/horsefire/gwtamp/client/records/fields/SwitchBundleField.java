package com.horsefire.gwtamp.client.records.fields;

import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.records.values.DataValue;
import com.horsefire.gwtamp.client.records.values.SwitchBundleValue;

public class SwitchBundleField extends NumberField {

	public static final String KEY = "switchBundle";

	private final SingleSwitchSubField[] m_switches;
	private final String[] m_keys;

	public SwitchBundleField(SingleSwitchSubField[] switches) {
		super(KEY, true, "");
		m_switches = switches;
		m_keys = new String[m_switches.length];
		for (int i = 0; i < m_switches.length; i++) {
			m_keys[i] = m_switches[i].getKey();
		}
	}

	public SingleSwitchSubField[] getSwitches() {
		return m_switches;
	}

	public String[] getKeys() {
		return m_keys;
	}

	@Override
	public DataValue createValue(JSONValue value) {
		return new SwitchBundleValue((long) value.isNumber().doubleValue(),
				m_keys);
	}
}
