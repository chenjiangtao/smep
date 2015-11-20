package com.aesirteam.smep.mms.engine;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.mms.db.domain.MmsReportLog;
import com.aesirteam.smep.mms.db.mapper.SmepMmsReportLogMapper;
import com.aesirteam.smep.util.FluxMonitor;
import com.huawei.insa2.util.Args;

@Component("mmsReportDbEngine")
public class MmsReportDbEngine extends BaseEngine<MmsReportLog> {
	protected final static Logger logger = LoggerFactory.getLogger(MmsReportDbEngine.class);
	protected static String className = MmsReportDbEngine.class.getSimpleName();
	
	@Resource(name="smepMmsReportLogMapper")
	SmepMmsReportLogMapper mapper;
	
	public MmsReportDbEngine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
		fluxMonitor = new FluxMonitor(className, 400L, 3, true);
	}
	
	@Override
	protected void consumeTask() {
		List<MmsReportLog> list =  super.getMessageList();
		// 无数据
		if (0 == list.size()) return;	
		/*
		for (MmsReportLog message : list) {
			System.out.println(String.format("transactionid=%s, mmsversion=%s, MMSRelayServerID=%s, MessageID=%s, srcTerminalId=%s, destTerminalId=%s, Timestamp=%s, statuscode=%s, statustext=%s, Createor=%s, Createtime=%s", 
					message.getTransactionid(),
					message.getMmsversion(),
					message.getMmsrelayserverid(),
					message.getMessageid(),
					message.getSrcTerminalId(),
					message.getDestTerminalId(),
					message.getMmtimestamp(),
					message.getMmstatus(),
					message.getStatustext(),
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
	protected void closeSMProxy() { }
	
	@Override
	protected boolean isConnected() {
		return true;
	}

}
