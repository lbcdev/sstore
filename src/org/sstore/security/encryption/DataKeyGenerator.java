package org.sstore.security.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import org.sstore.utils.Constants;

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
		key = handler.cipher(filename.getBytes());
		
		MessageDigest sha;
		try {
			sha = MessageDigest.getInstance("SHA-1");
			byte[] inputKey = sha.digest(key);
			if (inputKey.length != length) {
				inputKey = Arrays.copyOf(inputKey, length);
			}
			return inputKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SecretKeySpec gen(String filename) {
		MessageDigest sha;
		SecretKeySpec skspec = null;
		byte[] key = (Constants.ROOT_KEY + filename).getBytes();
		try {
			sha = MessageDigest.getInstance("SHA-1");
			byte[] inputKey = sha.digest(key);
			if (inputKey.length != Constants.DEF_KEY_LEN) {
				inputKey = Arrays.copyOf(inputKey, Constants.DEF_KEY_LEN);
			}
			skspec = new SecretKeySpec(inputKey, "AES");
			return skspec;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return skspec;
	}

	
}
