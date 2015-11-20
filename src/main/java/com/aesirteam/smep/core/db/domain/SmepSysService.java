package com.aesirteam.smep.core.db.domain;

import java.io.Serializable;
import java.util.Date;

public class SmepSysService implements Serializable {

	private static final long serialVersionUID = 1L;

	private String servicename;

    private String classname;

    private String relationengine;

    private Date laststarttime;

    private Date laststoptime;

    private Integer currstate;
    
    private String desc;
    
    private Integer autostart;
    
    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getRelationengine() {
        return relationengine;
    }

    public void setRelationengine(String relationengine) {
        this.relationengine = relationengine;
    }

    public Date getLaststarttime() {
        return laststarttime;
    }

    public void setLaststarttime(Date laststarttime) {
        this.laststarttime = laststarttime;
    }

    public Date getLaststoptime() {
        return laststoptime;
    }

    public void setLaststoptime(Date laststoptime) {
        this.laststoptime = laststoptime;
    }

    public Integer getCurrstate() {
        return currstate;
    }

    public void setCurrstate(Integer currstate) {
        this.currstate = currstate;
    }

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getAutostart() {
		return autostart;
	}

	public void setAutostart(Integer autostart) {
		this.autostart = autostart;
	}
}