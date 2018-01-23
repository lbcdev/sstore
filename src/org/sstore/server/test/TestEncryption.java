package org.sstore.server.test;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.sstore.utils.StreamFileUtils;

public class TestEncryption {

	public static void main(String[] args) {
		String in = "/Users/lbchen/data/in.png";
		String cFile = "/Users/lbchen/data/cFile.png";
		String out = "/Users/lbchen/data/out.png";

		String word = "Hello world";

		try {
			String cWord = encrypt(word);
			System.out.println(cWord);
			String dcWord = decryptStr(cWord);
			System.out.println(dcWord);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// byte[] plainBytes = StreamFileUtils.readBytes(in);
		// try {
		// StreamFileUtils.writeBytes(cFile, encrypt(plainBytes));
		// byte[] cBytes = StreamFileUtils.readBytes(cFile);
		// StreamFileUtils.writeBytes(out, decrypt(cBytes));
		//
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public static String encrypt(String data) throws Exception {
		byte[] ivBytes;
		String password = "Hello";
		/*
		 * you can give whatever you want for password. This is for testing
		 * purpose
		 */
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		byte[] saltBytes = bytes;
		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65556, 256);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		// encrypting the word
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encryptedTextBytes = cipher.doFinal(data.getBytes("UTF-8"));
		// prepend salt and vi
		byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];
		System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
		System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
		System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);
		return new Base64().encodeToString(buffer);
	}

	@SuppressWarnings("static-access")
	public static byte[] decrypt(byte[] cdata) throws Exception {
		String password = "Hello";
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// strip off the salt and iv
		ByteBuffer buffer = ByteBuffer.wrap(new Base64().decode(cdata));
		byte[] saltBytes = new byte[20];
		buffer.get(saltBytes, 0, saltBytes.length);
		byte[] ivBytes1 = new byte[cipher.getBlockSize()];
		buffer.get(ivBytes1, 0, ivBytes1.length);
		byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes1.length];

		buffer.get(encryptedTextBytes);
		// Deriving the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65556, 256);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes1));
		byte[] decryptedTextBytes = null;
		try {
			decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return decryptedTextBytes;
	}

	@SuppressWarnings("static-access")
	public static String decryptStr(String cdata) throws Exception {
		String password = "Hello";
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		// strip off the salt and iv
		ByteBuffer buffer = ByteBuffer.wrap(new Base64().decode(cdata));
		byte[] saltBytes = new byte[20];
		buffer.get(saltBytes, 0, saltBytes.length);
		byte[] ivBytes1 = new byte[cipher.getBlockSize()];
		buffer.get(ivBytes1, 0, ivBytes1.length);
		byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes1.length];

		buffer.get(encryptedTextBytes);
		// Deriving the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65556, 256);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes1));
		byte[] decryptedTextBytes = null;
		try {
			decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return new String(decryptedTextBytes);
	}
}
