package com.aesirteam.smep.mms.db.domain;

import java.io.Serializable;
import java.util.Date;

public class MmsReportLog implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private String transactionid;

    private String mmsversion;

    private String mmsrelayserverid;

    private String messageid;

    private String destTerminalId;

    private String srcTerminalId;

    private Date mmtimestamp;

    private Integer mmstatus;

    private String statustext;

    private String createor;

    private Date createtime = new Date();

    private String protocol;

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

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
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

    public Integer getMmstatus() {
        return mmstatus;
    }

    public void setMmstatus(Integer mmstatus) {
        this.mmstatus = mmstatus;
    }

    public String getStatustext() {
        return statustext;
    }

    public void setStatustext(String statustext) {
        this.statustext = statustext;
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
