/***********************************************************************
 * Module:  RspMmsMessage.java
 * Author:  ShiHukui
 * Purpose: Defines the Class RspMmsMessage
 ***********************************************************************/

package com.aesirteam.smep.client.message;

import com.aesirteam.smep.client.MasConstants;

public class RspMmsMessage extends ResponeMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * 获取彩信错误代码对应信息
	 * 
	 * @param errCode
	 *            错误代码
	 * @return RspMmsMessage
	 */
	public static RspMmsMessage getErrorMessage(int errCode) {

		RspMmsMessage result = new RspMmsMessage();
		result.setRspCode(String.valueOf(errCode));

		switch (errCode) {
		case MasConstants.REDIS_INIT_OBJECT_ISNULL:
			result.setRspCode(MasConstants.REDIS_INIT_OBJECT_ISNULL + "");
			result.setRspMsg("初始化redis服务器为空");
			result.setRspDetail("初始化redis服务器为空");
			break;

		case MasConstants.MMS_OBJECT_ISNULL:
			result.setRspCode(MasConstants.MMS_OBJECT_ISNULL + "");
			result.setRspMsg("彩信消息对象为空");
			result.setRspDetail("彩信消息对象为空");
			break;

		case MasConstants.MMS_SUBJECT_ISNULL:
			result.setRspCode(MasConstants.MMS_SUBJECT_ISNULL + "");
			result.setRspMsg("彩信主题为空");
			result.setRspDetail("彩信主题为空");
			break;

		case MasConstants.MMS_ADJFILES_ISNULL:
			result.setRspCode(MasConstants.MMS_ADJFILES_ISNULL + "");
			result.setRspMsg("彩信附件文件列表为空");
			result.setRspDetail("彩信附件文件列表为空");
			break;

		case MasConstants.MMS_ADJFILES_NOTEXISTS:
			result.setRspCode(MasConstants.MMS_ADJFILES_NOTEXISTS + "");
			result.setRspMsg("彩信附件文件列表不存在");
			result.setRspDetail("彩信附件文件列表不存在");
			break;

		case MasConstants.MMS_DESTADDR_ISNULL:
			result.setRspCode(MasConstants.MMS_DESTADDR_ISNULL + "");
			result.setRspMsg("彩信接收手机号码为空");
			result.setRspDetail("彩信接收手机号码为空");
			break;

		case MasConstants.MMS_CORPNO_ISNULL:
			result.setRspCode(MasConstants.MMS_CORPNO_ISNULL + "");
			result.setRspMsg("企业编号为空");
			result.setRspDetail("企业编号为空");
			break;

		case MasConstants.MMS_SRCADDR_ISNULL:
			result.setRspCode(MasConstants.MMS_SRCADDR_ISNULL + "");
			result.setRspMsg("彩信始发方的地址为空");
			result.setRspDetail("彩信始发方的地址为空");
			break;

		case MasConstants.MSG_SERIALIZABLE_ERROR:
			result.setRspCode(MasConstants.MSG_SERIALIZABLE_ERROR + "");
			result.setRspMsg("彩信消息对象序列化错误");
			result.setRspDetail("彩信消息对象序列化错误");
			break;

		case MasConstants.MMS_READ_ADJFILES_EXCEPTION:
			result.setRspCode(MasConstants.MMS_READ_ADJFILES_EXCEPTION + "");
			result.setRspMsg("读取附件文件时异常");
			result.setRspDetail("读取附件文件时异常");
			break;

		case MasConstants.REQMMSMESSAGE_ISNULL:
			result.setRspCode(MasConstants.REQMMSMESSAGE_ISNULL + "");
			result.setRspDetail("ReqMmsMessage不能为空");
			result.setRspMsg("");
			break;
			
		case MasConstants.MMS_SERVICESID_ISNULL:
			result.setRspCode(MasConstants.MMS_SERVICESID_ISNULL + "");
			result.setRspMsg("业务类型为空");
			result.setRspDetail("业务类型为空");
			break;
		}
		
		return result;
	}
}