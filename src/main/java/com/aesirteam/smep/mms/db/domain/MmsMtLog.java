package com.aesirteam.smep.mms.db.domain;

import java.io.Serializable;
import java.util.Date;

public class MmsMtLog implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private String transactionid;

    private String seqno;

    private String corpno;

    private String vaspid;

    private String vasid;

    private String mmsversion;

    private String mmssubject;

    private String srcTerminalId;

    private String destTerminalId;

    private String serviceId;

    private Integer registeredDelivery;

    private Integer msgLevel;

    private Integer mmsBodytype;

    private Date validTime;

    private Date atTime;

    private String mmsFile;

    private String createor;

    private Date createtime;

    private String protocol;

    private String msgid;

    private Integer statuscode;

    private String statustext;

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
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

    public String getVaspid() {
        return vaspid;
    }

    public void setVaspid(String vaspid) {
        this.vaspid = vaspid;
    }

    public String getVasid() {
        return vasid;
    }

    public void setVasid(String vasid) {
        this.vasid = vasid;
    }

    public String getMmsversion() {
        return mmsversion;
    }

    public void setMmsversion(String mmsversion) {
        this.mmsversion = mmsversion;
    }

    public String getMmssubject() {
        return mmssubject;
    }
    
    public void setMmssubject(String mmssubject) {
        this.mmssubject = mmssubject;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public Integer getMmsBodytype() {
        return mmsBodytype;
    }

    public void setMmsBodytype(Integer mmsBodytype) {
        this.mmsBodytype = mmsBodytype;
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

    public String getMmsFile() {
        return mmsFile;
    }

    public void setMmsFile(String mmsFile) {
        this.mmsFile = mmsFile;
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

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public Integer getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatustext() {
        return statustext;
    }

    public void setStatustext(String statustext) {
        this.statustext = statustext;
    }
}
