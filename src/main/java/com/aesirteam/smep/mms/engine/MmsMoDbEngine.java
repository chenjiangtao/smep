package com.aesirteam.smep.mms.engine;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.mms.db.domain.MmsMoLog;
import com.aesirteam.smep.mms.db.mapper.SmepMmsMoLogMapper;
import com.aesirteam.smep.util.FluxMonitor;
import com.huawei.insa2.util.Args;

@Component("mmsMoDbEngine")
public class MmsMoDbEngine extends BaseEngine<MmsMoLog> {
	protected final static Logger logger = LoggerFactory.getLogger(MmsMoDbEngine.class);
	protected static String className = MmsMoDbEngine.class.getSimpleName();
	
	@Resource(name="smepMmsMoLogMapper")
	SmepMmsMoLogMapper mapper;
	
	public MmsMoDbEngine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
		fluxMonitor = new FluxMonitor(className, 400L, 3, true);
	}

	@Override
	protected void consumeTask() {
		List<MmsMoLog> list =  super.getMessageList();
		// 无数据
		if (0 == list.size()) return;	
		/*
		for(MmsMoLog message : list) {
			System.out.println(String.format("transactionid=%s, mmsversion=%s, MMSRelayServerID=%s, LinkID=%s, srcTerminalId=%s, destTerminalId=%s, Timestamp=%s, subject=%s, content=%s, Createor=%s, Createtime=%s", 
					message.getTransactionid(),
					message.getMmsversion(),
					message.getMmsrelayserverid(),
					message.getLinkid(),
					message.getSrcTerminalId(),
					message.getDestTerminalId(),
					message.getMmtimestamp(),
					message.getMmssubject(),
					message.getMmscontext(),
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
