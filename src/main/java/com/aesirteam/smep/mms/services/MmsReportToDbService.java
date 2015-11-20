package com.aesirteam.smep.mms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.mms.db.domain.MmsReportLog;
import com.aesirteam.smep.mms.engine.MmsReportDbEngine;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.util.ObjectUtil;

@Service("mmsReportToDbService")
public class MmsReportToDbService extends ServiceStore<MmsReportDbEngine> {

	@Autowired
	protected MmsReportDbEngine mmsReportDbEngine;
	
	public MmsReportToDbService() {}
	
	@Override
	protected void producerTask() {		
		try {
			List<String> values = getJedis().lrange(mmsReportDbEngine.QUEUE_MMS_RP_TO_DB, mmsReportDbEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					MmsReportLog mmsReportLog = ObjectUtil.toMmsReportLog(jedisVal);
					mmsReportDbEngine.producer(mmsReportLog);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public MmsReportDbEngine getEngine() {
		return mmsReportDbEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return mmsReportDbEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return mmsReportDbEngine.getSysParams();
	}
	
}
