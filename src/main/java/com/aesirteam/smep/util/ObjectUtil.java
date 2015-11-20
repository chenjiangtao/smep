package com.aesirteam.smep.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.aesirteam.smep.client.message.ReqMmsMessage;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.mms.db.domain.MmsMoLog;
import com.aesirteam.smep.mms.db.domain.MmsReportLog;
import com.aesirteam.smep.mms.db.domain.MmsMtLog;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;
import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectUtil {
	
	protected final static String encode = "ISO-8859-1";
	protected final static int BUFFER = 256; 
	protected final static ObjectMapper objectMapper;
	
    static {
    	objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }
    
    public static ReqSmsMessage toReqSmsMessage(String str) throws Exception {
    	return string2Object(str, ReqSmsMessage.class, true);
    }
    
    public static ReqMmsMessage toReqMmsMessage(String str) throws Exception {
    	return string2Object(str, ReqMmsMessage.class, true);
    }
    
    public static MsgMtLog toMsgMtLog(String str) throws Exception {
    	return string2Object(str, MsgMtLog.class, true);
    }
    
    public static MsgMoLog toMsgMoLog(String str) throws Exception {
    	return string2Object(str, MsgMoLog.class, true);
    }
    
    public static MmsMtLog toMmsMtLog(String str) throws Exception {
    	return string2Object(str, MmsMtLog.class, true);
    }
    
    public static MmsReportLog toMmsReportLog(String str) throws Exception {
    	return string2Object(str, MmsReportLog.class, true);
    }
    
    public static MmsMoLog toMmsMoLog(String str) throws Exception {
    	return string2Object(str, MmsMoLog.class, true);
    }
    
    public static String object2String(Object obj) throws Exception {
    	return object2String(obj, true);
    }
	/**
	 * 序列化的对象装换为Base64的字符串
	 * 
	 * @param obj 序列化对象
	 * @param bZip 是否需要压缩
	 * @return 二进制的Base64字符串
	 * @throws IOException 
	 * @throws Exception
	 */
    
    public static String object2String(Object obj, boolean bZip) throws Exception {
		return bZip ? ZipUtil.compress(object2Json(obj)) : object2Json(obj);
	}
        
	/**
	 * 将Base64字符串反序列化为对象
	 * 
	 * @param base64Str 二进制的Base64字符串
	 * @param bZip 是否需要解压
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends java.io.Serializable> T string2Object(String str, Class<T> c, boolean bZip) throws Exception {
		if (bZip) {
			str = ZipUtil.uncompress(str);
		}
		return (T) json2Object(str, c);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends java.io.Serializable> T file2Object(String jsonFilename, Class<T> c) throws Exception {
		File file = new File(jsonFilename);
		if (!file.exists()) 
			throw new java.io.FileNotFoundException(jsonFilename);
		
		return (T) json2Object(file, c);
	}
	
    public static String object2Json(Object object) throws JsonGenerationException, JsonMappingException, IOException {
        return objectMapper.writeValueAsString(object);
    }
    
    public static <T extends java.io.Serializable> Object json2Object(String str, Class<T> c) throws JsonParseException, JsonMappingException, IOException {
    	return objectMapper.readValue(str, c);
    }
    
    protected static <T extends java.io.Serializable> Object json2Object(File jsonFile, Class<T> c) throws JsonParseException, JsonMappingException, IOException {
    	return objectMapper.readValue(jsonFile, c);
    }
    
    protected static class ZipUtil {
	    /**  
		* 字符串的压缩  
		*   
		* @param str 待压缩的字符串  
		* @return 返回压缩后的字符串  
		* @throws IOException  
		*/  
		private static String compress(String str) throws IOException {
			if (str == null || str.trim().length() == 0) {
				return null;
			}
			
			ByteArrayOutputStream baos = null;
			GZIPOutputStream gos = null;
			try {
				baos = new ByteArrayOutputStream();
				gos = new GZIPOutputStream(baos);
				gos.write(str.getBytes());
				gos.finish();
				return baos.toString(encode);
			} finally {
				if (null != baos) 
					baos.close();
				
				if (null != gos)
					gos.close();
			}
		}

		/**  
		* 字符串的解压  
		*   
		* @param str 对字符串解压  
		* @return 返回解压缩后的字符串  
		* @throws IOException  
		*/
		private static String uncompress(String str) throws IOException {
			if (str == null || str.length() == 0) {
				return str;
			}
			
			byte[] buffer = new byte[BUFFER];
			ByteArrayOutputStream baos = null;
			ByteArrayInputStream bais = null;
			GZIPInputStream gis = null;
			try {
				baos = new ByteArrayOutputStream();
				bais = new ByteArrayInputStream(str.getBytes(encode));
				gis = new GZIPInputStream(bais);
				int n;
				while ((n = gis.read(buffer)) >= 0) {
					baos.write(buffer, 0, n);
				}
				// toString()使用平台默认编码，也可以显式的指定如toString("GBK")
				return baos.toString();
			} finally {
				buffer = null;
				if (null != baos) 
					baos.close();
				
				if (null != bais) 
					bais.close();
				
				if (null != gis)
					gis.close();
			}
		}
	}
}
