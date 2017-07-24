package org.sstore.server.storage;

import java.util.Hashtable;

import org.sstore.utils.Constants;

/**
 * Databuffer is an in-memory structure on dataserver used to reduce write
 * latency. It caches data from PUT operations and write them to disk later.
 * 
 * @author lbchen
 *
 */
public class DataBuffer {

	/* Key in-memory structure for data cacheing. */
	private static Hashtable<String, byte[]> dataBuffer = new Hashtable<String, byte[]>();

	public DataBuffer() {
		dataBuffer = new Hashtable<String, byte[]>();
	}

	/**
	 * Add file into buffer, indexed by filename.
	 * 
	 * @param filename
	 *            index for file
	 * @param data
	 *            file content in bytes
	 */
	public static void cache(String filename, byte[] data) {
		if (dataBuffer.size() < Constants.DATABUF_SIZE) {
			dataBuffer.put(filename, data);
		}
	}

	public static byte[] searchBuffer(String filename) {
		if (dataBuffer != null) {
			return dataBuffer.get(filename);
		}
		return null;
	}

	public static void remove(String filename) {
		if (dataBuffer != null)
			dataBuffer.remove(filename);
	}

	public static void removeByCol(String[] filenames) {
		if (dataBuffer != null)
			for (String file : filenames) {
				dataBuffer.remove(file);
			}
	}
}
