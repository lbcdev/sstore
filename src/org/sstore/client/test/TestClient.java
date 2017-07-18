package org.sstore.client.test;

import org.sstore.client.ClientRpcImpl;
import org.sstore.utils.Constants;

public class TestClient {

	private static ClientRpcImpl clientrpc;
	public static void main(String[] args) {
		clientrpc = new ClientRpcImpl(Constants.METARPC_NAME);
		testPutReq(10);
	}

	public static void testPutReq(int testnum) {
		String local = "/Users/lbchen/got.png";
		for (int i = 0; i < testnum; i++) {
			String remote = "got" + i + ".jpg";
			clientrpc.putReq(local, remote);
		}
	}
}
