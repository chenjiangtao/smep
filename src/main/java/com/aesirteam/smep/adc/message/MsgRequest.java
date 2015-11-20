package com.aesirteam.smep.adc.message;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.aesirteam.smep.util.SecurityTool;

public class MsgRequest {
		
	private Document document = null;
	
	public MsgRequest(String msg) throws Exception {
		try {
			document = DocumentHelper.parseText(msg);	
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * 获取消息头对象
	 * @return
	 * @throws Exception 
	 */
	public MsgHeader getHeader() throws Exception {
		MsgHeader header = null;
		
		if (document != null)  {
			try {
				Element root = document.getRootElement().element("HEAD");
				
				header = new MsgHeader();
				header.setCode(root.element("CODE").getTextTrim());
				header.setSid(root.element("SID").getTextTrim());
				header.setTimestamp(root.element("TIMESTAMP").getTextTrim());
				header.setServiceId(root.element("SERVICEID").getTextTrim());
			} catch (Exception ex) {
				throw new Exception(ex.getMessage());
			}
		}	
		return header;
	}
	
	/**
	 * 获取消息体字符串(解密后)
	 * @return
	 * @throws Exception 
	 */
	public String getBody() throws Exception {
		if (document == null) return null; 
		
		try {
			Element root = document.getRootElement().element("BODY");
			return SecurityTool.decrypt(root.getTextTrim()); 
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
}
