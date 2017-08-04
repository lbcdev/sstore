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

	public static void main(String[] args) {
		ConcurrentWorkload cworkload = new ConcurrentWorkload();
		// cworkload.simplePut(10, 100, 10);
		cworkload.simpleMix(90, 64, 100, 10);
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
		float avgresp = (float) ((end - start) / putPerClient / numOfClient);
		float filepers = (float) ((putPerClient * numOfClient) * 1000 / (end - start));
		System.out.println("Avg. latency: " + avgresp);
		System.out.println("Avg. file per second: " + filepers);
	}

	public void simpleGet(int numOfClient, int putPerClient, int fileSize) {

	}

	public void simpleMix(int readPercent, int numOfClient, int putPerClient, int fileSize) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < numOfClient; i++) {
			Thread client = new Thread(new Worker(readPercent, putPerClient));
			client.start();
			try {
				client.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		float avgresp = (float) ((end - start) / (float) putPerClient / (float) numOfClient);
		float filepers = (float) ((putPerClient * numOfClient) * 1000 / (end - start));
		System.out.println("Avg. latency: " + avgresp);
		System.out.println("Avg. file per second: " + filepers);
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

	class Worker implements Runnable {
		int numofread, numofwrite;

		public Worker(int rp, int num) {

			numofread = num * (rp / 100);
			numofwrite = num - numofread;
		}

		public void run() {
			while (numofread-- > 0) {
				remote = numofread + "";
				clientrpc.getReq(remote, local);
			}
			while (numofwrite-- > 0) {
				remote = numofwrite + "";
				clientrpc.putReq(local, remote);
			}
		}
	}
}
