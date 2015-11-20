package com.aesirteam.smep.mms.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aesirteam.smep.client.message.MMSFile;
import com.aesirteam.smep.client.message.ReqMmsMessage;
import com.aesirteam.smep.core.ServiceStore;
import com.aesirteam.smep.mms.engine.MmsPendingEngine;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.util.MMSUtil;
import com.aesirteam.smep.util.ObjectUtil;

@Service("mmsPendingService")
public class MmsPendingService extends ServiceStore<MmsPendingEngine> {

	@Autowired
	protected MmsPendingEngine mmsPendingEngine;
	
	public MmsPendingService() {}
	
	private boolean isExist(String newFilename) {
		File file = new File(newFilename);
		return file.exists() && file.isFile();
	}
	
	private void createBase64File(String fileBody, String targetFilename) {
		try {
			MMSUtil.decoderBase64File(fileBody, targetFilename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void producerTask() {
		try {	
			List<String> values = getJedis().lrange(mmsPendingEngine.QUEUE_MMS_PENDING, mmsPendingEngine.BATCH_SIZE);
			if (null != values) {
				for(String jedisVal : values) {
					ReqMmsMessage message = ObjectUtil.toReqMmsMessage(jedisVal);
					//由于彩信的附件特殊性，不适宜将附件数据压入内存，应在此处做本地化处理
					for(MMSFile file : message.getFile()) {
						String newFilename = mmsPendingEngine.MMS_ADJFILE_PATH_UPLOAD.concat(file.getName());
						if (!isExist(newFilename)) {
							createBase64File(file.getBody(), newFilename);
						}
						file.setFilename(newFilename);
						file.setBody(null);
					}
					mmsPendingEngine.producer(message);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	@Override
	public MmsPendingEngine getEngine() {
		return mmsPendingEngine;
	}

	@Override
	public JedisFactory getJedis() {
		return mmsPendingEngine.getJedis();
	}

	@Override
	public SysParams getSysParams() {
		return mmsPendingEngine.getSysParams();
	}
	
}
