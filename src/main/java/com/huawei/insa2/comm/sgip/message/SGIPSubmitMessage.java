package com.huawei.insa2.comm.sgip.message;

import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.huawei.insa2.comm.sgip.SGIPConstant;
import com.huawei.insa2.util.TypeConvert;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SGIPSubmitMessage extends SGIPMessage {
	
	protected final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
	
	public SGIPSubmitMessage(String SPNumber, String ChargeNumber,
			String UserNumber[], String CorpId, String ServiceType,
			int FeeType, String FeeValue, String GivenValue, int AgentFlag,
			int MorelatetoMTFlag, int Priority, Date ExpireTime,
			Date ScheduleTime, int ReportFlag, int TP_pid, int TP_udhi,
			int MessageCoding, int MessageType, int MessageLen,
			byte MessageContent[], String reserve)
			throws IllegalArgumentException {
		
		if (SPNumber.length() > 21)
			throw new IllegalArgumentException(String.format("%s:SPNumber%s21",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.STRING_LENGTH_GREAT));

		if (ChargeNumber.length() > 21)
			throw new IllegalArgumentException(String.format("%s:ChargeNumber%s21",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.STRING_LENGTH_GREAT));
		/*
		if (UserNumber.length > 100)
			throw new IllegalArgumentException(String.valueOf(String
					.valueOf((new StringBuffer(String.valueOf(String
							.valueOf(SGIPConstant.SUBMIT_INPUT_ERROR))))
							.append(":UserNumber")
							.append(SGIPConstant.STRING_LENGTH_GREAT)
							.append("100"))));
		for (int i = 0; i < UserNumber.length; i++)
			if (UserNumber[i].length() > 21)
				throw new IllegalArgumentException(String.valueOf(String
						.valueOf((new StringBuffer(String.valueOf(String
								.valueOf(SGIPConstant.SUBMIT_INPUT_ERROR))))
								.append(":UserNumber[").append(i).append("]")
								.append(SGIPConstant.STRING_LENGTH_GREAT)
								.append("21"))));
		*/
		
		if (CorpId.length() > 5)
			throw new IllegalArgumentException(String.format("%s:CorpId%s5",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.STRING_LENGTH_GREAT));
		
		if (ServiceType.length() > 10)
			throw new IllegalArgumentException(String.format("%s:ServiceType%s10",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.STRING_LENGTH_GREAT));

		if (FeeType < 0 || FeeType > 255)
			throw new IllegalArgumentException(String.format("%s:FeeType%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));
		
		if (FeeValue.length() > 6)
			throw new IllegalArgumentException(String.format("%s:FeeValue%s6",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.STRING_LENGTH_GREAT));

		if (GivenValue.length() > 6)
			throw new IllegalArgumentException(String.format("%s:GivenValue%s6",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.STRING_LENGTH_GREAT));
		
		if (AgentFlag < 0 || AgentFlag > 255)
			throw new IllegalArgumentException(String.format("%s:AgentFlag%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));
		
		if (MorelatetoMTFlag < 0 || MorelatetoMTFlag > 255)
			throw new IllegalArgumentException(String.format("%s:MorelatetoMTFlag%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));
		
		if (Priority < 0 || Priority > 255)
			throw new IllegalArgumentException(String.format("%s:Priority%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));

		if (ReportFlag < 0 || ReportFlag > 255)
			throw new IllegalArgumentException(String.format("%s:ReportFlag%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));

		if (TP_pid < 0 || TP_pid > 255)
			throw new IllegalArgumentException(String.format("%s:TP_pid%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));

		if (TP_udhi < 0 || TP_udhi > 255)
			throw new IllegalArgumentException(String.format("%s:TP_udhi%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));

		if (MessageCoding < 0 || MessageCoding > 255)
			throw new IllegalArgumentException(String.format("%s:MessageCoding%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));
			
		if (MessageType < 0 || MessageType > 255)
			throw new IllegalArgumentException(String.format("%s:MessageType%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));
		
		if (MessageLen < 0 || MessageLen > 160)
			throw new IllegalArgumentException(String.format("%:MessageLen%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));

		if (MessageContent.length > 160)
			throw new IllegalArgumentException(String.format("%:MessageContent%s",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.INT_SCOPE_ERROR));

		if (reserve.length() > 8)
			throw new IllegalArgumentException(String.format("%s:reserve%s8",
					SGIPConstant.SUBMIT_INPUT_ERROR,
					SGIPConstant.STRING_LENGTH_GREAT));
			
		int len = 143 + 21 * UserNumber.length + MessageLen;
		super.buf = new byte[len];
		TypeConvert.int2byte(len, super.buf, 0);
		TypeConvert.int2byte(3, super.buf, 4);
		System.arraycopy(SPNumber.getBytes(), 0, super.buf, 20, SPNumber.length());
		System.arraycopy(ChargeNumber.getBytes(), 0, super.buf, 41, ChargeNumber.length());
		super.buf[62] = (byte) UserNumber.length;
		int i = 0;
		for (i = 0; i < UserNumber.length; i++)
			System.arraycopy(UserNumber[i].getBytes(), 0, super.buf, 63 + i * 21, UserNumber[i].length());

		int loc = 63 + i * 21;
		System.arraycopy(CorpId.getBytes(), 0, super.buf, loc, CorpId.length());
		loc += 5;
		System.arraycopy(ServiceType.getBytes(), 0, super.buf, loc, ServiceType.length());
		loc += 10;
		super.buf[loc++] = (byte) FeeType;
		System.arraycopy(FeeValue.getBytes(), 0, super.buf, loc, FeeValue.length());
		loc += 6;
		System.arraycopy(GivenValue.getBytes(), 0, super.buf, loc, GivenValue.length());
		loc += 6;
		super.buf[loc++] = (byte) AgentFlag;
		super.buf[loc++] = (byte) MorelatetoMTFlag;
		super.buf[loc++] = (byte) Priority;
		
		if (ExpireTime != null) {
			String tmpTime = dateFormat.format(ExpireTime).concat("032+");
			System.arraycopy(tmpTime.getBytes(), 0, super.buf, loc, 16);
		}
		loc += 16;
		if (ScheduleTime != null) {
			String tmpTime = dateFormat.format(ScheduleTime).concat("032+");
			System.arraycopy(tmpTime.getBytes(), 0, super.buf, loc, 16);
		}
		loc += 16;
		super.buf[loc++] = (byte) ReportFlag;
		super.buf[loc++] = (byte) TP_pid;
		super.buf[loc++] = (byte) TP_udhi;
		super.buf[loc++] = (byte) MessageCoding;
		super.buf[loc++] = (byte) MessageType;
		TypeConvert.int2byte(MessageLen, super.buf, loc);
		loc += 4;
		System.arraycopy(MessageContent, 0, super.buf, loc, MessageLen);
		loc += MessageLen;
		System.arraycopy(reserve.getBytes(), 0, super.buf, loc, reserve.length());
	
		super.msgMtLog = new MsgMtLog();
		super.msgMtLog.setRegisteredDelivery(ReportFlag == 1 ? 1 : 0);
		super.msgMtLog.setMsgLevel(Priority);
		super.msgMtLog.setServiceId(ServiceType);
		super.msgMtLog.setFeeTerminalId(ChargeNumber);
		super.msgMtLog.setTpPid(TP_pid);
		super.msgMtLog.setTpUdhi(TP_udhi);
		super.msgMtLog.setMsgFmt(MessageCoding);
		super.msgMtLog.setValidTime(ExpireTime);
		super.msgMtLog.setAtTime(ScheduleTime);
		super.msgMtLog.setSrcTerminalId(SPNumber);
		super.msgMtLog.setDestTerminalId(UserNumber[0]);
	}

	public String toString() {
		String tmpStr = "SGIP_Submit: ";
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append("Sequence_Id=")
				.append(getSequenceId())));
		tmpStr = String.valueOf(tmpStr) + String.valueOf(outStr);
		return tmpStr;
	}

	public int getCommandId() {
		return 3;
	}

	private String outStr;
}
