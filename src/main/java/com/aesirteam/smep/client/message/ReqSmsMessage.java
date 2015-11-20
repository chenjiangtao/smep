package com.aesirteam.smep.client.message;

public class ReqSmsMessage extends RequestMessage implements Cloneable {

	private static final long serialVersionUID = 1L;
	
	/** 短信内容(分段)*/
	private String[] msgContent;
	
	/** 短信内容已进行拆分*/
	private boolean split = false;
	
	/** 资费类型  01-免费  02-按条计费 */
	//private String feeType;
	
	/** 资费代码  (以分为单位) */
	//private String feeCode;
	
	/** cmpp3 linkId */
	private String linkId;
		
	public String[] getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String[] msgContent) {
		this.msgContent = msgContent;
	}

	public boolean isSplit() {
		return split;
	}

	public void setSplit(boolean split) {
		this.split = split;
	}
	/*
	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}
	*/
	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
	
	@Override
	public ReqSmsMessage clone() throws CloneNotSupportedException {
		ReqSmsMessage req = (ReqSmsMessage) super.clone(); 
		req.setDestAddr(null);
		req.setMsgContent(null);
		return req;
	}
}
