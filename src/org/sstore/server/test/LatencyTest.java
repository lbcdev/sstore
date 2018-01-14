package org.sstore.server.test;

import org.sstore.client.ClientRpcImpl;

public class LatencyTest {

	public static void main(String[] args) {
		String metahost = "localhost";
		ClientRpcImpl clientrpc = new ClientRpcImpl(metahost);
		String local = "resources/in.jpg";
		String remote = "in.jpg";
		clientrpc.putReqSecured(local, remote);
		local = "/Users/lbchen/data/out705.jpg";
		clientrpc.getReqSecured(remote);

		// remote = "in1.jpg";
		// clientrpc.putReq(local, remote);
		// local = "/Users/lbchen/out718.jpg";
		// clientrpc.getReq(remote, local);
	}
	
	
}
