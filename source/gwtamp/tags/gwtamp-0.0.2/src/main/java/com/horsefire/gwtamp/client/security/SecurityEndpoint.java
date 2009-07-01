package com.horsefire.gwtamp.client.security;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.rpc.RpcCallback;
import com.horsefire.gwtamp.client.rpc.RpcClient;
import com.horsefire.gwtamp.client.rpc.RpcResponse;
import com.horsefire.gwtamp.client.util.Log;

public class SecurityEndpoint {

	public static interface SimpleCallback {
		void done();
	}

	public static interface SecurityObserver {
		void securityStatusChange();
	}

	private static final String BASE_URL = "security.php?_operationType=";
	private static final String VALUE_STATUS = "status";
	private static final String VALUE_LOGIN = "login";
	private static final String VALUE_LOGOUT = "logout";

	private static final String JSON_KEY_LOGGEDIN = "loggedIn";
	private static final String JSON_KEY_USERNAME = "username";

	private static final String REQUEST_KEY_USERNAME = "username";
	private static final String REQUEST_KEY_PASSWORD = "password";

	public static final String REQUEST_VALUE_USERNAME = "user";

	private final RpcClient m_responseParser;
	private String m_username = null;
	private final Set<SecurityObserver> m_observers = new HashSet<SecurityObserver>();

	public SecurityEndpoint(RpcClient rpcClient) {
		m_responseParser = rpcClient;
	}

	private void makeRequest(final String url, final SimpleCallback callback) {
		m_responseParser.doGet(url, new RpcCallback() {
			public void response(RpcResponse response) {
				if (response.getResponseCode() == RpcResponse.STATUS_SUCCESS) {
					JSONArray data = response.getData();
					if (data.size() == 1) {
						JSONValue value = data.get(0);
						JSONObject object = value.isObject();
						value = object.get(JSON_KEY_LOGGEDIN);
						if (value != null && value.isBoolean() != null
								&& value.isBoolean().booleanValue()) {
							value = object.get(JSON_KEY_USERNAME);
							if (value != null && value.isString() != null) {
								setUsername(value.isString().stringValue());
							} else {
								Log
										.error("Security RPC says logged in, but didn't include username");
							}
						} else {
							setUsername(null);
						}
					} else {
						Log.error("RPC response had a non-1-length data array");
					}
				} else {
					Log.error("Error doing security RPC: ("
							+ response.getResponseCode() + ") "
							+ response.getMessage());
				}
				callback.done();
			}
		});
	}

	public void init(final SimpleCallback callback) {
		makeRequest(GWT.getHostPageBaseURL() + BASE_URL + VALUE_STATUS,
				callback);
	}

	public boolean isLoggedIn() {
		return m_username != null;
	}

	public String getUsername() {
		return m_username;
	}

	public void login(String username, String password,
			final SimpleCallback callback) {
		makeRequest(GWT.getHostPageBaseURL() + BASE_URL + VALUE_LOGIN + '&'
				+ REQUEST_KEY_USERNAME + '=' + URL.encode(username) + '&'
				+ REQUEST_KEY_PASSWORD + '=' + URL.encode(password), callback);
	}

	public void logout(final SimpleCallback callback) {
		makeRequest(GWT.getHostPageBaseURL() + BASE_URL + VALUE_LOGOUT,
				callback);
	}

	private void setUsername(String username) {
		if (m_username == null) {
			if (username == null) {
				return;
			}
		} else {
			if (m_username.equals(username)) {
				return;
			}
		}
		m_username = username;
		for (SecurityObserver o : m_observers) {
			o.securityStatusChange();
		}
	}

	public void addSecurityObserver(SecurityObserver o) {
		m_observers.add(o);
	}
}
