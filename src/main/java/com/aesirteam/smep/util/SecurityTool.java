package com.aesirteam.smep.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SecurityTool {

	private static String desSecret;

	public static String getDesSecret() {
		return desSecret;
	}

	public static void setDesSecret(String desSecret) {
		SecurityTool.desSecret = desSecret;
	}
	
	public static String encrypt(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IllegalStateException {

			DESTools des = DESTools.getInstance();
			String md5str = MD5.md5(str).concat(str);
			return Base64.encode(des.encrypt(md5str.getBytes("UTF8")));
	}

	public static String decrypt(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, IllegalStateException {
			DESTools des = DESTools.getInstance();
			byte[] b = Base64.decode(str);
			String md5body = new String(des.decrypt(b), "UTF8");
			if (md5body.length() < 32)
				return null;
			String md5Client = md5body.substring(0, 32);
			String bodyStr = md5body.substring(32);
			String md5Server = MD5.md5(bodyStr);
			return md5Client.equals(md5Server) ? bodyStr : null;
	}

	private static class DESTools {

		private Cipher cipher;

		private Key key;

		public static DESTools getInstance() throws NoSuchPaddingException,
				NoSuchAlgorithmException {
			return getInstance(getKeyByStr(desSecret));
		}

		public static DESTools getInstance(byte[] key)
				throws NoSuchPaddingException, NoSuchAlgorithmException {
			DESTools des = new DESTools();
			if (des.key == null) {
				SecretKeySpec spec = new SecretKeySpec(key, "DES");
				des.key = spec;
			}
			des.cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			return des;
		}

		public byte[] encrypt(byte b[]) throws InvalidKeyException,
				BadPaddingException, IllegalBlockSizeException,
				IllegalStateException {
			byte byteFina[] = null;
			cipher.init(1, key);
			byteFina = cipher.doFinal(b);
			return byteFina;
		}

		public byte[] decrypt(byte b[]) throws InvalidKeyException,
				BadPaddingException, IllegalBlockSizeException,
				IllegalStateException {
			byte byteFina[] = null;
			cipher.init(2, key);
			byteFina = cipher.doFinal(b);
			return byteFina;
		}

		public static byte[] getKeyByStr(String str) {
			byte bRet[] = new byte[str.length() / 2];
			for (int i = 0; i < str.length() / 2; i++) {
				Integer itg = new Integer(16 * getChrInt(str.charAt(2 * i))
						+ getChrInt(str.charAt(2 * i + 1)));
				bRet[i] = itg.byteValue();
			}

			return bRet;
		}

		private static int getChrInt(char chr) {
			int iRet = 0;
			if (chr == "0".charAt(0))
				iRet = 0;
			if (chr == "1".charAt(0))
				iRet = 1;
			if (chr == "2".charAt(0))
				iRet = 2;
			if (chr == "3".charAt(0))
				iRet = 3;
			if (chr == "4".charAt(0))
				iRet = 4;
			if (chr == "5".charAt(0))
				iRet = 5;
			if (chr == "6".charAt(0))
				iRet = 6;
			if (chr == "7".charAt(0))
				iRet = 7;
			if (chr == "8".charAt(0))
				iRet = 8;
			if (chr == "9".charAt(0))
				iRet = 9;
			if (chr == "A".charAt(0))
				iRet = 10;
			if (chr == "B".charAt(0))
				iRet = 11;
			if (chr == "C".charAt(0))
				iRet = 12;
			if (chr == "D".charAt(0))
				iRet = 13;
			if (chr == "E".charAt(0))
				iRet = 14;
			if (chr == "F".charAt(0))
				iRet = 15;
			return iRet;
		}
	}

	private static class Base64 {

		public static String encode(byte b[]) {
			int code = 0;
			if (b == null)
				return null;
			StringBuffer sb = new StringBuffer((b.length - 1) / 3 << 6);
			for (int i = 0; i < b.length; i++) {
				code |= b[i] << 16 - (i % 3) * 8 & 255 << 16 - (i % 3) * 8;
				if (i % 3 == 2 || i == b.length - 1) {
					sb.append(Base64Code[(code & 0xfc0000) >>> 18]);
					sb.append(Base64Code[(code & 0x3f000) >>> 12]);
					sb.append(Base64Code[(code & 0xfc0) >>> 6]);
					sb.append(Base64Code[code & 0x3f]);
					code = 0;
				}
			}

			if (b.length % 3 > 0)
				sb.setCharAt(sb.length() - 1, '=');
			if (b.length % 3 == 1)
				sb.setCharAt(sb.length() - 2, '=');
			return sb.toString();
		}

		public static byte[] decode(String code) {
			if (code == null)
				return null;
			int len = code.length();
			if (len % 4 != 0)
				throw new IllegalArgumentException(
						"Base64 string length must be 4*n");
			if (code.length() == 0)
				return new byte[0];
			int pad = 0;
			if (code.charAt(len - 1) == '=')
				pad++;
			if (code.charAt(len - 2) == '=')
				pad++;
			int retLen = (len / 4) * 3 - pad;
			byte ret[] = new byte[retLen];
			for (int i = 0; i < len; i += 4) {
				int j = (i / 4) * 3;
				char ch1 = code.charAt(i);
				char ch2 = code.charAt(i + 1);
				char ch3 = code.charAt(i + 2);
				char ch4 = code.charAt(i + 3);
				int tmp = Base64Decode[ch1] << 18 | Base64Decode[ch2] << 12
						| Base64Decode[ch3] << 6 | Base64Decode[ch4];
				ret[j] = (byte) ((tmp & 0xff0000) >> 16);
				if (i < len - 4) {
					ret[j + 1] = (byte) ((tmp & 0xff00) >> 8);
					ret[j + 2] = (byte) (tmp & 0xff);
					continue;
				}
				if (j + 1 < retLen)
					ret[j + 1] = (byte) ((tmp & 0xff00) >> 8);
				if (j + 2 < retLen)
					ret[j + 2] = (byte) (tmp & 0xff);
			}

			return ret;
		}

		private final static char Base64Code[] = { 'A', 'B', 'C', 'D', 'E',
				'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
				'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
				'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0',
				'1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

		private final static byte Base64Decode[] = { -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, 62, -1, 63, -1, 63, 52, 53, 54, 55, 56, 57,
				58, 59, 60, 61, -1, -1, -1, 0, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6,
				7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
				23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32,
				33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
				49, 50, 51, -1, -1, -1, -1, -1 };
	}

	private static class MD5 {

		@SuppressWarnings("unused")
		public static String md5(byte b[]) throws NoSuchAlgorithmException {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(b, 0, b.length);
			return byteArrayToHexString(md5.digest());
		}

		public static String md5(String data) throws NoSuchAlgorithmException,
				UnsupportedEncodingException {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte b[] = data.getBytes("UTF8");
			md5.update(b, 0, b.length);
			return byteArrayToHexString(md5.digest());
		}

		private static String byteArrayToHexString(byte b[]) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < b.length; i++)
				sb.append(byteToHexString(b[i]));
			return sb.toString();
		}

		private static String byteToHexString(byte b) {
			int n = b;
			if (n < 0)
				n = 256 + n;
			int d1 = n / 16;
			int d2 = n % 16;
			return hexDigits[d1] + hexDigits[d2];
		}

		private static String hexDigits[] = { "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	}
}
