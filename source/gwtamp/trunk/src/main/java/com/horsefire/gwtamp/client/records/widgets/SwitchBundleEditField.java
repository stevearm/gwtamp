package com.horsefire.gwtamp.client.records.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.horsefire.gwtamp.client.records.datasource.Change;
import com.horsefire.gwtamp.client.records.fields.SingleSwitchSubField;
import com.horsefire.gwtamp.client.records.fields.SwitchBundleField;
import com.horsefire.gwtamp.client.records.values.SwitchBundleValue;
import com.horsefire.gwtamp.client.records.values.SwitchSubValue;

public class SwitchBundleEditField extends EditField {

	private final Collection<SwitchEditField> m_editFields = new ArrayList<SwitchEditField>();

	public SwitchBundleEditField(SwitchBundleField field,
			SwitchBundleValue value) {
		super(field.getKey(), (value != null) ? value : new SwitchBundleValue(
				0, field.getKeys()));

		final FlexTable panel = new FlexTable();
		initWidget(panel);

		SingleSwitchSubField[] switches = field.getSwitches();
		for (int i = 0; i < switches.length; i++) {
			panel.setWidget(i, 0, new Label(switches[i].getTitle()));
			SwitchSubValue switchValue;
			if (value == null) {
				switchValue = new SwitchSubValue(switches[i].getKey(), false);
			} else {
				switchValue = value.getValue(switches[i].getKey());
			}
			SwitchEditField editField = new SwitchEditField(switchValue);
			panel.setWidget(i, 1, editField);
			m_editFields.add(editField);
		}
	}

	@Override
	public Change getChange() {
		List<SwitchSubValue> changes = new ArrayList<SwitchSubValue>();
		for (SwitchEditField editField : m_editFields) {
			changes.add(editField.getValue());
		}
		return new Change(getKey(), new SwitchBundleValue(
				(SwitchBundleValue) getInitialValue(), changes
						.toArray(new SwitchSubValue[changes.size()])));
	}

	@Override
	public boolean isModified() {
		return true;
	}
}
