package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.engine.SmsPendingEngine;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.util.ObjectUtil;

@Service("smsPendingService")
public class SmsPendingService extends ServiceStore<SmsPendingEngine> {
	
	@Autowired
	protected SmsPendingEngine smsPendingEngine;
		
	public SmsPendingService() {}
	
	@Override
	protected void producerTask() {
		try {	
			List<String> values = getJedis().lrange(smsPendingEngine.QUEUE_SMS_PENDING, smsPendingEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					ReqSmsMessage message = ObjectUtil.toReqSmsMessage(jedisVal);
					smsPendingEngine.producer(message);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public SmsPendingEngine getEngine() {
		return smsPendingEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return smsPendingEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return smsPendingEngine.getSysParams();
	}
	
}
