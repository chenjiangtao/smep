package com.aesirteam.smep.adc.db.domain;

import java.io.Serializable;
import java.util.Date;

public class SmepAdcStaffInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ufid;

    private String corpaccount;

    private Integer usertype;

    private Integer optype;

    private String opnote;

    private Date stdate = new Date();

    private Integer createor;

    private String staffName;

    private String staffMobile;

    private String staffSex;

    private String staffDeptid;

    public String getUfid() {
        return ufid;
    }

    public void setUfid(String ufid) {
        this.ufid = ufid;
    }

    public String getCorpaccount() {
        return corpaccount;
    }

    public void setCorpaccount(String corpaccount) {
        this.corpaccount = corpaccount;
    }

    public Integer getUsertype() {
        return usertype;
    }

    public void setUsertype(Integer usertype) {
        this.usertype = usertype;
    }

    public Integer getOptype() {
        return optype;
    }

    public void setOptype(Integer optype) {
        this.optype = optype;
    }

    public String getOpnote() {
        return opnote;
    }

    public void setOpnote(String opnote) {
        this.opnote = opnote;
    }

    public Date getStdate() {
        return stdate;
    }

    public void setStdate(Date stdate) {
        this.stdate = stdate;
    }

    public Integer getCreateor() {
        return createor;
    }

    public void setCreateor(Integer createor) {
        this.createor = createor;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffMobile() {
        return staffMobile;
    }

    public void setStaffMobile(String staffMobile) {
        this.staffMobile = staffMobile;
    }

    public String getStaffSex() {
        return staffSex;
    }

    public void setStaffSex(String staffSex) {
        this.staffSex = staffSex;
    }

    public String getStaffDeptid() {
        return staffDeptid;
    }

    public void setStaffDeptid(String staffDeptid) {
        this.staffDeptid = staffDeptid;
    }
}