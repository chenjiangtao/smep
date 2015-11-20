package com.aesirteam.smep.mms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.mms.db.domain.MmsMoLog;
import com.aesirteam.smep.mms.engine.MmsMoDbEngine;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.util.ObjectUtil;

@Service("mmsMoToDbService")
public class MmsMoToDbService extends ServiceStore<MmsMoDbEngine> {

	@Autowired
	protected MmsMoDbEngine mmsMoDbEngine;
	
	public MmsMoToDbService() {}

	@Override
	protected void producerTask() {
		try {
			List<String> values = getJedis().lrange(mmsMoDbEngine.QUEUE_MMS_MO_TO_DB, mmsMoDbEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					MmsMoLog mmsMotLog = ObjectUtil.toMmsMoLog(jedisVal);
					mmsMoDbEngine.producer(mmsMotLog);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public MmsMoDbEngine getEngine() {
		return mmsMoDbEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return mmsMoDbEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return mmsMoDbEngine.getSysParams();
	}
	
}
