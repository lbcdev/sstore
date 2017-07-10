package org.sstore.server.storage;
/**
 * Dataserver status, including active status, ttl, and etc.
 * @author lbchen
 *
 */
public class DataServerStatus {

	private boolean active = false;
	private int ttl;

	public void setTTL(int ttl) {
		this.ttl = ttl;
	}

	public int getTTL() {
		return this.ttl;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
