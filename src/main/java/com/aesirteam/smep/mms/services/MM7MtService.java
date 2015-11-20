package com.aesirteam.smep.mms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.client.message.ReqMmsMessage;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.mms.engine.MM7Engine;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.util.ObjectUtil;

@Service("mm7MtService")
public class MM7MtService extends ServiceStore<MM7Engine> {

	@Autowired
	protected MM7Engine mm7Engine;
	
	public MM7MtService() {}
	
	@Override
	protected void producerTask() {
		try {	
			List<String> values = getJedis().lrange(mm7Engine.QUEUE_MMS_MT_MM7, mm7Engine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					ReqMmsMessage reqMmsMessage = ObjectUtil.toReqMmsMessage(jedisVal);
					mm7Engine.producer(mm7Engine.getMM7SubmitReq(reqMmsMessage));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public MM7Engine getEngine() {
		return mm7Engine;
	}

	@Override
	public JedisFactory getJedis() {
		return mm7Engine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return mm7Engine.getSysParams();
	}
}
