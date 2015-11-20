package com.aesirteam.smep.testcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aesirteam.smep.client.message.ReqMmsMessage;
import com.aesirteam.smep.client.message.RspMmsMessage;
import com.aesirteam.smep.client.message.RspMobileBean;
import com.aesirteam.smep.client.message.RspSmsMessage;
import com.aesirteam.smep.client.MasInterface;
import com.aesirteam.smep.client.message.ReqSmsMessage;
import com.aesirteam.smep.core.ProtocolType;
import com.aesirteam.smep.resources.JedisFactory;
import com.aesirteam.smep.resources.SysParams;

public class TestClient {
	protected static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	protected static ApplicationContext ctx;
	protected static MasInterface mas;
	protected SysParams params;
	protected JedisFactory jedisFactory;
	protected static String destAddr, mobileSplitStr;
	protected static String[] msgContent, adjFiles;
	
	@Before
	public void setUp() throws Exception {
		if(null == ctx) {
			ctx = new ClassPathXmlApplicationContext(new String[] {"classpath*:project-masclient.xml"});
			mas = (MasInterface)ctx.getBean("masInterface");
		}
		
		params = mas.getSysParams();
		
		jedisFactory = mas.getJedisFactory();
		
		mobileSplitStr = params.getVal("mobileSplitStr", String.class);
				
		if (null == destAddr) {
			File daFile = new File(params.getVal("test.destAddrFile", String.class));
			assertEquals(daFile.exists(),true);	
			destAddr = getDestAddr(daFile);
		}
		
		if (null == msgContent) {
			File msgFile = new File(params.getVal("test.msgContentFile", String.class));
			assertEquals(msgFile.exists(),true);
			msgContent = getMsgContent(msgFile);
			assertNotNull(msgContent);
		}
		
		if (null == adjFiles) {
			File adjFilePath = new File(params.getVal("test.mmsAdjFiles", String.class));
			assertEquals(adjFilePath.exists(),true);
			adjFiles = getMmsAdjFiles(adjFilePath);
			assertNotNull(adjFiles);
		} 
		
	}
		
	@Test
	public void testSendSms() {
		//第一步: 发送前首先验证号码文件		
		RspMobileBean rspMobileBean = mas.validateMobile(destAddr);
		assertNotNull(rspMobileBean);
		assertEquals(rspMobileBean.getRspCode(), "0");
		
		//第二步: 组装ReqSmsMessage消息体
		ReqSmsMessage reqSms = new ReqSmsMessage();
		for (int i = 0 ; i < 4; i++) {
			reqSms.setCorpNo(params.getVal("test.corpNo", String.class));
			reqSms.setSrcAddr(params.getVal("test.srcAddr", String.class));
			
			//因为指定移动号码发送，指定协议为cmpp20
			switch(i) {
			case 0:
				assertEquals(rspMobileBean.getChinaMobileCount() > 0, true);
				reqSms.setDestAddr(rspMobileBean.getChinaMobile());
				reqSms.setDestAddrTotal(rspMobileBean.getChinaMobileCount());
				reqSms.setProtocolType(ProtocolType.CMPP20.getValue());
				break;
			case 1:
				assertEquals(rspMobileBean.getChinaMobileCount() > 0, true);
				reqSms.setDestAddr(rspMobileBean.getChinaMobile());
				reqSms.setDestAddrTotal(rspMobileBean.getChinaMobileCount());
				reqSms.setProtocolType(ProtocolType.CMPP30.getValue());
				reqSms.setLinkId(String.valueOf(System.currentTimeMillis()));
				break;
			case 2:
				assertEquals(rspMobileBean.getChinaTelecomCount() > 0, true);
				reqSms.setDestAddr(rspMobileBean.getChinaTelecom());
				reqSms.setDestAddrTotal(rspMobileBean.getChinaTelecomCount());
				reqSms.setProtocolType(ProtocolType.SMGP30.getValue());
				break;
			case 3:
				assertEquals(rspMobileBean.getChinaUnicomCount() > 0, true);
				reqSms.setDestAddr(rspMobileBean.getChinaUnicom());
				reqSms.setDestAddrTotal(rspMobileBean.getChinaUnicomCount());
				reqSms.setProtocolType(ProtocolType.SGIP12.getValue());
				break;
			}
				
			reqSms.setMsgContent(msgContent);
			
			reqSms.setSplit(msgContent.length > 1 ? true : false);
			reqSms.setPriorityLevel(params.getVal("test.priorityLevel", Integer.class));
			reqSms.setNeedStautsReport(params.getVal("test.needStautsReport", Integer.class));
			
			String atTime = params.getVal("test.atTime", String.class).toUpperCase();
			if (!atTime.equalsIgnoreCase("NULL") && isDate(atTime)) {
					try {
						reqSms.setAtTime(df.parse(atTime)); 
					} catch (ParseException e) {}
			}
			
			String vaildTime = params.getVal("test.vaildTime", String.class).toUpperCase();
			if (!vaildTime.equalsIgnoreCase("NULL") && isDate(vaildTime)) {
					try {
						reqSms.setVaildTime(df.parse(vaildTime)); 
					} catch (ParseException e) {}
			}
			
			reqSms.setCreateTime(new Date());
			reqSms.setCreateor(params.getVal("test.createor", String.class));
			
			//第三步: 提交短信
			RspSmsMessage rsp = mas.sendSms(reqSms);		
			assertNotNull(rsp);
			System.out.println(String.format("=====testSendSms:=====\r\nseqNo:%s:\tRspCode:%s", reqSms.getSeqNo(),rsp.getRspCode()));
			assertEquals(rsp.getRspCode(), "0");
		}
	}
	
	@Test
	public void testSendMms() {
		//第一步: 发送前首先验证号码文件		
		RspMobileBean rspMobileBean = mas.validateMobile(destAddr);
		assertNotNull(rspMobileBean);
		assertEquals(rspMobileBean.getRspCode(), "0");
		assertEquals(rspMobileBean.getChinaMobileCount() > 0, true);
		
		//第二步: 组装ReqMmsMessage消息体
		ReqMmsMessage reqMms = new ReqMmsMessage();
		reqMms.setCorpNo(params.getVal("test.corpNo", String.class));
		reqMms.setPriorityLevel(params.getVal("test.priorityLevel", Integer.class));
		reqMms.setNeedStautsReport(params.getVal("test.needStautsReport", Integer.class));
		reqMms.setDestAddr(rspMobileBean.getChinaMobile());
		reqMms.setDestAddrTotal(rspMobileBean.getChinaMobileCount());
		reqMms.setProtocolType(ProtocolType.MM7.getValue());
		reqMms.setSubject("测试彩信1");
		reqMms.setCreateTime(new Date());
		reqMms.setCreateor(params.getVal("test.createor", String.class));
		reqMms.setSrcAddr(params.getVal("test.sendAddress", String.class));
		reqMms.setAdjFilePaths(adjFiles);
		reqMms.setBodyType(ReqMmsMessage.BODY_FILENAME);
		try {
			String atTime = params.getVal("test.atTime", String.class).toUpperCase();
			if (!atTime.equalsIgnoreCase("NULL") && this.isDate(atTime)) {
				reqMms.setAtTime(df.parse(atTime));
			}
		
			String vaildTime = params.getVal("test.vaildTime", String.class).toUpperCase();
			if (!vaildTime.equalsIgnoreCase("NULL") && this.isDate(vaildTime)) {
				reqMms.setVaildTime(df.parse(vaildTime));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//第三步: 提交彩信
		RspMmsMessage rsp = mas.sendMms(reqMms);
		assertNotNull(rsp);
		System.out.println(String.format("=====testSendMms:=====\r\nseqNo:%s:\tRspCode:%s", reqMms.getSeqNo(),rsp.getRspCode()));
		assertEquals(rsp.getRspCode(), "0");
	}
	
	private String getDestAddr(File f) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String lineContent = null ;	
			StringBuffer sb = null;
			while((lineContent = br.readLine()) != null) {
				if (sb == null) sb = new StringBuffer();
				sb.append(lineContent).append(",");
			}
		
			if (sb != null) {
				return mas.formatDestAddr(sb.toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	private String[] getMsgContent(File f) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String lineContent = null ;	
			while((lineContent = br.readLine()) != null) {
				list.add(lineContent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return mas.formatMsgContent(list.toArray(new String[list.size()]));
	}
	
	private String[] getMmsAdjFiles(File f) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String lineContent = null ;	
			while((lineContent = br.readLine()) != null) {
				list.add(lineContent);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return mas.formatMsgContent(list.toArray(new String[list.size()]));
	}
	
	 private boolean isDate(String date)
     {
         Pattern p = Pattern.compile("([1][9][4-9][0-9])|([2][0-9][0-9][0-9])-(([0]?[0-9])|([1]?[0-2]))-(([0-2]?[0-9])|([3][0-1]))[ ](([0-1]?[0-9])|([2]?[0-3])):([0-5]?[0-9]):([0-5]?[0-9])$");
         Matcher m = p.matcher(date);
         return m.matches();
     }
}
