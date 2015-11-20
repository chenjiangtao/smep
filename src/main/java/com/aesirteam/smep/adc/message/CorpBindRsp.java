package com.aesirteam.smep.adc.message;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.aesirteam.smep.adc.services.AdcConstant;
import com.aesirteam.smep.util.SecurityTool;

public class CorpBindRsp {		
	private MsgHeader header;
	private String resultcode; 
	private String resultmsg;
	private String url;
	
	public CorpBindRsp(MsgHeader header, int resultcode) {
		this.header = header;
		
		/**
		 * resultcode 返回结果代码 0:成功; 1:对未定义错误的描述; 401:业务配置参数错误; 402:产品订购错误; 403:License数量错误; 404:未知操作类型
		 */
		switch (resultcode) {
			case AdcConstant.HEADER_SERVICEID_ISNULL:
				this.resultcode = "1";
				this.resultmsg = "SERVICEID不能为空";
				break;
			
			case AdcConstant.CORP_ACCOUNT_ISNULL:
				this.resultcode = "1";
				this.resultmsg = "企业编号CORPACCOUNT不能为空";
				break;
				
			case AdcConstant.CORP_NAME_ISNULL:
				this.resultcode = "1";
				this.resultmsg = "企业名称CORPNAME不能为空";
				break;
			
			case AdcConstant.CORP_POINT_ISNULL:
				this.resultcode = "401";
				this.resultmsg = "产品列表POINTLIST应至少有一项值";
				break;
				
			case AdcConstant.CORP_OPTYPE_ISNULL:
				this.resultcode = "1";
				this.resultmsg = "操作类型OPTYPE不能为空";
				break;
			
			case AdcConstant.CORP_OPTYPE_FAIL:
				this.resultcode = "1";
				this.resultmsg = "未定义的操作类型OPTYPE";
				break;
				
			case AdcConstant.CORP_ORDER_SUCCESS:
				this.resultcode = "0";
				this.resultmsg = "EC订购产品成功";
				break;
				
			case AdcConstant.CORP_ORDER_FAIL:
				this.resultcode = "402";
				this.resultmsg = "EC订购产品失败";
				break;
			
			case AdcConstant.CORP_ORDER_NOTFOUND:
				this.resultcode = "402";
				this.resultmsg = "SI系统未发现EC的订购关系";
				break;
				
			case AdcConstant.CORP_PARAM_FAIL:
				this.resultcode = String.valueOf(AdcConstant.CORP_PARAM_FAIL);
				this.resultmsg = "EC的业务配置参数错误";
				break;
				
			case AdcConstant.CORP_ORDER_EXISTS:
				this.resultcode = "402";
				this.resultmsg = "SI系统已存在该EC订购关系无需重复此操作";
				break;
			
			case AdcConstant.CORP_STATUS_CHANGE_SUCCESS:
				this.resultcode = "0";
				this.resultmsg = "EC订购状态变更成功";
				break;
			
			case AdcConstant.CORP_INFO_CHANGE_SUCCESS:
				this.resultcode = "0";
				this.resultmsg = "EC企业信息变更成功";
				break;
			
			case AdcConstant.CORP_UNORDER_SUCCESS:
				this.resultcode = "0";
				this.resultmsg = "EC退订产品成功";
				break;
			
			case AdcConstant.CORP_STATUS_UNORDER:
				this.resultcode = "1";
				this.resultmsg = "EC为退订状态无法完成此操作";
				break;
												
			default:
				this.resultcode = "404";
				this.resultmsg = "未知的操作类型";
				break;
		}
		this.url = "http://10086.cn";
	}
	
	public String toString() {
		try {
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("CorpBindRsp");
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
			root.addElement("RESULTCODE").addText(resultcode);
			root.addElement("RESULTMSG").addText(resultmsg);
			root.addElement("URL").addText(url);
			return SecurityTool.encrypt(root.asXML());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
