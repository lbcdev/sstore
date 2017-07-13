package org.sstore.security.encryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.sstore.utils.StreamFileUtils;

/**
 * Cipherhandler handles encryption and decryption of a file.
 * 
 * @author lbchen
 *
 */
public class CipherHandler {

	private Cipher cipher;
	private KeyGenerator keyGen;
	private SecretKey key;
	private DESKeySpec desKeySpec;
	private SecretKeyFactory kf;
	private String instance = "DES/ECB/PKCS5Padding";
	private byte[] desKeyData = { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
			(byte) 0x07, (byte) 0x08 };

	public CipherHandler() {
		try {
			cipher = Cipher.getInstance(instance);
			desKeySpec = new DESKeySpec(desKeyData);
			kf = SecretKeyFactory.getInstance("DES");
			key = kf.generateSecret(desKeySpec);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException e) {
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
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cdata = cipher.doFinal(in);
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
			cipher.init(Cipher.DECRYPT_MODE, key);
			decpherdata = cipher.doFinal(in);
			return decpherdata;
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return decpherdata;
	}
}
