package com.horsefire.gwtamp.client.records.widgets;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.fields.StringField;
import com.horsefire.gwtamp.client.records.values.StringValue;

public class StringEditField extends AbstractStringEditField {

	private final TextBox m_textBox;

	public StringEditField(StringField field, StringValue value) {
		super(field.getKey(), value);
		m_textBox = new TextBox();

		initWidget(m_textBox);
		m_textBox.setText(getCurrentValue());
		m_textBox.setMaxLength(field.getMaxLength());

		m_textBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				setCurrentValue(m_textBox.getText());
			}
		});
	}
}
