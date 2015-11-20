package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.engine.CMPP30Engine;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.util.ObjectUtil;

@Service("cmpp30MtService")
public class CMPP30MtService extends ServiceStore<CMPP30Engine> {
	
	@Autowired
	protected CMPP30Engine cmpp30Engine;
	
	public CMPP30MtService() {}
	
	@Override
	protected void producerTask() {		
		try {			
			List<String> values = getJedis().lrange(cmpp30Engine.QUEUE_SMS_MT_CMPP30, cmpp30Engine.BATCH_SIZE);
			if (null != values) { 
				for(String jedisVal : values) {
					ReqSmsMessage reqSmsMessage = ObjectUtil.toReqSmsMessage(jedisVal);
					cmpp30Engine.producer(cmpp30Engine.getCMPPMessage(reqSmsMessage));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public CMPP30Engine getEngine() {
		return cmpp30Engine;
	}

	@Override
	public JedisFactory getJedis() {
		return cmpp30Engine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return cmpp30Engine.getSysParams();
	}
	
}
