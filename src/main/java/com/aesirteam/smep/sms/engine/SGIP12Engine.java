package com.aesirteam.smep.sms.engine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.huawei.insa2.comm.sgip.message.SGIPDeliverMessage;
import com.huawei.insa2.comm.sgip.message.SGIPMessage;
import com.huawei.insa2.comm.sgip.message.SGIPReportMessage;
import com.huawei.insa2.comm.sgip.message.SGIPSubmitMessage;
import com.huawei.insa2.comm.sgip.message.SGIPSubmitRepMessage;
import com.huawei.insa2.util.Args;
import com.huawei.smproxy.SGIPSMProxy;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.aesirteam.smep.util.Ucs2Util;

@Component("sgip12Engine")
public class SGIP12Engine extends BaseEngine<SGIPMessage> {
	protected final static Logger logger = LoggerFactory.getLogger(SGIP12Engine.class);
	protected static String className = SGIP12Engine.class.getSimpleName();
	protected SigpSMProxy sigpSMProxy;
		
	public SGIP12Engine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
	}
	
	@Override
	protected void consumeTask() {
		SGIPMessage message =  super.getMessage();
		//无数据
		if (null == message) return;
		
		SGIPSubmitRepMessage rsp = null;
		try {
			rsp = (SGIPSubmitRepMessage) sigpSMProxy.send(message);
		} catch (IOException e) {
			logger.error(String.format("[%s SGIPSMProxy] Connection fail!!! [%d]", Thread.currentThread().getId(), cache.size()));
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
		String msgId = String.valueOf(rsp.getSrcNodeId()).concat(String.valueOf(rsp.getTimeStamp())).concat(String.valueOf(rsp.getSequenceId()));
		msgMtLog.setMsgid(msgId);
		
		if (0 == rsp.getResult()) {
			jedis.pushSubmitSmsOK(QUEUE_SMS_MT_TO_DB, msgMtLog);
		} else {
			jedis.pushSubmitSmsFAIL(QUEUE_SMS_MT_TO_DB, msgMtLog);
		}
		
		//计算流量
		fluxMonitor.incr();
	}

	protected class SigpSMProxy extends SGIPSMProxy {

		public SigpSMProxy(Args args) {
			super(args);
			startListener();
			setNeedReconnect(true);
			connect();
		}
    	
		@Override
		public SGIPMessage onDeliver(SGIPDeliverMessage msg) {
			jedis.pushDeliverSmsMo(QUEUE_SMS_MO_TO_DB, msg.getMsgMoLog());
			return super.onDeliver(msg);
		}
		
		@Override
		public SGIPMessage onReport(SGIPReportMessage msg) {
			//System.out.println(msg.getSequenceId() + " : " + msg.getSrcNodeId());
			jedis.pushDeliverSmsReport(QUEUE_SMS_RP_TO_DB, msg.getMsgMoLog());
			return super.onReport(msg);
			
		}
    }
	
	@Override
	protected boolean initSMProxy() {
		try {	
			sigpSMProxy = new SigpSMProxy(args);
			return true;
		} catch (IllegalStateException ex) {
			logger.error(String.format(" %s: %s",  ex.getMessage(), args.toString()));
		}
		return false;
	}
	
	@Override
	protected void closeSMProxy() {
		if (null != sigpSMProxy) {
			sigpSMProxy.stopListener();
			sigpSMProxy.setNeedReconnect(false);
			sigpSMProxy.close();
		}
	}
	
	@Override
	public boolean isConnected() {
		return null == sigpSMProxy.getConnState();
	}
	
	public SGIPMessage getSGIPMessage(ReqSmsMessage req) throws UnsupportedEncodingException {
		//SP的接入号码
		String SPNumber = req.getSrcAddr();
		//付费号码，手机号码前加“86”国别标志；当且仅当群发且对用户收费时为空；如果为空，则该条短消息产生的费用由UserNumber代表的用户支付；如果为全零字符串“000000000000000000000”，表示该条短消息产生的费用由SP支付
		String ChargeNumber = "000000000000000000000";
		//接收该短消息的手机号
		String UserNumber[] = { req.getDestAddr() };
		//企业代码
		String CorpId = "";
		//业务代码
		String ServiceType = args.get("serviceId", "");
		//计费类型
		int FeeType = "01".equals(args.get("feeType", "")) ? 1 : 2;
		//取值范围0-99999，该条短消息的收费值，单位为分，由SP定义对于包月制收费的用户，该值为月租费的值
		String FeeValue = (1 == FeeType) ? "00" : args.get("feeCode", "");
		//取值范围0-99999，赠送用户的话费，单位为分，由SP定义，特指由SP向用户发送广告时的赠送话费
		String GivenValue = "0";
		//代收费标志，0：应收；1：实收
		int AgentFlag = 0;
		//引起MT消息的原因 0-MO点播引起的第一条MT消息；1-MO点播引起的非第一条MT消息；2-非MO点播引起的MT消息；3-系统反馈引起的MT消息。
		int MorelatetoMTFlag = 2;
		//优先级0-9从低到高，默认为0
		int Priority = req.getPriorityLevel();
		//短消息寿命的终止时间，如果为空，表示使用短消息中心的缺省值
		Date ExpireTime = req.getVaildTime();
		//短消息定时发送的时间，如果为空，表示立刻发送该短消息。
		Date ScheduleTime = req.getAtTime();
		//状态报告标记 0-该条消息只有最后出错时要返回状态报告 1-该条消息无论最后是否成功都要返回状态报告 2-该条消息不需要返回状态报告 3-该条消息仅携带包月计费信息，不下发给用户，要返回状态报告
		int ReportFlag = req.getNeedStautsReport() == 1 ? 1 : 0;
		//GSM协议类型
		int TP_pid = 0;
		//GSM协议类型
		int TP_udhi = 0;
		//短消息的编码格式。0：纯ASCII字符串 3：写卡操作 4：二进制编码 8：UCS2编码 15: GBK编码
		int MessageCoding = 8;
		//信息类型：0-短消息信息
		int MessageType = 0;
		//int MessageLen = 0;
		//短消息内容
		byte MessageContent[] = Ucs2Util.getUcs2Bytes(req.getMsgContent()[0]);
		//保留
		String reserve = "";

		SGIPMessage message = new SGIPSubmitMessage(SPNumber, ChargeNumber,
				UserNumber, CorpId, ServiceType, FeeType, FeeValue, GivenValue,
				AgentFlag, MorelatetoMTFlag, Priority, ExpireTime,
				ScheduleTime, ReportFlag, TP_pid, TP_udhi, MessageCoding,
				MessageType, MessageContent.length, MessageContent, reserve);
		
		message.getMsgMtLog().setSeqno(req.getSeqNo());
		message.getMsgMtLog().setCorpno(req.getCorpNo());
		message.getMsgMtLog().setPkTotal(1);
		message.getMsgMtLog().setPkNumber(1);
		message.getMsgMtLog().setFeeType(args.get("feeType", ""));
		message.getMsgMtLog().setFeeCode(FeeValue);
		message.getMsgMtLog().setMsgContent(req.getMsgContent()[0]);
		message.getMsgMtLog().setCreateor(req.getCreateor());
		message.getMsgMtLog().setCreatetime(req.getCreateTime());
		message.getMsgMtLog().setProtocol(req.getProtocolType());
		return message;
	}
}
