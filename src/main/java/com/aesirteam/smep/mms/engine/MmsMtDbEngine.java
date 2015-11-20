package com.aesirteam.smep.mms.engine;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.mms.db.domain.MmsMtLog;
import com.aesirteam.smep.mms.db.mapper.SmepMmsMtLogMapper;
import com.aesirteam.smep.util.FluxMonitor;
import com.huawei.insa2.util.Args;

@Component("mmsMtDbEngine")
public class MmsMtDbEngine extends BaseEngine<MmsMtLog> {
	protected final static Logger logger = LoggerFactory.getLogger(MmsMtDbEngine.class);
	protected static String className = MmsMtDbEngine.class.getSimpleName();
	
	@Resource(name="smepMmsMtLogMapper")
	SmepMmsMtLogMapper mapper;
	
	public MmsMtDbEngine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
		fluxMonitor = new FluxMonitor(className, 400L, 3, true);
	}
	
	@Override
	protected void consumeTask() {
		List<MmsMtLog> list =  super.getMessageList();
		// 无数据
		if (0 == list.size()) return;	
		/*
		for(MmsMtLog message : list) {
			System.out.println(String.format("transactionid=%s, seqno=%s, corpno=%s, vaspid=%s, vasid=%s, mmsversion=%s, mmssubject=%s, srcTerminalId=%s, destTerminalId=%s, serviceId=%s, registeredDelivery=%s, msgLevel=%s, mmsBodytype=%s, validTime=%s, atTime=%s, mmsFile=%s, createor=%s, createtime=%s, msgid=%s, statuscode=%s, statustext=%s", 
					message.getTransactionid(),
					message.getSeqno(),
					message.getCorpno(),
					message.getVaspid(),
					message.getVasid(),
					message.getMmsversion(),
					message.getMmssubject(),
					message.getSrcTerminalId(),
					message.getDestTerminalId(),
					message.getServiceId(),
					message.getRegisteredDelivery(),
					message.getMsgLevel(),
					message.getMmsBodytype(),
					message.getValidTime(),
					message.getAtTime(),
					message.getMmsFile(),
					message.getCreateor(),
					message.getCreatetime(),
					message.getMsgid(),
					message.getStatuscode(),
					message.getStatustext()
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
	protected void closeSMProxy() { }
	
	@Override
	protected boolean isConnected() {
		return true;
	}
}
