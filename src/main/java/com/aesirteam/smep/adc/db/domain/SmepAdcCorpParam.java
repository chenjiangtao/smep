package com.aesirteam.smep.adc.db.domain;

import java.io.Serializable;

public class SmepAdcCorpParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer cpid;

    private String corpaccount;

    private String paramType;

    private String paramName;

    private String paramValue;

    public Integer getCpid() {
        return cpid;
    }

    public void setCpid(Integer cpid) {
        this.cpid = cpid;
    }

    public String getCorpaccount() {
        return corpaccount;
    }

    public void setCorpaccount(String corpaccount) {
        this.corpaccount = corpaccount;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}