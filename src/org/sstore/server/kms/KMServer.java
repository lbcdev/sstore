package org.sstore.server.kms;

import org.sstore.security.encryption.DataKeyGenerator;
import org.sstore.server.buffer.KeyCache;
import org.sstore.utils.Constants;

/**
 * Key management server stores, manages, and distributes keys for data. It can
 * run on a separate server for better performance.
 * 
 * @author lbchen
 *
 */
public class KMServer {

	private DataKeyGenerator keyGen;
	private KeyCache kcache;

	public KMServer() {
		keyGen = new DataKeyGenerator();
		kcache = new KeyCache();
	}

	/**
	 * return key to client.
	 * 
	 * @param fid
	 * @param cid
	 * @return
	 */
	public byte[] getKey(String fid, long cid) {
		if (kcache.lookup(fid) != null) {
			return kcache.lookup(fid);
		} else {
			return computeKey(fid, cid);
		}
	}

	byte[] computeKey(String fid, long cid) {
		return keyGen.genKey(fid, cid, Constants.DEFAULT_KEY_LENGTH);
	}

}
