package com.huawei.insa2.util;

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;

public class SecurityTools {
	private static final byte[] salt = "webplat".getBytes();

	public static String digest(String str) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("SHA");
			md5.update(salt); // ������ֵ
			return Base64.encode(md5.digest(str.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			throw new UnsupportedOperationException(ex.toString());
		}
	}

	public static void md5(byte[] data, int offset, int length, byte[] digest,
			int dOffset) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data, offset, length);
			md5.digest(digest, dOffset, 16);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (DigestException ex) {
			ex.printStackTrace();
		}
	}

	public static byte[] md5(byte[] data, int offset, int length) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data, offset, length);
			return md5.digest();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static byte[] encrypt(byte[] key, byte[] src) {
		try {
			return getCipher(key, Cipher.ENCRYPT_MODE).doFinal(src);
		} catch (BadPaddingException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (IllegalBlockSizeException ex) {
			throw new UnsupportedOperationException(ex.toString());
		}

	}

	public static String encrypt(String key, String src) {
		try {
			return Base64.encode(getCipher(key.getBytes("UTF8"),
					Cipher.ENCRYPT_MODE).doFinal(src.getBytes("UTF8")));
		} catch (UnsupportedEncodingException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (BadPaddingException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (IllegalBlockSizeException ex) {
			throw new UnsupportedOperationException(ex.toString());
		}
	}

	public static byte[] decrypt(byte[] key, byte[] src) {
		try {
			return getCipher(key, Cipher.DECRYPT_MODE).doFinal(src);
		} catch (IllegalBlockSizeException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (BadPaddingException ex) {
			throw new UnsupportedOperationException(ex.toString());
		}
	}

	public static String decrypt(String key, String src) {
		try {
			return new String(getCipher(key.getBytes("UTF8"),
					Cipher.DECRYPT_MODE).doFinal(Base64.decode(src)), "UTF8");
		} catch (UnsupportedEncodingException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (BadPaddingException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (IllegalBlockSizeException ex) {
			throw new UnsupportedOperationException(ex.toString());
		}
	}

	public static Cipher getCipher(byte[] key, int mode) {
		try {
			SecretKeyFactory keyFactory;
			KeySpec keySpec;
			Cipher c;
			if (key.length < 8) {
				byte[] oldkey = key;
				key = new byte[8];
				System.arraycopy(oldkey, 0, key, 0, oldkey.length);
			}
			if (key.length >= 24) {
				keyFactory = SecretKeyFactory.getInstance("DESede");
				keySpec = new DESedeKeySpec(key);
				c = Cipher.getInstance("DESede");
			} else {
				keyFactory = SecretKeyFactory.getInstance("DES");
				keySpec = new DESKeySpec(key);
				c = Cipher.getInstance("DES");
			}
			SecretKey k = keyFactory.generateSecret(keySpec);
			c.init(mode, k);
			return c;
		} catch (NoSuchAlgorithmException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (InvalidKeyException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (NoSuchPaddingException ex) {
			throw new UnsupportedOperationException(ex.toString());
		} catch (InvalidKeySpecException ex) {
			throw new UnsupportedOperationException(ex.toString());
		}
	}
}