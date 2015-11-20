package com.aesirteam.smep.mms.engine;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.aesirteam.smep.client.message.MMSFile;
import com.aesirteam.smep.client.message.ReqMmsMessage;
import com.aesirteam.smep.core.BaseEngine;
import com.aesirteam.smep.core.ProtocolType;
import com.aesirteam.smep.mms.db.domain.MmsMtLog;
import com.aesirteam.smep.util.FluxMonitor;
import com.aesirteam.smep.util.MMSUtil;
import com.aesirteam.smep.util.ObjectUtil;
import com.cmcc.mm7.vasp.common.MMConstants.RequestStatus;
import com.cmcc.mm7.vasp.common.MMContent;
import com.cmcc.mm7.vasp.conf.MM7Config;
import com.cmcc.mm7.vasp.message.MM7DeliverReq;
import com.cmcc.mm7.vasp.message.MM7DeliverRes;
import com.cmcc.mm7.vasp.message.MM7DeliveryReportReq;
import com.cmcc.mm7.vasp.message.MM7DeliveryReportRes;
import com.cmcc.mm7.vasp.message.MM7RSRes;
import com.cmcc.mm7.vasp.message.MM7ReadReplyReq;
import com.cmcc.mm7.vasp.message.MM7ReadReplyRes;
import com.cmcc.mm7.vasp.message.MM7SubmitReq;
import com.cmcc.mm7.vasp.message.MM7SubmitRes;
import com.cmcc.mm7.vasp.message.MM7VASPRes;
import com.cmcc.mm7.vasp.service.MM7Receiver;
import com.cmcc.mm7.vasp.service.MM7Sender;
import com.huawei.insa2.util.Args;

@Component("mm7Engine")
public class MM7Engine extends BaseEngine<MM7SubmitReq> {
	protected final static Logger logger = LoggerFactory.getLogger(MM7Engine.class);
	protected static String className = MM7Engine.class.getSimpleName();
	protected String charSet;
	protected MM7Proxy mm7Proxy;
	
	public MM7Engine() {
		super(className);
		args = new Args();
		CACHE_CAPACITY = args.get("cacheCapacity", 10000);
		BATCH_SIZE = args.get("batchSize", 100);
		fluxMonitor = new FluxMonitor(className, 400L, 3, true);
		charSet = args.get("charSet", "UTF-8");
	}
		
	@Override
	protected void consumeTask() {
		MM7SubmitReq message = super.getMessage();
		//无数据
		if (null == message) return;

		MM7SubmitRes rsp = null;
		try {
			if (ReqMmsMessage.BODY_FILENAME == message.getMmsMtLog().getMmsBodytype()) {
				
				MMSFile mmsFile[] = (MMSFile[]) ObjectUtil.json2Object(message.getMmsMtLog().getMmsFile(), MMSFile[].class);
				MMContent parentContent = new MMContent();
				parentContent.setContentType(com.cmcc.mm7.vasp.common.MMConstants.ContentType.MULTIPART_RELATED);
				parentContent.setContentID("Main");
				for(MMSFile fn : mmsFile) {
					MMContent subConntent = MMContent.createFromFile(fn.getFilename());
					subConntent.setContentID(fn.getName());
					subConntent.setContentType(MMSFile.getMMSContentType(fn.getType()));
					subConntent.setCharset(charSet);
					parentContent.addSubContent(subConntent);
				}
				message.setContent(parentContent);
			}
			rsp = (MM7SubmitRes)mm7Proxy.send(message);
		} catch (Exception ex) {
			logger.error(String.format("[%s MM7Proxy] Connection fail!!! [%d]", Thread.currentThread().getId(), cache.size()));
			producer(message);
			return;
		}
		
		MmsMtLog mmsMtLog = message.getMmsMtLog();
		if (null == rsp) {
			mmsMtLog.setStatuscode(-1);
			mmsMtLog.setStatustext("exception");
			jedis.pushSubmitMmsFAIL(QUEUE_MMS_MT_TO_DB, mmsMtLog);
		} else {
			mmsMtLog.setMsgid(rsp.getMessageID());
			mmsMtLog.setStatuscode(rsp.getStatusCode());
			mmsMtLog.setStatustext(rsp.getStatusText());
			
			if (RequestStatus.SUCCESS == rsp.getStatusCode())
				jedis.pushSubmitMmsOK(QUEUE_MMS_MT_TO_DB, mmsMtLog);
			else
				jedis.pushSubmitMmsFAIL(QUEUE_MMS_MT_TO_DB, mmsMtLog);
		}		
		//计算流量
		fluxMonitor.incr();
	}
	
	
	protected class MM7Proxy extends Thread {
		protected MM7Config config;
		protected Receiver receiver;
		protected boolean connected = true;
		
		public MM7Proxy(MM7Config config) {
			this.config = config;
		}
				
		public MM7RSRes send(MM7SubmitReq obj) throws Exception {
			MM7Sender mm7Sender = new MM7Sender(config);
			connected = mm7Sender.isConnected();
			if (!connected)
				throw new java.net.SocketException();
			
			MM7RSRes res = mm7Sender.send(obj);
			if (res instanceof MM7SubmitRes) {
				return res;
			} else {
				return null;
			}
		}
		
		@Override
		public void run() {
			receiver = new Receiver();
			receiver.start();
			for(;;);
		}
		
		private class Receiver extends MM7Receiver {
			public Receiver() {
				super(config);
			}
			
			//处理到VASP的传送（deliver）多媒体消息
			public MM7VASPRes doDeliver(MM7DeliverReq mm7DeliverReq) {
				
				mm7DeliverReq.getMmsMoLog().setTransactionid(mm7DeliverReq.getTransactionID());
				mm7DeliverReq.getMmsMoLog().setMmsversion(mm7DeliverReq.getMM7Version());
				mm7DeliverReq.getMmsMoLog().setProtocol(ProtocolType.MM7.getValue());
				
				if (mm7DeliverReq.isContentExist()) {
					MMContent parentContent = mm7DeliverReq.getContent();
					// multipart/related
					if (parentContent.isMultipart()) {
						mm7DeliverReq.getMmsMoLog().setIsmultipart(1);
						List<MMContent> contentList = parentContent.getSubContents();
						MMSFile mmsFile[] = new MMSFile[contentList.size()];
						int i = 0;
						for (MMContent subContent : contentList) {
							String contentID = subContent.getContentID().replaceAll("<|>", "");
							if (contentID == null || contentID.length() == 0)
								contentID = "content_".concat(String.valueOf(System.currentTimeMillis()));
							mmsFile[i] = new MMSFile();
							mmsFile[i].setName(contentID);
							mmsFile[i].setFilename(MMS_ADJFILE_PATH_RECV.concat(contentID));
							mmsFile[i].setType(MMSFile.getMMSFileType(contentID));
							MMSUtil.createMMContentFile(mmsFile[i].getFilename(), subContent.getContent());
							i++;
						}
						try {
							mm7DeliverReq.getMmsMoLog().setMmscontext(ObjectUtil.object2Json(mmsFile));
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						mm7DeliverReq.getMmsMoLog().setIsmultipart(0);
					}
				}
				jedis.pushDeliverMmsMo(QUEUE_MMS_MO_TO_DB, mm7DeliverReq.getMmsMoLog());
				
				MM7DeliverRes res = new MM7DeliverRes();
				res.setTransactionID(mm7DeliverReq.getTransactionID());
				res.setStatusCode(1000);
				return res;
			}

			//处理到VASP的发送报告
			public MM7VASPRes doDeliveryReport(MM7DeliveryReportReq mm7DeliveryReportReq) {
				
				mm7DeliveryReportReq.getMmsReportLog().setTransactionid(mm7DeliveryReportReq.getTransactionID());
				mm7DeliveryReportReq.getMmsReportLog().setMmsversion(mm7DeliveryReportReq.getMM7Version());
				mm7DeliveryReportReq.getMmsReportLog().setProtocol(ProtocolType.MM7.getValue());
				jedis.pushDeliverMmsReport(QUEUE_MMS_RP_TO_DB, mm7DeliveryReportReq.getMmsReportLog());
				
				MM7DeliveryReportRes res = new MM7DeliveryReportRes();
				res.setTransactionID(mm7DeliveryReportReq.getTransactionID());
				res.setStatusCode(1000);
				return res;
			}

			//处理到VASP的读后回复报告
			public MM7VASPRes doReadReply(MM7ReadReplyReq mm7ReadReplyReq) {
				//System.out.println("ReadReply transactionid=" + mm7ReadReplyReq.getTransactionID());
				MM7ReadReplyRes res = new MM7ReadReplyRes();
				res.setTransactionID(mm7ReadReplyReq.getTransactionID());
				res.setStatusCode(1000);
				return res;
			}
		}

		public boolean isConnected() {
			return connected;
		}
	}
	
	@Override
	protected boolean initSMProxy() {
		MM7Config mm7Config = new MM7Config();
		mm7Config.setAuthenticationMode(args.get("authMode", 0));
		mm7Config.setUserName(args.get("userName", "zxme"));
		mm7Config.setPassword(args.get("password", "zxme"));
		mm7Config.setMaxMsgSize(args.get("maxMsgSize", 100000));
		mm7Config.setLogPath("/vas_log");
		mm7Config.setLogLevel(0);
		mm7Config.setCharSet(args.get("charSet", "GB2312"));
		mm7Config.setMMSCURL(args.get("mmscURL", "/vas"));
		mm7Config.setMMSCIP(Arrays.asList(args.get("mmscIP", "127.0.0.1:8088")));
		mm7Config.setMmscId(args.get("mmscID", "927001"));
		mm7Config.setUseSSL(args.get("useSSL", true));
		mm7Config.setListenIP(args.get("listenIP", "0.0.0.0"));
		mm7Config.setListenPort(args.get("listenPort", 8089));
		mm7Config.setBackLog(50);
		mm7Config.setTimeOut(args.get("timeout", 90000));
		mm7Config.setReSendCount(2);
		mm7Config.setLogNum(100);
		mm7Config.setLogInterval(120);
		mm7Config.setLogSize(1000);
		mm7Config.setConnConfig(args.get("keepAlive", "on"), 
								args.get("maxKeepAliveRequests", 100),
								args.get("serverMaxKeepAlive", 100),
								args.get("minKeepAliveRequests", 1),
								args.get("keepAliveTimeout", 30),
								args.get("step", 1));
		
		mm7Proxy = new MM7Proxy(mm7Config);
		mm7Proxy.start();
		return true;
	}

	@Override
	protected void closeSMProxy() {
		mm7Proxy.receiver.stop();
		mm7Proxy.interrupt();
	}
	
	@Override
	public boolean isConnected() {
		return mm7Proxy.isConnected();
	}
	
	public MM7SubmitReq getMM7SubmitReq(ReqMmsMessage req) throws UnsupportedEncodingException{
		MM7SubmitReq submit = new MM7SubmitReq();
		submit.setTransactionID(MMSUtil.getTransactionId());
		submit.setVASID(args.get("vasId", ""));
		submit.setVASPID(args.get("vaspId", ""));
		submit.setServiceCode(args.get("serviceId", ""));
		submit.addTo(req.getDestAddr());
		submit.setSenderAddress(req.getSrcAddr());
		submit.setSubject(req.getSubject());
		submit.setDeliveryReport(req.getNeedStautsReport() == 1 ? true :false);
		
		submit.getMmsMtLog().setCorpno(req.getCorpNo());
		submit.getMmsMtLog().setSeqno(req.getSeqNo());
		submit.getMmsMtLog().setTransactionid(submit.getTransactionID());
		submit.getMmsMtLog().setVaspid(submit.getVASPID());
		submit.getMmsMtLog().setVasid(submit.getVASID());
		submit.getMmsMtLog().setMmsversion(submit.getMM7Version());
		submit.getMmsMtLog().setMmssubject(req.getSubject());
		submit.getMmsMtLog().setSrcTerminalId(submit.getSenderAddress());
		submit.getMmsMtLog().setDestTerminalId(submit.getTo().get(0));
		submit.getMmsMtLog().setServiceId(submit.getServiceCode());
		submit.getMmsMtLog().setRegisteredDelivery(req.getNeedStautsReport());
		submit.getMmsMtLog().setMsgLevel(req.getPriorityLevel());
		submit.getMmsMtLog().setAtTime(req.getAtTime());
		submit.getMmsMtLog().setValidTime(req.getVaildTime());
		submit.getMmsMtLog().setMmsBodytype(req.getBodyType());
		if (ReqMmsMessage.BODY_FILENAME == req.getBodyType()) {
			try {
				submit.getMmsMtLog().setMmsFile(ObjectUtil.object2Json(req.getFile()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		submit.getMmsMtLog().setCreateor(req.getCreateor());
		submit.getMmsMtLog().setCreatetime(req.getCreateTime());
		submit.getMmsMtLog().setProtocol(req.getProtocolType());
		
		return submit;
	}
}
