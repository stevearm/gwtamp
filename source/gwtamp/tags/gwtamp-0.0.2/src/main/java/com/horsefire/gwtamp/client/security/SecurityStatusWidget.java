package com.horsefire.gwtamp.client.security;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.security.SecurityEndpoint.SimpleCallback;

public class SecurityStatusWidget extends Composite {

	private final SecurityEndpoint m_securityEndpoint;
	private final HorizontalPanel m_panel;

	public SecurityStatusWidget(SecurityEndpoint securityEndpoint) {
		m_securityEndpoint = securityEndpoint;
		m_panel = new HorizontalPanel();
		initWidget(m_panel);
	}

	@Override
	protected void onLoad() {
		refreshWidget();
	}

	private void refreshWidget() {
		m_panel.clear();
		if (m_securityEndpoint.isLoggedIn()) {
			m_panel.add(new Label("Logged in as "
					+ m_securityEndpoint.getUsername()));
			m_panel.add(new Button("Logout", new ClickListener() {
				public void onClick(Widget sender) {
					m_securityEndpoint.logout(new SimpleCallback() {
						public void done() {
							refreshWidget();
						}
					});
				}
			}));
		} else {
			Grid grid = new Grid(1, 3);
			m_panel.add(grid);
			grid.setWidget(0, 0, new Label("Password:"));
			final TextBox passwordBox = new PasswordTextBox();
			grid.setWidget(0, 1, passwordBox);
			final Button loginButton = new Button("Login", new ClickListener() {
				public void onClick(Widget sender) {
					m_securityEndpoint.login(
							SecurityEndpoint.REQUEST_VALUE_USERNAME,
							passwordBox.getText(), new SimpleCallback() {
								public void done() {
									if (m_securityEndpoint.isLoggedIn()) {
										refreshWidget();
									} else {
										Window.alert("Bad password");
									}
								}
							});
				}
			});
			passwordBox.addKeyboardListener(new KeyboardListener() {
				public void onKeyPress(Widget sender, char keyCode,
						int modifiers) {
					if (keyCode == KeyboardListener.KEY_ENTER) {
						loginButton.click();
					}
				}

				public void onKeyUp(Widget sender, char keyCode, int modifiers) {
				}

				public void onKeyDown(Widget sender, char keyCode, int modifiers) {
				}
			});
			grid.setWidget(0, 2, loginButton);
		}
	}
}
