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
		int num = 5000;
		long start, end;
		test.init(num);
		start = System.currentTimeMillis();
		test.kht(num);
		end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println("per op: " + (float) (end - start) / (float) num);
		float kht_t = end - start;

		start = System.currentTimeMillis();
		test.kcache(num);
		end = System.currentTimeMillis();
		float kcache_t = end - start;
		System.out.println(end - start);
		System.out.println("per op: " + (float) (end - start) / (float) num);

		System.out.println((kcache.size() * klen) / 1000 + " KB");
		float percent = (kht_t - kcache_t) / kht_t;
		System.out.println((int) (percent * 100) + "%");

	}

	void kht(int num) {
		for (int i = 0; i < num; i++) {

			DataKeyGenerator keyGen = new DataKeyGenerator();
			// byte[] key = keyGen.genKey("jifjdifdfd.jpg", cid, klen);
			SecretKeySpec skey = keyGen.gen("jifjdifdfd.jpg", cid, klen);

			handler = new CipherHandler(skey);
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);

			keyGen = new DataKeyGenerator();
			// key = keyGen.genKey("jifjdifdfd.jpg", cid, klen);
			skey = keyGen.gen("jifjdifdfd.jpg", cid, klen);

			handler = new CipherHandler(skey);
			byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			StreamFileUtils.writeBytes(dout, dcdata);
		}
	}

	void kcache(int num) {
		for (int i = 0; i < num; i++) {
			String fname = "jifjdifdfd-" + i;
//			byte[] key = kcache.get(fname);
			SecretKeySpec skey = skcache.get(fname);

			handler = new CipherHandler(skey);
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);

//			key = kcache.get(fname);
			skey = skcache.get(fname);
			handler = new CipherHandler(skey);
			byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			StreamFileUtils.writeBytes(dout, dcdata);
		}
	}

	void init(int num) {
		for (int i = 0; i < num; i++) {
			DataKeyGenerator keyGen = new DataKeyGenerator();
			String fname = "jifjdifdfd-" + i;
			// byte[] key = keyGen.genKey(fname, cid, klen);
			SecretKeySpec skey = keyGen.gen("jifjdifdfd.jpg", cid, klen);
			skcache.put(fname, skey);
//			kcache.put(fname, key);
		}
	}
}
