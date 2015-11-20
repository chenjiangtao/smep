/***********************************************************************
 * Module:  MasConstants.java
 * Author:  ShiHukui
 * Purpose: Defines the Class MasConstants
 ***********************************************************************/

package com.aesirteam.smep.client;

public final class MasConstants {

	public static final int UNKNOWN_EXCEPTION = -100;
	public static final int REQSMSMESSAGE_ISNULL = -101;
	public static final int REQMMSMESSAGE_ISNULL = -102;
	public static final int REDIS_INIT_OBJECT_ISNULL = -1;
	public static final int MESSAGE_INIT_OBJECT_ISNULL = -10;
	public static final int MESSAGE_CONVERT_EXCEPTION = -11;

	public static final int SUBMIT_TO_REDIS_SUCCESS = 0; // 短彩信提交成功
	public static final int SUBMIT_TO_REDIS_FAIL = -2; // 短彩信提交至Redis失败
	public static final int SUBMIT_TO_REDIS_MAXLIMIT = -3; // 插入redis队列最大数
	public static final int SUBMIT_TO_REDIS_STALIMIT = -4; // 插入redis饱和数
															// 饱和数为最大数80%
	public static final int SMS_SEQNO_ISNULL = -1000;//
	public static final int SMS_CORPNO_ISNULL = -1001;
	public static final int SMS_SERVICESID_ISNULL = -1002;
	public static final int SMS_DESTADDR_ISNULL = -1003;
	public static final int SMS_DESTADDR_ISINVALID = -1004;
	public static final int SMS_CONTENT_ISNULL = -1005;
	public static final int SMS_MESSAGE_ISNULL = -1006;
	// public static final int SMS

	public static final int MMS_OBJECT_ISNULL = -2000; // 彩信消息对象为空
	public static final int MMS_SUBJECT_ISNULL = -2001; // 彩信主题为空
	public static final int MMS_ADJFILES_ISNULL = -2002; // 彩信文件为空
	public static final int MMS_ADJFILES_NOTEXISTS = -2003; // 彩信文件不存在
	public static final int MMS_DESTADDR_ISNULL = -2004; // 彩信待发送电话号码为空
	public static final int MMS_SRCADDR_ISNULL = -2005; // 彩信MM始发方的地址为空
	public static final int MMS_CORPNO_ISNULL = -2006; // 彩信企业编号为空
	public static final int MMS_READ_ADJFILES_EXCEPTION = -2007; // 读取附件文件时异常
	public static final int MMS_SERVICESID_ISNULL = -2008;
	public static final int MSG_SERIALIZABLE_ERROR = -2020; // 彩信消息序列化失败

	public static final int MOBILE_VALIDATE_SUCCESS = 0; // 手机号码验证通过
	public static final int INIT_MOBILE_VALIDATE_RULE_FAIL = -3000; // 初始化手机号码验证规则失败
	public static final int MOBILE_IS_NULL = -3001; // 待验证手机号码为空
}
