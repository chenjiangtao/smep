package com.huawei.insa2.util;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import java.net.*;

public class Cfg {

	private static DocumentBuilderFactory factory;
	private static DocumentBuilder builder;
	private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\""
			+ System.getProperty("file.encoding") + "\"?>";
	private static String indent = "  ";
	private boolean isDirty;
	private Document doc;
	private Element root;
	private String file;

	public Cfg(String url) throws IOException {
		this(url, false);
	}

	@SuppressWarnings("deprecation")
	public Cfg(String url, boolean create) throws IOException {

		if (url == null) {
			throw new IllegalArgumentException("url is null");
		}
		if (url.indexOf(':') > 1) {
			this.file = url;
		} else {
			this.file = new File(url).toURL().toString();
		}
		new URL(this.file);
		try {
			load();
		} catch (FileNotFoundException ex) {
			if (!create) {
				throw ex;
			} else {
				loadXMLParser();
				doc = builder.newDocument();
				root = doc.createElement("config");
				doc.appendChild(root);
				isDirty = true;
				flush();
				return;
			}
		}
	}

	public Args getArgs(String key) {
		Map<String, Object> args = new HashMap<String, Object>();
		String[] children = childrenNames(key);
		for (int i = 0; i < children.length; i++) {
			args.put(children[i], get(key + '/' + children[i], null));
		}
		return new Args(args);
	}

	private static void writeIndent(PrintWriter pw, int level) {
		for (int i = 0; i < level; i++) {
			pw.print(indent);
		}
	}

	private static void writeNode(Node node, PrintWriter pw, int deep) {
		switch (node.getNodeType()) {
		case Node.COMMENT_NODE:
			writeIndent(pw, deep);
			pw.print("<!--");
			pw.print(node.getNodeValue());
			pw.println("-->");
			return;
		case Node.TEXT_NODE:
			String value = node.getNodeValue().trim();
			if (value.length() == 0) {
				return;
			}
			writeIndent(pw, deep);
			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				switch (c) {
				case '<':
					pw.print("&lt;");
					break;
				case '>':
					pw.print("&lt;");
					break;
				case '&':
					pw.print("&amp;");
					break;
				case '\'':
					pw.print("&apos;");
					break;
				case '\"':
					pw.print("&quot;");
					break;
				default:
					pw.print(c);
				}
			}
			pw.println();
			return;
		case Node.ELEMENT_NODE:
			if (!node.hasChildNodes()) {
				return;
			}
			for (int i = 0; i < deep; i++) {
				pw.print(indent);
			}
			String nodeName = node.getNodeName();
			pw.print('<');
			pw.print(nodeName);

			NamedNodeMap nnm = node.getAttributes();
			if (nnm != null) {
				for (int i = 0; i < nnm.getLength(); i++) {
					Node attr = nnm.item(i);
					pw.print(' ');
					pw.print(attr.getNodeName());
					pw.print("=\"");
					pw.print(attr.getNodeValue());
					pw.print('\"');
				}
			}

			if (node.hasChildNodes()) {
				NodeList children = node.getChildNodes();
				if (children.getLength() == 0) {
					pw.print('<');
					pw.print(nodeName);
					pw.println("/>");
					return;
				}
				if (children.getLength() == 1) {
					Node n = children.item(0);
					if (n.getNodeType() == Node.TEXT_NODE) {
						String v = n.getNodeValue();
						if (v != null) {
							v = v.trim();
						}
						if (v == null || v.length() == 0) {
							pw.println(" />");
							return;
						} else {
							pw.print('>');
							pw.print(v);
							pw.print("</");
							pw.print(nodeName);
							pw.println('>');
							return;
						}
					}
				}
				pw.println(">");
				for (int i = 0; i < children.getLength(); i++) {
					writeNode(children.item(i), pw, deep + 1);
				}
				for (int i = 0; i < deep; i++) {
					pw.print(indent);
				}
				pw.print("</");
				pw.print(nodeName);
				pw.println(">");
			} else {
				pw.println("/>");
			}
			return;
		case Node.DOCUMENT_NODE:
			pw.println(XML_HEAD);
			NodeList nl = node.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				writeNode(nl.item(i), pw, 0);
			}
			return;
		}
	}

	private Node findNode(String key) {
		Node ancestor = root;
		for (StringTokenizer st = new StringTokenizer(key, "/"); st
				.hasMoreTokens();) {
			String nodeName = st.nextToken();
			NodeList nl = ancestor.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (nodeName.equals(n.getNodeName())) {
					ancestor = n;
					if (!st.hasMoreTokens()) {
						return n;
					}
					break;
				}
			}
		}
		return null;
	}

	private Node createNode(String key) {
		Node ancestor = root;
		token: for (StringTokenizer st = new StringTokenizer(key, "/"); st
				.hasMoreTokens();) {
			String nodeName = st.nextToken();
			NodeList nl = ancestor.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (nodeName.equals(n.getNodeName())) {
					ancestor = n;
					if (st.hasMoreTokens()) {
						continue token;
					} else {
						return ancestor;
					}
				}
			}

			for (;;) {
				Node n = doc.createElement(nodeName);
				ancestor.appendChild(n);
				ancestor = n;
				if (!st.hasMoreTokens()) {
					return ancestor;
				}
				nodeName = st.nextToken();
			}
		}
		return null;
	}

	/*
	 * private Node createNode(Node ancestor, String key) {
	 * 
	 * searchToken: // �����������ѭ���õı�� for (StringTokenizer st = new
	 * StringTokenizer(key, "/"); st .hasMoreTokens();) { String nodeName =
	 * st.nextToken(); NodeList nl = ancestor.getChildNodes(); for (int i = 0; i
	 * < nl.getLength(); i++) { if (nodeName.equals(nl.item(i).getNodeName())) {
	 * ancestor = nl.item(i); continue searchToken; } } return null; } return
	 * ancestor; }
	 */
	public String get(String key, String def) {
		if (key == null) {
			throw new NullPointerException("parameter key is null");
		}

		Node node = findNode(key);
		if (node == null) {
			return def;
		}
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
				return nl.item(i).getNodeValue().trim();
			}
		}
		node.appendChild(doc.createTextNode(def));
		return def;
	}

	public void put(String key, String value) {
		if (key == null) {
			throw new NullPointerException("parameter key is null");
		}
		if (value == null) {
			throw new NullPointerException("parameter value is null");
		}
		value = value.trim();
		Node node = createNode(key);

		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) { // ������һ���ı��ӽڵ�
				String childValue = child.getNodeValue().trim();
				if (childValue.length() == 0) {
					continue;
				}

				if (childValue.equals(value)) {
					return;
				} else {
					child.setNodeValue(value);
					isDirty = true;
					return;
				}
			}
		}

		if (nl.getLength() == 0) {
			node.appendChild(doc.createTextNode(value));
		} else {
			Node f = node.getFirstChild();
			if (f.getNodeType() == Node.TEXT_NODE) {
				f.setNodeValue(value);
			} else {
				node.insertBefore(doc.createTextNode(value), f);
			}
		}
		isDirty = true;
	}

	public boolean getBoolean(String key, boolean def) {
		String str = String.valueOf(def);
		boolean result;
		String resstr = get(key, str);
		Boolean resboolean = Boolean.valueOf(resstr);
		result = resboolean.booleanValue();
		return result;
	}

	public int getInt(String key, int def) {
		int result;
		String str = String.valueOf(def);
		String resstr = get(key, str);
		try {
			result = Integer.parseInt(resstr);
		} catch (NumberFormatException e) {
			return def;
		}
		return result;
	}

	public float getFloat(String key, float def) {
		float result;
		String str = String.valueOf(def);
		String resstr = get(key, str);
		try {
			result = Float.parseFloat(resstr);
		} catch (NumberFormatException e) {
			return def;
		}
		return result;
	}

	public double getDouble(String key, double def) {
		double result;
		String str = String.valueOf(def);
		String resstr = get(key, str);
		try {
			result = Double.parseDouble(resstr);
		} catch (NumberFormatException e) {
			return def;
		}
		return result;
	}

	public long getLong(String key, long def) {
		long result;
		String str = String.valueOf(def);
		String resstr = get(key, str);
		try {
			result = Long.parseLong(resstr);
		} catch (NumberFormatException e) {
			return def;
		}
		return result;
	}

	public byte[] getByteArray(String key, byte[] def) {
		byte[] result;
		String str = new String(def);
		String resstr = get(key, str);
		result = resstr.getBytes();
		return result;
	}

	public void putBoolean(String key, boolean value) {
		String str = String.valueOf(value);
		try {
			put(key, str);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void putInt(String key, int value) {
		String str = String.valueOf(value);
		try {
			put(key, str);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void putFloat(String key, float value) {
		String str = String.valueOf(value);
		try {
			put(key, str);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void putDouble(String key, double value) {
		String str = String.valueOf(value);
		try {
			put(key, str);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void putLong(String key, long value) {
		String str = String.valueOf(value); // ��long��ת����String����
		try {
			put(key, str);
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void putByteArray(String key, byte[] value) {
		put(key, Base64.encode(value));
	}

	public void removeNode(String key) {
		Node node = findNode(key);
		if (node == null) {
			return;
		}
		Node parentnode = node.getParentNode();
		if (parentnode != null) {
			parentnode.removeChild(node);
			isDirty = true;
		}
	}

	public void clear(String key) {
		Node node = findNode(key);
		if (node == null)
			throw new RuntimeException("InvalidName");
		Node lastnode = null;
		while (node.hasChildNodes()) {
			lastnode = node.getLastChild();
			node.removeChild(lastnode);
		}
		if (lastnode != null)
			isDirty = true;
	}

	public String[] childrenNames(String key) {
		Node node = findNode(key);// ����key�ڵ�
		if (node == null) {
			return new String[0];
		}
		NodeList nl = node.getChildNodes();
		LinkedList<String> list = new LinkedList<String>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.hasChildNodes()) {
				list.add(child.getNodeName());
			}
		}
		String[] ret = new String[list.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (String) list.get(i);
		}
		return ret;
	}

	public boolean nodeExist(String key) {
		Node theNode = this.findNode(key);
		if (theNode == null) {
			return false;
		} else if (theNode.hasChildNodes()) {
			return true;
		} else {
			return false;
		}
	}

	private void loadXMLParser() throws IOException {
		if (builder == null) {
			try {
				factory = DocumentBuilderFactory.newInstance();
				factory.setIgnoringComments(true);
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException ex) {
				throw new IOException("XML Parser load error:"
						+ ex.getLocalizedMessage());
			}
		}
	}

	public void load() throws IOException {

		loadXMLParser();

		try {
			InputSource is = new InputSource(new InputStreamReader(
					new URL(file).openStream()));
			is.setEncoding(System.getProperty("file.encoding"));
			this.doc = builder.parse(is);
		} catch (SAXException ex) {
			ex.printStackTrace();
			String message = ex.getMessage();
			Exception e = ex.getException();
			if (e != null) {
				message += "embedded exception:" + e;
			}
			throw new IOException("XML file parse error:" + message);
		}
		root = doc.getDocumentElement();
		if (!"config".equals(root.getNodeName())) {
			throw new IOException("Config file format error, "
					+ "root node must be <config>");
		}
	}

	public void flush() throws IOException {
		if (isDirty) {
			String proc = new URL(this.file).getProtocol().toLowerCase();
			if (!proc.equalsIgnoreCase("file")) {
				throw new java.lang.UnsupportedOperationException(
						"Unsupport write config URL on protocal " + proc);
			}
			String fileName = new URL(this.file).getPath();
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(fileName), 2048);
			PrintWriter pw = new PrintWriter(bos);
			writeNode(doc, pw, 0);
			pw.flush();
			pw.close();
			isDirty = false;
		}
	}
	/*
	 * private String change(String str) throws IOException { if
	 * (str.indexOf('&') != -1 || str.indexOf('<') != -1 || str.indexOf('>') !=
	 * -1) { ByteArrayOutputStream bos = new ByteArrayOutputStream();
	 * ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes()); byte
	 * temp; byte[] ba1 = { '&', 'a', 'm', 'p', ';' }; byte[] ba2 = { '&', 'l',
	 * 't', ';' }; byte[] ba3 = { '&', 'g', 't', ';' }; while ((temp = (byte)
	 * bis.read()) != -1) { switch (temp) { case '&': bos.write(ba1); break;
	 * case '<': bos.write(ba2); break; case '>': bos.write(ba3); break;
	 * default: bos.write(temp); } } return bos.toString(); } return str; }
	 */
}