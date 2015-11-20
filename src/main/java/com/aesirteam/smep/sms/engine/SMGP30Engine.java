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
import com.huawei.insa2.comm.smgp.message.SMGPDeliverMessage;
import com.huawei.insa2.comm.smgp.message.SMGPMessage;
import com.huawei.insa2.comm.smgp.message.SMGPSubmitMessage;
import com.huawei.insa2.comm.smgp.message.SMGPSubmitRespMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.TypeConvert;
import com.huawei.smproxy.SMGPSMProxy;

@Component("smgp30Engine")
public class SMGP30Engine extends BaseEngine<SMGPMessage> {
	protected final static Logger logger = LoggerFactory.getLogger(SMGP30Engine.class);
	protected static String className = SMGP30Engine.class.getSimpleName();
	protected SmgpSMProxy smgpSMProxy;
		
	public SMGP30Engine () {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
	}
		
	@Override
	protected void consumeTask() {
		SMGPMessage message =  super.getMessage();
		//无数据
		if (null == message) return;
				
		SMGPSubmitRespMessage rsp = null;
		try {
			rsp = (SMGPSubmitRespMessage) smgpSMProxy.send(message);
		} catch (IOException e) {
			logger.error(String.format("[%s SMGPSMProxy] Connection fail!!! [%d]", Thread.currentThread().getId(), cache.size()));
			//发送时网关连接异常
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
		//System.out.println("smgp30: " + msgMtLog.getSeqNo() + "\t" + msgMtLog.getMsgId() + "\t" + msgMtLog.getDest_Terminal_Id() + "\t" + msgMtLog.getMsg_Content());
		if (0 == rsp.getStatus()) {
			jedis.pushSubmitSmsOK(QUEUE_SMS_MT_TO_DB, msgMtLog);
		} else {
			jedis.pushSubmitSmsFAIL(QUEUE_SMS_MT_TO_DB, msgMtLog);
		}
		
		//计算流量
		fluxMonitor.incr();
	}
	
	protected class SmgpSMProxy extends SMGPSMProxy {

		public SmgpSMProxy(Args args) {
			super(args);
			setNeedReconnect(true);
		}
		
		@Override
		public SMGPMessage onDeliver(SMGPDeliverMessage msg) {
			if (msg.getIsReport() == 1) {
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
			smgpSMProxy = new SmgpSMProxy(args);
			return true;
		} catch (IllegalStateException ex) {
			logger.error(String.format(" %s: %s",  ex.getMessage(), args.toString()));
		}
		return false;
	}
	
	@Override
	protected void closeSMProxy() {
		if (null != smgpSMProxy) {
			smgpSMProxy.setNeedReconnect(false);
			smgpSMProxy.close();
		}
	}
	
	@Override
	public boolean isConnected() {
		return null == smgpSMProxy.getConnState();
	}
	
	public SMGPMessage getSMGPMessage(ReqSmsMessage req) throws UnsupportedEncodingException {
		//短消息类型  0-MO消息（终端发给SP）；6-MT消息（SP发给终端，包括WEB上发送的点对点短消息）；7-点对点短消息； 
		int msgType = 6;
		//是否要求返回状态报告  0-不要求返回状态报告；1-要求返回状态报告；
		int needReport = req.getNeedStautsReport();
		//短消息发送优先级。0-低优先级；1-普通优先级；2-较高优先级；3-高优先级；
		int priority = 0;
		
		switch (req.getPriorityLevel()) {
		case 4: case 5: case 6:
			priority = 1;
			break;
		case 7: case 8:
			priority = 2;
			break;
		case 9:
			priority = 3;
			break;
		}
		
		//业务代码
		String serviceId = args.get("serviceId", "");
		//计费类型 00-免费，此时FixedFee和FeeCode无效；01-按条计信息费，此时FeeCode表示每条费用，FixedFee无效； 02-按包月收取信息费，此时FeeCode无效，FixedFee表示包月费用； 03-按封顶收取信息费
		String feeType = "01".equals(args.get("feeType", "")) ? "00" : "01";
		//资费代码（以分为单位）
		String feeCode = ("00".equals(feeType)) ?  "00" : args.get("feeCode", ""); 
		//封顶费（以分为单位）
		String fixedFee = "00";
		//信息格式 0：ASCII串 3：短信写卡操作 4：二进制信息 8：UCS2编码  15：含GB汉字
		int msgFormat = 8;
		//存活有效期，格式遵循SMPP3.3协议
		Date validTime = req.getVaildTime(); 
		//定时发送时间，格式遵循SMPP3.3协议
		Date atTime = req.getAtTime();
		//发送方号码   对于MT消息，SrcTermID格式为“118＋SP服务代码＋其它（可选）”，例如SP服务代码为1234时，SrcTermID可以为1181234或118123456等。
		String srcTermId = req.getSrcAddr();
		//计费用户号码 ChargeTermID为空时，如果是MT消息，则表示对被叫用户号码计费；如果是MO或点对点消息，则表示对主叫用户号码计费。 ChargeTermID为非空时，表示对计费用户号码计费。
		String chargeTermId = req.getSrcAddr();
		//短消息接收号码
		String destTermId[] = { req.getDestAddr() };
		//短消息内容
		byte msgContent[] = Ucs2Util.getUcs2Bytes(req.getMsgContent()[0]);
		//保留
		String reserve = "";
		
		SMGPMessage message = new SMGPSubmitMessage(msgType, needReport,
				priority, serviceId, feeType, feeCode, fixedFee, msgFormat,
				validTime, atTime, srcTermId, chargeTermId, destTermId,
				msgContent, reserve);
		
		message.getMsgMtLog().setSeqno(req.getSeqNo());
		message.getMsgMtLog().setCorpno(req.getCorpNo());
		message.getMsgMtLog().setPkTotal(1);
		message.getMsgMtLog().setPkNumber(1);
		message.getMsgMtLog().setTpPid(0);
		message.getMsgMtLog().setTpUdhi(0);
		message.getMsgMtLog().setFeeType(feeType);
		message.getMsgMtLog().setFeeCode(feeCode);
		message.getMsgMtLog().setMsgContent(req.getMsgContent()[0]);
		message.getMsgMtLog().setCreateor(req.getCreateor());
		message.getMsgMtLog().setCreatetime(req.getCreateTime());
		message.getMsgMtLog().setProtocol(req.getProtocolType());
		return message;
	}
}
