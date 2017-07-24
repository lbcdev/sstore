package org.sstore.server.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sstore.server.asyncio.AsyncWrite;
import org.sstore.utils.Constants;

/**
 * DataServerFileIO handles all input/output operations like put, get, and
 * delete on the data.
 * 
 * All operations on files should be written in this class.
 * 
 * @author lbchen
 *
 */
public class DataServerFileIO {

	private static String rootdir = Constants.DATAROOTDIR;
	private final static Logger log = Logger.getLogger(DataServerFileIO.class.getName());
	private DataBuffer buffer;

	// default constructor.
	public DataServerFileIO() {
		super();
		buffer = new DataBuffer();
	}

	// constructor with specific sub path.
	public DataServerFileIO(String subpath) {
		rootdir = rootdir + subpath;
		buffer = new DataBuffer();
	}

	/** get all files under dir */
	public Set<String> getFiles(String subdir) {
		String dir = rootdir + subdir;
		log.info("Read directory: " + dir);
		File file = new File(rootdir);
		File[] files = file.listFiles();
		Set<String> fileset = new HashSet<String>();
		if (files != null) {
			for (File f : files) {
				fileset.add(f.getName());
			}
		}
		return fileset;
	}

	/** return bytes of the requested file by name */
	public byte[] get(String remote) {
		String fname = rootdir + remote;
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

	public void asyncPut(String filename, byte[] bytes) {
		DataBuffer.cache(filename, bytes);
		Thread asyncThread = new Thread(new AsyncWrite(rootdir + filename, bytes));
		asyncThread.start();
	}

	/** write bytes to a file defined by filename */
	public void put(String filename, byte[] bytes) {
		try {
			FileOutputStream out = new FileOutputStream(rootdir + filename);
			out.write(bytes);
			out.flush();
			log.info("put size " + bytes.length);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
