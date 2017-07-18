package org.sstore.security.encryption;

/**
 * This generator generates data encryption key for each file based on unique
 * filename and client id.
 * 
 * @author lbchen
 *
 */
public class DataKeyGenerator {

	/** generate data encryption key with filename and client id. */
	public byte[] genKey(String filename, long clientId, int length) {
		byte[] key = Long.toString(clientId).getBytes();
		CipherHandler handler = new CipherHandler(key, length);
		return handler.cipher(filename.getBytes());
	}
}
