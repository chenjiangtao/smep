package com.aesirteam.smep.sms.engine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.aesirteam.smep.util.Ucs2Util;
import com.huawei.insa2.comm.cmpp.message.CMPPDeliverMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitRepMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.TypeConvert;
import com.huawei.smproxy.SMProxy;

@Component("cmpp20Engine")
public class CMPP20Engine extends BaseEngine<CMPPMessage> {
	protected final static Logger logger = LoggerFactory.getLogger(CMPP20Engine.class);
	protected static String className = CMPP20Engine.class.getSimpleName();
	protected HwSMProxy hwSMProxy;
		
	public CMPP20Engine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
	}
	
	@Override
	protected void consumeTask() {
		CMPPMessage message = super.getMessage();
		//无数据
		if (null == message) return;
		
		CMPPSubmitRepMessage rsp = null;
		try {
			rsp = (CMPPSubmitRepMessage) hwSMProxy.send(message);
		} catch (IOException e) {
			logger.error(String.format("[%s SMProxy] Connection fail!!! [%d]", Thread.currentThread().getId(), cache.size()));
			producer(message);
			return;
		}
		
		//当rsp==null时，不能确定网关是否发了此条，先判定为发送失败进行重发
		if (null == rsp) {
			logger.warn(String.format("Retry send message [SequenceId: %s]", message.getSequenceId()));
			producer(message);
			return;
		}
			
		MsgMtLog msgMtLog = message.getMsgMtLog();
		msgMtLog.setMsgid(String.valueOf(TypeConvert.byte2long(rsp.getMsgId())));
		
		if (0 == rsp.getResult()) {
			jedis.pushSubmitSmsOK(QUEUE_SMS_MT_TO_DB, msgMtLog);
		} else {
			jedis.pushSubmitSmsFAIL(QUEUE_SMS_MT_TO_DB, msgMtLog);
		}
		
		//计算流量
		fluxMonitor.incr();
	}
	
	protected class HwSMProxy extends SMProxy {
				
		public HwSMProxy(Args args) {
			super(args);
			setNeedReconnect(true);
		}
				
		@Override
		public CMPPMessage onDeliver(CMPPDeliverMessage msg) {
			//System.out.println(msg.toString());
			if (msg.getRegisteredDeliver() == 1) {
				jedis.pushDeliverSmsReport(QUEUE_SMS_RP_TO_DB, msg.getMsgMoLog());
			} else {
				jedis.pushDeliverSmsMo(QUEUE_SMS_MO_TO_DB, msg.getMsgMoLog());
			}
			return super.onDeliver(msg);
		}
	}
	
	@Override
	protected boolean initSMProxy() {
		try {	
			hwSMProxy = new HwSMProxy(args);
			return true;
		} catch (IllegalStateException ex) {
			logger.error(String.format(" %s: %s",  ex.getMessage(), args.toString()));
		}
		return false;
	}
	
	@Override
	protected void closeSMProxy() {
		if (null != hwSMProxy) {
			hwSMProxy.setNeedReconnect(false);
			hwSMProxy.close();
		}
	}
	
	@Override
	public boolean isConnected() {
		return null == hwSMProxy.getConnState();
	}
	
	public CMPPMessage getCMPPMessage(ReqSmsMessage req) throws UnsupportedEncodingException {
		// 相同Msg_Id的信息总条数，从1开始
		int pk_Total = 1;
		// 相同Msg_Id的信息序号，从1开始
		int pk_Number = 1;
		// 是否要求返回状态确认报告：0：不需要 1：需要
		int registered_Delivery = req.getNeedStautsReport();
		// 信息级别
		int msg_Level = 9-req.getPriorityLevel();
		// 业务类型
		String service_Id = args.get("serviceId", "");
		// 计费用户类型字段 0：对目的终端MSISDN计费；1：对源终端MSISDN计费；2：对SP计费;
		// 3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。
		int fee_UserType = 2;
		// 被计费用户的号码，当Fee_UserType为3时该值有效，当Fee_UserType为0、1、2时该值无意义。
		String fee_Terminal_Id = "";
		// GSM协议类型
		int tp_Pid = 0;
		// GSM协议类型 长短信一般为1
		int tp_Udhi = 0;
		// 信息格式 0：ASCII串 3：短信写卡操作 4：二进制信息 8：UCS2编码 15：含GB汉字
		int msg_Fmt = 8;
		// 信息内容来源(SP_Id)
		String msg_Src = args.get("msgSrc", "");
		// 资费类别 01：对“计费用户号码”免费  02：对“计费用户号码”按条计信息费  03：对“计费用户号码”按包月收取信息费   04：对“计费用户号码”的信息费封顶  05：对“计费用户号码”的收费是由SP实现
		String fee_Type = args.get("feeType", "");
		// 资费代码（以分为单位）
		String fee_Code = args.get("feeCode", "");
		// 存活有效期，格式遵循SMPP3.3协议
		Date valid_Time = req.getVaildTime();
		// 定时发送时间，格式遵循SMPP3.3协议
		Date at_Time = req.getAtTime();
		// 源号码 SP的服务代码或前缀为服务代码的长号
		String src_Terminal_Id = req.getSrcAddr();
		// 接收短信的MSISDN号码
		String dest_Terminal_Id[] = { req.getDestAddr() };
		// 信息内容
		//System.out.println(req.getMsgContent()[0]);
		byte msg_Content[] = Ucs2Util.getUcs2Bytes(req.getMsgContent()[0]);
		//System.out.println(Ucs2Util.bytesToHexString(msg_Content));
		
		// 保留
		String reserve = "";
		
		CMPPMessage message = new CMPPSubmitMessage(pk_Total, pk_Number,
				registered_Delivery, msg_Level, service_Id, fee_UserType,
				fee_Terminal_Id, tp_Pid, tp_Udhi, msg_Fmt, msg_Src, fee_Type,
				fee_Code, valid_Time, at_Time, src_Terminal_Id,
				dest_Terminal_Id, msg_Content, reserve);
		
		message.getMsgMtLog().setSeqno(req.getSeqNo());
		message.getMsgMtLog().setCorpno(req.getCorpNo());
		message.getMsgMtLog().setMsgContent(req.getMsgContent()[0]);
		message.getMsgMtLog().setCreateor(req.getCreateor());
		message.getMsgMtLog().setCreatetime(req.getCreateTime());
		message.getMsgMtLog().setProtocol(req.getProtocolType());
		return message;
	}
}
