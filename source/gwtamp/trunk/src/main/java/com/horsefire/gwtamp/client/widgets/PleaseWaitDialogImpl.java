package com.horsefire.gwtamp.client.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class PleaseWaitDialogImpl implements PleaseWaitDialog {

	private static final Label EMPTY_LABEL = new Label("");

	private final DialogBox m_dialogBox;
	private final Grid m_grid = new Grid(1, 1);
	private boolean m_isShowing = false;

	public PleaseWaitDialogImpl() {
		m_dialogBox = new DialogBox(false, true);
		m_dialogBox.setText("Please wait");
		m_dialogBox.add(m_grid);
	}

	public void show(String message) {
		if (message == null || message.isEmpty()) {
			m_grid.setWidget(0, 0, EMPTY_LABEL);
		} else {
			m_grid.setWidget(0, 0, new Label(message));
		}
		if (!m_isShowing) {
			m_isShowing = true;
			m_dialogBox.setPopupPosition(100, 100);
			m_dialogBox.show();
		}
	}

	public void hide() {
		if (m_isShowing) {
			m_isShowing = false;
			m_dialogBox.hide();
		}
	}

	public boolean isShowing() {
		return m_isShowing;
	}
}
