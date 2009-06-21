package com.horsefire.gwtamp.client.rpc;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

public class ResponseMock extends Response {

	public int statusCode = 0;
	public String text = "";

	@Override
	public String getHeader(String header) {
		return null;
	}

	@Override
	public Header[] getHeaders() {
		return null;
	}

	@Override
	public String getHeadersAsString() {
		return null;
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getStatusText() {
		return null;
	}

	@Override
	public String getText() {
		return text;
	}
}
