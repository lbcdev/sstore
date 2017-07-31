package org.sstore.server.asyncio;

import java.io.FileOutputStream;
import java.io.IOException;

import org.sstore.utils.Constants;

public class AsyncWrite implements Runnable {

	private String filename;
	private byte[] data;

	public AsyncWrite(String filename, byte[] bytes) {
		this.filename = filename;
		this.data = bytes;
	}

	public void run() {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
