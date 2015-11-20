package com.aesirteam.smep.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
public class Ucs2Util {
	
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}
	
	public static byte[] getUcs2Bytes(String strValue) throws UnsupportedEncodingException {
		byte[] bytes = strValue.getBytes("UTF-16BE");	
		return copyBytesArray(bytes);
	}
	
	public static String decodeUtf8StrFromUcs2Bytes(byte[] rawData) {
        
		if(rawData.length == 0 || rawData.length % 2 != 0)
            return null;//"Not UCS-2 Code";
        
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < rawData.length; i += 2) {
                sb.append(new String(UCS2toUTF8Code(encodeUCS2FromByte(rawData[i], rawData[i + 1])), "UTF-8"));
            }
        } catch (UnsupportedEncodingException ex) {
        	ex.printStackTrace();
            return null;
        }
        return sb.toString();
    }
	
	/** 长短信拆分 */
	public static List<byte[]> getBytesOfLongMessageCmpp(String msg_content) {
		List<byte[]> listReturn = new ArrayList<byte[]>();
		byte[] bytesUCS2 = null;
		int bytesUCS2Len = 0;
		int maxUCS2MessageLen = 140;
		int tp_udhi_bytes_length = 6;
		
		try {
			bytesUCS2 = msg_content.getBytes("UnicodeBigUnmarked");
			bytesUCS2Len = bytesUCS2.length;

			if (bytesUCS2Len <= maxUCS2MessageLen) {
				listReturn.add(bytesUCS2);
			} else {
				// 长短信发送
				// int tpUdhi = 1;
				// int msgFmt = 0x08;
				int messageCountAfterSplit = bytesUCS2Len / (maxUCS2MessageLen - tp_udhi_bytes_length) + 1;

				// 长短信分为多少条发送
				byte[] tp_udhiHead = new byte[tp_udhi_bytes_length];
				tp_udhiHead[0] = 0x05;
				tp_udhiHead[1] = 0x00;
				tp_udhiHead[2] = 0x03;
				tp_udhiHead[3] = 0x0A;
				tp_udhiHead[4] = (byte) messageCountAfterSplit;
				tp_udhiHead[5] = 0x01;

				// 默认为第一条
				for (int i = 0; i < messageCountAfterSplit; i++) {
					tp_udhiHead[5] = (byte) (i + 1);
					byte[] bytesSplit2 = null;
					if (i != messageCountAfterSplit - 1) {
						// 不为最后一条
						bytesSplit2 = new byte[140];
						System.arraycopy(tp_udhiHead, 0, bytesSplit2, 0, tp_udhiHead.length);
						System.arraycopy(bytesUCS2, i * (maxUCS2MessageLen - tp_udhi_bytes_length),
								bytesSplit2, tp_udhiHead.length, (maxUCS2MessageLen - tp_udhi_bytes_length));
					} else {
						bytesSplit2 = new byte[tp_udhi_bytes_length + bytesUCS2Len - i * (maxUCS2MessageLen - tp_udhi_bytes_length)];
						System.arraycopy(tp_udhiHead, 0, bytesSplit2, 0, tp_udhiHead.length);
						System.arraycopy(bytesUCS2, i * (maxUCS2MessageLen - tp_udhi_bytes_length), 
								bytesSplit2, tp_udhiHead.length, bytesUCS2Len - i * (maxUCS2MessageLen - tp_udhi_bytes_length));
					}

					listReturn.add(bytesSplit2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			listReturn.add(bytesUCS2);
		}

		return listReturn;
	}
	
	private static byte[] copyBytesArray(byte[] bytesSrc) {
		if (bytesSrc == null)
			return null;
		
		byte[] bytesDest = new byte[bytesSrc.length];
		System.arraycopy(bytesSrc, 0, bytesDest, 0, bytesSrc.length);
		return bytesDest;
	} 
	
    private static short encodeUCS2FromByte(byte highByte, byte lowByte) {
        return (short) ((short) (convertByteToShort(highByte) << 8) | convertByteToShort(lowByte));
    }
    
    private static byte[] UCS2toUTF8Code(short ucs2Code) {
        byte[] utf8Code = null;
        if (ucs2Code < 0 || ucs2Code > (short) 0x0800) {
            utf8Code = new byte[3];
            utf8Code[0] = (byte) ((convertShortToInt(ucs2Code) >>> 12) | 0xe0); 
            utf8Code[1] = (byte) ((convertShortToInt((short) (ucs2Code & 0x0fc0)) >>> 6) | 0x80); 
            utf8Code[2] = (byte) ((ucs2Code & 0x003f) | 0x80); 
        } else if ((short) 0x0080 > ucs2Code) {
            utf8Code = new byte[1];
            utf8Code[0] = (byte) ucs2Code;
        } else { 
            utf8Code = new byte[2];
            utf8Code[0] = (byte) ((ucs2Code >>> 6) | 0xc0);
            utf8Code[1] = (byte) ((ucs2Code & 0x003f) | 0x80);
        }
        return utf8Code;
    }
    
    private static int convertShortToInt(short shortValue) {
        return shortValue < 0 ? shortValue ^ 0xffff0000 : shortValue;
    }
    
    private static short convertByteToShort(byte byteValue) {
        return byteValue < 0 ? (short) (byteValue ^ 0xff00) : (short) byteValue;
    }
    	
    public static void main(String args[]) {
    	
    	try {
			byte tmp[] = getUcs2Bytes("(1/3)中新社北京12月24日电(记者 刘辰瑶)中国国务院国有资产监督管理委员会官网24日晚间更新了“委领导”一栏，张毅为国务院");
			System.out.println(tmp.length);
			
			tmp = getUcs2Bytes("(2/3)督公开资料显示，张毅，男，1950年生，黑龙江海伦人，1997年8月进入黑龙江省级领导班子，此后历任河北省委副书记、省纪");
			System.out.println(tmp.length);
			
			tmp = getUcs2Bytes("(3/3)央纪委副书记等职，2012年6月10日当选为宁夏回族自治区党委书记(至2013年3月)。");
			System.out.println(tmp.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	String ss = "<<aaaa.txt>>";
    	System.out.println(ss.replaceAll("<|>", ""));
    	
    	List<String> list = new ArrayList<String>();
    	list.add("13985168990");
    	list.add("13985168991");
    	System.out.println(list.toString().replaceAll("\\[|\\]", ""));
    }
}
