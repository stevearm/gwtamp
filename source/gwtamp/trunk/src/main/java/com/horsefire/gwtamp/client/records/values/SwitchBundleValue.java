package com.horsefire.gwtamp.client.records.values;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class encapsulates an ordered list of boolean values into a single int.
 * The boolean values are aligned to the LSB of the int, so for 3 boolean values
 * [true, true, false] the int would be 3 (011 binary)
 */
public class SwitchBundleValue extends DataValue {

	private final Map<String, SwitchSubValue> m_values = new HashMap<String, SwitchSubValue>();
	private final String[] m_keys;

	public SwitchBundleValue(long switchBundleInt, String[] keys) {
		m_keys = keys;
		for (int i = 0; i < m_keys.length; i++) {
			long tempLong = switchBundleInt >> i;
			boolean booleanValue = ((tempLong & 1) == 1);
			m_values
					.put(m_keys[i], new SwitchSubValue(m_keys[i], booleanValue));
		}
	}

	public SwitchBundleValue(SwitchBundleValue baseValue,
			SwitchSubValue[] overrides) {
		m_keys = baseValue.m_keys;
		m_values.putAll(baseValue.m_values);
		for (SwitchSubValue override : overrides) {
			m_values.put(override.getKey(), override);
		}
	}

	public SwitchSubValue getValue(String key) {
		return m_values.get(key);
	}

	public SwitchSubValue[] getValues() {
		Collection<SwitchSubValue> values = m_values.values();
		return values.toArray(new SwitchSubValue[values.size()]);
	}

	@Override
	public String getJsonString() {
		long result = 0;
		for (int i = (m_keys.length - 1); i >= 0; i--) {
			result = result << 1;
			SwitchSubValue value = getValue(m_keys[i]);
			if (value != null && value.getValue()) {
				result++;
			}
		}
		return Long.toString(result);
	}
}
