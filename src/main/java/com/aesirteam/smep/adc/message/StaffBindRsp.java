package com.aesirteam.smep.adc.message;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.aesirteam.smep.adc.services.AdcConstant;
import com.aesirteam.smep.util.SecurityTool;

public class StaffBindRsp {
	
	private MsgHeader header;
	private String corpAccount;
	private List<StaffInfoResult> result;
	private int resultcode = 0;
	
	public StaffBindRsp(MsgHeader header, String corpAccount, List<StaffInfoResult> result) {
		this.header = header;
		this.corpAccount = corpAccount;
		this.result = result;
	}
	
	public StaffBindRsp(MsgHeader header, String corpAccount, int resultcode) {
		this.header = header;
		this.corpAccount = corpAccount;
		this.resultcode = resultcode;
	}
	
	public String toString() {
		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("StaffBindRsp");
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
			Element sub = root.addElement("STAFFLIST");
			
			if (resultcode == 0) {
				for(StaffInfoResult info : result) {
					Element e = sub.addElement("STAFFINFO");
					e.addElement("UFID").addText(info.getUfId().length() == 0 ? "" : info.getUfId());
					e.addElement("STAFFNAME").addText(info.getStaffName().length() == 0 ? "" : info.getStaffName());
					e.addElement("STAFFMOBILE").addText(info.getStaffMobile().length() == 0 ? "" : info.getStaffMobile());
					e.addElement("RESULTCODE").addText(info.getResultcode());
					e.addElement("RESULTMSG").addText(info.getResultmsg());
				}
			} else {
				Element e = sub.addElement("STAFFINFO");
				e.addElement("UFID");
				e.addElement("STAFFNAME");
				e.addElement("STAFFMOBILE");
				switch(resultcode) {
					case AdcConstant.HEADER_SERVICEID_ISNULL:
						e.addElement("RESULTCODE").addText("1");
						e.addElement("RESULTMSG").addText("SERVICEID不能为空");
						break;
						
					case AdcConstant.CORP_ACCOUNT_ISNULL:
						e.addElement("RESULTCODE").addText("1");
						e.addElement("RESULTMSG").addText("企业编号CORPACCOUNT不能为空");
						break;
						
					case AdcConstant.CORP_ORDER_NOTFOUND:
						e.addElement("RESULTCODE").addText("501");
						e.addElement("RESULTMSG").addText("未发现EC订购关系,亦可能EC处于退订或暂停状态");
						break;
				}
			}
			
			return SecurityTool.encrypt(root.asXML());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
