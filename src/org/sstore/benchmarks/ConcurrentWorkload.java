package org.sstore.benchmarks;

import java.util.Random;

import org.sstore.client.ClientRpcImpl;
import org.sstore.utils.Constants;
import org.sstore.utils.SstoreConfig;

/**
 * Generate concurrent workload with multi-threading.
 * 
 * @author lbchen
 *
 */
public class ConcurrentWorkload {

	static ClientRpcImpl clientrpc;
	String local, remote;
	static int objectCount, operCount, clientCount, readProp, insertProp, deleteProp;
	static String distributionType;

	public static void main(String[] args) {
		ConcurrentWorkload cworkload = new ConcurrentWorkload();
		// cworkload.simplePut(10, 100, 10);
		String configFile = "resources/workloads/readOnlyWorkloads";
		cworkload.prepare(configFile);
		cworkload.run();
	}

	public ConcurrentWorkload() {

	}

	public void prepare(String configFile) {
		clientrpc = new ClientRpcImpl("localhost");
		local = "resources/s40.jpg";

		SstoreConfig configs = new SstoreConfig(configFile);
		objectCount = configs.getInteger("object_count");
		operCount = configs.getInteger("operation_count");
		clientCount = configs.getInteger("client_count");
		readProp = configs.getInteger("read_prop");
		insertProp = configs.getInteger("delete_prop");
		deleteProp = configs.getInteger("insert_prop");
		distributionType = configs.getProp("distribution_type");

		System.out.println("objectCount: " + objectCount);
		System.out.println("operCount: " + operCount);
		System.out.println("clientCount: " + clientCount);
		System.out.println("readProp: " + readProp);

	}

	public void run() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < clientCount; i++) {
			Thread client = new Thread(new Worker(readProp, operCount));
			client.start();
			try {
				client.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		float avgresp = (float) ((end - start) / operCount / clientCount);
		float filepers = (float) ((operCount * clientCount) * 1000 / (end - start));
		System.out.println("Total Avg. latency: " + avgresp);
		System.out.println("Total thruput: " + filepers);
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

		}

		public void run() {

			long start = System.currentTimeMillis();
			switch (distributionType) {
			case "uniform":
				uniform();
				break;
			case "part_popular":
				partPopular();
				break;
			case "latest":
				break;
			default:
				break;
			}

			long eclipse = System.currentTimeMillis() - start;
			System.out.println(eclipse);
			if (eclipse > 0) {
				float avgresp = (float) eclipse / operCount;
				float thruput = (float) (operCount * 1000 / eclipse);
				System.out.println("Avg. latency: " + avgresp);
				System.out.println("thruput: " + thruput);
			}
		}

		/**
		 * Uniform distribution, picking an object randomly.
		 */
		public void uniform() {
			System.out.println("start uniform workloads");

			int count = operCount;
			long maxLate = 0, minLate = 0;
			// random number for read/write
			Random rand1 = new Random(System.currentTimeMillis());
			// random object id
			Random randId = new Random(System.currentTimeMillis());
			while (count-- > 0) {

				String remote = "secure-20-" + randId.nextInt(objectCount) + ".jpg";
//				System.out.println(remote);

				long start = System.currentTimeMillis();
				if (rand1.nextInt(100) > readProp) {
					clientrpc.putReqSecured(local, remote);
				} else {
					clientrpc.getReqSecured(remote);
				}
				long eclipse = System.currentTimeMillis() - start;
				maxLate = eclipse > maxLate ? eclipse : maxLate;
				minLate = Math.min(eclipse, minLate);

			}
			System.out.println("Max latency: " + maxLate);
			System.out.println("Minimum latency: " + minLate);
		}

		public void partPopular() {
			System.out.println("start part_popular workloads");

			String remote = null;
			long maxLate = 0, minLate = 0;
			// pick a start id randomly.
			Random randRange = new Random(System.currentTimeMillis());
			// int idStart = randRange.nextInt(objectCount);
			// random number for popular items.
			Random randPop = new Random(System.currentTimeMillis());
			int count = operCount;

			while (count-- > 0) {
				long start = System.currentTimeMillis();
				if (randPop.nextInt(100) > 10) {
					// pick from popular range randomly.
					int id = randRange.nextInt(objectCount / 10);
					remote = "secure-20-" + id + ".jpg";
				} else {
					int id = objectCount / 10 + randRange.nextInt(objectCount * 9 / 10);
					remote = "secure-20-" + id + ".jpg";
				}
				System.out.println(remote);
				if (randPop.nextInt(100) > readProp) {
					clientrpc.putReqSecured(local, remote);
				} else {
					clientrpc.getReqSecured(remote);
					long eclipse = System.currentTimeMillis() - start;
					maxLate = eclipse > maxLate ? eclipse : maxLate;
					minLate = Math.min(eclipse, minLate);
				}
				System.out.println("Max latency: " + maxLate);
				System.out.println("Minimum latency: " + minLate);
			}
		}
	}
}
