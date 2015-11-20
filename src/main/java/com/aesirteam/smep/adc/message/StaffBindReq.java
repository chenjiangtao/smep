package com.aesirteam.smep.adc.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class StaffBindReq {
	
	private MsgHeader header;
	private String corpAccount;
	private List<StaffInfo> staffList;
	
	@SuppressWarnings("unchecked")
	public StaffBindReq (String msg) throws Exception {
		MsgRequest request = new MsgRequest(msg);
		
		//获取消息头
		this.header = request.getHeader();
		
		//解析消息体
		String bodyText;
		if (null == (bodyText = request.getBody())) {
			throw new Exception("StaffBindReq消息体解析错误");
		}
		
		try {
			Document document =  DocumentHelper.parseText(bodyText);
			Element root = document.getRootElement();
			this.corpAccount = root.element("CORPACCOUNT").getTextTrim();
			
			staffList = new ArrayList<StaffInfo>();
			
			if (null != root.element("STAFFLIST").element("STAFFINFO")) {
				for (Iterator<Element> it = root.element("STAFFLIST").elementIterator("STAFFINFO"); it.hasNext(); ) {
					Element e = it.next();
					
					StaffInfo info = new StaffInfo();
					info.setUfId(e.element("UFID").getTextTrim());
					info.setUserType(e.element("USERTYPE").getTextTrim());
					info.setStaffName(e.element("STAFFNAME").getTextTrim());
					info.setStaffMobile(e.element("STAFFMOBILE").getTextTrim());
					info.setOptype(e.element("OPTYPE").getTextTrim());
					info.setOpnote(e.element("OPNOTE").getTextTrim());
					info.setUserInfoMapList(new HashMap<String, String>());
					for (Iterator<Element> sit = e.element("USERINFOMAPLIST").elementIterator("USERINFOMAP"); sit.hasNext(); ) {
						Element se = sit.next();
						info.getUserInfoMapList().put(se.element("USERINFONAME").getTextTrim(), se.element("USERINFOVALUE").getTextTrim());
					}
					staffList.add(info);
				}
			}
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
		
	public MsgHeader getHeader() {
		return header;
	}
	
	/**
	 * @return 企业编号
	 */
	public String getCorpAccount() {
		return corpAccount;
	}
	
	/**
	 * @return 用户列表
	 */
	public List<StaffInfo> getStaffList() {
		return staffList;
	}
}
