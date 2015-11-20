package com.aesirteam.smep.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
//import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;
import com.aesirteam.smep.util.FluxMonitor;
import com.huawei.insa2.util.Args;

public abstract class BaseEngine<E> extends ThreadGroup {
	protected BlockingQueue<E> cache = new LinkedBlockingQueue<E>();
	protected List<ConsumeThread> consumeThreads;
	protected Args args;
	protected int threads = 20;
	protected boolean alive = false;
	protected FluxMonitor fluxMonitor;
	
	public int BATCH_SIZE;
	public int CACHE_CAPACITY;
	
	public String QUEUE_SMS_PENDING = null;
	public String QUEUE_MMS_PENDING = null;
	
	public String MMS_ADJFILE_PATH_UPLOAD = null;
	public String MMS_ADJFILE_PATH_RECV = null;
			
	public final String QUEUE_SMS_MT_CMPP20 = "q:sms:mt:cmpp20";
	public final String QUEUE_SMS_MT_CMPP30 = "q:sms:mt:cmpp30";
	public final String QUEUE_SMS_MT_SMGP30 = "q:sms:mt:smgp30";
	public final String QUEUE_SMS_MT_SGIP12 = "q:sms:mt:sgip12";
	public final String QUEUE_SMS_MT_TO_DB = "q:sms:mt:to:db";
	public final String QUEUE_SMS_MO_TO_DB = "q:sms:mo:to:db";
	public final String QUEUE_SMS_RP_TO_DB = "q:sms:rp:to:db";
	
	public final String QUEUE_MMS_MT_MM7 = "q:mms:mt:mm7";
	public final String QUEUE_MMS_MT_TO_DB = "q:mms:mt:to:db";
	public final String QUEUE_MMS_MO_TO_DB = "q:mms:mo:to:db";
	public final String QUEUE_MMS_RP_TO_DB = "q:mms:rp:to:db";
	
	@Resource(name="sysParams")
	protected SysParams params;

	@Resource(name="jedisFactory")
	protected JedisFactory jedis;
		
	public BaseEngine(final String className) {
		super("tg".concat(className));
		setDaemon(true);
		fluxMonitor = new FluxMonitor(className);
	}
		
	//@PostConstruct
	public void init() {
		QUEUE_SMS_PENDING = params.getVal("sms.queueName", String.class);
		QUEUE_MMS_PENDING = params.getVal("mms.queueName", String.class);
		
		String mms_content_root_path = params.getVal("mms.content.root.path", String.class);
		if (null == mms_content_root_path)
			mms_content_root_path = System.getProperty("user.dir");
		
		MMS_ADJFILE_PATH_UPLOAD = mms_content_root_path.concat("/mmsfiles/submit/");
		MMS_ADJFILE_PATH_RECV = mms_content_root_path.concat("/mmsfiles/receive/");
			
		//创建彩信附件的文件夹
		File file = new File(MMS_ADJFILE_PATH_UPLOAD);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		
		file = new File(MMS_ADJFILE_PATH_RECV);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();
		}
		
		if (!initSMProxy()) {
			alive = false;
			return;
		}
		
		consumeThreads = new ArrayList<ConsumeThread>();
		
		//创建线程池
		for(int i = 0; i < threads; i++) {
			ConsumeThread ct = new ConsumeThread(String.format("%s#%d", getName(), i));
			consumeThreads.add(ct);
			ct.start();
		}
		
		alive = true;
	}
		
	public synchronized void producer(E message) {
		cache.offer(message);
	}
	
	public synchronized boolean haveFreeOfCache() {
		return CACHE_CAPACITY - cache.size() >= BATCH_SIZE;
	}
	
	protected synchronized E getMessage() {
		while(cache.isEmpty()) {
			try { wait(100); } catch (InterruptedException ex) {}
		}
		notify();
		return cache.poll();		
	}
	
	protected synchronized List<E> getMessageList() {
		while (cache.isEmpty()) {
			try { wait(100); } catch (InterruptedException ex) {}
		}
		notify();
		List<E> list = new ArrayList<E>(BATCH_SIZE);
		cache.drainTo(list, BATCH_SIZE);
		return list;
	}
	
	@PreDestroy
	public boolean shutdown() {
		synchronized(this) 
		{
			alive = false;
			notifyAll();
		}
				
		//断开与网关的连接并释放socket资源
		closeSMProxy();
		
		Thread[] activeThreads = new Thread[this.activeCount()];
		int threadCount = enumerate(activeThreads);
		
		//等待所有工作线程结束
		boolean shutdownAll = true;
		for(int i = 0; i < threadCount; i++) {
			shutdownAll = false;
			try {
				activeThreads[i].join();
			} catch (InterruptedException e) {
				shutdownAll = true;
			}
		}
		
		clearCache();
		
		return shutdownAll;
	}
	
	
	private void clearCache() {	
		if (!alive) {			
			//清空工作队列 (需要检查队列长度，如果存在加入redis)
			cache.clear();		
			//中断线程池中的所有的工作线程
			interrupt();
		}
	}
	
	abstract protected boolean initSMProxy();
	
	abstract protected void closeSMProxy();
	
	abstract protected void consumeTask();
	
	abstract protected boolean isConnected();
	
	private class ConsumeThread extends Thread {
				
		public ConsumeThread(String threadName) {
			super(BaseEngine.this.getParent(), threadName);
			setDaemon(false);
		}
		
		@Override
		public void run() {
			do { consumeTask(); } while(alive);
		}
	}
		
	public void setThreads(int threads) {
		this.threads = threads;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public SysParams getSysParams() {
		return params;
	}
	
	public JedisFactory getJedis() {
		return jedis;
	}
	
	public void setParam(String key, Object val) {
		if (val instanceof String) {
			args.set(key, val + "");
		} else if (val instanceof Integer) {
			args.set(key, Integer.parseInt(val + ""));
		} else if (val instanceof Long) {
			args.set(key, Long.parseLong(val + ""));
		} else if (val instanceof Float) {
			args.set(key, Float.parseFloat(val + ""));
		} else if (val instanceof Boolean) {
			args.set(key, Boolean.parseBoolean(val + ""));
		} else {
			args.set(key, val);
		}
	}
	
	public String getParams() {
		StringBuffer sb = new StringBuffer();
		Set<Map.Entry<String, Object>> set = args.getArgs().entrySet();
		for(Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> entry = it.next();
			sb.append("\t").append(entry.getKey()).append("=").append(entry.getValue()).append("\r\n");
		}
		return sb.toString();
	}
}
