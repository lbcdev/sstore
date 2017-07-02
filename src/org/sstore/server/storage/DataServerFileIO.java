package org.sstore.server.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * DataServerFileIO handles all input/output operations like put, get, and
 * delete on the data.
 * 
 * @author lbchen
 *
 */
public class DataServerFileIO {

	private static String datadir = "/Users/lbchen/data/";

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
