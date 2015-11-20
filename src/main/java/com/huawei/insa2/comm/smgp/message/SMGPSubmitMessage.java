package com.huawei.insa2.comm.smgp.message;

import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.huawei.insa2.comm.smgp.SMGPConstant;
import com.huawei.insa2.util.TypeConvert;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SMGPSubmitMessage extends SMGPMessage {
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
	private StringBuffer strBuf;

	public SMGPSubmitMessage(int msgType, int needReport, int priority,
			String serviceId, String feeType, String feeCode, String fixedFee,
			int msgFormat, Date validTime, Date atTime, String srcTermId,
			String chargeTermId, String destTermId[], byte msgContent[],
			String reserve) throws IllegalArgumentException {
		
		if (msgType < 0 || msgType > 255)
			throw new IllegalArgumentException(String.format("%s:MsgType%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.INT_SCOPE_ERROR));
		
		if (needReport < 0 || needReport > 255)
			throw new IllegalArgumentException(String.format("%s:NeedReport%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.INT_SCOPE_ERROR));
			
		if (priority < 0 || priority > 9)
			throw new IllegalArgumentException(String.format("%s:%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.PRIORITY_ERROR));
		
		if (serviceId == null)
			throw new IllegalArgumentException(String.format("%s:ServiceID%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));

		if (serviceId.length() > 10)
			throw new IllegalArgumentException(String.format("%s:ServiceID%s10",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));

		if (feeType == null)
			throw new IllegalArgumentException(String.format("%s:FeeType%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));
		
		if (feeType.length() > 2)
			throw new IllegalArgumentException(String.format("%s:FeeType%s2",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));

		if (feeCode == null)
			throw new IllegalArgumentException(String.format("%s:FeeCode%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));

		if (feeCode.length() > 6)
			throw new IllegalArgumentException(String.format("%s:FeeCode%s6",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));

		if (fixedFee == null)
			throw new IllegalArgumentException(String.format("%s:FixedFee%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));
		
		if (fixedFee.length() > 6)
			throw new IllegalArgumentException(String.format("%s:FixedFee%s6",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));

		if (msgFormat < 0 || msgFormat > 255)
			throw new IllegalArgumentException(String.format("%s:MsgFormat%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.INT_SCOPE_ERROR));

		if (srcTermId == null)
			throw new IllegalArgumentException(String.format("%s:SrcTermID%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));
		
		if (srcTermId.length() > 21)
			throw new IllegalArgumentException(String.format("%s:SrcTermID%s21",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));

		if (chargeTermId == null)
			throw new IllegalArgumentException(String.format("%s:ChargeTermID%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));

		if (chargeTermId.length() > 21)
			throw new IllegalArgumentException(String.format("%s:ChargeTermID%s21",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));
		/*
		if (destTermId == null)
			throw new IllegalArgumentException(String.format("%s:DestTermID%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));

		if (destTermId.length > 100)
			throw new IllegalArgumentException(
					String.valueOf(String.valueOf((new StringBuffer(String
							.valueOf(String
									.valueOf(SMGPConstant.SUBMIT_INPUT_ERROR))))
							.append(":").append(SMGPConstant.DESTTERMID_ERROR))));
		for (int i = 0; i < destTermId.length; i++)
			if (destTermId[i].length() > 21)
				throw new IllegalArgumentException(String.valueOf(String
						.valueOf((new StringBuffer(String.valueOf(String
								.valueOf(SMGPConstant.SUBMIT_INPUT_ERROR))))
								.append(":one DestTermID ")
								.append(SMGPConstant.STRING_LENGTH_GREAT)
								.append("21"))));
		*/
		
		if (msgContent == null)
			throw new IllegalArgumentException(String.format("%s:MsgContent%s",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_NULL));
		
		if (msgContent.length > 252)
			throw new IllegalArgumentException(String.format("%s:MsgContent%s252",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));
		
		if (reserve != null && reserve.length() > 8)
			throw new IllegalArgumentException(String.format("%s:reserve%s8",
					SMGPConstant.SUBMIT_INPUT_ERROR,
					SMGPConstant.STRING_LENGTH_GREAT));
			
		int len = 126 + 21 * destTermId.length + msgContent.length;
		super.buf = new byte[len];
		TypeConvert.int2byte(len, super.buf, 0);
		TypeConvert.int2byte(2, super.buf, 4);
		super.buf[12] = (byte) msgType;
		super.buf[13] = (byte) needReport;
		super.buf[14] = (byte) priority;
		System.arraycopy(serviceId.getBytes(), 0, super.buf, 15, serviceId.length());
		System.arraycopy(feeType.getBytes(), 0, super.buf, 25, feeType.length());
		System.arraycopy(feeCode.getBytes(), 0, super.buf, 27, feeCode.length());
		System.arraycopy(fixedFee.getBytes(), 0, super.buf, 33, fixedFee.length());
		super.buf[39] = (byte) msgFormat;
		if (validTime != null) {
			String tmpTime = dateFormat.format(validTime).concat("032+");
			System.arraycopy(tmpTime.getBytes(), 0, super.buf, 40, 16);
		}
		if (atTime != null) {
			String tmpTime = dateFormat.format(atTime).concat("032+");
			System.arraycopy(tmpTime.getBytes(), 0, super.buf, 57, 16);
		}
		System.arraycopy(srcTermId.getBytes(), 0, super.buf, 74, srcTermId.length());
		System.arraycopy(chargeTermId.getBytes(), 0, super.buf, 95, chargeTermId.length());
		super.buf[116] = (byte) destTermId.length;
		int i = 0;
		for (i = 0; i < destTermId.length; i++)
			System.arraycopy(destTermId[i].getBytes(), 0, super.buf, 117 + i * 21, destTermId[i].length());

		int loc = 117 + i * 21;
		//super.buf[loc] = (byte) msgContent.length();
		super.buf[loc] = (byte) msgContent.length;
		//System.arraycopy(msgContent.getBytes(), 0, super.buf, loc + 1, msgContent.length());
		System.arraycopy(msgContent, 0, super.buf, loc + 1, msgContent.length);
		//loc = loc + 1 + msgContent.length();
		loc = loc + 1 + msgContent.length;
		if (reserve != null)
			System.arraycopy(reserve.getBytes(), 0, super.buf, loc, reserve.length());
		
		super.msgMtLog = new MsgMtLog();
		super.msgMtLog.setRegisteredDelivery(needReport);
		super.msgMtLog.setServiceId(serviceId);
		super.msgMtLog.setMsgLevel(priority);
		super.msgMtLog.setFeeTerminalId(chargeTermId);
		super.msgMtLog.setMsgFmt(msgFormat);
		super.msgMtLog.setFeeType(feeType);
		super.msgMtLog.setFeeCode(feeCode);
		super.msgMtLog.setValidTime(validTime);
		super.msgMtLog.setAtTime(atTime);
		super.msgMtLog.setSrcTerminalId(srcTermId);
		super.msgMtLog.setDestTerminalId(destTermId[0]);
	}

	public String toString() {
		StringBuffer outBuf = new StringBuffer(600);
		outBuf.append("SMGPSubmitMessage: ");
		outBuf.append("PacketLength=".concat(String.valueOf(String.valueOf(super.buf.length))));
		outBuf.append(",RequestID=2");
		outBuf.append(",SequenceID=".concat(String.valueOf(String.valueOf(getSequenceId()))));
		if (strBuf != null)
			outBuf.append(strBuf.toString());
		return outBuf.toString();
	}

	public int getRequestId() {
		return 2;
	}
}
