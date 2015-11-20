package com.aesirteam.smep.adc.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.aesirteam.smep.adc.db.domain.SmepAdcCorpInfo;
import com.aesirteam.smep.adc.db.domain.SmepAdcCorpParam;


public class CorpBindReq {
	
	private MsgHeader header;
	private String corpName;
	private String corpAccount; 
	private String license;
	private String optype;
	private String opnote;
	private String point;
	private String corpShortName;
	private String corpLinkMan;
	private String corpLinkPhone;
	private String corpLinkMobile;
	private Map<String, String> paramMap, corpInfoMap, orderPointMap;
	
	
	@SuppressWarnings("unchecked")
	public CorpBindReq(String msg) throws Exception {
		MsgRequest request = new MsgRequest(msg);
		
		//获取消息头
		this.header = request.getHeader();
		
		//解析消息体
		String bodyText;
		if (null == (bodyText = request.getBody())) {
			throw new Exception("CorpBindReq消息体解析错误");
		}
				
		try {
			Document document =  DocumentHelper.parseText(bodyText);
			Element root = document.getRootElement();
			this.corpName = root.element("CORPNAME").getTextTrim();
			this.corpAccount = root.element("CORPACCOUNT").getTextTrim();
			this.license = root.element("LICENSE").getTextTrim();
			this.optype = root.element("OPTYPE").getTextTrim();
			this.opnote = root.element("OPNOTE").getTextTrim();
			
			//PARAMMAP
			paramMap = new HashMap<String, String>();
			if (null != root.element("PARAMLIST").element("PARAMMAP")) {
				for(Iterator<Element> it = root.element("PARAMLIST").elementIterator("PARAMMAP"); it.hasNext(); )
				{
					Element e = it.next();
					String name = e.element("PARAMNAME").getTextTrim();
					String value = e.element("PARAMVALUE").getTextTrim();
					if (0 < name.length() && 0 < value.length())
						paramMap.put(name, value);
				}
			}
			
			//CORPINFOMAP
			corpInfoMap = new HashMap<String, String>();
			if (null != root.element("CORPINFOLIST").element("CORPINFOMAP")) {
				for(Iterator<Element> it = root.element("CORPINFOLIST").elementIterator("CORPINFOMAP"); it.hasNext(); )
				{
					Element e = it.next();
					String name = e.element("CORPINFONAME").getTextTrim();
					String value = e.element("CORPINFOVALUE").getTextTrim();
					if (0 < name.length() && 0 < value.length()) {
						corpInfoMap.put(name, value);
					
						if ("CORP_SHORTNAME".equals(name)) 
							this.corpShortName = value;
						else if ("CORP_LINKMAN".equals(name)) 
							this.corpLinkMan = value;
						else if ("CORP_LINKPHONE".equals(name)) 
							this.corpLinkPhone = value;
						else if ("CORP_LINKMOBILE".equals(name)) 
							this.corpLinkMobile = value;
					}
				}
			}
			
			//ORDERPOINTMAP
			orderPointMap = new HashMap<String, String>();
			int count = 1;
			if (null != root.element("POINTLIST").element("ORDERPOINTMAP")) {
				for(Iterator<Element> it = root.element("POINTLIST").elementIterator("ORDERPOINTMAP"); it.hasNext(); )
				{
					Element e = it.next();
					String name = e.element("POINTNAME").getTextTrim();
					String value = e.element("POINTVALUE").getTextTrim();
					if (0 < name.length() && 0 < value.length()) {
						orderPointMap.put(name, value);
						this.point = (count == 1) ? name : String.format("%s,%s", point, name);
						count++;
					}
				}	
			}
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	public SmepAdcCorpInfo convertCorpInfo() {
		SmepAdcCorpInfo info = new SmepAdcCorpInfo();
		info.setCorpaccount(corpAccount);
		info.setLicense(license);
		info.setOptype(Integer.valueOf(optype));
		info.setOpnote(opnote);
		info.setPoint(point);
		info.setStdate(new Date());
		info.setCreateor(0);
		info.setCorpName(corpName);
		info.setCorpShortname(corpShortName);
		info.setCorpLinkman(corpLinkMan);
		info.setCorpLinkphone(corpLinkPhone);
		info.setCorpLinkmobile(corpLinkMobile);
		return info;
	}
	
	public List<SmepAdcCorpParam> convertCorpParam() {
		List<SmepAdcCorpParam> list = new ArrayList<SmepAdcCorpParam>();
		Set<Map.Entry<String, String>> set = paramMap.entrySet();
		for(Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			SmepAdcCorpParam smepAdcCorpParam = new SmepAdcCorpParam();
			smepAdcCorpParam.setCorpaccount(corpAccount);
			smepAdcCorpParam.setParamType("PARAMMAP");
			smepAdcCorpParam.setParamName(entry.getKey());
			smepAdcCorpParam.setParamValue(entry.getValue());
			list.add(smepAdcCorpParam);
		}
		
		set = corpInfoMap.entrySet();
		for(Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			SmepAdcCorpParam smepAdcCorpParam = new SmepAdcCorpParam();
			smepAdcCorpParam.setCorpaccount(corpAccount);
			smepAdcCorpParam.setParamType("CORPINFOMAP");
			smepAdcCorpParam.setParamName(entry.getKey());
			smepAdcCorpParam.setParamValue(entry.getValue());
			list.add(smepAdcCorpParam);
		}
		
		set = orderPointMap.entrySet();
		for(Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, String> entry = it.next();
			SmepAdcCorpParam smepAdcCorpParam = new SmepAdcCorpParam();
			smepAdcCorpParam.setCorpaccount(corpAccount);
			smepAdcCorpParam.setParamType("ORDERPOINTMAP");
			smepAdcCorpParam.setParamName(entry.getKey());
			smepAdcCorpParam.setParamValue(entry.getValue());
			list.add(smepAdcCorpParam);
		}
		
		return list;
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
	 * @return 业务License
	 */
	public String getLicense() {
		return license;
	}
	
	/**
	 * @return 订购状态：1 : 订购；2 : 暂停服务；3 : 恢复服务；4 : 取消；5 : 订购关系变更；6 : 基本信息变更
	 */
	public String getOptype() {
		return optype;
	}
	
	/**
	 * @return 订购描述
	 */
	public String getOpnote() {
		return opnote;
	}
	
	/**
	 * @return 业务功能点标识
	 */
	public String getPoint() {
		return point;
	}
	
	/**
	 * @return 企业名称
	 */
	public String getCorpName() {
		return corpName;
	}
	
	/**
	 * @return 企业简称
	 */
	public String getCorpShortName() {
		return corpShortName;
	}
	
	/**
	 * @return 企业联系人
	 */
	public String getCorpLinkMan() {
		return corpLinkMan;
	}
	
	/**
	 * @return 联系人座机号
	 */
	public String getCorpLinkPhone() {
		return corpLinkPhone;
	}
	
	/**
	 * @return 联系人手机号
	 */
	public String getCorpLinkMobile() {
		return corpLinkMobile;
	}
	
	/**
	 * @return 业务参数列表
	 */
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	
	/**
	 * @return 产品功能点列表
	 */
	public Map<String, String> getOrderPointMap() {
		return orderPointMap;
	}
	
	/**
	 * @return 企业信息参数列表
	 */
	public Map<String, String> getCorpInfoMap() {
		return corpInfoMap;
	}
}
