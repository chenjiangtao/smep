package com.huawei.insa2.comm.sgip.message;

import java.io.UnsupportedEncodingException;

import com.aesirteam.smep.core.ProtocolType;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;
import com.aesirteam.smep.util.Ucs2Util;
import com.huawei.insa2.comm.sgip.SGIPConstant;
import com.huawei.insa2.util.TypeConvert;

public class SGIPDeliverMessage extends SGIPMessage {

	public SGIPDeliverMessage(byte buf[]) throws IllegalArgumentException {
		int len = TypeConvert.byte2int(buf, 57);
		len = 69 + len;
		if (buf.length != len) {
			throw new IllegalArgumentException(SGIPConstant.SMC_MESSAGE_ERROR);
		} else {
			super.buf = new byte[len];
			System.arraycopy(buf, 0, super.buf, 0, buf.length);
			super.src_node_Id = TypeConvert.byte2int(super.buf, 0);
			super.time_Stamp = TypeConvert.byte2int(super.buf, 4);
			super.sequence_Id = TypeConvert.byte2int(super.buf, 8);
		}
		
		//System.out.println("mo:\r\n" + Ucs2Util.bytesToHexString(super.buf));
		
		super.msgMoLog = new MsgMoLog();
		String msgId = String.format("%d%d%d", super.src_node_Id, super.time_Stamp, super.sequence_Id);	
		super.msgMoLog.setMsgid(msgId);
		super.msgMoLog.setDestid(getSPNumber()); 
		super.msgMoLog.setTpPid(getTpPid());
		super.msgMoLog.setTpUdhi(getTpUdhi());
		super.msgMoLog.setMsgFmt(getMsgFmt());
		super.msgMoLog.setSrcTerminalId(getUserNumber());
		super.msgMoLog.setRegisteredDelivery(0);
		super.msgMoLog.setMsgLength(getMsgLength());
		if (8 == getMsgFmt())
			super.msgMoLog.setMsgContent(Ucs2Util.decodeUtf8StrFromUcs2Bytes(getMsgContent()));
		else
			try {
				String msgContent = (15 == getMsgFmt()) ? new String(getMsgContent(), "GB18030").trim() : new String(getMsgContent()).trim(); 
				super.msgMoLog.setMsgContent(msgContent);
			} catch (UnsupportedEncodingException e) {}
		super.msgMoLog.setReserve(getReserve());
		super.msgMoLog.setProtocol(ProtocolType.SGIP12.getValue());
	}
		
	public String getUserNumber() {
		byte tmpId[] = new byte[21];
		System.arraycopy(super.buf, 12, tmpId, 0, 21);
		String tmpStr = (new String(tmpId)).trim();
		if (tmpStr.indexOf('\0') >= 0)
			return tmpStr.substring(0, tmpStr.indexOf('\0'));
		else
			return tmpStr;
	}

	public String getSPNumber() {
		byte tmpId[] = new byte[21];
		System.arraycopy(super.buf, 33, tmpId, 0, 21);
		String tmpStr = (new String(tmpId)).trim();
		if (tmpStr.indexOf('\0') >= 0)
			return tmpStr.substring(0, tmpStr.indexOf('\0'));
		else
			return tmpStr;
	}

	public int getTpPid() {
		int tmpId = super.buf[54];
		return tmpId;
	}

	public int getTpUdhi() {
		int tmpId = super.buf[55];
		return tmpId;
	}

	public int getMsgFmt() {
		int tmpFmt = super.buf[56];
		return tmpFmt;
	}

	public int getMsgLength() {
		return TypeConvert.byte2int(super.buf, 57);
	}

	public byte[] getMsgContent() {
		int len = getMsgLength();
		byte tmpContent[] = new byte[len];
		System.arraycopy(super.buf, 61, tmpContent, 0, len);
		return tmpContent;
	}

	public String getReserve() {
		int loc = 61 + getMsgLength();
		byte tmpReserve[] = new byte[8];
		System.arraycopy(super.buf, loc, tmpReserve, 0, 8);
		return (new String(tmpReserve)).trim();
	}

	public String toString() {
		String tmpStr = "SGIP_DELIVER: ";
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append("Sequence_Id=")
				.append(getSequenceId())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",UserNumber=")
				.append(getUserNumber())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",SPNumber=").append(
				getSPNumber())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",TpPid=").append(
				getTpPid())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",TpUdhi=").append(
				getTpUdhi())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",MsgFmt=").append(
				getMsgFmt())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",MsgLength=")
				.append(getMsgLength())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",MsgContent=")
				.append(new String(getMsgContent()))));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",Reserve=").append(
				getReserve())));
		return tmpStr;
	}

	public int getCommandId() {
		return 4;
	}
}
