package org.sstore.server.test;

import java.util.Arrays;

import org.sstore.security.encryption.CipherHandler;
import org.sstore.utils.StreamFileUtils;

public class TestCipherHandler {
	private CipherHandler handler;
	public static void main(String[] args) {
		TestCipherHandler testhandler = new TestCipherHandler();
		testhandler.test();
	}
	
	public void test(){
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
}
