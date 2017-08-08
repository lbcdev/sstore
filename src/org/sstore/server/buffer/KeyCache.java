package org.sstore.server.buffer;

import java.util.HashMap;

/**
 * Key cache to store keys for efficiency.
 * 
 * @author lbchen
 *
 */
public class KeyCache {
	private HashMap<String, byte[]> keyBuffer;
	private int count;

	public KeyCache() {
		count = 0;
		keyBuffer = new HashMap<String, byte[]>();
	}

	public void cache(String filename, byte[] key) {
		keyBuffer.put(filename, key);
		count++;
	}

	public byte[] lookup(String filename) {
		return keyBuffer.get(filename);
	}

	public HashMap<String, byte[]> getCache() {
		return keyBuffer;
	}

	public int size() {
		return count;
	}
}
