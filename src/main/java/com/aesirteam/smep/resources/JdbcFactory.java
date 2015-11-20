package com.aesirteam.smep.resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.huawei.insa2.util.Args;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

@Component("jdbcFactory")
public class JdbcFactory {
	protected final static Logger logger = LoggerFactory.getLogger(JedisFactory.class);
	protected Map<String, Object> params;
	protected boolean needModify;
	
	@Resource(name="dataSource")
	BoneCPDataSource connectionPool;
	
	public JdbcFactory() {}
	
	public int reset() {
		
		if (!needModify)
			return -100;
		
		Args args = new Args(params);
		
		try {
			String driverClass = connectionPool.getDriverClass();
			if (null != driverClass && driverClass.trim().length() > 0) {
				Class.forName(driverClass);
			}
		} catch (ClassNotFoundException ex) {
			return -101;
		}
		
		BoneCPConfig config = new BoneCPConfig();
		String jdbcUrl = args.get("jdbcUrl", null);
		if (null == jdbcUrl || jdbcUrl.trim().length() == 0) { 
			jdbcUrl = connectionPool.getJdbcUrl();
		}
		config.setJdbcUrl(jdbcUrl);
		
		String username = args.get("username", null);
		if (null == username || username.trim().length() == 0) {
			username = connectionPool.getUsername(); 
		}
		config.setUsername(username);
		
		String password = args.get("password", null);
		if (null == password  || password.trim().length() == 0) {
			password = connectionPool.getPassword(); 
		}
		config.setPassword(password);
		
		//设置数据库中的空闲连接数检查周期(分钟)
		long idleConnectionTestPeriodInMinutes = args.get("idleConnectionTestPeriodInMinutes", -1);
		if (idleConnectionTestPeriodInMinutes <= 0) {
			idleConnectionTestPeriodInMinutes = connectionPool.getIdleConnectionTestPeriodInMinutes();
		}
		config.setIdleConnectionTestPeriodInMinutes(idleConnectionTestPeriodInMinutes);
		
		//设置连接空闲时间(分钟)
		long idleMaxAgeInMinutes = args.get("idleMaxAgeInMinutes", -1);
		if (idleMaxAgeInMinutes <= 0) {
			idleMaxAgeInMinutes = connectionPool.getIdleMaxAgeInMinutes();
		}
		config.setIdleMaxAgeInMinutes(idleMaxAgeInMinutes);
		
		//设置每个分区中的最大连接数
		int maxConnectionsPerPartition = args.get("maxConnectionsPerPartition", -1);
		if (maxConnectionsPerPartition <= 0) {
			maxConnectionsPerPartition = connectionPool.getMaxConnectionsPerPartition();
		}
		config.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
		
		//设置每个分区中的最小连接数
		int minConnectionsPerPartition = args.get("minConnectionsPerPartition", -1);
		if (minConnectionsPerPartition <= 0) {
			minConnectionsPerPartition = connectionPool.getMinConnectionsPerPartition();
		}
		config.setMinConnectionsPerPartition(minConnectionsPerPartition);
		
		//设置分区数
		int partitionCount = args.get("partitionCount", -1);
		if (partitionCount <= 0) {
			partitionCount = connectionPool.getPartitionCount();
		}
		config.setPartitionCount(partitionCount);
		
		//当连接池中的连接耗尽的时候 BoneCP一次同时获取的连接数
		int acquireIncrement = args.get("acquireIncrement", -1);
		if (acquireIncrement <= 0) {
			acquireIncrement = connectionPool.getAcquireIncrement();
		}
		config.setAcquireIncrement(acquireIncrement);
		
		//连接释放处理
		int releaseHelperThreads = args.get("releaseHelperThreads", -1);
		if (releaseHelperThreads <= 0) {
			releaseHelperThreads = connectionPool.getReleaseHelperThreads();
		}
		config.setReleaseHelperThreads(releaseHelperThreads);
		
		//关闭连接池
		if (null != connectionPool) {
			connectionPool.close();
			connectionPool = null;
		}
		
		connectionPool = new BoneCPDataSource(config);
		//测试连接
		boolean eSuccess = false;
		try {
			Connection connection = connectionPool.getConnection();
			if (null != connection) {
				//System.out.println("Connection successful!");
				eSuccess = connection.createStatement().execute("select 1");
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -2;
		} 
		return eSuccess ? 0 : -1;
	}
	
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
		
	public void setParams(Map<String, Object> params) {
		this.params = params;
		needModify = (null == params || params.size() == 0) ? false : true;
	}
}
