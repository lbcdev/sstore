package org.sstore.server.test;

import java.util.HashMap;

import org.sstore.security.encryption.DataKeyGenerator;

public class TestKHTandKCache {

	private static HashMap<String, byte[]> kcache = new HashMap<String, byte[]>();
	static long cid = 1838339;
	static int klen = 16;

	public static void main(String[] args) {
		TestKHTandKCache test = new TestKHTandKCache();
		int num = 100000;
		long start, end;
		test.init(num);
		start = System.currentTimeMillis();
		test.kht(num);
		end = System.currentTimeMillis();
		System.out.println(end - start);
		
		start = System.currentTimeMillis();
		test.kcache(num);
		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println((kcache.size() * klen) + " byte");

	}

	void kht(int num) {
		for (int i = 0; i < num; i++) {
			DataKeyGenerator keyGen = new DataKeyGenerator();
			keyGen.genKey("jifjdifdfd.jpg", cid, klen);
		}
	}

	void kcache(int num) {
		for (int i = 0; i < num; i++) {
			String fname = "jifjdifdfd" + num;
			kcache.get(fname);
		}
	}

	void init(int num) {
		for (int i = 0; i < num; i++) {
			DataKeyGenerator keyGen = new DataKeyGenerator();
			String fname = "jifjdifdfd" + num;
			byte[] key = keyGen.genKey(fname, cid, klen);
			kcache.put(fname, key);
		}
	}
}
