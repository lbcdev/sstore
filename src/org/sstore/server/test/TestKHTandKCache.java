package org.sstore.server.test;

import java.util.HashMap;

import org.sstore.security.encryption.CipherHandler;
import org.sstore.security.encryption.DataKeyGenerator;
import org.sstore.utils.StreamFileUtils;

public class TestKHTandKCache {

	static CipherHandler handler;
	private static HashMap<String, byte[]> kcache = new HashMap<String, byte[]>();
	static long cid = 1838339;
	static int klen = 16;
	static String rootdir = "/Users/lbchen/";
	static String in = rootdir + "got.png";
	static String cout = rootdir + "cout.jpg";
	static String dout = rootdir + "dout.jpg";

	public static void main(String[] args) {
		TestKHTandKCache test = new TestKHTandKCache();
		int num = 5000;
		long start, end;
		test.init(num);
		start = System.currentTimeMillis();
		test.kht(num);
		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("per op: " + (float)(end - start) / (float) num);
		float kht_t = end - start;

		start = System.currentTimeMillis();
		test.kcache(num);
		end = System.currentTimeMillis();
		float kcache_t = end - start;
		System.out.println(end - start);
		System.out.println("per op: " + (float)(end - start) / (float) num);

		System.out.println((kcache.size() * klen) / 1000 + " KB");
		float percent = (kht_t - kcache_t) / kht_t;
		System.out.println((int)(percent * 100) + "%");

	}

	void kht(int num) {
		for (int i = 0; i < num; i++) {

			DataKeyGenerator keyGen = new DataKeyGenerator();
			byte[] key = keyGen.genKey("jifjdifdfd.jpg", cid, klen);

			handler = new CipherHandler(key, 16);
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);
			
			keyGen = new DataKeyGenerator();
			key = keyGen.genKey("jifjdifdfd.jpg", cid, klen);
			handler = new CipherHandler(key, 16);
			byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			StreamFileUtils.writeBytes(dout, dcdata);
		}
	}

	void kcache(int num) {
		for (int i = 0; i < num; i++) {
			String fname = "jifjdifdfd-" + i;
			byte[] key = kcache.get(fname);
			handler = new CipherHandler(key, 16);
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);
			
			key = kcache.get(fname);
			handler = new CipherHandler(key, 16);
			byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			StreamFileUtils.writeBytes(dout, dcdata);
		}
	}

	void init(int num) {
		for (int i = 0; i < num; i++) {
			DataKeyGenerator keyGen = new DataKeyGenerator();
			String fname = "jifjdifdfd-" + i;
			byte[] key = keyGen.genKey(fname, cid, klen);
			kcache.put(fname, key);
		}
	}
}
