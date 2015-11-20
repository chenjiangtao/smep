package com.aesirteam.smep.adc.message;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.aesirteam.smep.util.SecurityTool;

public class DeptBindRsp {
	private MsgHeader header;
	private String corpAccount;
	private List<DeptInfoResult> result;
	
	public DeptBindRsp(MsgHeader header, String corpAccount, List<DeptInfoResult> result) {
		this.header = header;
		this.corpAccount = corpAccount;
		this.result = result;
	}
	
	public String toString() {
		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("DeptBindRsp");
			Element head = root.addElement("HEAD");
			head.addElement("CODE").addText(header.getCode());
			head.addElement("SID").addText(header.getSid());
			head.addElement("TIMESTAMP").addText(header.getTimestamp());
			head.addElement("SERVICEID").addText(header.getServiceId());
			root.addElement("BODY").setText(encodeBody());
			return root.asXML();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private String encodeBody() {
		Document document = DocumentHelper.createDocument();
		try {
			Element root = document.addElement("BODY");
			root.addElement("CORPACCOUNT").addText(corpAccount);
			for(DeptInfoResult info : result) {
				Element e = root.addElement("DEPS");
				e.addElement("DEPTID").addText(info.getDeptId());
				e.addElement("RESULTCODE").addText(info.getResultcode());
				e.addElement("RESULTMSG").addText(info.getResultmsg());
			}
			return SecurityTool.encrypt(root.asXML());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
