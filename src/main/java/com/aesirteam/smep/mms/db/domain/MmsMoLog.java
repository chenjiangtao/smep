package com.aesirteam.smep.mms.db.domain;

import java.io.Serializable;
import java.util.Date;

public class MmsMoLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String transactionid;

	private String mmsversion;

	private String mmsrelayserverid;

	private String linkid;

	private String destTerminalId;

	private String srcTerminalId;

	private Date mmtimestamp;

	private String replychargingid;

	private Integer priority;

	private String mmssubject;

	private Integer ismultipart;

	private String createor;

	private Date createtime = new Date();

	private String protocol;

	private String mmscontext;
	
    private String destcc;

    private String destbcc;


	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getMmsversion() {
		return mmsversion;
	}

	public void setMmsversion(String mmsversion) {
		this.mmsversion = mmsversion;
	}

	public String getMmsrelayserverid() {
		return mmsrelayserverid;
	}

	public void setMmsrelayserverid(String mmsrelayserverid) {
		this.mmsrelayserverid = mmsrelayserverid;
	}

	public String getLinkid() {
		return linkid;
	}

	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}

	public String getDestTerminalId() {
		return destTerminalId;
	}

	public void setDestTerminalId(String destTerminalId) {
		this.destTerminalId = destTerminalId;
	}

	public String getSrcTerminalId() {
		return srcTerminalId;
	}

	public void setSrcTerminalId(String srcTerminalId) {
		this.srcTerminalId = srcTerminalId;
	}

	public Date getMmtimestamp() {
		return mmtimestamp;
	}

	public void setMmtimestamp(Date mmtimestamp) {
		this.mmtimestamp = mmtimestamp;
	}

	public String getReplychargingid() {
		return replychargingid;
	}

	public void setReplychargingid(String replychargingid) {
		this.replychargingid = replychargingid;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getMmssubject() {
		return mmssubject;
	}

	public void setMmssubject(String mmssubject) {
		this.mmssubject = mmssubject;
	}

	public Integer getIsmultipart() {
		return ismultipart;
	}

	public void setIsmultipart(Integer ismultipart) {
		this.ismultipart = ismultipart;
	}

	public String getCreateor() {
		return createor;
	}

	public void setCreateor(String createor) {
		this.createor = createor;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getMmscontext() {
		return mmscontext;
	}

	public void setMmscontext(String mmscontext) {
		this.mmscontext = mmscontext;
	}

	public String getDestcc() {
		return destcc;
	}

	public void setDestcc(String destcc) {
		this.destcc = destcc;
	}

	public String getDestbcc() {
		return destbcc;
	}

	public void setDestbcc(String destbcc) {
		this.destbcc = destbcc;
	}
}
