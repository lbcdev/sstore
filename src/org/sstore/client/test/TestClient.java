package org.sstore.client.test;

import org.sstore.client.ClientRpcImpl;
import org.sstore.utils.Constants;

public class TestClient {

	private static ClientRpcImpl clientrpc;

	public static void main(String[] args) {
		String metahost = "localhost";
		clientrpc = new ClientRpcImpl(metahost);
//		nornalPut(200);
		securePut(500);
//		normalGet(1000);
//		secureGet(60);	
	}

	public static void nornalPut(int num) {
		long st = System.currentTimeMillis();
		String local = "resources/in.jpg";
		for (int i = 0; i < num; i++) {
			String remote = "normal-20-" + i + ".jpg";
			clientrpc.putReq(local, remote);
		}
		long end = System.currentTimeMillis();
		System.out.println((end - st)/(float)num);
	}

	public static void securePut(int num) {
		long st = System.currentTimeMillis();
		System.out.println("Put files...");
		String local = "resources/l200.png";
		for (int i = 0; i < num; i++) {
			String remote = "secure-20-" + i + ".jpg";
			clientrpc.putReqSecured(local, remote);
		}
		long end = System.currentTimeMillis();
		System.out.println((end - st)/(float)num);
	}

	public static void secureRandGet(int num) {
		long st = System.currentTimeMillis();
	//	clientrpc.monitorOn(primary);
		for (int i = 0; i < num; i++) {
			String remote = "secure-20-" + i + ".jpg";
			clientrpc.getReqSecured(remote);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - st);
	}
	
	public static void secureGet(int num) {
		long st = System.currentTimeMillis();
	//	clientrpc.monitorOn(primary);
		for (int i = 0; i < num; i++) {
			String remote = "secure-20-" + i + ".jpg";
			clientrpc.getReqSecured(remote);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - st);
	}
	
	public static void normalGet(int num) {
		long st = System.currentTimeMillis();
		for (int i = 0; i < num; i++) {
			String remote = "normal-" + i + ".jpg";
			clientrpc.getReq(remote, "");
		}
		long end = System.currentTimeMillis();
		System.out.println(end - st);
	}
}
