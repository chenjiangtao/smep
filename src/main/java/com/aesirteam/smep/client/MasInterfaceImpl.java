package com.aesirteam.smep.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import redis.clients.jedis.exceptions.JedisConnectionException;
import com.aesirteam.smep.client.message.*;
import com.aesirteam.smep.resources.*;
import com.aesirteam.smep.util.*;

public class MasInterfaceImpl implements MasInterface {

	@Resource(name="jedisFactory")
	protected JedisFactory jedis;
	
	@Resource(name="sysParams")
	protected SysParams params;
	
	@Resource(name="illegalContentFilter")
	protected IllegalContentFilter rulesFilter;
	
	@Override
	public RspSmsMessage sendSms(ReqSmsMessage reqSmsMessage) {
		//检查消息体及各项值的合法性
		if (null == reqSmsMessage) {
			return RspSmsMessage.getErrorMessage(MasConstants.REQSMSMESSAGE_ISNULL);
		}
		
		String smsQueueName = params.getVal("sms.queueName", String.class);
		long maxQueueLength = params.getVal("sms.queueMaxLength", Long.class);
		boolean bZip = params.getVal("sms.bZip", Boolean.class);
		String mobileSplitStr = params.getVal("mobileSplitStr", String.class);
		
		//检查当前队列等待数量是否到达最大上限值
		long currQueueLength = 0;
		try {
			currQueueLength = jedis.llen(smsQueueName);
		} catch (JedisConnectionException ex) {
			return RspSmsMessage.getErrorMessage(MasConstants.REDIS_INIT_OBJECT_ISNULL);
		}
		
		
		if (currQueueLength >= maxQueueLength) {
			return RspSmsMessage.getErrorMessage(MasConstants.SUBMIT_TO_REDIS_MAXLIMIT);
		}
		
		if (null == reqSmsMessage.getCorpNo() || reqSmsMessage.getCorpNo().length() == 0) {
			return RspSmsMessage.getErrorMessage(MasConstants.SMS_CORPNO_ISNULL);
		}
		/*
		if (null == reqSmsMessage.getServiceId() || reqSmsMessage.getServiceId().length() == 0) {
			return RspSmsMessage.getErrorMessage(MasConstants.SMS_SERVICESID_ISNULL);
		}
		*/
		String destAddr = formatDestAddr(reqSmsMessage.getDestAddr());
		if (null == destAddr || destAddr.length() == 0) {
			return RspSmsMessage.getErrorMessage(MasConstants.SMS_DESTADDR_ISNULL);
		}
		
		reqSmsMessage.setDestAddr(destAddr);
		reqSmsMessage.setDestAddrTotal(destAddr.split(mobileSplitStr).length);
				
		String[] msgContent = reqSmsMessage.getMsgContent(); 
		if (null == msgContent || msgContent.length == 0) {
			return RspSmsMessage.getErrorMessage(MasConstants.SMS_CONTENT_ISNULL);
		}
		
		//剔除空字符串的内容分段
		reqSmsMessage.setMsgContent(formatMsgContent(msgContent));
		
		//将请求对象转为字符串
		String objStr = null;
		try {
			objStr = ObjectUtil.object2String(reqSmsMessage, bZip);
		} catch (Exception e) {
			return RspSmsMessage.getErrorMessage(MasConstants.MESSAGE_CONVERT_EXCEPTION);
		}
		
		//ReqSmsMessage _reqSmsMessage = ObjectUtil.string2Object(objStr, ReqSmsMessage.class, bZip);
		//System.out.println(_reqSmsMessage.getCorpNo());

		RspSmsMessage rspSmsMessage = new RspSmsMessage();
		try {
			if (0 < jedis.push(smsQueueName, objStr, true)) {
				rspSmsMessage.setRspCode(MasConstants.SUBMIT_TO_REDIS_SUCCESS + "");
				rspSmsMessage.setRspMsg("提交短信成功");
				rspSmsMessage.setRspDetail(reqSmsMessage.getSeqNo());
			} else {
				rspSmsMessage.setRspCode(MasConstants.SUBMIT_TO_REDIS_FAIL + "");
				rspSmsMessage.setRspMsg("提交短信失败");
				rspSmsMessage.setRspDetail(reqSmsMessage.getSeqNo());
			}		
		} catch (JedisConnectionException e) {
			rspSmsMessage.setRspCode(MasConstants.SUBMIT_TO_REDIS_FAIL + "");
			rspSmsMessage.setRspMsg("系统异常");
			rspSmsMessage.setRspDetail(e.getMessage());
		}
		
		return rspSmsMessage;
	}

	@Override
	public RspSmsMessage sendSmsByFile(ReqSmsMessage reqSmsMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RspMmsMessage sendMms(ReqMmsMessage reqMmsMessage) {
		//检查消息体及各项值的合法性
		if (null == reqMmsMessage) {
			return RspMmsMessage.getErrorMessage(MasConstants.REQMMSMESSAGE_ISNULL);
		}
		
		String mmsQueueName = params.getVal("mms.queueName", String.class);
		long maxQueueLength = params.getVal("mms.queueMaxLength", Long.class);
		boolean bZip = params.getVal("mms.bZip", Boolean.class);
		String mobileSplitStr = params.getVal("mobileSplitStr", String.class);
		
		//检查当前队列等待数量是否到达最大上限值
		long currQueueLength = 0;
		try {
			currQueueLength = jedis.llen(mmsQueueName);
		} catch (JedisConnectionException ex) {
			return RspMmsMessage.getErrorMessage(MasConstants.REDIS_INIT_OBJECT_ISNULL);
		}
		
		
		if (currQueueLength >= maxQueueLength) {
			return RspMmsMessage.getErrorMessage(MasConstants.SUBMIT_TO_REDIS_MAXLIMIT);
		}
		
		if (null == reqMmsMessage.getCorpNo() || reqMmsMessage.getCorpNo().length() == 0) {
			return RspMmsMessage.getErrorMessage(MasConstants.MMS_CORPNO_ISNULL);
		}
		/*
		if (null == reqMmsMessage.getServiceId() || reqMmsMessage.getServiceId().length() == 0) {
			return RspMmsMessage.getErrorMessage(MasConstants.MMS_SERVICESID_ISNULL);
		}
		*/
		String destAddr = formatDestAddr(reqMmsMessage.getDestAddr());
		if (null == destAddr || destAddr.length() == 0) {
			return RspMmsMessage.getErrorMessage(MasConstants.MMS_DESTADDR_ISNULL);
		}
		
		reqMmsMessage.setDestAddr(destAddr);
		reqMmsMessage.setDestAddrTotal(destAddr.split(mobileSplitStr).length);
		
		if (null == reqMmsMessage.getAdjFilePaths() || 0 == reqMmsMessage.getAdjFilePaths().length) {
			return RspMmsMessage.getErrorMessage(MasConstants.MMS_ADJFILES_ISNULL);
		}
		
		try {
			if (ReqMmsMessage.BODY_FILENAME == reqMmsMessage.getBodyType())
				reqMmsMessage.setFile(MMSUtil.getMMSFile(reqMmsMessage.getAdjFilePaths()));
		} catch (Exception e) {
			return RspMmsMessage.getErrorMessage(MasConstants.MMS_READ_ADJFILES_EXCEPTION);
		}
		
		//将请求对象转为字符串
		String objStr = null;
		try {
			objStr = ObjectUtil.object2String(reqMmsMessage, bZip);
		} catch (Exception e) {
			return RspMmsMessage.getErrorMessage(MasConstants.MESSAGE_CONVERT_EXCEPTION);
		}
		
		RspMmsMessage rspMmsMessage = new RspMmsMessage();
		try {
			if (0 < jedis.push(mmsQueueName, objStr, true)) {
				rspMmsMessage.setRspCode(MasConstants.SUBMIT_TO_REDIS_SUCCESS + "");
				rspMmsMessage.setRspMsg("提交彩信成功");
				rspMmsMessage.setRspDetail(reqMmsMessage.getSeqNo());
			} else {
				rspMmsMessage.setRspCode(MasConstants.SUBMIT_TO_REDIS_FAIL + "");
				rspMmsMessage.setRspMsg("提交彩信失败");
				rspMmsMessage.setRspDetail(reqMmsMessage.getSeqNo());
			}		
		} catch (JedisConnectionException e) {
			rspMmsMessage.setRspCode(MasConstants.SUBMIT_TO_REDIS_FAIL + "");
			rspMmsMessage.setRspMsg("系统异常");
			rspMmsMessage.setRspDetail(e.getMessage());
		}
		
		return rspMmsMessage;
	}

	@Override
	public RspMmsMessage sendMmsByFile(ReqMmsMessage reqMmsMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RspMobileBean validateMobile(String mobiles) {
		String mobileSplitStr = params.getVal("mobileSplitStr", String.class);
		
		// 整理输入的号码，去除多余分隔符
		mobiles = formatDestAddr(mobiles);

		if (null == mobiles || 0 == mobiles.length()) {
			return RspMobileBean.getErrorMessage(MasConstants.MOBILE_IS_NULL);
		}

		// 装载号码到Map,并初始化号码状态 0：表示错误或未检查
		ValidateMobileFactory validateMobileFactory = new ValidateMobileFactory();
		StringTokenizer token = new StringTokenizer(mobiles, mobileSplitStr);
		while (token.hasMoreTokens()) {
			validateMobileFactory.addMobile(token.nextToken(), (byte) -1);
		}
		token = null;

		return validateMobileFactory.getResult();
	}

	@Override
	public RspMobileBean createMobileFile(String srcFile) {
		try {
			// 从文件中读取号码
			ValidateMobileFactory validateMobileFactory = new ValidateMobileFactory();
			SplitMobileThread splitMobileThread = new SplitMobileThread(srcFile, validateMobileFactory);
			splitMobileThread.start();

			do {
				try { Thread.sleep(100); } catch (InterruptedException e) {} 
			} while(!splitMobileThread.isFinish());

			//获取验证结果
			RspMobileBean rsp = validateMobileFactory.getResult();
			rsp.setRspMsg("");
			rsp.setRspDetail("");

			String jsonStr =  ObjectUtil.object2String(rsp, false);
			if (null == jsonStr) {
				rsp = RspMobileBean.getErrorMessage(MasConstants.UNKNOWN_EXCEPTION);
				rsp.setRspMsg("创建号码文件失败(Json)");
				return rsp;
			}
			
			//json字符串写入文件并计算md5
			String newFilename = writeNumFile(srcFile.substring(0, srcFile.lastIndexOf(File.separator)), jsonStr);
			
			if (null == newFilename) {
				rsp = RspMobileBean.getErrorMessage(MasConstants.UNKNOWN_EXCEPTION);
				rsp.setRspMsg("创建号码文件失败(WriteFile)");
				return rsp;
			}
			String md5Str = MD5Util.getMD5ByFilename(newFilename);
			
			rsp.setRspMsg(newFilename);
			rsp.setRspDetail(md5Str);
			
			//写文件的描述文件
			writeNumFileInfo(rsp);
			return rsp;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RspMobileBean validateMobileFile(String targetFile) {
		if (null == targetFile || targetFile.length() == 0)
			return null;
		
		//计算文件md5
		String md5Str = MD5Util.getMD5ByFilename(targetFile);
		
		//读取xml文件
		String xmlfile = targetFile.replaceAll(".txt", ".xml");
		Document document;
		
		try {
			document = loadXML(xmlfile);
			Element root = document.getRootElement();
			String _targetFile = new File(targetFile).getName();
			String _xmlFile = new File(root.element("HEAD").element("FILE").getTextTrim()).getName();
			if (_targetFile.equalsIgnoreCase(_xmlFile) && md5Str.equals(root.element("HEAD").element("MD5").getTextTrim())) {
				//验证通过读取文件
				return (RspMobileBean) ObjectUtil.file2Object(targetFile, RspMobileBean.class);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}  finally {
			document = null;
		}
		return null;
	}

	@Override
	public Map<QTYPE, Object> findQueueInfo(QTYPE type, String corpNo, String seqNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> checkMessageContent(String messageContent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getTotalRunInfo(QTYPE type, String corpNo,
			String creatorId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String formatDestAddr(String mobiles) {
		String mobileSplitStr = params.getVal("mobileSplitStr", String.class);
		
		//替换所有的空格
		if (mobiles.indexOf(" ") >= 0) {
			mobiles = mobiles.replaceAll("\\s+", "");
		}
		
		//替换字头与字尾的分隔符,
		if (mobiles.startsWith(mobileSplitStr)
				|| mobiles.endsWith(mobileSplitStr)
				|| mobiles.indexOf("+") >= 0) {
			mobiles = mobiles.replaceAll(String.format("[+]|^%1$s*|%1$s*$", mobileSplitStr), "");
		}
	
		//替换中段多个分隔符,,,为单个分隔符
		if (mobiles.indexOf(String.format("%1$s%1$s", mobileSplitStr)) >= 0) {
			mobiles = mobiles.replaceAll(String.format("%s(?=[^\\w])", mobileSplitStr), "");
		}
		return mobiles;
	}
	
	@Override
	public String[] formatMsgContent(String[] msgContent) {
		List<String> list = new ArrayList<String>();
		for (String msg : msgContent) {
			if (msg.trim().length() > 0)
				list.add(msg);
		}
		
		if (msgContent.length == list.size()) {
			return msgContent;
		} else {
			return list.toArray(new String[list.size()]);
		}
	}
	
	private Document loadXML(String xmlfile) throws FileNotFoundException, DocumentException {	    
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new FileInputStream(xmlfile));	    
		return doc;	
	}
	
	/**
	 * 在源文件位置生成新整理后的号码文件
	 * @param path
	 * @param text
	 * @return
	 */
	private String writeNumFile(String path, String text) {
		if (null == text || 0 == text.length()) {
			return null;
		}
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			String newFilename = String.format("%s%s%s.txt", path, File.separator, UUID.randomUUID().toString());
			fileWriter = new FileWriter(newFilename, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(text);
			return newFilename;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (null != bufferedWriter) { 
				try { bufferedWriter.close(); } catch (Exception ex) {} 
			}
			
			if (null != fileWriter) { 
				try { fileWriter.close(); } catch (Exception ex) {} 
			}
		}
		return null;
	}
	
	private void writeNumFileInfo(RspMobileBean rsp) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("FILEINFO");
		Element head = root.addElement("HEAD");
		head.addElement("FILE").addText(rsp.getRspMsg());
		head.addElement("MD5").addText(rsp.getRspDetail());
		head.addElement("TIME").addText(String.valueOf(new Date()));
		Element body = root.addElement("BODY");
		body.addElement("TOTAL").setText(String.valueOf(rsp.getTotalCount()));
		body.addElement("VALID").setText(String.valueOf(rsp.getValidCount()));
		body.addElement("INVALID").setText(String.valueOf(rsp.getInvalidCount()));
		body.addElement("CHINAMOBILE").setText(String.valueOf(rsp.getChinaMobileCount()));
		body.addElement("CHINATELECOM").setText(String.valueOf(rsp.getChinaTelecomCount()));
		body.addElement("CHINAUNICOM").setText(String.valueOf(rsp.getChinaUnicomCount()));

		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			String infoFilename = rsp.getRspMsg().replaceAll(".txt", ".xml");
			fileWriter = new FileWriter(infoFilename, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(root.asXML());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (null != bufferedWriter) { 
				try { bufferedWriter.close(); } catch (Exception ex) {}
			}
			
			if (null != fileWriter) {
				try { fileWriter.close(); } catch (Exception ex) {}
			}
		}
	}
	
	private class ValidateMobileFactory {
		
		//移动号段: 1340~1348、135~139、147、150~152、157~159、182~184、187~188
		protected String mobileSegNum = params.getVal("mobileSegNum", String.class);
				
		//电信号段: 133、1349、153、180~181、189
		protected String  telecomSegNum = params.getVal("telecomSegNum", String.class);
				
		//联通号段 : 130~132、145、155~156、185~186
		protected String  unicomSegNum = params.getVal("unicomSegNum", String.class);
		
		protected String mobileSplitStr = params.getVal("mobileSplitStr", String.class);
		
		Map<String, Byte> numMap = new HashMap<String, Byte>();

		public RspMobileBean getResult() {
			RspMobileBean rsp = new RspMobileBean();

			rsp.setTotalCount(numMap.size());
			
			String[] regexs = new String[] { mobileSegNum, telecomSegNum, unicomSegNum};
			
			//创建三大运营商分别创建分拣号码线程
			SortingMobileThread[] smt = new SortingMobileThread[regexs.length];
			byte threadIndex = 0;
			for (String regexStr : regexs) {
				smt[threadIndex] = new SortingMobileThread(numMap, regexStr, threadIndex);
				smt[threadIndex].start();
				threadIndex++;
			}

			do {
				try { Thread.sleep(100); } catch (InterruptedException e) {} 
			} while ( !smt[0].isFinish() || !smt[1].isFinish() || !smt[2].isFinish());
			
			//按分拣结果组装号码段
			rsp.setChinaMobileCount(smt[0].getMobileCount());
			rsp.setChinaMobile(formatDestAddr(smt[0].getMobiles()));

			rsp.setChinaTelecomCount(smt[1].getMobileCount());
			rsp.setChinaTelecom(formatDestAddr(smt[1].getMobiles()));

			rsp.setChinaUnicomCount(smt[2].getMobileCount());
			rsp.setChinaUnicom(formatDestAddr(smt[2].getMobiles()));

			rsp.setValidCount(rsp.getChinaMobileCount() + rsp.getChinaTelecomCount() + rsp.getChinaUnicomCount());

			// 错号号码的分拣及组装
			StringBuffer sbInvalid = new StringBuffer();
			int invalidCount = 0;
			for (Iterator<Map.Entry<String, Byte>> it = numMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Byte> entry = it.next();
				if (entry.getValue() >= 0) continue;
				invalidCount++;
				sbInvalid.append(mobileSplitStr).append(entry.getKey());
			}
			numMap = null;

			rsp.setInvalidCount(invalidCount);
			rsp.setInvalidMobile(formatDestAddr(sbInvalid.toString()));
			sbInvalid = null;

			rsp.setRspCode(MasConstants.MOBILE_VALIDATE_SUCCESS + "");
			rsp.setRspMsg("验证完成");
			rsp.setRspDetail(String.format(
					"号码总数:%d, 错误数:%d, 合格数:%d,其中:移动号码:%d; 电信号码:%d; 联通号码:%d",
					rsp.getTotalCount(), rsp.getInvalidCount(),
					rsp.getValidCount(), rsp.getChinaMobileCount(),
					rsp.getChinaTelecomCount(), rsp.getChinaUnicomCount()));
			return rsp;
		}
		
		public void addMobile(String mobile, Byte flag) {
			if (!numMap.containsKey(mobile)) {
				numMap.put(mobile, (byte) -1);
			}
		}
	}
	
	private class SortingMobileThread extends Thread {
		protected String mobileSplitStr = params.getVal("mobileSplitStr", String.class);
		private boolean finish = false;
		private String regexStr;
		private Map<String, Byte> mapObject;
		private String mobiles;
		private byte threadIndex;
		private int mobileCount = 0;

		public SortingMobileThread(Map<String, Byte> mapObject, String regexStr, byte threadIndex) {
			this.mapObject = mapObject;
			this.regexStr = regexStr;
			this.threadIndex = threadIndex;

		}

		public void run() {
			// long startTm = System.currentTimeMillis();
			StringBuffer sb = new StringBuffer();
			Pattern pattern = Pattern.compile(regexStr);
			for (Iterator<Map.Entry<String, Byte>> it = mapObject.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Byte> entry = it.next();
				String m = entry.getKey();
				if (pattern.matcher(m).matches()) {
					mapObject.put(m, threadIndex);
					sb.append(mobileSplitStr).append(m);
					mobileCount++;
				}
			}
			this.mobiles = sb.toString();
			sb = null;
			this.finish = true;
		}

		public boolean isFinish() {
			return finish;
		}

		public String getMobiles() {
			return mobiles;
		}

		public int getMobileCount() {
			return mobileCount;
		}
	}

	private class SplitMobileThread extends Thread {
		protected String mobileSplitStr = params.getVal("mobileSplitStr", String.class);
		private boolean finish = false;
		private BufferedReader reader;
		private ValidateMobileFactory factory;

		public SplitMobileThread(String srcFile, ValidateMobileFactory factory)
				throws Exception {

			File file = new File(srcFile);
			if (!file.exists() || file.isDirectory()) {
				throw new Exception("File not found");
			}

			reader = new BufferedReader(new FileReader(file));

			this.factory = factory;
		}

		public void run() {
			// 读取号码文件
			try {
				String lineContent = null;
				while ((lineContent = reader.readLine()) != null) {
					String destAddrStr = formatDestAddr(lineContent);
					// 按照分隔符进行号码分段
					Matcher mainMatcher = Pattern.compile(String.format("(\\d[^%s]*)", mobileSplitStr)).matcher(destAddrStr);
					while (mainMatcher.find()) {
						String mainMatcherStr = mainMatcher.group();
						//匹配符合手机号码规则并进行拆分
						Matcher subMatcher =  Pattern.compile("(86)?([13|14|15|18])(.*?)\\d{10}").matcher(mainMatcherStr);
						while (subMatcher.find()) {
							factory.addMobile(subMatcher.group(), (byte) -1);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			} finally {
				if (null != reader) try { reader.close(); } catch (IOException e) {}
				this.finish = true;
			}
		}
		
		public boolean isFinish() {
			return finish;
		}
	}

	@Override
	public JedisFactory getJedisFactory() {
		return jedis;
	}

	@Override
	public SysParams getSysParams() {
		return params;
	}

	@Override
	public IllegalContentFilter getIllegalContentFilter() {
		return rulesFilter;
	}
}
