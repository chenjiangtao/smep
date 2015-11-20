/***********************************************************************
 * Module:  RequestMessage.java
 * Author:  ShiHukui
 * Purpose: Defines the Class RequestMessage
 ***********************************************************************/

package com.aesirteam.smep.client.message;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class RequestMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 消息流水号 */
	private String seqNo = UUID.randomUUID().toString();
	
	/** 企业编号 */
	private String corpNo;
	
	/** 业务类型 */
	//private String serviceId;
	
	/** 发送方号码 */
	private String srcAddr;
	
	/** 目标号码 */
	private String destAddr;
	
	/** 目标号码数 */
	private int destAddrTotal;
	
	/** 发送优先级  0~9 由低到高*/
	private int priorityLevel = 0;
	
	/** 是否需要状态报告 0：不需 1：需要*/
	private int needStautsReport = 0;
	
	/** 是否需要回复 0：不需 1：需要*/
	private int needReply = 0;
	
	/** 发送时间*/
	private Date atTime;
	
	/** 有效时间 */
	private Date vaildTime;
	
	/** 创建者ID */
	private String createor;
	
	/** 创建时间  */
	private Date createTime = new Date();
	
	/** spid */
	//private String spId;
	
	/** 发送协议 cmpp20/cmpp30/sgip12/sgip13/mm7 */
	private String protocolType;
	
	public String getSeqNo() {
		return seqNo.replaceAll("-", "");
	}

	/** @param newSeqNo */
	public void setSeqNo(String newSeqNo) {
		seqNo = newSeqNo;
	}

	public String getCorpNo() {
		return corpNo;
	}

	/** @param newCorpNo */
	public void setCorpNo(String newCorpNo) {
		corpNo = newCorpNo;
	}
	
	/*
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String newServiceId) {
		serviceId = newServiceId;
	}
	*/
	
	public String getSrcAddr() {
		return srcAddr;
	}

	/** @param newSrcAddr */
	public void setSrcAddr(String newSrcAddr) {
		srcAddr = newSrcAddr;
	}

	public String getDestAddr() {
		return destAddr;
	}

	/** @param newDestAddr */
	public void setDestAddr(String newDestAddr) {
		destAddr = newDestAddr;
	}

	public int getDestAddrTotal() {
		return destAddrTotal;
	}

	/** @param newDestAddrTotal */
	public void setDestAddrTotal(int newDestAddrTotal) {
		destAddrTotal = newDestAddrTotal;
	}

	public int getPriorityLevel() {
		return priorityLevel;
	}

	/** @param newPriorityLevel */
	public void setPriorityLevel(int newPriorityLevel) {
		priorityLevel = newPriorityLevel;
	}

	public int getNeedStautsReport() {
		return needStautsReport;
	}

	/** @param newNeedStautsReport */
	public void setNeedStautsReport(int newNeedStautsReport) {
		needStautsReport = newNeedStautsReport;
	}

	public Date getAtTime() {
		return atTime;
	}

	/** @param newAtTime */
	public void setAtTime(Date newAtTime) {
		atTime = newAtTime;
	}

	public Date getVaildTime() {
		return vaildTime;
	}

	/** @param newVaildTime */
	public void setVaildTime(Date newVaildTime) {
		vaildTime = newVaildTime;
	}

	public String getCreateor() {
		return createor;
	}

	/** @param newCreateor */
	public void setCreateor(String newCreateor) {
		createor = newCreateor;
	}

	public Date getCreateTime() {
		return createTime;
	}

	/** @param newCreateTime */
	public void setCreateTime(Date newCreateTime) {
		createTime = newCreateTime;
	}

	public int getNeedReply() {
		return needReply;
	}

	public void setNeedReply(int needReply) {
		this.needReply = needReply;
	}
	/*
	public String getSpId() {
		return spId;
	}
	
	public void setSpId(String spId) {
		this.spId = spId;
	}
	*/
	
	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
}