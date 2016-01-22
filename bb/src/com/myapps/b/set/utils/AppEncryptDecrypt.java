package com.myapps.b.set.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class Encrypts and Decrypts data using AES256
 * @author 324520
 * 
 */
public class AppEncryptDecrypt
{
	private static AppEncryptDecrypt encryptDecrypt;
	private static byte[] mKey;

	private AppEncryptDecrypt()
	{

	}

	/**
	 * Returns an instance of AppGuardEncryptDecrypt
	 * 
	 * @param key
	 *            key retrieved from rules file
	 * @return instance of AppGuardEncryptDecrypt
	 * @throws NoSuchAlgorithmException
	 */
	public static AppEncryptDecrypt getInstance(String key)
	throws NoSuchAlgorithmException {
		if (encryptDecrypt == null)
			encryptDecrypt = new AppEncryptDecrypt();
		mKey = getKey(key);
		return encryptDecrypt;
	}

	/**
	 * This method generates secret key for Encryption and Decryption using
	 * AES256
	 * 
	 * @param key
	 *            key retrieved from rules file
	 * @return generated Secret key in byte format
	 * @throws NoSuchAlgorithmException
	 */
	private static byte[] getKey(String key) throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(key.getBytes());
		kgen.init(256, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	/**
	 * This method encrypt the input data using AES Algorithm
	 * 
	 * @param data
	 *            the data to be encrypted
	 * @return encrypted data
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] encrypt(byte[] data) throws NoSuchAlgorithmException,
	NoSuchPaddingException, InvalidKeyException,
	InvalidAlgorithmParameterException, IllegalBlockSizeException,
	BadPaddingException {
		SecretKeySpec skeySpec = new SecretKeySpec(mKey, "AES");
		IvParameterSpec iv = new IvParameterSpec(mKey);
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(data);
		return encrypted;
	}

	/**
	 * This method decrypts the given data using AES Algorithm
	 * 
	 * @param encryptedByteData
	 *            the data to be decrypted
	 * @return decrypted data
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] decrypt(byte[] encryptedByteData)
	throws NoSuchAlgorithmException, NoSuchPaddingException,
	InvalidKeyException, InvalidAlgorithmParameterException,
	IllegalBlockSizeException, BadPaddingException {
		SecretKeySpec skeySpec = new SecretKeySpec(mKey, "AES");
		IvParameterSpec iv = new IvParameterSpec(mKey);
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		byte[] decrypted = cipher.doFinal(encryptedByteData);
		return decrypted;

	}

	/**
	 * This method encrypts the given String
	 * 
	 * @param data
	 *            data to be encrypted
	 * @return encrpted data
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String encryptString(String data) throws NoSuchAlgorithmException,
	InvalidKeyException, NoSuchPaddingException,
	InvalidAlgorithmParameterException, IllegalBlockSizeException,
	BadPaddingException {

		byte[] result = encrypt(data.getBytes());
		return Base64.encodeToString(result, 0);

	}

	/**
	 * This method decrypts the given encrypted String
	 * 
	 * @param encryptedData
	 *            data to be decrypted
	 * @return decrypted data
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decryptString(String encryptedData)
	throws InvalidKeyException, NoSuchAlgorithmException,
	NoSuchPaddingException, InvalidAlgorithmParameterException,
	IllegalBlockSizeException, BadPaddingException {

		byte[] encryptedByteData = Base64.decode(encryptedData, 0);
		byte[] result = decrypt(encryptedByteData);
		return new String(result);

	}

	/**
	 * This method encrypts the given file
	 * 
	 * @param fileData
	 *            data to be encrypted
	 * @return encrypted data
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] encryptFile(byte[] fileData) throws InvalidKeyException,
	NoSuchAlgorithmException, NoSuchPaddingException,
	InvalidAlgorithmParameterException, IllegalBlockSizeException,
	BadPaddingException {
		return encrypt(fileData);

	}

	/**
	 * This method decrypts the given file
	 * 
	 * @param encryptedFileData
	 *            data to be decrypted
	 * @return decrypted original data
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] decryptFile(byte[] encryptedFileData)
	throws InvalidKeyException, NoSuchAlgorithmException,
	NoSuchPaddingException, InvalidAlgorithmParameterException,
	IllegalBlockSizeException, BadPaddingException {
		return decrypt(encryptedFileData);

	}

	public static String encryptToHex(String seed, String cleartext) throws Exception {
		byte[] result = encrypt(cleartext.getBytes("UTF-8"), seed);
		return toHex(result);
	}

	private static byte[] encrypt(byte[] data, String key) {
		byte[] encrypted = null;
		byte[] keyBytes = key.getBytes();

		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			encrypted = new byte[cipher.getOutputSize(data.length)];
			int ctLength = cipher.update(data, 0, data.length, encrypted, 0);
			ctLength += cipher.doFinal(encrypted, ctLength);
		} catch (Exception e) {
			//logger.log(Level.SEVERE, e.getMessage());
		} 
		return encrypted;

	}
	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}
	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length()/2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2*buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}
	private final static String HEX = "0123456789ABCDEF";
	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	}

}
