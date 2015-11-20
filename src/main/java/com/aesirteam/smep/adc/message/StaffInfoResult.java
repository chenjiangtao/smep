package com.aesirteam.smep.adc.message;

public class StaffInfoResult {
	private String ufId;
	private String staffName;
	private String staffMobile;
	private String resultcode; 
	private String resultmsg;
	
	public String getUfId() {
		return ufId;
	}
	public void setUfId(String ufId) {
		this.ufId = ufId;
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
	public String getResultcode() {
		return resultcode;
	}
	public void setResultcode(String resultcode) {
		this.resultcode = resultcode;
	}
	public String getResultmsg() {
		return resultmsg;
	}
	public void setResultmsg(String resultmsg) {
		this.resultmsg = resultmsg;
	}
}
