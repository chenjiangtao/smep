package com.aesirteam.smep.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * md5算法工具
 * @since 2009-8-7 14:46:27
 */
public class MD5Util {
	
    private static MessageDigest messageDigest = null;

    static {
        try {
        	messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ne) { }
    }

    /**
     * 对一个文件求他的md5值
     * @param f 要求md5值的文件
     * @return md5串
     */
    public static String getMD5ByFilename(String filename) {
    	File file = new File(filename);
    	
    	if (!file.exists() || !file.isFile()) return null;
    	
    	FileInputStream in = null;
        FileChannel ch = null;        
    	try {
        	in = new FileInputStream(file);
            ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messageDigest.update(byteBuffer);
            return new String(Hex.encodeHex(messageDigest.digest())); 
    	} catch (Exception ex) {
    		
    	} finally {
    		if (null != ch) try { ch.close(); } catch (Exception ex) {}
    		if (null != in) try { in.close(); } catch (Exception ex) {}
    	}
    	
    	return null;
    }

    /**
     * 求一个字符串的md5值
     * @param target 字符串
     * @return md5 value
     */
    public static String encode(String target) {
        return DigestUtils.md5Hex(target);
    }

}