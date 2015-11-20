package com.aesirteam.smep.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.aesirteam.smep.core.db.domain.SmepSysParam;
import com.aesirteam.smep.core.db.domain.SmepSysService;
import com.aesirteam.smep.core.db.mapper.SmepSysParamMapper;
import com.aesirteam.smep.core.db.mapper.SmepSysServiceMapper;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;

public abstract class ServiceStore<E extends BaseEngine<?>> {
	private final static ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"classpath*:project-services.xml"});//null;
	private final static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
	private String className = getClass().getName();	
	private final int TRY_RETRY_COUNT = 3;
	private boolean isStarted = false;
	private WatchThread watchThread;
	private JedisFactory jedis;
	private SysParams sysParams;
	private E engine;
	private String serviceName, relationEngine, engineDesc;
	private Date lastStartTime, lastStopTime;
	private int currState;
	private boolean autostart;
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String className) {
		Class<T> classz;
		try {
			classz = (Class<T>) Class.forName(className);
			return ctx.getBean(classz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<SmepSysService> getServiceNames() {
		SmepSysServiceMapper smepSysServiceMapper = get(SmepSysServiceMapper.class.getName());
		return smepSysServiceMapper.getAllData(null);
	}
	
	private List<SmepSysParam> getEngineParams(String classNameByEngine) {
		SmepSysParamMapper smepSysParamMapper = get(SmepSysParamMapper.class.getName());
		return smepSysParamMapper.getData(classNameByEngine);
	}
	
	private void updataServiceCurrState() {
		SmepSysServiceMapper smepSysServiceMapper = get(SmepSysServiceMapper.class.getName());
		SmepSysService record = new SmepSysService();
		record.setClassname(className);
		if (isStarted)
			record.setLaststarttime(new java.util.Date());
		else
			record.setLaststoptime(new java.util.Date());
		record.setCurrstate(isStarted ? 1 : 0);
		smepSysServiceMapper.updateData(record);
	}
	
	public int start() {
		if (isStarted) {
			info("Started already");
			return 0;
		}
				
		isStarted = false;
		
		sysParams = getSysParams();
		List<SmepSysParam> list = getEngineParams(sysParams.getClass().getName());
		for(SmepSysParam record : list) {
			sysParams.set(record.getParamName(), record.getParamValue());
		}
		//System.out.println("系统全局参数:");
		//System.out.print(sysParams.getParams());
		
		JEDIS_OK: {
			jedis = getJedis();
			//init params
			list = getEngineParams(jedis.getClass().getName());
			for(SmepSysParam record : list) {
				jedis.setParam(record.getParamName(), record.getParamValue());
			}

			for (int i = 0; i < TRY_RETRY_COUNT; jedis.init(), i++) {
				if (jedis.isAlive()) {
					//System.out.println("Redis消息队列:");
					//System.out.print(jedis.getParams());
					break JEDIS_OK;
				}
				taskWaitOneSecond();
			}

			if (!jedis.isAlive()) {
				error("Start Fail. ".concat(jedis.getError()));
				return -1;
			}
		}
		
		ENGINE_OK: {
			engine = getEngine();
			//init params
			list = getEngineParams(engine.getClass().getName());
			for(SmepSysParam record : list) {
				engine.setParam(record.getParamName(), record.getParamValue());
			}
			
			for (int i = 0; i < TRY_RETRY_COUNT; engine.init(), i++) {
				if (engine.isAlive()) {
					//System.out.println(desc.concat(":"));
					//System.out.print(engine.getParams());
					break ENGINE_OK;
				}
				taskWaitOneSecond();
			}

			if (!engine.isAlive()) {
				error("Start Fail. Engine not available");
				return -1;
			}
		}
		
		//创建值守线程
		watchThread = new WatchThread(className.concat("#WatchThread"));
		watchThread.start();
			
		info("Started!!!");
		isStarted = true;
		
		updataServiceCurrState();
		
		return 0;
	}

	public int stop() {
		if (!isStarted) { 
			info("Stopped already");
			return 0;
		}
		
		//停止值守线程
		watchThread.kill();
				
		//释放RedisPool资源
		jedis.destroyPool();
		
		int status = -1;
	
		//断开网关连接
		if (engine.shutdown()) {
			isStarted = false;
			status = 0;
		}
		
		if (status != 0) {
			error("Stop Fail.");
		}
		
		info("Stopped!!!");
		
		updataServiceCurrState();
		
		return status;
	}

	public int restart() {
		int val = 0;
		
		if (isStarted) {
			val = stop();
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
		}
		
		if (val == 0 && !isStarted) { 
			val = start();
		}
		
		return val;
	}
	
	abstract public E getEngine();
	
	abstract public JedisFactory getJedis();
	
	abstract public SysParams getSysParams();

	abstract protected void producerTask();
	
	private void taskWaitOneSecond() {
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
	}
	
	private void info(String msg) {
		System.out.println(String.format("%s INFO  %s: %s", sdf.format(System.currentTimeMillis()), serviceName, msg));
	}
	
	private void error(String msg) {
		System.out.println(String.format("%s ERROR  %s: %s", sdf.format(System.currentTimeMillis()), serviceName, msg));
	}
	
	private class WatchThread extends Thread {

		private State state;
		private boolean alive = true;

		public WatchThread(String name) {
			super(name);
			setDaemon(false);
		}

		public final void run() {
			do {
				//检查引擎状态,未活动则返回
				//检查网关连接状态,若失败则重新连接
				if (!jedis.isAlive() || !engine.isAlive() || !engine.isConnected()) {
					taskWaitOneSecond();
					continue;
				}
				
				if (engine.haveFreeOfCache()) {
					producerTask();
				}
			} while(alive);
		}

		public State getState() {
			return state;
		}

		public void kill() {
			alive = false;
		}
	}

	public String getRelationEngine() {
		return relationEngine;
	}

	public void setRelationEngine(String relationEngine) {
		this.relationEngine = relationEngine;
	}

	public Date getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(Date lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public Date getLastStopTime() {
		return lastStopTime;
	}

	public void setLastStopTime(Date lastStopTime) {
		this.lastStopTime = lastStopTime;
	}

	public int getCurrState() {
		return currState;
	}

	public void setCurrState(int currState) {
		this.currState = currState;
	}

	public String getEngineDesc() {
		return engineDesc;
	}

	public void setEngineDesc(String engineDesc) {
		this.engineDesc = engineDesc;
	}

	public boolean getAutostart() {
		return autostart;
	}

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
