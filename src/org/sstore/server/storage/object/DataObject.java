package org.sstore.server.storage.object;

/**
 * Data objects, including data bytes and metadata like OPS, encryption rate,
 * and some flags.
 * 
 * @author lbchen
 *
 */
public class DataObject {
	String id;
	// operation per second, not used yet.
	int ops;
	// encryption rate, not yet used.
	float eRate;
	// encrypted or not flag.
	boolean isEncrypted;

	byte[] data;
	// TTL for lazy encryption.
	int ttl;
	// created time in ms.
	long liveSince;
	// operation frequency
	float freq;
	// last operation time
	long lastOper;

	public float getFreq() {
		return freq;
	}

	public void setFreq(float freq) {
		this.freq = freq;
	}

	public long getLastOper() {
		return lastOper;
	}

	public void setLastOper(long lastOper) {
		this.lastOper = lastOper;
	}

	// initiate creation time.
	public DataObject() {
		liveSince = System.currentTimeMillis();
		eRate = 1f;
	}

	public long getCTime() {
		return liveSince;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getOps() {
		return ops;
	}

	public void setOps(int ops) {
		this.ops = ops;
	}

	public float geteRate() {
		return eRate;
	}

	public void seteRate(float eRate) {
		this.eRate = eRate;
	}

	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public String toString() {
		return "ttl: " + ttl + ", isEncrypted: " + isEncrypted 
				+ ", eRate: " + eRate + ", lastOperation: " + lastOper;
	}
}
