package com.aesirteam.smep.resources;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.aesirteam.smep.mms.db.domain.MmsMoLog;
import com.aesirteam.smep.mms.db.domain.MmsReportLog;
import com.aesirteam.smep.mms.db.domain.MmsMtLog;
import com.aesirteam.smep.sms.db.domain.MsgMoLog;
import com.aesirteam.smep.sms.db.domain.MsgMtLog;
import com.aesirteam.smep.util.ObjectUtil;
import com.huawei.insa2.util.Args;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Component("jedisFactory")
public class JedisFactory {
	protected JedisPool jedisPool;
	protected Args args;
	protected String error = "NOT_INIT", eMsg, smsQueuePrefix, mmsQueuePrefix;
	
	@Resource(name="sysParams")
	protected SysParams params;
	
	public JedisFactory() {
		args = new Args();
	}
	
	public JedisFactory(Map<String, Object> redisParam) {	
		args = new Args(redisParam);
	}
	
	//@PostConstruct
	public void init() {
		//创建连接池
		if (error.equals("NOT_INIT")) {
			smsQueuePrefix = params.getVal("sms.submitQueuePrefix", String.class);
			mmsQueuePrefix = params.getVal("mms.submitQueuePrefix", String.class);
			
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxActive(args.get("maxactive", -1));
			jedisPoolConfig.setMaxIdle(args.get("maxidle", -1));
			jedisPoolConfig.setMaxWait(args.get("maxwait", -1));
			jedisPoolConfig.setTestOnBorrow(true);
			jedisPool = new JedisPool(jedisPoolConfig, args.get("host", "localhost"), args.get("port", 6379), args.get("timeout", 2000), args.get("password", null), args.get("database", 0));
			//连接诊断测试
			try {
				Jedis jedis = jedisPool.getResource();
				jedisPool.returnBrokenResource(jedis);
				error = null;
			} catch (JedisConnectionException ex) {
				eMsg = String.format(" %s: %s", getClass().getSimpleName(), args.toString());
			}
		}
	}
		
	public long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.llen(key);
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public List<String> lrange(String key, int len) {
		Jedis jedis = null;
		List<String> values = null;
		try {
			jedis = jedisPool.getResource();
			if (0 < jedis.llen(key)) {
				values = jedis.lrange(key, 0, len - 1);
			}
			return values;
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} finally {
			if (null != values) {
				for(String val : values) {
					jedis.lrem(key, 1, val);
				}
			}
			
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}	
	
	public long push(String key, String val, boolean isTail) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return isTail ? jedis.rpush(key, val) : jedis.lpush(key, val);
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void setSubmitSmsTotal(String corpNo, String seqNo, long val) {
		setSubmitTotal(String.format("%s:%s:%s:SUBMIT", smsQueuePrefix, corpNo, seqNo), val);
	}
	
	public void setSubmitMmsTotal(String corpNo, String seqNo, long val) {
		setSubmitTotal(String.format("%s:%s:%s:SUBMIT", mmsQueuePrefix, corpNo, seqNo), val);
	}

	private void setSubmitTotal(String key, long total) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			
			if (!jedis.exists(key)) {
				//总条数
				jedis.hset(key, "TOTAL", String.valueOf(total));
				//剩余条数
				jedis.hset(key, "DCOUNT", String.valueOf(total));
				//发送成功数
				jedis.hset(key, "OK", String.valueOf(0));
				//发送失败数
				jedis.hset(key, "FAIL", String.valueOf(0));
				//发送失败数
				jedis.hset(key, "FAILMOBILES", "");
			} else {
				jedis.hincrBy(key, "TOTAL", total);
				jedis.hincrBy(key, "DCOUNT", total);
			}
		}  catch (JedisConnectionException ex) {
			//logger.error(String.format("Jedis connection fail!!! %s", args.toString()), ex);
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	
	public void pushSubmitSmsOK(String queueName, MsgMtLog msgMtLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String key = String.format("%s:%s:%s:SUBMIT", smsQueuePrefix, msgMtLog.getCorpno(), msgMtLog.getSeqno());
			//System.out.println(key);
			if (jedis.exists(key)) {
				//发送计数
				jedis.hincrBy(key, "OK", 1);
				jedis.hincrBy(key, "DCOUNT", -1);
				
				//插入入库队列
				jedis.rpush(queueName, ObjectUtil.object2String(msgMtLog));
			}
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void pushSubmitMmsOK(String queueName, MmsMtLog mmsMtLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String key = String.format("%s:%s:%s:SUBMIT", mmsQueuePrefix, mmsMtLog.getCorpno(), mmsMtLog.getSeqno());
			//System.out.println(key);
			if (jedis.exists(key)) {
				//发送计数
				jedis.hincrBy(key, "OK", 1);
				jedis.hincrBy(key, "DCOUNT", -1);
				
				//插入入库队列
				jedis.rpush(queueName, ObjectUtil.object2String(mmsMtLog));
			}
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void pushSubmitSmsFAIL(String queueName, MsgMtLog msgMtLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String key = String.format("%s:%s:%s:SUBMIT", smsQueuePrefix, msgMtLog.getCorpno(), msgMtLog.getSeqno());
			if (jedis.exists(key)) {
				jedis.hincrBy(key, "FAIL", 1);
				jedis.hincrBy(key, "DCOUNT", -1);
				String hval = jedis.hget(key, "FAILMOBILES");
				jedis.hset(key, "FAILMOBILES", String.format("%s%s,", hval, msgMtLog.getDestTerminalId()));
				
				//插入入库队列
				jedis.rpush(queueName, ObjectUtil.object2String(msgMtLog));
			}
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {
			
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void pushSubmitMmsFAIL(String queueName, MmsMtLog mmsMtLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String key = String.format("%s:%s:%s:SUBMIT", mmsQueuePrefix, mmsMtLog.getCorpno(), mmsMtLog.getSeqno());
			if (jedis.exists(key)) {
				jedis.hincrBy(key, "FAIL", 1);
				jedis.hincrBy(key, "DCOUNT", -1);
				String hval = jedis.hget(key, "FAILMOBILES");
				jedis.hset(key, "FAILMOBILES", String.format("%s%s,", hval, mmsMtLog.getDestTerminalId()));
				
				//插入入库队列
				jedis.rpush(queueName, ObjectUtil.object2String(mmsMtLog));
			}
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {
			
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void pushDeliverSmsReport(String queueName, MsgMoLog msgMoLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			//插入入库队列
			jedis.rpush(queueName, ObjectUtil.object2String(msgMoLog));
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {
			
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void pushDeliverMmsReport(String queueName, MmsReportLog mmsReportLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			//插入入库队列
			jedis.rpush(queueName, ObjectUtil.object2String(mmsReportLog));
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {
			
		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void pushDeliverSmsMo(String queueName, MsgMoLog msgMoLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//插入入库队列
			jedis.rpush(queueName, ObjectUtil.object2String(msgMoLog));
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	public void pushDeliverMmsMo(String queueName, MmsMoLog mmsMoLog) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			//插入入库队列
			jedis.rpush(queueName, ObjectUtil.object2String(mmsMoLog));
		}  catch (JedisConnectionException ex) {
			// 销毁对象
			jedisPool.returnBrokenResource(jedis);
			throw ex;
		} catch (Exception ex) {

		} finally {
			if (null != jedis)
				jedisPool.returnResource(jedis);
		}
	}
	
	/**
	 * 释放Jedis实例到连接池
	 * @param jedis
	 */
	public void destroyPool() {
		jedisPool.destroy();
		//jedisPool = null;
		error = "NOT_INIT";
	}

	public boolean isAlive() {
		return error == null;
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
	
	public String getError() {
		return eMsg;
	}
}
