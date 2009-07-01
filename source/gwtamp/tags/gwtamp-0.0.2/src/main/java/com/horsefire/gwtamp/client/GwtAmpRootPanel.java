package com.horsefire.gwtamp.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class GwtAmpRootPanel {

	public static final String DIV_ID = "mainGui";
	
	public static void initWidget(Widget widget) {
		final RootPanel rootPanel = RootPanel.get(DIV_ID);
		rootPanel.add(widget);
	}
}
