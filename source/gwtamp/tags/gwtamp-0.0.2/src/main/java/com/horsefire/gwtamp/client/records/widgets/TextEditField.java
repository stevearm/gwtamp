package com.horsefire.gwtamp.client.records.widgets;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.values.StringValue;

public class TextEditField extends AbstractStringEditField {

	private final TextArea m_textBox;

	public TextEditField(String key, StringValue value) {
		super(key, value);
		m_textBox = new TextArea();

		initWidget(m_textBox);
		m_textBox.setText(getCurrentValue());

		m_textBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				setCurrentValue(m_textBox.getText());
			}
		});
	}
}
