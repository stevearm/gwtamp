package com.horsefire.gwtamp.client.records.widgets;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.records.values.IntegerValue;

public class IntegerEditField extends AbstractIntegerEditField {

	private final TextBox m_textArea;

	public IntegerEditField(String key, IntegerValue value) {
		super(key, value);
		m_textArea = new TextBox();

		initWidget(m_textArea);
		m_textArea.setText("" + getCurrentValue());

		m_textArea.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				try {
					setCurrentValue(Long.parseLong(m_textArea.getText()));
				} catch (NumberFormatException e) {
					Window.alert("Can't parse that as a number");
					m_textArea.setText("" + getCurrentValue());
				}
			}
		});
	}
}
