package com.aesirteam.smep.adc.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class DeptBindReq {
	
	private MsgHeader header;
	private String corpAccount;
	private List<DeptInfo> deptList;
		
	@SuppressWarnings("unchecked")
	public DeptBindReq(String msg) throws Exception {
		MsgRequest request = new MsgRequest(msg);
		
		//获取消息头
		this.header = request.getHeader();
		
		//解析消息体
		String bodyText;
		if (null == (bodyText = request.getBody())) {
			throw new Exception("DeptBindReq消息体解析错误");
		}
		
		try {
			Document document =  DocumentHelper.parseText(bodyText);
			Element root = document.getRootElement();
			this.corpAccount = root.element("CORPACCOUNT").getTextTrim();
			
			deptList = new ArrayList<DeptInfo>();
			for (Iterator<Element> it = root.elementIterator("DEPS"); it.hasNext(); ) {
				Element e = it.next();
				//System.out.println("<======>");
				//System.out.println(String.format("%s\t%s", e.element("DEPTID").getTextTrim(),e.element("OPTYPE").getTextTrim()));
				DeptInfo info = new DeptInfo();
				info.setDeptId(e.element("DEPTID").getTextTrim());
				info.setParentId(e.element("PARENTID").getTextTrim());
				info.setDepName(e.element("DEPNAME").getTextTrim());
				info.setDepDes(e.element("DEPDES").getTextTrim());
				info.setDepAddress(e.element("DEPADDRESS").getTextTrim());
				info.setDepTelNo(e.element("DEPTELNO").getTextTrim());
				info.setDepFaxNo(e.element("DEPFAXNO").getTextTrim());
				info.setDepMngId(e.element("DEPMNGID").getTextTrim());
				info.setBuildTime(e.element("BUILDTIME").getTextTrim());
				info.setUpdateDate(e.element("UPDATEDATE").getTextTrim());
				info.setOptype(e.element("OPTYPE").getTextTrim());
				info.setDeptInfoMapList(new HashMap<String, String>());
				
				for (Iterator<Element> sit = e.element("DEPTINFOMAPLIST").elementIterator("DEPTINFOMAP"); sit.hasNext(); ) {
					Element se = sit.next();
					info.getDeptInfoMapList().put(se.element("DEPTINFONAME").getTextTrim(), se.element("DEPTINFOVALUE").getTextTrim());
				}
				deptList.add(info);
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
	 * @return 部门列表
	 */
	public List<DeptInfo> getDeptList() {
		return deptList;
	}
}
