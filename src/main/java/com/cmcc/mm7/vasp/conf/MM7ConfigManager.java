/**File Name:MM7ConfigManager.java
 * Company:  中国移动集团公司
 * Date  :   2004-1-3
 * */

package com.cmcc.mm7.vasp.conf;

import java.io.*;
import java.util.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.HashMap;

public class MM7ConfigManager {
	public Map<String, Object> hashmap = new HashMap<String, Object>();

	public MM7ConfigManager() {
	}

	/** read XML file through the parameter and get value */
	public void load(String xml) {
		hashmap.clear();
		try {
			hashmap = readXMLFile(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** save config file */
	public void save(String configFileName) {
		// String indent = "	";
		// boolean newLines = false;
		// String Charset = "gb2312";
		// Document doc = null;
		SAXBuilder sb = new SAXBuilder();
		String inFile = System.getProperty("user.dir");
		String fileNameArray[] = configFileName.split("/");
		if (fileNameArray != null) {
			if (fileNameArray[0].indexOf(":") > 0) {
				inFile = configFileName;
			} else {
				if (fileNameArray[0].equals("..")) {
					int index = inFile.lastIndexOf("/");
					inFile = inFile.substring(0, index);
				}
				for (int i = 1; i < fileNameArray.length; i++) {
					inFile = inFile + File.separator + fileNameArray[i];
				}
			}
		}
		try {
			// doc =
			sb.build(new FileInputStream(configFileName));
		} catch (JDOMException jdom) {
			System.err.println(jdom.getMessage());
			System.exit(1);
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
	}

	/** read XML File and parser it */
	private Map<String, Object> readXMLFile(String xml) throws Exception {
		Document doc;
		hashmap.clear();
		try {
			SAXBuilder saxb = new SAXBuilder();
			//doc = saxb.build(new FileInputStream(inFile));
			doc = saxb.build(new StringReader(xml));
			
			Element root = doc.getRootElement();
			// List children = root.getChildren();
			int size = root.getChildren().size();
			List<String> mmscIP = new ArrayList<String>();
			for (int i = 0; i < size; i++) {
				Element element = (Element) root.getChildren().get(i);
				if (element.getName().equals("mmscIP")) {
					mmscIP.add(element.getTextTrim());
				} else {
					hashmap.put(element.getName(), element.getTextTrim());
				}
			}
			hashmap.put("mmscIP", mmscIP);
		} catch (JDOMException jdom) {
			System.err.println(jdom.getMessage());
			System.exit(1);
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
		return (this.hashmap);
	}
}