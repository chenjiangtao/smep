package com.aesirteam.smep.mms.engine;


import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisConnectionException;
import com.aesirteam.smep.client.message.ReqMmsMessage;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.core.ProtocolType;
import com.aesirteam.smep.util.FluxMonitor;
import com.aesirteam.smep.util.ObjectUtil;
import com.huawei.insa2.util.Args;

@Component("mmsPendingEngine")
public class MmsPendingEngine extends BaseEngine<ReqMmsMessage> {
	protected final static Logger logger = LoggerFactory.getLogger(MmsPendingEngine.class);
	protected static String className = MmsPendingEngine.class.getSimpleName();
 
	public MmsPendingEngine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
		fluxMonitor = new FluxMonitor(className, 400L, 3, true);
	}

	@Override
	protected void consumeTask() {
		StringTokenizer destAddr;
		try {
			ReqMmsMessage message =  super.getMessage();
			//无数据
			if (null == message) return;
			
			//拆分成单条短信
			destAddr = new StringTokenizer(message.getDestAddr(), params.getVal("mobileSplitStr", String.class));
			
			//发送总条数
			jedis.setSubmitMmsTotal(message.getCorpNo(), message.getSeqNo(), destAddr.countTokens());
			
			while (destAddr.hasMoreTokens()) {
				ReqMmsMessage numReq = message.clone();
				if (null == numReq) break;
				
				numReq.setDestAddr(destAddr.nextToken());
				numReq.setDestAddrTotal(1);
				
				String numReqStr = ObjectUtil.object2String(numReq, params.getVal("mms.bZip", Boolean.class));
				//根据protocalType选择发送队列
				if (ProtocolType.MM7.getValue().equalsIgnoreCase(numReq.getProtocolType())) {
					jedis.push(QUEUE_MMS_MT_MM7, numReqStr, true);
				}
			}	
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
