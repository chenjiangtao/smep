package com.aesirteam.smep.sms.engine;


import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;
import com.aesirteam.smep.sms.db.mapper.SmepMsgReportLogMapper;
import com.huawei.insa2.util.Args;

@Component("smsReportDbEngine")
public class SmsReportDbEngine extends BaseEngine<MsgMoLog> {
	protected final static Logger logger = LoggerFactory.getLogger(SmsReportDbEngine.class);
	protected static String className = SmsReportDbEngine.class.getSimpleName();
	
	@Resource(name="smepMsgReportLogMapper")
	SmepMsgReportLogMapper mapper;
	
	public SmsReportDbEngine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
	}
	
	@Override
	protected void consumeTask() {
		List<MsgMoLog> list =  super.getMessageList();
		// 无数据
		if (null == list) return;

		//System.out.println("insert report: " + list.size());
		/*
		for(MsgMoLog message : list) {
			//if (message.getSrcTerminalId().startsWith("86130"))
			System.out.println(String.format("report: msgid=%s, destid=%s, serviceId=%s, tpPid=%s, tpUdhi=%s, msgFmt=%s, srcTerminalId=%s, registeredDelivery=%s, msgLength=%s, msgContent=%s, reserve=%s, stat=%s, submitTime=%s, doneTime=%s, destTerminalId=%s, smscSequence=%s, srcTerminalType=%s, linkid=%s, createor=%s, createtime=%s, protocol=%s", 
					message.getMsgid(),
					message.getDestid(),
					message.getServiceId(),
					message.getTpPid(),
					message.getTpUdhi(),
					message.getMsgFmt(),
					message.getSrcTerminalId(),
					message.getRegisteredDelivery(),
					message.getMsgLength(),
					message.getMsgContent(),
					message.getReserve(),
					message.getStat(),
					message.getSubmitTime(),
					message.getDoneTime(),
					message.getDestTerminalId(),
					message.getSmscSequence(),
					message.getSrcTerminalType(),
					message.getLinkid(),
					message.getCreateor(),
					message.getCreatetime(),
					message.getProtocol()
			));
		}*/
		
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
