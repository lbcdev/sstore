package org.sstore.benchmarks;

import org.sstore.utils.StreamFileUtils;

public class TestCWorkload {

	public static void main(String[] args) {
		int num = 100;
		Thread counter = new Thread(new Counter());
		counter.start();
		for (int i = 0; i < num; i++) {
			Thread th = new Thread(new CWorkload());
			th.start();
		}
	}
}

class Counter implements Runnable {
	public void run() {
		while (true) {
			int prev = RequestHandler.reqCount;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int curr = RequestHandler.reqCount;
			System.out.println("throughput per second: " + (curr - prev));
		}
	}
}

class CWorkload implements Runnable {

	public void run() {
		while (true) {
			RequestHandler handler = new RequestHandler();
			handler.processReq();
		}
	}
}

class RequestHandler {
	static int reqCount = 0;
	String inpath = "/Users/lbchen/got.png";
	String outpath = "/Users/lbchen/out.jpg";

	synchronized void processReq() {
		byte[] data = StreamFileUtils.readBytes(inpath);
		StreamFileUtils.writeBytes(outpath, data);
		reqCount++;
	}

}