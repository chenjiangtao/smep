/***********************************************************************
 * Module:  MasInterface.java
 * Author:  ShiHukui
 * Purpose: Defines the Interface MasInterface
 ***********************************************************************/

package com.aesirteam.smep.client;


import java.util.List;
import java.util.Map;

import com.aesirteam.smep.client.message.*;
import com.aesirteam.smep.resources.IllegalContentFilter;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;

public interface MasInterface {
   
	/** 
    *  发送短信
    * @param reqSmsMessage 短信请求实体类
    * @return RspSmsMessage
    */
   RspSmsMessage sendSms(ReqSmsMessage reqSmsMessage);
   
   /**
    * 发送短信(号码文件)
    * @param reqSmsMessage 短信请求实体类
    * @return RspSmsMessage
    */
   RspSmsMessage sendSmsByFile(ReqSmsMessage reqSmsMessage);
   
   /** 
    * 发送彩信
    * @param reqMmsMessage  彩信请求实体类
    * @return RspMmsMessage
    */
   RspMmsMessage sendMms(ReqMmsMessage reqMmsMessage);
   
   /**
    * 发送彩信(号码文件)
    * @param reqMmsMessage
    * @return RspMmsMessage
    */
   RspMmsMessage sendMmsByFile(ReqMmsMessage reqMmsMessage);
   
   /**
	* 验证手机号码， 并区分出三网号段
	* @param mobiles   手机号码字符串
	* @return RspMobileBean
	*/
   RspMobileBean validateMobile(String mobiles);
   
   /**
    * 根据上传的号码文件分拣及验证后,生成服务器端的数据文件
    * @param srcFile  上传号码文件路径
    * @return RspMobileBean [rspMsg: 新文件名    rspDetail: 文件md5的验证戳]
    */
   RspMobileBean createMobileFile(String srcFile);
   
   /**
    * 验证号码文件，成功则返回文件内容
    * @param targetFile 目标号码文件
    * @return RspMobileBean
    */
   RspMobileBean validateMobileFile(String targetFile);
   
  /**
  *  查询队列运行信息
  * @param type
  * @param corpNo
  * @param seqNo
  * @return Map<QTYPE, Object>
  */
   Map<QTYPE, Object> findQueueInfo(QTYPE type, String corpNo, String seqNo);
   
   /**
    * 文本内容检查
    * @param messageContent 内容字符串
    * @return 违反规则的内容列表
    */
   List<String> checkMessageContent(String messageContent);
   
  /**
   * 获取队列信息
   * @param type
   * @param corpNo
   * @param creatorId
   * @return
   */
   Map<String, Object> getTotalRunInfo(QTYPE type,String corpNo,String creatorId);
   
   /**
    * 替换所有的空格、字头与字尾的分隔符、中段多个分隔符为单分隔符
    * @param mobiles
    * @return
    */
   String formatDestAddr(String mobiles);
   
   /**
    * 删除内容中的空字符串的分段
    * @param msgContent
    * @return
    */
   String[] formatMsgContent(String[] msgContent);
   
   //获取配置及资源
   JedisFactory getJedisFactory();
   
   SysParams getSysParams();
   
   IllegalContentFilter getIllegalContentFilter();
   
}