package com.aesirteam.smep.sms.db.domain;

import java.io.Serializable;
import java.util.Date;

public class MsgMoLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msgid;

    private String destid;

    private String serviceId;

    private Integer tpPid;

    private Integer tpUdhi;

    private Integer msgFmt;

    private String srcTerminalId;

    private Integer registeredDelivery;

    private Integer msgLength;

    private String msgContent;

    private String reserve;

    private String stat;

    private Date submitTime;

    private Date doneTime;

    private String destTerminalId;

    private Integer smscSequence;

    private Integer srcTerminalType;

    private String linkid;
    
    private String createor;

    private Date createtime = new Date();
    
    private String protocol;
    
    public String getMsgid() {
        return msgid;
    }
    
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
    
    public String getDestid() {
        return destid;
    }

    public void setDestid(String destid) {
        this.destid = destid;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public String getSrcTerminalId() {
        return srcTerminalId;
    }

    public void setSrcTerminalId(String srcTerminalId) {
        this.srcTerminalId = srcTerminalId;
    }

    public Integer getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(Integer registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public Integer getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(Integer msgLength) {
        this.msgLength = msgLength;
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

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }

    public String getDestTerminalId() {
        return destTerminalId;
    }

    public void setDestTerminalId(String destTerminalId) {
        this.destTerminalId = destTerminalId;
    }

    public Integer getSmscSequence() {
        return smscSequence;
    }

    public void setSmscSequence(Integer smscSequence) {
        this.smscSequence = smscSequence;
    }

    public Integer getSrcTerminalType() {
        return srcTerminalType;
    }

    public void setSrcTerminalType(Integer srcTerminalType) {
        this.srcTerminalType = srcTerminalType;
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