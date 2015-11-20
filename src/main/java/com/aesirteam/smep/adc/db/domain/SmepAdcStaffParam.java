package com.aesirteam.smep.adc.db.domain;

import java.io.Serializable;

public class SmepAdcStaffParam implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer spid;

    private String ufid;

    private String paramName;

    private String paramValue;

    public Integer getSpid() {
        return spid;
    }

    public void setSpid(Integer spid) {
        this.spid = spid;
    }

    public String getUfid() {
        return ufid;
    }

    public void setUfid(String ufid) {
        this.ufid = ufid;
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