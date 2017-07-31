package org.sstore.benchmarks;

import org.sstore.client.ClientRpcImpl;
import org.sstore.utils.Constants;

/**
 * Generate concurrent workload with multi-threading.
 * 
 * @author lbchen
 *
 */
public class ConcurrentWorkload {

	ClientRpcImpl clientrpc;
	String local, remote;

	public ConcurrentWorkload() {
		clientrpc = new ClientRpcImpl(Constants.METARPC_NAME);
		local = "/Users/lbchen/got.png";
		remote = "/Users/lbchen/data/exp/";
	}

	/**
	 * Generate simple put workload with fix sized files.
	 * 
	 * @param numOfClient
	 * @param putPerClient
	 * @param fileSize
	 */
	public void simplePut(int numOfClient, int putPerClient, int fileSize) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < numOfClient; i++) {
			Thread client = new Thread(new PutWorker(putPerClient));
			client.start();
			try {
				client.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		long avgresp = (end - start) / putPerClient;
		int filepers = (int) ((putPerClient * numOfClient) / (end - start) * 1000);
		System.out.println(avgresp);
		System.out.println(filepers);
	}

	public void simpleGet(int numOfClient, int putPerClient, int fileSize) {

	}

	public void simpleMix(int readPercent, int numOfClient, int putPerClient, int fileSize) {

	}

	class PutWorker implements Runnable {
		int readnum;

		public PutWorker(int readPerClient) {
			readnum = readPerClient;
		}

		public void run() {
			while (readnum-- > 0) {
				clientrpc.putReq(local, remote + readnum);
			}
		}
	}
}
