package com.huawei.insa2.comm.cmpp.message;

import com.aesirteam.smep.sms.db.domain.MsgMtLog;

import com.huawei.insa2.comm.cmpp.CMPPConstant;
import com.huawei.insa2.util.TypeConvert;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CMPPSubmitMessage extends CMPPMessage {
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
	private String outStr;

	public CMPPSubmitMessage(int pk_Total, int pk_Number,
			int registered_Delivery, int msg_Level, String service_Id,
			int fee_UserType, String fee_Terminal_Id, int tp_Pid, int tp_Udhi,
			int msg_Fmt, String msg_Src, String fee_Type, String fee_Code,
			Date valid_Time, Date at_Time, String src_Terminal_Id,
			String dest_Terminal_Id[], byte msg_Content[], String reserve)
			throws IllegalArgumentException {
				
		if (pk_Total < 1 || pk_Total > 255)
			throw new IllegalArgumentException(String.format("%s:%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.PK_TOTAL_ERROR));

		if (pk_Number < 1 || pk_Number > 255)
			throw new IllegalArgumentException(String.format("%s:%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.PK_NUMBER_ERROR));

		if (registered_Delivery < 0 || registered_Delivery > 1)
			throw new IllegalArgumentException(String.format("%s:%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.REGISTERED_DELIVERY_ERROR));

		if (msg_Level < 0 || msg_Level > 255)
			throw new IllegalArgumentException(String.format("%s:msg_Level%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.INT_SCOPE_ERROR));

		if (service_Id.length() > 10)
			throw new IllegalArgumentException(String.format(
					"%s:service_Id%s10", CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));

		if (fee_UserType < 0 || fee_UserType > 3)
			throw new IllegalArgumentException(String.format("%s:%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.FEE_USERTYPE_ERROR));

		if (fee_Terminal_Id.length() > 32)
			throw new IllegalArgumentException(String.format(
					"%s:fee_Terminal_Id%s32", CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));

		if (tp_Pid < 0 || tp_Pid > 255)
			throw new IllegalArgumentException(String.format("%s:tp_Pid%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.INT_SCOPE_ERROR));

		if (tp_Udhi < 0 || tp_Udhi > 255)
			throw new IllegalArgumentException(String.format("%s:tp_Udhi%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.INT_SCOPE_ERROR));

		if (msg_Fmt < 0 || msg_Fmt > 255)
			throw new IllegalArgumentException(String.format("%s:msg_Fmt%s",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.INT_SCOPE_ERROR));

		if (msg_Src.length() > 6)
			throw new IllegalArgumentException(String.format("%s:msg_Src%s6",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));

		if (fee_Type.length() > 2)
			throw new IllegalArgumentException(String.format("%s:fee_Type%s2",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));

		if (fee_Code.length() > 6)
			throw new IllegalArgumentException(String.format("%s:fee_Code%s6",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));

		if (src_Terminal_Id.length() > 21)
			throw new IllegalArgumentException(String.format(
					"%s:src_Terminal_Id%s21", CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));
		
		/*
		 * if(dest_Terminal_Id.length > 100) throw new
		 * IllegalArgumentException(String.valueOf(String.valueOf((new
		 * StringBuffer
		 * (String.valueOf(String.valueOf(CMPPConstant.SUBMIT_INPUT_ERROR
		 * )))).append
		 * (":dest_Terminal_Id").append(CMPPConstant.STRING_LENGTH_GREAT
		 * ).append("100")))); for(int i = 0; i < dest_Terminal_Id.length; i++)
		 * if(dest_Terminal_Id[i].length() > 21) throw new
		 * IllegalArgumentException(String.valueOf(String.valueOf((new
		 * StringBuffer
		 * (String.valueOf(String.valueOf(CMPPConstant.SUBMIT_INPUT_ERROR
		 * )))).append
		 * (":dest_Terminal_Id").append(CMPPConstant.STRING_LENGTH_GREAT
		 * ).append("21"))));
		 */

		if (msg_Fmt == 0) {
			if (msg_Content.length > 160)
				throw new IllegalArgumentException(String.format(
						"%s:msg_Content%s160", CMPPConstant.SUBMIT_INPUT_ERROR,
						CMPPConstant.STRING_LENGTH_GREAT));
		} else if (msg_Content.length > 140)
			throw new IllegalArgumentException(String.format(
					"%s:msg_Content%s140", CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));

		if (reserve.length() > 8)
			throw new IllegalArgumentException(String.format("%s:reserve%s8",
					CMPPConstant.SUBMIT_INPUT_ERROR,
					CMPPConstant.STRING_LENGTH_GREAT));

		int len = 138 + 21 * dest_Terminal_Id.length + msg_Content.length;
		super.buf = new byte[len];
		TypeConvert.int2byte(len, super.buf, 0);
		TypeConvert.int2byte(4, super.buf, 4);
		super.buf[20] = (byte) pk_Total;
		super.buf[21] = (byte) pk_Number;
		super.buf[22] = (byte) registered_Delivery;
		super.buf[23] = (byte) msg_Level;
		System.arraycopy(service_Id.getBytes(), 0, super.buf, 24, service_Id.length());
		super.buf[34] = (byte) fee_UserType;
		System.arraycopy(fee_Terminal_Id.getBytes(), 0, super.buf, 35, fee_Terminal_Id.length());
		super.buf[56] = (byte) tp_Pid;
		super.buf[57] = (byte) tp_Udhi;
		super.buf[58] = (byte) msg_Fmt;
		System.arraycopy(msg_Src.getBytes(), 0, super.buf, 59, msg_Src.length());
		System.arraycopy(fee_Type.getBytes(), 0, super.buf, 65, fee_Type.length());
		System.arraycopy(fee_Code.getBytes(), 0, super.buf, 67, fee_Code.length());
		if (valid_Time != null) {
			String tmpTime = dateFormat.format(valid_Time).concat("032+");
			System.arraycopy(tmpTime.getBytes(), 0, super.buf, 73, 16);
		}
		if (at_Time != null) {
			String tmpTime = dateFormat.format(at_Time).concat("032+");
			System.arraycopy(tmpTime.getBytes(), 0, super.buf, 90, 16);
		}
		System.arraycopy(src_Terminal_Id.getBytes(), 0, super.buf, 107, src_Terminal_Id.length());
		super.buf[128] = (byte) dest_Terminal_Id.length;
		int i = 0;
		for (i = 0; i < dest_Terminal_Id.length; i++)
			System.arraycopy(dest_Terminal_Id[i].getBytes(), 0, super.buf, 129 + i * 21, dest_Terminal_Id[i].length());

		int loc = 129 + i * 21;
		super.buf[loc] = (byte) msg_Content.length;
		System.arraycopy(msg_Content, 0, super.buf, loc + 1, msg_Content.length);
		loc = loc + 1 + msg_Content.length;
		System.arraycopy(reserve.getBytes(), 0, super.buf, loc, reserve.length());
		
		super.msgMtLog = new MsgMtLog();
		super.msgMtLog.setPkTotal(pk_Total);
		super.msgMtLog.setPkNumber(pk_Number);
		super.msgMtLog.setRegisteredDelivery(registered_Delivery);
		super.msgMtLog.setMsgLevel(msg_Level);
		super.msgMtLog.setServiceId(service_Id);
		super.msgMtLog.setFeeUsertype(fee_UserType);
		//super.msgMtLog.setFeeTerminalId(fee_Terminal_Id);
		super.msgMtLog.setTpPid(tp_Pid);
		super.msgMtLog.setTpUdhi(tp_Udhi);
		super.msgMtLog.setMsgFmt(msg_Fmt);
		super.msgMtLog.setMsgSrc(msg_Src);
		super.msgMtLog.setFeeType(fee_Type);
		super.msgMtLog.setFeeCode(fee_Code);
		super.msgMtLog.setValidTime(valid_Time);
		super.msgMtLog.setAtTime(at_Time);
		super.msgMtLog.setSrcTerminalId(src_Terminal_Id);
		super.msgMtLog.setDestTerminalId(dest_Terminal_Id[0]);
	}
	
	@Override
	public String toString() {
		String tmpStr = "CMPP_Submit: ";
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append("Sequence_Id=")
				.append(getSequenceId())));
		tmpStr = String.valueOf(tmpStr) + String.valueOf(outStr);
		return tmpStr;
	}
	
	@Override
	public int getCommandId() {
		return 4;
	}
}