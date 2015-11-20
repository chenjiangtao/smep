package com.aesirteam.smep.adc.db.domain;

import java.io.Serializable;
import java.util.Date;

public class SmepAdcCorpInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String corpaccount;

    private String license;

    private Integer optype;

    private String opnote;

    private String point;

    private Date stdate = new Date();

    private Integer createor;

    private String corpName;

    private String corpShortname;

    private String corpLinkman;

    private String corpLinkphone;

    private String corpLinkmobile;

    public String getCorpaccount() {
        return corpaccount;
    }

    public void setCorpaccount(String corpaccount) {
        this.corpaccount = corpaccount;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
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

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
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

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getCorpShortname() {
        return corpShortname;
    }

    public void setCorpShortname(String corpShortname) {
        this.corpShortname = corpShortname;
    }

    public String getCorpLinkman() {
        return corpLinkman;
    }

    public void setCorpLinkman(String corpLinkman) {
        this.corpLinkman = corpLinkman;
    }

    public String getCorpLinkphone() {
        return corpLinkphone;
    }

    public void setCorpLinkphone(String corpLinkphone) {
        this.corpLinkphone = corpLinkphone;
    }

    public String getCorpLinkmobile() {
        return corpLinkmobile;
    }

    public void setCorpLinkmobile(String corpLinkmobile) {
        this.corpLinkmobile = corpLinkmobile;
    }
}