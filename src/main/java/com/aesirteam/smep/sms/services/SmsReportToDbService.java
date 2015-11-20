package com.aesirteam.smep.sms.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;
import com.aesirteam.smep.sms.engine.SmsReportDbEngine;
import com.aesirteam.smep.util.ObjectUtil;

@Service("smsReportToDbService")
public class SmsReportToDbService extends ServiceStore<SmsReportDbEngine> {
	
	@Autowired
	protected SmsReportDbEngine smsReportDbEngine;

	public SmsReportToDbService() {}

	@Override
	protected void producerTask() {
		try {
			List<String> values = getJedis().lrange(smsReportDbEngine.QUEUE_SMS_RP_TO_DB, smsReportDbEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					MsgMoLog msgMoLog = ObjectUtil.toMsgMoLog(jedisVal);
					smsReportDbEngine.producer(msgMoLog);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public SmsReportDbEngine getEngine() {
		return smsReportDbEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return smsReportDbEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return smsReportDbEngine.getSysParams();
	}
}
