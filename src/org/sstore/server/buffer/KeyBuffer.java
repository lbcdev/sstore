package org.sstore.server.buffer;

import java.util.Hashtable;

/**
 * Key buffer to cache keys for efficiency.
 * 
 * @author lbchen
 *
 */
public class KeyBuffer {
	private Hashtable<String, byte[]> keyBuffer;

	public KeyBuffer() {
		keyBuffer = new Hashtable<String, byte[]>();
	}

	public void cache(String filename, byte[] key) {
		keyBuffer.put(filename, key);
	}

	public byte[] searchBuffer(String filename) {
		return keyBuffer.get(filename);
	}
}
