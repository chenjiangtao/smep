package com.aesirteam.smep.sms.engine;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.aesirteam.smep.sms.db.mapper.SmepMsgMtLogMapper;
import com.huawei.insa2.util.Args;

@Component("smsMtDbEngine")
public class SmsMtDbEngine extends BaseEngine<MsgMtLog> {
	protected final static Logger logger = LoggerFactory.getLogger(SmsMtDbEngine.class);
	protected static String className = SmsMtDbEngine.class.getSimpleName();
	
	@Resource(name="smepMsgMtLogMapper")
	SmepMsgMtLogMapper mapper;
		
	public SmsMtDbEngine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
	}
	
	@Override
	protected void consumeTask() {
		List<MsgMtLog> list =  super.getMessageList();
		// 无数据
		if (0 == list.size()) return;	
		/*
		for(MsgMtLog message : list) {
			//if (message.getDestTerminalId().startsWith("130"))
			System.out.println(String.format("msgid=%s, seqno=%s, corpno=%s, pkTotal=%s, pkNumber=%s, registeredDelivery=%s, msgLevel=%s, serviceId=%s, feeUsertype=%s, feeTerminalId=%s, tpPid=%s, tpUdhi=%s, msgFmt=%s, msgSrc=%s, feeType=%s, feeCode=%s, validTime=%s, atTime=%s, srcTerminalId=%s, destTerminalId=%s, msgContent=%s, reserve=%s, feeTerminalType=%s, destTerminalType=%s, linkid=%s, createor=%s, createtime=%s", 
					message.getMsgid(),
					message.getSeqno(),
					message.getCorpno(),
					message.getPkTotal(),
					message.getPkNumber(),
					message.getRegisteredDelivery(),
					message.getMsgLevel(),
					message.getServiceId(),
					message.getFeeUsertype(),
					message.getFeeTerminalId(),
					message.getTpPid(),
					message.getTpUdhi(),
					message.getMsgFmt(),
					message.getMsgSrc(),
					message.getFeeType(),
					message.getFeeCode(),
					message.getValidTime(),
					message.getAtTime(),
					message.getSrcTerminalId(),
					message.getDestTerminalId(),
					message.getMsgContent(),
					message.getReserve(),
					message.getFeeTerminalType(),
					message.getDestTerminalType(),
					message.getLinkid(),
					message.getCreateor(),
					message.getCreatetime()
			));
		}
		*/
		
		try {
			//计算流量
			fluxMonitor.incr(mapper.insertData(list));
			Thread.sleep(1000);
		} catch (Exception ex) {
			ex.printStackTrace();
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
