package com.huawei.insa2.comm.cmpp.message;

import com.huawei.insa2.comm.cmpp.CMPPConstant;
import com.huawei.insa2.util.TypeConvert;

public class CMPPSubmitRepMessage extends CMPPMessage {

	public CMPPSubmitRepMessage(byte buf[]) throws IllegalArgumentException {
		super.buf = new byte[13];
		if (buf.length != 13) {
			throw new IllegalArgumentException(CMPPConstant.SMC_MESSAGE_ERROR);
		} else {
			System.arraycopy(buf, 0, super.buf, 0, 13);
			super.sequence_Id = TypeConvert.byte2int(super.buf, 0);
		}
	}

	public byte[] getMsgId() {
		byte tmpMsgId[] = new byte[8];
		System.arraycopy(super.buf, 4, tmpMsgId, 0, 8);
		return tmpMsgId;
	}

	public int getResult() {
		return super.buf[12];
	}

	public String toString() {
		String tmpStr = "CMPP_Submit_REP: ";
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append("Sequence_Id=")
				.append(getSequenceId())));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",MsgId=").append(
				new String(getMsgId()))));
		tmpStr = String.valueOf(String.valueOf((new StringBuffer(String
				.valueOf(String.valueOf(tmpStr)))).append(",Result=").append(
				getResult())));
		return tmpStr;
	}

	public int getCommandId() {
		return 0x80000004;
	}
}