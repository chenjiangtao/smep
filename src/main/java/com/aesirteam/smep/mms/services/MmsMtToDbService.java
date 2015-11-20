package com.aesirteam.smep.mms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.mms.db.domain.MmsMtLog;
import com.aesirteam.smep.mms.engine.MmsMtDbEngine;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.util.ObjectUtil;

@Service("mmsMtToDbService")
public class MmsMtToDbService extends ServiceStore<MmsMtDbEngine> {
	
	@Autowired
	protected MmsMtDbEngine mmsMtDbEngine;
	
	public MmsMtToDbService() {}
	
	@Override
	protected void producerTask() {		
		try {
			List<String> values = getJedis().lrange(mmsMtDbEngine.QUEUE_MMS_MT_TO_DB, mmsMtDbEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					MmsMtLog mmsMtLog = ObjectUtil.toMmsMtLog(jedisVal);
					mmsMtDbEngine.producer(mmsMtLog);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public MmsMtDbEngine getEngine() {
		return mmsMtDbEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return mmsMtDbEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return mmsMtDbEngine.getSysParams();
	}
}
