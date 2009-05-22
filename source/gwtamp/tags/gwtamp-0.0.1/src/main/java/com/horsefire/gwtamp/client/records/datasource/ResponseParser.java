package com.horsefire.gwtamp.client.records.datasource;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.horsefire.gwtamp.client.util.Log;

public class ResponseParser {

	public static class ResponseBean {
		public int responseCode;
		public String message;
		public JSONValue dataValue;
	}

	private static final String JSON_KEY_RESPONSE = "response";
	private static final String JSON_KEY_STATUS = "status";
	private static final String JSON_KEY_DATA = "data";
	private static final String JSON_KEY_MESSAGE = "message";

	public ResponseBean getResponse(String response) {
		JSONValue tempValue = null;
		try {
			tempValue = JSONParser.parse(response);
		} catch (JSONException e) {
			Log.error("Can't parse response into JSON: " + response);
			return null;
		}

		JSONObject tempObject = tempValue.isObject();
		if (tempObject == null) {
			Log.error("Response is not an object");
			return null;
		}

		tempValue = tempObject.get(JSON_KEY_RESPONSE);
		if (tempValue == null || tempValue.isObject() == null) {
			Log.error("Response doesn't contain a '" + JSON_KEY_RESPONSE
					+ "' field, or it's not an object");
			return null;
		}

		tempObject = tempValue.isObject();

		ResponseBean responseBean = new ResponseBean();

		tempValue = tempObject.get(JSON_KEY_STATUS);
		if (tempValue == null || tempValue.isNumber() == null) {
			Log.error("'" + JSON_KEY_STATUS
					+ "' doesn't exist or isn't a number");
			return null;
		}
		responseBean.responseCode = Integer.parseInt(tempValue.isNumber()
				.toString());

		if (responseBean.responseCode == 0) {
			tempValue = tempObject.get(JSON_KEY_DATA);
			if (tempValue == null) {
				Log.error("'" + JSON_KEY_DATA + "' doesn't exist");
				return null;
			}
			responseBean.dataValue = tempValue;
		} else {
			tempValue = tempObject.get(JSON_KEY_MESSAGE);
			if (tempValue == null || tempValue.isString() == null) {
				Log.error("'" + JSON_KEY_MESSAGE
						+ "' doesn't exist or isn't a string");
			}
			responseBean.message = tempValue.isString().stringValue();
		}

		return responseBean;
	}
}
