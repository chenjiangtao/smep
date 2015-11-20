package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.engine.CMPP20Engine;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.util.ObjectUtil;

@Service("cmpp20MtService")
public class CMPP20MtService extends ServiceStore<CMPP20Engine> {
	
	@Autowired
	protected CMPP20Engine cmpp20Engine;
	
	public CMPP20MtService() {}

	@Override
	protected void producerTask() {		
		try {
			List<String> values = getJedis().lrange(cmpp20Engine.QUEUE_SMS_MT_CMPP20, cmpp20Engine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					ReqSmsMessage reqSmsMessage = ObjectUtil.toReqSmsMessage(jedisVal);
					cmpp20Engine.producer(cmpp20Engine.getCMPPMessage(reqSmsMessage));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public CMPP20Engine getEngine() {
		return cmpp20Engine;
	}

	@Override
	public JedisFactory getJedis() {
		return cmpp20Engine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return cmpp20Engine.getSysParams();
	}
}
