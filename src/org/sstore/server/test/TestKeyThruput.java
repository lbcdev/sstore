package org.sstore.server.test;

import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;

import org.sstore.security.encryption.DataKeyGenerator;

public class TestKeyThruput {
	private static HashMap<String, SecretKeySpec> kcache = new HashMap<String, SecretKeySpec>();
	static long cid = 1838339;
	static int klen = 16;
	static String filename = "lbchencn.jpg";

	public static void main(String[] args) {
		TestKeyThruput test = new TestKeyThruput();
		int num = 10000;
		long start, end;
		test.init(num);
		start = System.currentTimeMillis();
		test.kht(num);
		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("thruput: " + (float) num / (float) (end - start));
		float kht_t = end - start;

		start = System.currentTimeMillis();
		test.kcache(num);
		end = System.currentTimeMillis();
		float kcache_t = end - start;
		System.out.println(end - start);
		System.out.println("thruput: " + (float) num / (float) (end - start));

		System.out.println((kcache.size() * klen) / 1000 + " KB");
		float percent = (kht_t - kcache_t) / kht_t;
		System.out.println((int) (percent * 100) + "%");
	}

	void kht(int num) {
		for (int i = 0; i < num; i++) {

			DataKeyGenerator keyGen = new DataKeyGenerator();
			byte[] skey = keyGen.genKey(filename + i, cid, klen);

		}
	}

	void kcache(int num) {
		for (int i = 0; i < num; i++) {
			String fname = filename + i;
			SecretKeySpec skey = kcache.get(fname);
		}
	}

	void init(int num) {
		for (int i = 0; i < num; i++) {
			DataKeyGenerator keyGen = new DataKeyGenerator();
			String fname = "jifjdifdfd-" + i;
			SecretKeySpec skey = keyGen.gen(fname, cid, klen);
			kcache.put(fname, skey);
		}
	}
}
