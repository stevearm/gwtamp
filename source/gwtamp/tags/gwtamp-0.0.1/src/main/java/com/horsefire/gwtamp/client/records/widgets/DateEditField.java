package com.horsefire.gwtamp.client.records.widgets;

import java.util.Date;

import org.zenika.widget.client.datePicker.DatePicker;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.datasource.Change;
import com.horsefire.gwtamp.client.records.values.DateValue;

public class DateEditField extends EditField {

	private final DatePicker m_textBox;

	public DateEditField(String key, final DateValue value) {
		super(key, value);

		m_textBox = new DatePicker();
		initWidget(m_textBox);

		m_textBox.setText(getInitialValueNotNull().getUserString());

		m_textBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				DateValue dateValue = DateValue.parse(m_textBox.getText());
				if (dateValue == null) {
					Window.alert("Can't read that as a date");
					m_textBox.setText(getInitialValueNotNull().getUserString());
				}
			}
		});
	}

	private DateValue getInitialValueNotNull() {
		DateValue value = (DateValue) getInitialValue();
		if (value == null) {
			value = new DateValue(new Date().getTime());
		}
		return value;
	}

	private DateValue getCurrentValue() {
		return DateValue.parse(m_textBox.getText());
	}

	@Override
	public Change getChange() {
		return new Change(getKey(), getCurrentValue());
	}

	@Override
	public boolean isModified() {
		final DateValue currentValue = getCurrentValue();
		return (currentValue.get() != getInitialValueNotNull().get());
	}
}
