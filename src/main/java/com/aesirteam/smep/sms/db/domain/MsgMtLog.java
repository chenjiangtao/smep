package com.aesirteam.smep.sms.db.domain;

import java.io.Serializable;
import java.util.Date;

public class MsgMtLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msgid;

    private String seqno;

    private String corpno;

    private Integer pkTotal;

    private Integer pkNumber;

    private Integer registeredDelivery;

    private Integer msgLevel;

    private String serviceId;

    private Integer feeUsertype;

    private String feeTerminalId;

    private Integer tpPid;

    private Integer tpUdhi;

    private Integer msgFmt;

    private String msgSrc;

    private String feeType;

    private String feeCode;

    private Date validTime;

    private Date atTime;

    private String srcTerminalId;

    private String destTerminalId;

    private String msgContent;

    private String reserve;

    private Integer feeTerminalType;

    private Integer destTerminalType;

    private String linkid;
    
    private String createor;

    private Date createtime;
    
    private String protocol;
    
    public String getMsgid() {
        return msgid;
    }
   
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
    
    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public String getCorpno() {
        return corpno;
    }

    public void setCorpno(String corpno) {
        this.corpno = corpno;
    }

    public Integer getPkTotal() {
        return pkTotal;
    }

    public void setPkTotal(Integer pkTotal) {
        this.pkTotal = pkTotal;
    }

    public Integer getPkNumber() {
        return pkNumber;
    }

    public void setPkNumber(Integer pkNumber) {
        this.pkNumber = pkNumber;
    }

    public Integer getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(Integer registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public Integer getMsgLevel() {
        return msgLevel;
    }

    public void setMsgLevel(Integer msgLevel) {
        this.msgLevel = msgLevel;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getFeeUsertype() {
        return feeUsertype;
    }

    public void setFeeUsertype(Integer feeUsertype) {
        this.feeUsertype = feeUsertype;
    }

    public String getFeeTerminalId() {
        return feeTerminalId;
    }

    public void setFeeTerminalId(String feeTerminalId) {
        this.feeTerminalId = feeTerminalId;
    }

    public Integer getTpPid() {
        return tpPid;
    }

    public void setTpPid(Integer tpPid) {
        this.tpPid = tpPid;
    }

    public Integer getTpUdhi() {
        return tpUdhi;
    }

    public void setTpUdhi(Integer tpUdhi) {
        this.tpUdhi = tpUdhi;
    }

    public Integer getMsgFmt() {
        return msgFmt;
    }

    public void setMsgFmt(Integer msgFmt) {
        this.msgFmt = msgFmt;
    }

    public String getMsgSrc() {
        return msgSrc;
    }

    public void setMsgSrc(String msgSrc) {
        this.msgSrc = msgSrc;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getFeeCode() {
        return feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public Date getAtTime() {
        return atTime;
    }

    public void setAtTime(Date atTime) {
        this.atTime = atTime;
    }

    public String getSrcTerminalId() {
        return srcTerminalId;
    }

    public void setSrcTerminalId(String srcTerminalId) {
        this.srcTerminalId = srcTerminalId;
    }

    public String getDestTerminalId() {
        return destTerminalId;
    }

    public void setDestTerminalId(String destTerminalId) {
        this.destTerminalId = destTerminalId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public Integer getFeeTerminalType() {
        return feeTerminalType;
    }

    public void setFeeTerminalType(Integer feeTerminalType) {
        this.feeTerminalType = feeTerminalType;
    }

    public Integer getDestTerminalType() {
        return destTerminalType;
    }

    public void setDestTerminalType(Integer destTerminalType) {
        this.destTerminalType = destTerminalType;
    }

    public String getLinkid() {
        return linkid;
    }

    public void setLinkid(String linkid) {
        this.linkid = linkid;
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
}