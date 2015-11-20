/***********************************************************************
 * Module:  ReqMmsMessage.java
 * Author:  ShiHukui
 * Purpose: Defines the Class ReqMmsMessage
 ***********************************************************************/

package com.aesirteam.smep.client.message;

public class ReqMmsMessage extends RequestMessage implements Cloneable {

	private static final long serialVersionUID = 1L;
	
	/** 主题 */
	private String subject;

	/** 彩信文件列表 */
	private String[] adjFilePaths;
	
	/** 彩信附件列表 */
	private MMSFile[] file;
	
	/** 以文件名方式存放彩信附件 */
	public static int BODY_FILENAME = 0;
	
	/** 以序列化后字符串方式存放彩信附件*/
	public static int BODY_STREAM = 1;
	
	/** 彩信附件存放方式 */
	private int bodyType;
	
	/** 彩信阅读回复报告 */
	//private boolean readReply = false;
	
	/** 发送状态报告  */
	private int statusCode ;
	
	/** 发送状态报告描述 */
	private String statusText;
	
	/** 发送到彩信网关的时间  */
	//private Date sendDate;
	
	/** 彩信网关返回消息编号 */
	//private String submitMessageID;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String[] getAdjFilePaths() {
		return adjFilePaths;
	}

	public void setAdjFilePaths(String[] adjFilePaths) {
		this.adjFilePaths = adjFilePaths;
	}

	public MMSFile[] getFile() {
		return file;
	}

	public void setFile(MMSFile[] file) {
		this.file = file;
	}

	public int getBodyType() {
		return bodyType;
	}

	public void setBodyType(int bodyType) {
		this.bodyType = bodyType;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	/*
	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getSubmitMessageID() {
		return submitMessageID;
	}

	public void setSubmitMessageID(String submitMessageID) {
		this.submitMessageID = submitMessageID;
	}
	*/
	@Override
	public ReqMmsMessage clone() throws CloneNotSupportedException {
		ReqMmsMessage req = (ReqMmsMessage)super.clone();
		req.setDestAddr(null);
		return req;
	}
}