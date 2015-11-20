package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;
import com.aesirteam.smep.sms.engine.SmsMoDbEngine;
import com.aesirteam.smep.util.ObjectUtil;
import com.aesirteam.smep.core.ServiceStore;

@Service("smsMoToDbService")
public class SmsMoToDbService extends ServiceStore<SmsMoDbEngine> {

	@Autowired
	protected SmsMoDbEngine smsMoDbEngine;
	
	public SmsMoToDbService() {}

	@Override
	protected void producerTask() {
		try {			
			List<String> values = getJedis().lrange(smsMoDbEngine.QUEUE_SMS_MO_TO_DB, smsMoDbEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					MsgMoLog msgMoLog = ObjectUtil.toMsgMoLog(jedisVal);
					smsMoDbEngine.producer(msgMoLog);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public SmsMoDbEngine getEngine() {
		return smsMoDbEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return smsMoDbEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return smsMoDbEngine.getSysParams();
	}
	
}
