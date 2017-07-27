package org.sstore.server.storage;

import java.util.HashMap;

import org.sstore.utils.Constants;

/**
 * Data status buffer is a "sibling" of data buffer. It records key metadata of
 * buffered data include: access per second, last access time, and etc.
 * 
 * @author lbchen
 *
 */
public class DataStatusBuffer {
	private static HashMap<String, BufferedDataStatus> dsbuffer = new HashMap<String, BufferedDataStatus>();

	public DataStatusBuffer() {

	}

	public HashMap<String, BufferedDataStatus> getBuffer() {
		return dsbuffer;
	}

	public int size() {
		return dsbuffer.size();
	}

	public synchronized static void cache(String filename, BufferedDataStatus datastatus) {
		if (dsbuffer.size() < Constants.DATABUF_SIZE) {

			dsbuffer.put(filename, datastatus);
		}
	}

	public static BufferedDataStatus searchBuffer(String filename) {
		if (dsbuffer != null) {
			return dsbuffer.get(filename);
		}
		return null;
	}

	public static void remove(String filename) {
		if (dsbuffer != null)
			dsbuffer.remove(filename);
	}

	public static void removeByCol(String[] filenames) {
		if (dsbuffer != null)
			for (String file : filenames) {
				dsbuffer.remove(file);
			}
	}
}
