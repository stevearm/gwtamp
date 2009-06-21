package com.horsefire.gwtamp.client.rpc;

import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.horsefire.gwtamp.client.util.Log;

public class RpcClient {

	private static final String HTTP_ERROR_MESSAGE = "Error talking to server";

	public void doGet(String url, RpcCallback callback) {
		doRequest(new RequestBuilder(RequestBuilder.GET, url), "", callback);
	}

	public void doPost(String url, Map<String, String> body,
			RpcCallback callback) {
		StringBuilder realBody = new StringBuilder();
		for (String key : body.keySet()) {
			realBody.append(URL.encode(key)).append('=').append(
					URL.encode(body.get(key))).append('&');
		}
		realBody.setLength(realBody.length() - 1);
		doRequest(new RequestBuilder(RequestBuilder.GET, url), realBody
				.toString(), callback);
	}

	private void doRequest(RequestBuilder builder, String body,
			final RpcCallback callback) {
		final RequestCallback requestCallback = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Log.debug(HTTP_ERROR_MESSAGE, exception);
				callback.response(new RpcResponse(RpcResponse.STATUS_RPC_ERROR,
						HTTP_ERROR_MESSAGE));
			}

			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() != Response.SC_OK) {
					callback.response(new RpcResponse(
							RpcResponse.STATUS_RPC_ERROR, HTTP_ERROR_MESSAGE));
					return;
				}
				RpcResponse rpcResponse = RpcResponseParser
						.parseResponse(response.getText());
				if (rpcResponse == null) {
					rpcResponse = new RpcResponse(RpcResponse.STATUS_RPC_ERROR,
							"Unreadable response from server");
				}
				callback.response(rpcResponse);
			}
		};
		if (!body.isEmpty()) {
			builder.setHeader("Content-type",
					"application/x-www-form-urlencoded");
		}
		try {
			builder.sendRequest(body, requestCallback);
		} catch (final RequestException e) {
			callback.response(new RpcResponse(RpcResponse.STATUS_RPC_ERROR,
					HTTP_ERROR_MESSAGE));
		}
	}
}
