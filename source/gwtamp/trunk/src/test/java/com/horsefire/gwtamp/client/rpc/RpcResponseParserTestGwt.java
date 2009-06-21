package com.horsefire.gwtamp.client.rpc;

import com.horsefire.gwtamp.client.AbstractGwtTestCase;

public class RpcResponseParserTestGwt extends AbstractGwtTestCase {

	public void testUnparsableResponses() {
		assertNull(RpcResponseParser.parseResponse(null));
		assertNull(RpcResponseParser.parseResponse(""));
		assertNull(RpcResponseParser.parseResponse(" "));
		assertNull(RpcResponseParser.parseResponse("{}"));
		assertNull(RpcResponseParser.parseResponse("{\"status\":0}"));
		assertNull(RpcResponseParser.parseResponse("{}"));
		assertNull(RpcResponseParser.parseResponse("{}"));
	}

	public void testSuccessfulResponses() {
		final String beforeArray = new StringBuilder().append('{').append('"')
				.append(RpcResponse.JSON_KEY_RESPONSE).append('"').append(":{")
				.append('"').append(RpcResponse.JSON_KEY_STATUS).append('"')
				.append(':').append(RpcResponse.STATUS_SUCCESS).append(',')
				.append('"').append(RpcResponse.JSON_KEY_DATA).append('"')
				.append(':').append('[').toString();
		final String afterArray = "]}}";

		RpcResponse response = RpcResponseParser.parseResponse(beforeArray
				+ afterArray);
		assertNotNull(response);
		assertEquals(RpcResponse.STATUS_SUCCESS, response.getResponseCode());
		assertNotNull(response.getData());
		assertTrue(response.getData().size() == 0);
		assertNull(response.getMessage());

		response = RpcResponseParser.parseResponse(beforeArray
				+ "{},{\"something\":{}},{\"test\":5}" + afterArray);
		assertNotNull(response);
		assertEquals(RpcResponse.STATUS_SUCCESS, response.getResponseCode());
		assertNotNull(response.getData());
		assertTrue(response.getData().size() == 3);
		assertNull(response.getMessage());
	}

	public void testErrorResponses() {
		final String beforeCode = new StringBuilder().append('{').append('"')
				.append(RpcResponse.JSON_KEY_RESPONSE).append('"').append(":{")
				.append('"').append(RpcResponse.JSON_KEY_STATUS).append('"')
				.append(':').toString();
		final String betweenCodeAndMessage = new StringBuilder().append(',')
				.append('"').append(RpcResponse.JSON_KEY_MESSAGE).append('"')
				.append(':').append('"').toString();
		final String afterMessage = "\"}}";

		final int errorCode = 1;
		final String errorMessage = "Failure";
		RpcResponse response = RpcResponseParser.parseResponse(beforeCode
				+ errorCode + betweenCodeAndMessage + errorMessage
				+ afterMessage);
		assertNotNull(response);
		assertEquals(errorCode, response.getResponseCode());
		assertNull(response.getData());
		assertNotNull(response.getMessage());
		assertEquals(errorMessage, response.getMessage());
	}
}
