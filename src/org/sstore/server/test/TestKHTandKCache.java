package org.sstore.server.test;

import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;

import org.sstore.security.encryption.CipherHandler;
import org.sstore.security.encryption.DataKeyGenerator;
import org.sstore.utils.StreamFileUtils;

public class TestKHTandKCache {

	static CipherHandler handler;
	private static HashMap<String, byte[]> kcache = new HashMap<String, byte[]>();
	private static HashMap<String, SecretKeySpec> skcache = new HashMap<String, SecretKeySpec>();
	static long cid = 1838339;
	static int klen = 16;
	static String rootdir = "/Users/lbchen/sstoredata/";
	static String in = rootdir + "ism.png";
	static String cout = rootdir + "ismc.png";
	static String dout = rootdir + "ismd.png";

	public static void main(String[] args) {
		TestKHTandKCache test = new TestKHTandKCache();
		int num = 1000;
		int cc = 16;
		test.init(num);
		float raw_t = test.raw_r(cc, num);
		float kht_t = test.kht_r(cc, num);
		float kcache_t = test.kcache_r(cc, num);
		float percent = (kht_t - kcache_t) / kht_t;
		System.out.println((int) (percent * 100) + "%");

	}

	float raw_r(int cc, int num) {
		long start, end;
		start = System.currentTimeMillis();

		while (cc-- > 0) {
			Thread th = new Thread(new RawWorker(num));
			th.start();
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("per op: " + (float) (end - start) / (float) num);
		return end - start;
	}

	float kht_w(int num) {
		long start, end;
		start = System.currentTimeMillis();

		for (int i = 0; i < num; i++) {
			DataKeyGenerator keyGen = new DataKeyGenerator();
			// byte[] key = keyGen.genKey("jifjdifdfd.jpg", cid, klen);
			SecretKeySpec skey = keyGen.gen("jifjdifdfd.jpg", cid, klen);

			handler = new CipherHandler(skey);
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("per op: " + (float) (end - start) / (float) num);
		float kht_t = end - start;
		return kht_t;
	}

	float kht_r(int cc, int num) {
		long start, end;
		start = System.currentTimeMillis();
		while (cc-- > 0) {
			Thread th = new Thread(new KhtWorker(num));
			th.start();
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("per op: " + (float) (end - start) / (float) num);
		return end - start;
	}

	float kcache_w(int num) {
		long start, end;
		start = System.currentTimeMillis();

		for (int i = 0; i < num; i++) {
			String fname = "jifjdifdfd-" + i;
			// byte[] key = kcache.get(fname);
			SecretKeySpec skey = skcache.get(fname);

			handler = new CipherHandler(skey);
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);
		}

		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("per op: " + (float) (end - start) / (float) num);
		return end - start;
	}

	float kcache_r(int cc, int num) {
		long start, end;
		start = System.currentTimeMillis();

		while (cc-- > 0) {
			Thread th = new Thread(new KcacheWorker(num));
			th.start();
			try {
				th.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("per op: " + (float) (end - start) / (float) num);
		return end - start;
	}

	void init(int num) {
		for (int i = 0; i < num; i++) {
			DataKeyGenerator keyGen = new DataKeyGenerator();
			String fname = "jifjdifdfd-" + i;
			// byte[] key = keyGen.genKey(fname, cid, klen);
			SecretKeySpec skey = keyGen.gen("jifjdifdfd.jpg", cid, klen);
			skcache.put(fname, skey);
			// kcache.put(fname, key);
		}
	}

	class RawWorker implements Runnable {
		int num;

		public RawWorker(int num) {
			this.num = num;
		}

		public void run() {
			for (int i = 0; i < num; i++) {
				StreamFileUtils.readBytes(cout);
			}
		}
	}

	class KhtWorker implements Runnable {
		int num;

		public KhtWorker(int num) {
			this.num = num;
		}

		public void run() {
			for (int i = 0; i < num; i++) {
				DataKeyGenerator keyGen = new DataKeyGenerator();
				// byte[] key = keyGen.genKey("jifjdifdfd.jpg", cid, klen);
				SecretKeySpec skey = keyGen.gen("jifjdifdfd.jpg", cid, klen);

				handler = new CipherHandler(skey);
				byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			}
		}
	}

	class KcacheWorker implements Runnable {
		int num;

		public KcacheWorker(int num) {
			this.num = num;
		}

		public void run() {
			for (int i = 0; i < num; i++) {
				String fname = "jifjdifdfd-" + i;
				// byte[] key = kcache.get(fname);
				SecretKeySpec skey = skcache.get(fname);

				handler = new CipherHandler(skey);
				byte[] ddata = handler.decipher(StreamFileUtils.readBytes(cout));
				// StreamFileUtils.writeBytes(dout, dcdata);
			}
		}
	}
}
