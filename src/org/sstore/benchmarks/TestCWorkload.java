package org.sstore.benchmarks;

import org.sstore.utils.StreamFileUtils;

public class TestCWorkload {

	public static void main(String[] args) {
		int num = 500;
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
			RequestHandler.randw();
		}
	}
}

class RequestHandler {
	static int reqCount = 0;
	static String inpath = "/Users/lbchen/got.png";
	static String outpath = "/Users/lbchen/out.jpg";

	void processreq() {
		Thread th = new Thread(new HandlerThread());
		th.start();
	}

	synchronized static void randw() {
		byte[] data = StreamFileUtils.readBytes(inpath);
		StreamFileUtils.writeBytes(outpath, data);
		reqCount++;
	}

}

class HandlerThread implements Runnable {

	public void run() {
		RequestHandler.randw();
		Thread.yield();
	}
}