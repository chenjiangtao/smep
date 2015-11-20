package com.aesirteam.smep.sms.engine;

import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisConnectionException;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.core.ProtocolType;
import com.aesirteam.smep.util.ObjectUtil;
import com.huawei.insa2.util.Args;

@Component("smsPendingEngine")
public class SmsPendingEngine extends BaseEngine<ReqSmsMessage> {
	protected final static Logger logger = LoggerFactory.getLogger(SmsPendingEngine.class);
	protected static String className = SmsPendingEngine.class.getSimpleName();
		
	public SmsPendingEngine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
	}
	
	@Override
	protected void consumeTask() {
		StringTokenizer destAddr;
		try {
			ReqSmsMessage message =  super.getMessage();
			//无数据
			if (null == message) return;
			
			//拆分成单条短信
			destAddr = new StringTokenizer(message.getDestAddr(), params.getVal("mobileSplitStr", String.class));
			String[] msgContentArray = message.getMsgContent();
			
			//发送总条数
			jedis.setSubmitSmsTotal(message.getCorpNo(), message.getSeqNo(), destAddr.countTokens() * msgContentArray.length);
			
			while (destAddr.hasMoreTokens()) {
				ReqSmsMessage numReq = message.clone();
				if (null == numReq) break;
				
				numReq.setDestAddr(destAddr.nextToken());
				numReq.setDestAddrTotal(1);
				
				//拆分短信内容分段
				for(String content : msgContentArray) {
					String[] msgContent = { content };
					numReq.setMsgContent(msgContent);
					numReq.setSplit(false);
										
					String numReqStr = ObjectUtil.object2String(numReq, params.getVal("sms.bZip", Boolean.class));
					
					//根据protocalType选择发送队列
					if (ProtocolType.CMPP20.getValue().equalsIgnoreCase(numReq.getProtocolType())) {
						jedis.push(QUEUE_SMS_MT_CMPP20, numReqStr, true);
					} else if (ProtocolType.CMPP30.getValue().equalsIgnoreCase(numReq.getProtocolType())) {
						jedis.push(QUEUE_SMS_MT_CMPP30, numReqStr, true);
					} else if (ProtocolType.SMGP30.getValue().equalsIgnoreCase(numReq.getProtocolType())) {
						jedis.push(QUEUE_SMS_MT_SMGP30, numReqStr, true);
					} else if (ProtocolType.SGIP12.getValue().equalsIgnoreCase(numReq.getProtocolType())) {
						jedis.push(QUEUE_SMS_MT_SGIP12, numReqStr, true);
					}
					
					//计算流量
					fluxMonitor.incr();
				}
			} //end .. while (destAddr.hasMoreTokens())
		} catch (JedisConnectionException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			destAddr = null;
		}
	}
	
	@Override
	protected boolean initSMProxy() {
		return true;
	}
	
	@Override
	protected void closeSMProxy() {}

	@Override
	protected boolean isConnected() {
		return true;
	}
}
