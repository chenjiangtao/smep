package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.engine.SMGP30Engine;
import com.aesirteam.smep.util.ObjectUtil;

@Service("smgp30MtService")
public class SMGP30MtService extends ServiceStore<SMGP30Engine> {

	@Autowired
	protected SMGP30Engine smgp30Engine;

	public SMGP30MtService() {}
	
	@Override
	protected void producerTask() {
		try {			
			List<String> values = getJedis().lrange(smgp30Engine.QUEUE_SMS_MT_SMGP30, smgp30Engine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					ReqSmsMessage reqSmsMessage = ObjectUtil.toReqSmsMessage(jedisVal);
					smgp30Engine.producer(smgp30Engine.getSMGPMessage(reqSmsMessage));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public SMGP30Engine getEngine() {
		return smgp30Engine;
	}

	@Override
	public JedisFactory getJedis() {
		return smgp30Engine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return smgp30Engine.getSysParams();
	}
	
}
