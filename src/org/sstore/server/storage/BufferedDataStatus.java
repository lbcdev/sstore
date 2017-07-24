package org.sstore.server.storage;

/**
 * This entity class records key attributes of buffered data: access per second,
 * last access time, and etc.
 * 
 * @author lbchen
 *
 */
public class BufferedDataStatus {

	private int accessPerSecond;
	private long lastAccess;

	public BufferedDataStatus(int aps, long lat) {
		accessPerSecond = aps;
		lastAccess = lat;
	}

	public void setAps(int aps) {
		accessPerSecond = aps;
	}

	public int getAps() {
		return accessPerSecond;
	}

	public void setLat(long lat) {
		lastAccess = lat;
	}

	public long getLat() {
		return lastAccess;
	}
}
