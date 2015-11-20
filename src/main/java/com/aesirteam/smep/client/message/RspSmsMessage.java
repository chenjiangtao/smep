/***********************************************************************
 * Module:  RspSmsMessage.java
 * Author:  ShiHukui
 * Purpose: Defines the Class RspSmsMessage
 ***********************************************************************/

package com.aesirteam.smep.client.message;

import com.aesirteam.smep.client.MasConstants;

public class RspSmsMessage extends ResponeMessage {

	private static final long serialVersionUID = 1L;

	public static RspSmsMessage getErrorMessage(int resultCode) {

		RspSmsMessage result = new RspSmsMessage();
		result.setRspCode(String.valueOf(resultCode));
		
		switch (resultCode) {
		
		case MasConstants.REDIS_INIT_OBJECT_ISNULL: // REDIS_INIT_OBJECT_ISNULL
			result.setRspCode(MasConstants.REDIS_INIT_OBJECT_ISNULL + "");
			result.setRspMsg("初始化redis服务器 为空");
			result.setRspDetail("");
			break;

		case MasConstants.MESSAGE_INIT_OBJECT_ISNULL: //
			result.setRspCode(MasConstants.MESSAGE_INIT_OBJECT_ISNULL + "");
			result.setRspMsg("消息对象不能 为空值");
			result.setRspDetail("");
			break;

		case MasConstants.MESSAGE_CONVERT_EXCEPTION:
			result.setRspCode(MasConstants.MESSAGE_CONVERT_EXCEPTION + "");
			result.setRspMsg("消息对象转换错误");
			result.setRspDetail("");
			break;

		case MasConstants.SMS_SEQNO_ISNULL:
			result.setRspCode(MasConstants.SMS_SEQNO_ISNULL + "");
			result.setRspMsg("消息流水号为空");
			result.setRspDetail("消息流水号为空");
			break;

		case MasConstants.SMS_CORPNO_ISNULL:
			result.setRspCode(MasConstants.SMS_CORPNO_ISNULL + "");
			result.setRspMsg("企业编号为空");
			result.setRspDetail("企业编号为空");
			break;

		case MasConstants.SMS_CONTENT_ISNULL:
			result.setRspCode(MasConstants.SMS_CONTENT_ISNULL + "");
			result.setRspMsg("短信内容为空");
			result.setRspDetail("短信内容为空");
			break;

		case MasConstants.SMS_DESTADDR_ISINVALID:
			result.setRspCode(MasConstants.SMS_DESTADDR_ISINVALID + "");
			result.setRspMsg("短信号码格式不正确");
			result.setRspDetail("短信号码格式不正确");
			break;

		case MasConstants.SMS_DESTADDR_ISNULL:
			result.setRspCode(MasConstants.SMS_DESTADDR_ISNULL + "");
			result.setRspMsg("短信号码为空");
			result.setRspDetail("短信号码为空");
			break;

		case MasConstants.SMS_MESSAGE_ISNULL:
			result.setRspCode(String.valueOf(MasConstants.SMS_MESSAGE_ISNULL));
			result.setRspMsg("消息体为空");
			result.setRspDetail("消息体为空");
			break;

		case MasConstants.SMS_SERVICESID_ISNULL:
			result.setRspCode(MasConstants.SMS_SERVICESID_ISNULL + "");
			result.setRspMsg("业务类型为空");
			result.setRspDetail("业务类型为空");
			break;

		case MasConstants.SUBMIT_TO_REDIS_MAXLIMIT:
			result.setRspCode(MasConstants.SUBMIT_TO_REDIS_MAXLIMIT + "");
			result.setRspDetail("短信发送繁忙,请稍后发送");
			result.setRspMsg("短信发送繁忙,请稍后发送");
			break;

		case MasConstants.REQSMSMESSAGE_ISNULL:
			result.setRspCode(MasConstants.REQSMSMESSAGE_ISNULL + "");
			result.setRspDetail("ReqSmsMessage不能为空");
			result.setRspMsg("");
			break;
		}
		
		return result;
	}
}