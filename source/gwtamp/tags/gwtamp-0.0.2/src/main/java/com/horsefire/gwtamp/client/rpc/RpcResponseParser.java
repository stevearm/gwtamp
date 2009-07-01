package com.horsefire.gwtamp.client.rpc;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.util.Log;

/**
 * This class parses the initial response from the server into a RpcResponse
 */
class RpcResponseParser {

	public static RpcResponse parseResponse(final String response) {
		if (response == null || response.isEmpty()) {
			Log.error("Asked to parse null response");
			return null;
		}
		JSONValue tempValue = null;
		try {
			tempValue = JSONParser.parse(response);
		} catch (JSONException e) {
			Log.error("Can't parse response into JSON: " + response);
			return null;
		}

		JSONObject tempObject = tempValue.isObject();
		if (tempObject == null) {
			Log.error("Response is not an object: " + response);
			return null;
		}

		tempValue = tempObject.get(RpcResponse.JSON_KEY_RESPONSE);
		if (tempValue == null || tempValue.isObject() == null) {
			Log.error("'" + RpcResponse.JSON_KEY_RESPONSE
					+ "' does not exist or is not an object");
			return null;
		}

		tempObject = tempValue.isObject();

		tempValue = tempObject.get(RpcResponse.JSON_KEY_STATUS);
		if (tempValue == null || tempValue.isNumber() == null) {
			Log.error("'" + RpcResponse.JSON_KEY_STATUS
					+ "' does not exist or is not a number");
			return null;
		}
		final int responseCode = (int) tempValue.isNumber().doubleValue();

		if (responseCode == RpcResponse.STATUS_SUCCESS) {
			tempValue = tempObject.get(RpcResponse.JSON_KEY_DATA);
			if (tempValue == null || tempValue.isArray() == null) {
				Log.error("'" + RpcResponse.JSON_KEY_DATA
						+ "' does not exist or is not an array");
				return null;
			}
			return new RpcResponse(responseCode, tempValue.isArray());
		}

		tempValue = tempObject.get(RpcResponse.JSON_KEY_MESSAGE);
		if (tempValue == null || tempValue.isString() == null) {
			Log.error("'" + RpcResponse.JSON_KEY_MESSAGE
					+ "' does not exist or is not a string");
			return null;
		}
		return new RpcResponse(responseCode, tempValue.isString().stringValue());
	}
}
