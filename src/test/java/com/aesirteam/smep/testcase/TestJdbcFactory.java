package com.aesirteam.smep.testcase;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aesirteam.smep.resources.JdbcFactory;


public class TestJdbcFactory {
	
	protected static ApplicationContext ctx;
	protected JdbcFactory jdbcFactory;
	
	@Before
	public void setUp() throws Exception {
		if(null == ctx) {
			ctx = new ClassPathXmlApplicationContext(new String[] {"classpath*:project-database.xml"});
		}
		
		jdbcFactory = (JdbcFactory) ctx.getBean("jdbcFactory");
	}
	
	@Test
	public void testReset() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("password", "repLcmc0613");
		jdbcFactory.setParams(params);
		
		assertEquals(jdbcFactory.reset(), 0);
		
		try {
			boolean rVal = jdbcFactory.getConnection().createStatement().execute("select 1");
			assertEquals(rVal, true);
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		
	}

}
