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
	
	public static void main(String[] args){
		ConcurrentWorkload cwordload = new ConcurrentWorkload();
		cwordload.simplePut(10, 100, 10);
	}
	public ConcurrentWorkload() {
		clientrpc = new ClientRpcImpl(Constants.METARPC_NAME);
		local = "/Users/lbchen/got.png";
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
		System.out.println(end - start);
		float avgresp = (float) (end - start) / putPerClient;
		float filepers = (float) ((putPerClient * numOfClient) * 1000 / (end - start) );
		System.out.println("Avg. latency: " + avgresp);
		System.out.println("Avg. file per second: " + filepers);
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
				remote = readnum + "";
				clientrpc.putReq(local, remote);
			}
		}
	}
}
