package org.sstore.server.test;

import java.util.ArrayList;
import java.util.List;

import org.sstore.utils.StreamFileUtils;

public class ReadWriteBW {

	static String rootdir = "/Users/lbchen/sstoredata/";
	static byte[] dbuf;

	public static void main(String[] args) {
		ReadWriteBW rw = new ReadWriteBW();
		rw.smallRead(10000);
		rw.smallWrite(10000);
	}

	public void smallRead(int num) {
		long start = System.currentTimeMillis();
		String in = rootdir + "got.png";
		// List<byte[]> fbuf = new ArrayList<byte[]>();
		for (int i = 0; i < num; i++) {
			dbuf = StreamFileUtils.readBytes(in);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	public void smallWrite(int num) {
		long start = System.currentTimeMillis();
		String out = rootdir + "got-out.png";
		for (int i = 0; i < num; i++) {
			StreamFileUtils.writeBytes(out, dbuf);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
