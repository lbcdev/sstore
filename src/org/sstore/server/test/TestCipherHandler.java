package org.sstore.server.test;

import java.util.Arrays;

import org.sstore.security.encryption.CipherHandler;
import org.sstore.utils.StreamFileUtils;

public class TestCipherHandler {
	private CipherHandler handler;

	public static void main(String[] args) {
		TestCipherHandler handler = new TestCipherHandler();
		int times = 5000;
		handler.testsmfile(times);
		handler.testmdfile((times / 10));
		handler.testlgfile((times / 100));
	}

	public void testsmfile(int times) {
		long start = System.currentTimeMillis();
		while (times-- > 0) {
			String keystr = "a girl has no name";
			byte[] key = keystr.getBytes();
			handler = new CipherHandler(key, 16);
			String rootdir = "/Users/lbchen/";
			String in = rootdir + "got.png";
			String cout = rootdir + "cout.jpg";
			String dout = rootdir + "dout.jpg";
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);
			byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			StreamFileUtils.writeBytes(dout, dcdata);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	public void testmdfile(int times) {
		long start = System.currentTimeMillis();
		while (times-- > 0) {
			String keystr = "a girl has no name";
			byte[] key = keystr.getBytes();
			handler = new CipherHandler(key, 16);
			String rootdir = "/Users/lbchen/";
			String in = rootdir + "gotmd.png";
			String cout = rootdir + "coutmd.jpg";
			String dout = rootdir + "doutmd.jpg";
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);
			byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			StreamFileUtils.writeBytes(dout, dcdata);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	public void testlgfile(int times) {
		long start = System.currentTimeMillis();
		while (times-- > 0) {
			String keystr = "a girl has no name";
			byte[] key = keystr.getBytes();
			handler = new CipherHandler(key, 16);
			String rootdir = "/Users/lbchen/";
			String in = rootdir + "gotlg.png";
			String cout = rootdir + "coutlg.jpg";
			String dout = rootdir + "doutlg.jpg";
			byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
			StreamFileUtils.writeBytes(cout, cdata);
			byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
			StreamFileUtils.writeBytes(dout, dcdata);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
