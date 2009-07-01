package com.horsefire.gwtamp.client.rpc;

import com.google.gwt.json.client.JSONArray;

/**
 * This class encapsulates the simple response that the server always returns: A
 * javascript object, with a single object inside named 'response'. Inside this
 * object is an integer named 'status'. If status equals STATUS_SUCCESS, then
 * there must be an array named 'data'. If status doesn't equal STATUS_SUCCESS,
 * there must be a user-viewable string named 'message'.
 */
public final class RpcResponse {

	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_RPC_ERROR = -1;

	static final String JSON_KEY_RESPONSE = "response";
	static final String JSON_KEY_STATUS = "status";
	static final String JSON_KEY_DATA = "data";
	static final String JSON_KEY_MESSAGE = "message";

	private final int m_responseCode;
	private final String m_message;
	private final JSONArray m_dataValue;

	public RpcResponse(int responseCode, String errorMessage) {
		m_responseCode = responseCode;
		m_message = errorMessage;
		m_dataValue = null;
	}

	public RpcResponse(int responseCode, JSONArray data) {
		m_responseCode = responseCode;
		m_message = null;
		m_dataValue = data;
	}

	public int getResponseCode() {
		return m_responseCode;
	}

	public String getMessage() {
		return m_message;
	}

	public JSONArray getData() {
		return m_dataValue;
	}
}
