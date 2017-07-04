package org.sstore.client.test;

import org.sstore.client.ClientRpcImpl;

public class TestClient {

	private static ClientRpcImpl clientrpc = new ClientRpcImpl();
	public static void main(String[] args) {
		
		testPutReq(4);
	}

	public static void testPutReq(int testnum) {
		String local = "resources/in1.jpg";
		for (int i = 0; i < testnum; i++) {
			String remote = "in" + i + ".jpg";
			clientrpc.putReq(local, remote);
		}
	}
}
