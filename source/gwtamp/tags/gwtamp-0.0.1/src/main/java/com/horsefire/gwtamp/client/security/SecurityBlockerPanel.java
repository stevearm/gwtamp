package com.horsefire.gwtamp.client.security;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.horsefire.gwtamp.client.security.SecurityEndpoint.SecurityObserver;
import com.horsefire.gwtamp.client.security.SecurityEndpoint.SimpleCallback;

public class SecurityBlockerPanel extends Composite {

	private final Grid m_grid = new Grid(1, 1);
	private final Widget m_realGui;
	private final SecurityStatusWidget m_loginWidget;
	private final SecurityEndpoint m_securityManager;

	public SecurityBlockerPanel(SecurityEndpoint securityManager,
			Widget realGui, SecurityStatusWidget securityStatusWidget) {
		initWidget(m_grid);
		m_loginWidget = securityStatusWidget;
		m_realGui = realGui;
		m_securityManager = securityManager;
	}

	@Override
	protected void onLoad() {
		m_grid.setWidget(0, 0, new Label("Loading..."));
		m_securityManager.init(new SimpleCallback() {
			public void done() {
				m_securityManager.addSecurityObserver(new SecurityObserver() {
					public void securityStatusChange() {
						updatePanel();
					}
				});
				updatePanel();
			}
		});
	}

	private void updatePanel() {
		if (m_securityManager.isLoggedIn()) {
			m_grid.setWidget(0, 0, m_realGui);
		} else {
			m_grid.setWidget(0, 0, m_loginWidget);
		}
	}
}
