package org.sstore.server.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.sstore.client.ClientRpcImpl;

/**
 * DataServerFileIO handles all input/output operations like put, get, and
 * delete on the data.
 * 
 * @author lbchen
 *
 */
public class DataServerFileIO {

	private static String datadir = "/Users/lbchen/data/";
	private final static Logger log = Logger.getLogger(DataServerFileIO.class.getName());

	/** return bytes of the requested file by name */
	public byte[] get(String remote) {
		String fname = datadir + remote;
		File file = new File(fname);
		log.info("get file: " + fname);
		byte[] data = new byte[(int) file.length()];
		log.info("get data length: " + data.length);
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(data);
			in.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/** write bytes to a file defined by filename */
	public void put(String filename, byte[] bytes) {
		try {
			FileOutputStream out = new FileOutputStream(datadir + filename);
			out.write(bytes);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
