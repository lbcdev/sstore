package org.sstore.server.kms;

/**
 * Key status, includes access per second and last time used.
 * 
 * @author lbchen
 *
 */
public class KeyStatus {
	private int aps;
	private long lastUsed;

	public int getAps() {
		return aps;
	}

	public void setFrequent(int aps) {
		this.aps = aps;
	}

	public long getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(long lastUsed) {
		this.lastUsed = lastUsed;
	}

}
