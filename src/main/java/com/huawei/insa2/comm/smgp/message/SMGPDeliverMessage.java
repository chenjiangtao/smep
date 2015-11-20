package com.huawei.insa2.comm.smgp.message;

import com.aesirteam.smep.core.ProtocolType;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;
import com.aesirteam.smep.util.Ucs2Util;
import com.huawei.insa2.comm.smgp.SMGPConstant;
import com.huawei.insa2.util.TypeConvert;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SMGPDeliverMessage extends SMGPMessage {
	
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
	private int deliverType;
	
	public SMGPDeliverMessage(byte buf[]) throws IllegalArgumentException {
		deliverType = 0;
		deliverType = buf[14];
		//System.out.println("buf len: " + buf.length + " msglength: " + (buf[72] & 0xff));
		//System.out.println(Ucs2Util.bytesToHexString(buf));
		
		int len1 = 81 + (buf[72] & 0xff);    //正式网关 
		int len2 = 86 + (buf[72] & 0xff);    //SimpleUMS模拟网关
		if (buf.length != len1 &&  buf.length != len2)  
			throw new IllegalArgumentException(SMGPConstant.SMC_MESSAGE_ERROR);
		else {
			super.buf = (buf.length - (buf[72] & 0xff) == 81) ? new byte[len1] : new byte[len2];
			System.arraycopy(buf, 0, super.buf, 0, buf.length);
			super.sequence_Id = TypeConvert.byte2int(super.buf, 0);
		}
		
		super.msgMoLog = new MsgMoLog();
		super.msgMoLog.setMsgid(deliverType == 1 ? String.valueOf(TypeConvert.byte2long(getStatusMsgId())) : String.valueOf(TypeConvert.byte2int(getMsgId())));
		super.msgMoLog.setDestid(getDestTermID()); 
		super.msgMoLog.setTpPid(0);
		super.msgMoLog.setTpUdhi(0);
		super.msgMoLog.setMsgFmt(getMsgFormat());
		super.msgMoLog.setSrcTerminalId(getSrcTermID());
		super.msgMoLog.setRegisteredDelivery(getIsReport());
		super.msgMoLog.setMsgLength(getMsgLength());
		if (deliverType == 0) {
			if (8 == getMsgFormat())
				super.msgMoLog.setMsgContent(Ucs2Util.decodeUtf8StrFromUcs2Bytes(getMsgContent()));
			else
				try {
					String msgContent = (15 == getMsgFormat()) ? new String(getMsgContent(), "GB18030").trim() : new String(getMsgContent()).trim(); 
					super.msgMoLog.setMsgContent(msgContent);
				} catch (UnsupportedEncodingException e) {}
		} else {
			String msg = (new String(getMsgContent())).trim();
			super.msgMoLog.setStat(getStat(msg));
			super.msgMoLog.setSubmitTime(getSubmitTime(msg));
			super.msgMoLog.setDoneTime(getDoneTime(msg));
		}
		//super.msgMoLog.setReserve(getReserve());
		super.msgMoLog.setProtocol(ProtocolType.SMGP30.getValue());
	}
	
    protected String substring (String msg, String prefix, int size) {
    	int beginIndex = msg.indexOf(prefix) + prefix.length();
    	int endIndex = beginIndex + size;
    	return msg.substring(beginIndex, endIndex);
    }
    
	public byte[] getMsgId() {
		byte msgId[] = new byte[10];
		System.arraycopy(super.buf, 4, msgId, 0, 10);
		return msgId;
	}

	public int getIsReport() {
		return super.buf[14];
	}

	public int getMsgFormat() {
		return super.buf[15];
	}

	public Date getRecvTime() {
		Date date;
		try {
			byte tmpbyte[] = new byte[4];
			System.arraycopy(super.buf, 16, tmpbyte, 0, 4);
			String tmpstr = new String(tmpbyte);
			int tmpYear = Integer.parseInt(tmpstr);
			tmpbyte = new byte[2];
			System.arraycopy(super.buf, 20, tmpbyte, 0, 2);
			tmpstr = new String(tmpbyte);
			int tmpMonth = Integer.parseInt(tmpstr) - 1;
			System.arraycopy(super.buf, 22, tmpbyte, 0, 2);
			tmpstr = new String(tmpbyte);
			int tmpDay = Integer.parseInt(tmpstr);
			System.arraycopy(super.buf, 24, tmpbyte, 0, 2);
			tmpstr = new String(tmpbyte);
			int tmpHour = Integer.parseInt(tmpstr);
			System.arraycopy(super.buf, 26, tmpbyte, 0, 2);
			tmpstr = new String(tmpbyte);
			int tmpMinute = Integer.parseInt(tmpstr);
			System.arraycopy(super.buf, 28, tmpbyte, 0, 2);
			tmpstr = new String(tmpbyte);
			int tmpSecond = Integer.parseInt(tmpstr);
			Calendar calendar = Calendar.getInstance();
			calendar.set(tmpYear, tmpMonth, tmpDay, tmpHour, tmpMinute, tmpSecond);
			Date date1 = calendar.getTime();
			return date1;
		} catch (Exception e) {
			date = null;
		}
		return date;
	}

	public String getSrcTermID() {
		byte srcTermId[] = new byte[21];
		System.arraycopy(super.buf, 30, srcTermId, 0, 21);
		return (new String(srcTermId)).trim();
	}

	public String getDestTermID() {
		byte destTermId[] = new byte[21];
		System.arraycopy(super.buf, 51, destTermId, 0, 21);
		return (new String(destTermId)).trim();
	}

	public int getMsgLength() {
		return super.buf[72] & 0xff;
	}

	public byte[] getMsgContent() {
		int len = super.buf[72] & 0xff;
		byte content[] = new byte[len];
		System.arraycopy(super.buf, 73, content, 0, len);
		return content;
	}
	/*
	public String getReserve() {
		int loc = 73 + (super.buf[72] & 0xff);
		byte reserve[] = new byte[8];
		System.arraycopy(super.buf, loc, reserve, 0, 8);
		return (new String(reserve)).trim();
	}
	*/
	public byte[] getStatusMsgId() {
		if (deliverType == 1) {
			byte tmpId[] = new byte[10];
			//+3 id:
			System.arraycopy(super.buf, 73 + 3, tmpId, 0, 10);
			return tmpId;
		} else {
			return null;
		}
	}

	public Date getSubmitTime(String msgContent) {
		if (deliverType == 1) {
			String tmpStr = substring(msgContent, "submit_date:", 10);
			try {
				return dateFormat.parse(tmpStr);
			} catch (ParseException e) {}
		} 
		return null;
	}
	
	public Date getDoneTime(String msgContent) {
		if (deliverType == 1) {
			String tmpStr = substring(msgContent, "done_date:", 10);
			try {
				return dateFormat.parse(tmpStr);
			} catch (ParseException e) {}
		}
		return null;
	}
	
	public String getStat(String msgContent) {
		if (deliverType == 1) {
			return substring(msgContent, "stat:", 7);
		} 
		return null;
	}
	
	public String toString() {
		StringBuffer strBuf = new StringBuffer(600);
		strBuf.append("SMGPDeliverMessage: ");
		strBuf.append("Sequence_Id=".concat(String.valueOf(String
				.valueOf(getSequenceId()))));
		strBuf.append(",MsgID=".concat(String.valueOf(String
				.valueOf(new String(getMsgId())))));
		strBuf.append(",IsReport=".concat(String.valueOf(String
				.valueOf(getIsReport()))));
		strBuf.append(",MsgFormat=".concat(String.valueOf(String
				.valueOf(getMsgFormat()))));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		if (getRecvTime() != null)
			strBuf.append(",RecvTime=".concat(String.valueOf(String
					.valueOf(dateFormat.format(getRecvTime())))));
		else
			strBuf.append(",RecvTime=null");
		strBuf.append(",SrcTermID=".concat(String.valueOf(String
				.valueOf(getSrcTermID()))));
		strBuf.append(",DestTermID=".concat(String.valueOf(String
				.valueOf(getDestTermID()))));
		strBuf.append(",MsgLength=".concat(String.valueOf(String
				.valueOf(getMsgLength()))));
		strBuf.append(",MsgContent=".concat(String.valueOf(String
				.valueOf(new String(getMsgContent())))));
		/*strBuf.append(",reserve=".concat(String.valueOf(String
				.valueOf(getReserve()))));*/
		return strBuf.toString();
	}

	public int getRequestId() {
		return 3;
	}
}
