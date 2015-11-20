package com.aesirteam.smep.core;

public enum ProtocolType {
	
	MM7("mm7"), CMPP20("cmpp20"), CMPP30("cmpp30"), SMGP30("smgp30"), SGIP12("sgip12");

	private final String value;
	
	ProtocolType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
