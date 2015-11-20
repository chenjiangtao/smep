package com.aesirteam.smep.adc.message;

public class MsgHeader {
	
	/*<HEAD>
	<CODE>消息标志</CODE>
	<SID>消息序列号</SID>
	<TIMESTAMP>时间戳</TIMESTAMP>
	<SERVICEID>业务代码</SERVICEID>
	</HEAD>
*/
	/**
	 * 消息标识
	 */
	private String code;
	
	/**
	 * 消息序列号
	 */
	private String sid;
	
	/**
	 * 时间戳
	 */
	private String timestamp;
	
	/**
	 *业务代码
	 */
	private String serviceId;
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getSid() {
		return sid;
	}
	
	public void setSid(String sid) {
		this.sid = sid;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	

}
