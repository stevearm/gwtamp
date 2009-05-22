package com.horsefire.gwtamp.client.security;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.horsefire.gwtamp.client.records.datasource.ResponseParser;
import com.horsefire.gwtamp.client.records.datasource.ResponseParser.ResponseBean;
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

	private final ResponseParser m_responseParser = new ResponseParser();
	private String m_username = null;
	private Set<SecurityObserver> m_observers = new HashSet<SecurityObserver>();

	public SecurityEndpoint() {
	}

	private void makeRequest(final String url, final SimpleCallback callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.sendRequest("", new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Log.error("Problem reading reply from a GET to: " + url,
							exception);
					if (callback != null) {
						callback.done();
					}
				}

				public void onResponseReceived(Request request,
						Response response) {
					ResponseBean responseBean = m_responseParser
							.getResponse(response.getText());
					if (responseBean != null) {
						if (responseBean.responseCode == 0) {
							JSONObject jsonObject = responseBean.dataValue
									.isObject();
							if (jsonObject != null) {
								boolean loggedIn = jsonObject.get(
										JSON_KEY_LOGGEDIN).isBoolean()
										.booleanValue();
								if (loggedIn) {
									setUsername(jsonObject.get(
											JSON_KEY_USERNAME).isString()
											.stringValue());
								} else {
									setUsername(null);
								}
							}
						} else {
							Log.error("Response from GET:" + url + " was "
									+ responseBean.responseCode + " with '"
									+ responseBean.message + "'");
						}
					}
					if (callback != null) {
						callback.done();
					}
				}
			});
		} catch (RequestException e) {
			Log.error("Problem sending a GET to: " + url, e);
			if (callback != null) {
				callback.done();
			}
		}
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
