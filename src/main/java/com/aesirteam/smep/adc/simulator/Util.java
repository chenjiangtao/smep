package com.aesirteam.smep.adc.simulator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import com.aesirteam.smep.util.SecurityTool;
import com.aesirteam.smep.adc.simulator.soap.APServiceLocator;
import com.aesirteam.smep.adc.simulator.soap.APServiceSoap;

public class Util {
	protected final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSSS");
	protected final String filePath = java.net.URLDecoder.decode(getClass().getResource("conf.properties").getFile(),"utf-8");
	protected static Map<String, String> props;
	
	public Util() throws IOException {
		if (null == props) {
			props = new HashMap<String, String>();
			props.put("secret", readProperty("secret"));
			props.put("endpoint", readProperty("endpoint"));
		}
	}
	
	public String soapHttpClient(String action, String puddata) {
		System.out.println("client-request: ".concat(puddata));
		try {
			System.out.println(decrypt(puddata));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			byte flag = 0;
			String ret = null;
			
			if ("CorpBinding".equals(action))  
				flag = 1; 
			else if ("DeptBinding".equals(action))
				flag = 2;
			else if ("StaffBinding".equals(action))
				flag = 3;
			
			APServiceSoap call = new APServiceLocator().getAPServiceSoap(new java.net.URL(getEndpoint()));
			
			switch(flag)
			{
				case 1:
					ret = call.corpBinding(puddata);
					break;
					
				case 2:
					ret = call.deptBinding(puddata);
					break;
					
				case 3:
					ret = call.staffBinding(puddata);
					break;
			}
			call = null;
			System.out.println("\r\nclient-response: ".concat(ret));
			try {
				System.out.println(decrypt(ret));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	protected String readProperty(String propName) throws IOException {
		InputStream in = getClass().getResourceAsStream("conf.properties");
		if (null == in) 
			throw new IOException("File not found: conf.properties");
		
		Properties p = new Properties();
		try {
			p.load(in);
			return p.getProperty(propName);
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (null != in) {
				in.close();
			}
			p = null;
		}
	}
	
	protected void writeProperty(String propName, String propValue) throws IOException {
		InputStream in = getClass().getResourceAsStream("conf.properties");
		if (null == in) 
			throw new IOException("File not found: conf.properties");
		
		Properties p = new Properties();
		OutputStream out = null;
		try {
			p.load(in);
			out = new FileOutputStream(filePath);
			p.setProperty(propName, propValue);
			p.store(out,"");
			System.out.println("Update properties file: ".concat(propName).concat(" = ").concat(propValue));
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (null != out) {
				out.close();
			}
			
			if (null != in) {
				in.close();
			}
			
			p = null;
		}
	}
	
	public String getTimestamp() {
		return sdf.format(new java.util.Date());
	}
	
	public void setEndpoint(String val) throws IOException {
		String key = "endpoint";
		if (props.containsKey(key)) {
			props.put(key, val);
			writeProperty(key, val);
		}
	}
	
	public String getEndpoint() throws IOException {
		String key = "endpoint";
		return props.containsKey(key) ? props.get(key) : "";
	}
	
	public void setSecret(String val) throws IOException {
		String key = "secret";
		if (props.containsKey(key)) {
			props.put(key, val);
			writeProperty(key, val);
		}
	}
	
	public String getSecret() throws IOException {
		String key = "secret";
		return props.containsKey(key) ? props.get(key) : "";
	}
	
	public String getXmlText(Document doc) throws IOException {
		XMLWriter xmlWriter = null;
		try {
			StringWriter sw = new StringWriter();
			OutputFormat format = OutputFormat.createCompactFormat();
			format.setEncoding("UTF8");
			xmlWriter = new XMLWriter(sw, format);
			xmlWriter.write(doc);
			xmlWriter.flush();
			return sw.getBuffer().toString();
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (null != xmlWriter) {
				xmlWriter.close();
				xmlWriter = null;
			}
		}
	}
	
	public String encrypt(String str) throws Exception {
		SecurityTool.setDesSecret(getSecret());
		return SecurityTool.encrypt(str);
	}

	public String decrypt(String str) throws Exception {
		Document document = DocumentHelper.parseText(str);
		Element root = document.getRootElement();
		
		//String code = root.element("HEAD").element("CODE").getTextTrim();
		//String sid = root.element("HEAD").element("SID").getTextTrim();
		//System.out.println(code);
		String bodyStr = root.element("BODY").getTextTrim();
		if (null == bodyStr || bodyStr.trim().length() == 0)
			throw new java.lang.NoSuchFieldError();
		
		SecurityTool.setDesSecret(getSecret());
		return SecurityTool.decrypt(bodyStr);
	}
	
	public static void main(String args[]) {
		Util util;
		try {
			util = new Util();
			System.out.println(java.net.URLDecoder.decode(util.getClass().getResource("conf.properties").getFile(),"utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
