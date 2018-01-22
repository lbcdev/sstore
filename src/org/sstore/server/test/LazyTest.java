package org.sstore.server.test;

import java.util.HashMap;
import java.util.Random;

import javax.crypto.spec.SecretKeySpec;

import org.sstore.client.ClientRpcImpl;
import org.sstore.security.encryption.CipherHandler;
import org.sstore.security.encryption.DataKeyGenerator;
import org.sstore.server.storage.DataServerFileIO;
import org.sstore.utils.SstoreConfig;

public class LazyTest {

	static ClientRpcImpl clientrpc;
	String local, remote;
	static int objectCount, operCount, clientCount, readProp, insertProp, deleteProp;
	static String distributionType;
	private static DataServerFileIO fileIO;
	CipherHandler chandle;

	public static void main(String[] args) {
		String rootpath = "localhost-1100/";
		fileIO = new DataServerFileIO(rootpath);
		LazyTest test = new LazyTest();
		test.secureGet();
	}

	public SecretKeySpec requestKey(String remotepath) {
		DataKeyGenerator keyGen = new DataKeyGenerator();
		SecretKeySpec key = keyGen.gen(remotepath);
		return key;
	}

	public void secureGet() {
		HashMap<String, byte[]> objTable = new HashMap<String, byte[]>();
		String remote = "";
		int oper = 20000;
		int objCount = 400;
		for (int i = 0; i < objCount; i++) {
			remote = "secure-20-" + i + ".jpg";
			objTable.put(remote, fileIO.get(remote));
		}
		long start = System.currentTimeMillis();

		for (int i = 0; i < oper; i++) {
			remote = "secure-20-" + i % objCount + ".jpg";
			SecretKeySpec skey = requestKey(remote);
			byte[] data = objTable.get(remote);
		}
		
		long eclipse = System.currentTimeMillis() - start;
		System.out.println(eclipse);
		if (eclipse > 0) {
			float avgresp = (float) eclipse / oper;
			float thruput = (float) (oper * 1000 / eclipse);
			System.out.println("Avg. latency: " + avgresp);
			System.out.println("thruput: " + thruput);
		}
		
		start = System.currentTimeMillis();
		for (int i = 0; i < oper; i++) {
			remote = "secure-20-" + i % objCount + ".jpg";
			byte[] cdata = objTable.get(remote);
			SecretKeySpec skey = requestKey(remote);
			chandle = new CipherHandler(skey);
			byte[] data = chandle.cipher(cdata);
		}

		eclipse = System.currentTimeMillis() - start;
		System.out.println(eclipse);
		if (eclipse > 0) {
			float avgresp = (float) eclipse / oper;
			float thruput = (float) (oper * 1000 / eclipse);
			System.out.println("Avg. latency: " + avgresp);
			System.out.println("thruput: " + thruput);
		}
	}

	public void prepare(String configFile) {
		clientrpc = new ClientRpcImpl("localhost");
		local = "resources/in.png";

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
			int count = operCount;
			long maxLate = 0, minLate = 0;
			// random number for read/write
			Random rand1 = new Random(System.currentTimeMillis());
			// random object id
			Random randId = new Random(System.currentTimeMillis());
			while (count-- > 0) {

				String remote = "secure-20-" + randId.nextInt(objectCount) + ".jpg";
				// System.out.println(remote);

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
				// System.out.println(remote);
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
