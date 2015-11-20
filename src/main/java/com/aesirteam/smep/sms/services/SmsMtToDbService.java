package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.aesirteam.smep.sms.engine.SmsMtDbEngine;
import com.aesirteam.smep.util.ObjectUtil;

@Service("smsMtToDbService")
public class SmsMtToDbService extends ServiceStore<SmsMtDbEngine> {

	@Autowired
	protected SmsMtDbEngine smsMtDbEngine;
	
	public SmsMtToDbService() {}
	
	@Override
	protected void producerTask() {	
		try {
			List<String> values = getJedis().lrange(smsMtDbEngine.QUEUE_SMS_MT_TO_DB, smsMtDbEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					MsgMtLog msgMtLog = ObjectUtil.toMsgMtLog(jedisVal);
					smsMtDbEngine.producer(msgMtLog);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public SmsMtDbEngine getEngine() {
		return smsMtDbEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return smsMtDbEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return smsMtDbEngine.getSysParams();
	}
}
