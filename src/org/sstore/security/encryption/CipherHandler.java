package org.sstore.security.encryption;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cipherhandler handles encryption and decryption of a file.
 * 
 * AES is used as the default encryption algorithm, more algorihtms are expected
 * in future.
 * 
 * @author lbchen
 *
 */
public class CipherHandler {

	static Cipher aesCipher;
	static KeyGenerator keyGen;
	static SecretKeyFactory kf;
	static SecretKeySpec skspec;

	public CipherHandler(String key, int length) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] inputKey = sha.digest(key.getBytes());
			inputKey = Arrays.copyOf(inputKey, length);
			skspec = new SecretKeySpec(inputKey, "AES");
			aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encrypt data and return it.
	 * 
	 * @param in
	 *            input bytes
	 * @return encrypted data
	 */
	public byte[] cipher(byte[] in) {
		byte[] cdata = null;
		try {
			aesCipher.init(Cipher.ENCRYPT_MODE, skspec);
			cdata = aesCipher.doFinal(in);
			return cdata;
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return cdata;
	}

	/**
	 * Decrypt ciphered data and return it.
	 * 
	 * @param in
	 *            ciphered data
	 * @return deciphered data
	 */
	public byte[] decipher(byte[] in) {
		byte[] decpherdata = null;
		try {
			aesCipher.init(Cipher.DECRYPT_MODE, skspec);
			decpherdata = aesCipher.doFinal(in);
			return decpherdata;
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return decpherdata;
	}
}
