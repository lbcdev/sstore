package org.sstore.server.test;

import org.sstore.security.encryption.CipherHandler;
import org.sstore.utils.StreamFileUtils;

public class TestCipherHandler {
	private CipherHandler handler;
	public static void main(String[] args) {
		TestCipherHandler testhandler = new TestCipherHandler();
//		testhandler.test();
	}
	
//	public void test(){
//		handler = new CipherHandler();
//		String rootdir = "/Users/lbchen/";
//		String in = rootdir + "in.jpg";
//		String cout = rootdir + "data/localhost-1100/in.jpg";
//		String dout = rootdir + "dout.jpg";
//		byte[] cdata = handler.cipher(StreamFileUtils.readBytes(in));
//		StreamFileUtils.writeBytes(cout, cdata);
//		byte[] dcdata = handler.decipher(StreamFileUtils.readBytes(cout));
//		StreamFileUtils.writeBytes(dout, dcdata);
//	}
}
