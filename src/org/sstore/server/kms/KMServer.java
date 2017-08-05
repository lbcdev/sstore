package org.sstore.server.kms;

import org.sstore.security.encryption.DataKeyGenerator;
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

	public KMServer() {
		keyGen = new DataKeyGenerator();
	}

	public byte[] getKey(long fid) {
		byte[] key = null;

		return key;
	}

	byte[] computeKey(long fid, long cid) {
		return keyGen.genKey(String.valueOf(fid), cid, Constants.DEFAULT_KEY_LENGTH);
	}
	
	
}
