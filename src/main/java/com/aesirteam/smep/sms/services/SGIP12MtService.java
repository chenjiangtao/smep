package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.engine.SGIP12Engine;
import com.aesirteam.smep.util.ObjectUtil;

@Service("sgip12MtService")
public class SGIP12MtService extends ServiceStore<SGIP12Engine> {
		
	@Autowired
	protected SGIP12Engine sgip12Engine;
	
	public SGIP12MtService() {}

	@Override
	protected void producerTask() {
		try {			
			List<String> values = getJedis().lrange(sgip12Engine.QUEUE_SMS_MT_SGIP12, sgip12Engine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					ReqSmsMessage reqSmsMessage = ObjectUtil.toReqSmsMessage(jedisVal);
					sgip12Engine.producer(sgip12Engine.getSGIPMessage(reqSmsMessage));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public SGIP12Engine getEngine() {
		return sgip12Engine;
	}

	@Override
	public JedisFactory getJedis() {
		return sgip12Engine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return sgip12Engine.getSysParams();
	}
	
}
