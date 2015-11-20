/**File Name:MM7Sender.java
 * Company:  中国移动集团公司
 * Date  :   2004-1-17
 * */

package com.cmcc.mm7.vasp.service;

import java.io.*;
import java.net.*;
import java.text.*;
import sun.misc.*;
import org.jdom.*;
import org.jdom.input.*;
import java.security.*;
import com.cmcc.mm7.vasp.common.*;
import com.cmcc.mm7.vasp.conf.*;
import com.cmcc.mm7.vasp.message.*;

public class MM7Sender {
	private MM7Config mm7Config;
	private BufferedOutputStream sender;
	private BufferedInputStream receiver;
	private StringBuffer sb;
	private StringBuffer beforAuth;
	private String AuthInfor;
	// private String DigestInfor;
	private StringBuffer afterAuth;
	private StringBuffer entityBody;
	private MM7RSRes res;
	private ByteArrayOutputStream baos;
	private ConnectionPool pool;
	private ConnectionWrap connWrap;
	private int ResendCount;
	private Socket client;
	//private int ReadTimeOutCount = 0;
	private ByteArrayOutputStream sendBaos;
	//private StringBuffer SevereBuffer;
	//private StringBuffer InfoBuffer;
	//private StringBuffer FinerBuffer;
	//private ByteArrayOutputStream Severebaos;
	//private ByteArrayOutputStream Infobaos;
	//private ByteArrayOutputStream Finerbaos;
	//private long LogTimeBZ;
	//private long SameMinuteTime;
	//private int N;
	//private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
	//private static final SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private DecimalFormat df;
	//private String logFileName;
	//private byte[] TimeOutbCount;
	private ConnectionWrap TimeOutWrap;
	private boolean TimeOutFlag;
	public int tempnum = 0;

	long lStart;

	public MM7Sender() // 构造方法
	{
		reset();
	}

	private void reset() {
		//File f;
		mm7Config = new MM7Config();
		sender = null;
		receiver = null;
		AuthInfor = "";
		//DigestInfor = "";
		sb = new StringBuffer();
		beforAuth = new StringBuffer();
		afterAuth = new StringBuffer();
		entityBody = new StringBuffer();
		res = new MM7RSRes();
		baos = new ByteArrayOutputStream();
		ResendCount = 0;
		connWrap = null;
		// Modify by hudm 2004-06-09
		// pool = ConnectionPool.getInstance();
		pool = new ConnectionPool();
		// /end Modify by hudm 2004-06-09
		client = null;
		sendBaos = new ByteArrayOutputStream();
		//LogTimeBZ = System.currentTimeMillis();
		//SameMinuteTime = System.currentTimeMillis();
		//N = 1;
		//sdf = new SimpleDateFormat("yyyyMMddHHmm");
		df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		//logFileName = "";
		//TimeOutbCount = null;
		TimeOutWrap = null;
		TimeOutFlag = false;
	}

	/** 构造方法 */
	public MM7Sender(MM7Config config) throws Exception {
		reset();
		mm7Config = config;
		pool.setConfig(mm7Config);
	}

	public void setConfig(MM7Config config) // 设置MM7Config
	{
		mm7Config = config;
		pool.setConfig(mm7Config);
	}

	public MM7Config getConfig() // 获得MM7Config
	{
		return (mm7Config);
	}
	/*
	private void setSameMinuteTime(long time) {
		SameMinuteTime = time;
	}

	private long getSameMinuteTime() {
		return SameMinuteTime;
	}
	*/
	// 发送消息
	public MM7RSRes send(MM7VASPReq mm7VASPReq) {
		lStart = System.currentTimeMillis();
		tempnum++;

		if (mm7VASPReq == null) {
			MM7RSErrorRes ErrorRes = new MM7RSErrorRes();
			ErrorRes.setStatusCode(-105);
			ErrorRes.setStatusText("待发送的消息为空!");
			return ErrorRes;
		}
		
		sb = new StringBuffer();
		beforAuth = new StringBuffer();
		afterAuth = new StringBuffer();
		entityBody = new StringBuffer();
		sendBaos = new ByteArrayOutputStream();
		sender = null;
		receiver = null;
		
		try {
			String mmscURL = mm7Config.getMMSCURL();
			//int httpindex = -1;
			//int index = -1;
			if (mmscURL == null)
				mmscURL = "";
			
			beforAuth.append(String.format("POST %s HTTP/1.1\r\n", mmscURL));
			
			/** 为了当有多个MMSCIP时，可以进行平均分配 */
			if (pool.getIPCount() < mm7Config.getMMSCIP().size()) {
				beforAuth.append(String.format("Host:%s\r\n", mm7Config.getMMSCIP().get(pool.getIPCount())));
				pool.setIPCount(pool.getIPCount() + 1);
			} else {
				pool.setIPCount(0);
				beforAuth.append(String.format("Host:%s\r\n", mm7Config.getMMSCIP().get(0)));
				pool.setIPCount(pool.getIPCount() + 1);
			}

			/** 设置ContentType，不带附件为text/xml;带附件为multipart/related */
			if (mm7VASPReq instanceof MM7SubmitReq) {
				MM7SubmitReq submitReq = (MM7SubmitReq) mm7VASPReq;
				if (submitReq.isContentExist())
					beforAuth.append("Content-Type:multipart/related; boundary=\"--NextPart_0_2817_24856\";")
							  .append("type=\"text/xml\";start=\"</tnn-200102/mm7-vasp>\"")
							  .append("\r\n");
				else
					beforAuth.append(String.format("Content-Type:text/xml;charset=\"%s\"\r\n", mm7Config.getCharSet()));
				
			} else if (mm7VASPReq instanceof MM7ReplaceReq) {
				MM7ReplaceReq replaceReq = (MM7ReplaceReq) mm7VASPReq;
				if (replaceReq.isContentExist())
					beforAuth.append("Content-Type:multipart/related; boundary=\"--NextPart_0_2817_24856\";\r\n");
				else
					beforAuth.append(String.format("Content-Type:text/xml;charset=\"%s\"\r\n", mm7Config.getCharSet()));
			} else if (mm7VASPReq instanceof MM7CancelReq) {
				//MM7CancelReq cancelReq = (MM7CancelReq) mm7VASPReq;
				beforAuth.append(String.format("Content-Type:text/xml;charset=\"%s\"\r\n", mm7Config.getCharSet()));
			} else {
				MM7RSErrorRes ErrorRes = new MM7RSErrorRes();
				ErrorRes.setStatusCode(-106);
				ErrorRes.setStatusText("没有匹配的消息，请确认要发送的消息是否正确！");
				return ErrorRes;
			}

			// 设置Content-Trans-Encoding
			beforAuth.append("Content-Transfer-Encoding:8bit" + "\r\n");
			AuthInfor = "Authorization:Basic ".concat(getBASE64(mm7Config.getUserName())).concat(":").concat(mm7Config.getPassword()).concat("\r\n");

			afterAuth.append("SOAPAction:\"\"\r\n");
			RetriveApiVersion apiver = new RetriveApiVersion();
			afterAuth.append(String.format("MM7APIVersion:%s\r\n", apiver.getApiVersion()));
			/**
			 * 判断是否是长连接，若是长连接，则将Connection设为keep-alive，
			 * 否则设为close，以告诉服务器端客户端是长连接还是短连接
			 */
			if (pool.getKeepAlive().equals("on")) {
				afterAuth.append("Connection: Keep-Alive\r\n");
			} else {
				afterAuth.append("Connection:Close\r\n");
			}

			byte[] bcontent = getContent(mm7VASPReq);
			if (bcontent.length > mm7Config.getMaxMsgSize()) {
				MM7RSErrorRes ErrorRes = new MM7RSErrorRes();
				ErrorRes.setStatusCode(-113);
				ErrorRes.setStatusText("消息内容的尺寸超出允许发送的大小！");
				return ErrorRes;
			}

			String env = "";
			try {
				ByteArrayOutputStream tempbaos = new ByteArrayOutputStream();
				tempbaos.write(bcontent);

				// 开启日志模式时才执行
				if (mm7Config.getLogLevel() > 0) {
					int envbeg = tempbaos.toString().indexOf(MMConstants.BEGINXMLFLAG);
					int envend = tempbaos.toString().indexOf("</env:Envelope>");
					env = tempbaos.toString().substring(envbeg, envend);
					env = env.concat("</env:Envelope>");
				}
				tempbaos.close();
				tempbaos = null;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				afterAuth.append("Content-Length:".concat(String.valueOf(bcontent.length)).concat("\r\n"));
				afterAuth.append("Mime-Version:1.0\r\n");
				afterAuth.append("\r\n");
				entityBody.append(new String(bcontent));
				sendBaos = getSendMessage(bcontent);

				if (sendBaos != null) {
					res = SendandReceiveMessage(sendBaos);

					// 当未启动日志模式时直接返回
					if (mm7Config.getLogLevel() <= 0) {
						return res;
					}
				} else {
					MM7RSErrorRes ErrorRes = new MM7RSErrorRes();
					ErrorRes.setStatusCode(-104);
					ErrorRes.setStatusText("Socket不通！");
					return ErrorRes;
				}
				
				int envbeg = baos.toString().indexOf(MMConstants.BEGINXMLFLAG);
				int envend = baos.toString().indexOf("</env:Envelope>");
				if (envbeg > 0 && envend > 0)
					env = baos.toString().substring(envbeg);
			}
			return res;
		} catch (Exception e) {
			MM7RSErrorRes ErrorRes = new MM7RSErrorRes();
			e.printStackTrace();
			ErrorRes.setStatusCode(-100);
			ErrorRes.setStatusText("系统错误！原因：" + e);
			return ErrorRes;
		}
	}
	/*
	private void deleteFile(String logpath, int lognum, String MMSCID) {
		File parfile = new File(logpath);
		if (parfile.isDirectory()) {
			File[] subfile = parfile.listFiles();
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < subfile.length; i++) {
				String name = subfile[i].getName();
				if (name.indexOf(MMSCID) >= 0) {
					list.add(name);
				}
			}
			if (list.size() >= lognum) {
				int deleteLength = list.size() - lognum + 1;
				Comparator<String> comp = Collections.reverseOrder();
				Collections.sort(list, comp);
				for (int i = list.size() - deleteLength; i < list.size(); i++) {
					String strfile = (String) list.get(i);
					File ff = new File(logpath + "/" + strfile);
					ff.delete();
				}
			}
		}
	}
	*/
	
	private byte[] getContent(MM7VASPReq mm7VASPReq) {
		byte[] b = null;
		SOAPEncoder encoder = new SOAPEncoder(mm7Config);
		encoder.setMessage(mm7VASPReq);
		try {
			encoder.encodeMessage();
		} catch (Exception e) {
			System.err.println(e);
		}
		b = encoder.getMessage();
		return (b);
	}

	/** 进行BASE64编码 */
	public static String getBASE64(String value) {
		if (value == null)
			return null;
		BASE64Encoder BaseEncode = new BASE64Encoder();
		return (BaseEncode.encode(value.getBytes()));
	}
	
	private MM7RSRes parseXML() {
		SAXBuilder sax = new SAXBuilder();
		//SevereBuffer.append("recv=" + baos.toString() + "\r\n");
		try {
			if (baos.toString() == null || baos.toString().equals("")) {
				MM7RSErrorRes errRes = new MM7RSErrorRes();
				errRes.setStatusCode(-107);
				errRes.setStatusText("错误！接收到的消息为空！");
				return errRes;
			} else {
				// System.out.println("baos="+baos.toString());
				int index = -1;
				//int xmlend = -1;
				index = baos.toString().indexOf(MMConstants.BEGINXMLFLAG);
				if (index == -1) {
					int httpindex = -1;
					httpindex = baos.toString().indexOf("HTTP1.1");
					String strstat = "";
					if (httpindex >= 0) {
						int index11 = baos.toString().indexOf("\r\n");
						strstat = baos.toString().substring(httpindex + 7, index11);
					}
					MM7RSErrorRes err = new MM7RSErrorRes();
					err.setStatusCode(-108);
					if (!strstat.equals(""))
						err.setStatusText(strstat);
					else
						err.setStatusText("Bad Request");
					return err;
				}
				String xmlContent = baos.toString().substring(index, baos.toString().length());
				String xmlContentTemp = "";
				byte[] byteXML = baos.toByteArray();
				int index1 = xmlContent.indexOf("encoding=\"UTF-8\"");
				if (index1 > 0) {
					xmlContentTemp = new String(byteXML, "UTF-8");
					int xmlind = xmlContentTemp.indexOf(MMConstants.BEGINXMLFLAG);
					int xmlindend = xmlContentTemp.lastIndexOf("Envelope>");
					if (xmlindend > 0) {
						xmlContentTemp = xmlContentTemp.substring(xmlind, xmlindend + 9);
						String xml = xmlContentTemp.substring(0, index1)
								.concat("encoding=\"GB2312\"")
								.concat(xmlContentTemp.substring(index1 + "encoding=\"UTF-8\"".length()));
						xmlContent = xml;
					}
				}

				ByteArrayInputStream in = new ByteArrayInputStream(xmlContent.getBytes());
				Document doc = sax.build(in);
				Element root = doc.getRootElement();
				Element first = (Element) root.getChildren().get(0);
				if (first.getName().equalsIgnoreCase("body")) {
					Element Message = (Element) first.getChildren().get(0);
					if (Message.getName().equals("Fault")) {
						MM7RSErrorRes errRes = new MM7RSErrorRes();
						errRes.setStatusCode(-110);
						errRes.setStatusText("Server could not fulfill the request");
						return errRes;
					} else {
						MM7RSErrorRes errRes = new MM7RSErrorRes();
						errRes.setStatusCode(-111);
						errRes.setStatusText("Server error");
						return errRes;
					}
				} else {
					Element envBody = (Element) root.getChildren().get(1);
					Element Message = (Element) envBody.getChildren().get(0);
					Element envHeader = (Element) root.getChildren().get(0);
					Element transID = (Element) envHeader.getChildren().get(0);
					String transactionID = transID.getTextTrim();
					int size = Message.getChildren().size();
					if (Message.getName().equals("SubmitRsp")) {
						MM7SubmitRes submitRes = new MM7SubmitRes();
						submitRes.setTransactionID(transactionID);
						for (int i = 0; i < size; i++) {
							Element ele = (Element) Message.getChildren().get(i);
							if (ele.getName().equals(MMConstants.STATUS)) {
								for (int j = 0; j < ele.getChildren().size(); j++) {
									Element subEle = (Element) ele.getChildren().get(j);
									if (subEle.getName().equals(MMConstants.STATUSCODE))
										submitRes.setStatusCode(Integer.parseInt(subEle.getTextTrim()));
									else if (subEle.getName().equals(MMConstants.STATUSTEXT))
										submitRes.setStatusText(subEle.getTextTrim());
									else if (subEle.getName().equals(MMConstants.STATUSDETAIL))
										submitRes.setStatusDetail(subEle.getTextTrim());
								}
							} else if (ele.getName().equals(MMConstants.MESSAGEID)) {
								submitRes.setMessageID(ele.getTextTrim());
							}
						}
						return submitRes;
					} else if (Message.getName().equals("CancelRsp")) {
						MM7CancelRes cancelRes = new MM7CancelRes();
						cancelRes.setTransactionID(transactionID);
						for (int i = 0; i < size; i++) {
							Element ele = (Element) Message.getChildren().get(i);
							if (ele.getName().equals(MMConstants.STATUS)) {
								for (int j = 0; j < ele.getChildren().size(); j++) {
									Element subEle = (Element) ele.getChildren().get(j);
									if (subEle.getName().equals(MMConstants.STATUSCODE))
										cancelRes.setStatusCode(Integer.parseInt(subEle.getTextTrim()));
									else if (subEle.getName().equals(MMConstants.STATUSTEXT))
										cancelRes.setStatusText(subEle.getTextTrim());
									else if (subEle.getName().equals(MMConstants.STATUSDETAIL))
										cancelRes.setStatusDetail(subEle.getTextTrim());
								}
							}
						}
						return cancelRes;
					} else if (Message.getName().equals("ReplaceRsp")) {
						MM7ReplaceRes replaceRes = new MM7ReplaceRes();
						replaceRes.setTransactionID(transactionID);
						for (int i = 0; i < size; i++) {
							Element ele = (Element) Message.getChildren().get(i);
							if (ele.getName().equals(MMConstants.STATUS)) {
								for (int j = 0; j < ele.getChildren().size(); j++) {
									Element subEle = (Element) ele.getChildren().get(j);
									if (subEle.getName().equals(MMConstants.STATUSCODE))
										replaceRes.setStatusCode(Integer.parseInt(subEle.getTextTrim()));
									else if (subEle.getName().equals(MMConstants.STATUSTEXT))
										replaceRes.setStatusText(subEle.getTextTrim());
									else if (subEle.getName().equals(MMConstants.STATUSDETAIL))
										replaceRes.setStatusDetail(subEle.getTextTrim());
								}
							}
						}
						return replaceRes;
					} else {
						MM7RSRes res = new MM7RSRes();
						res.setTransactionID(transactionID);
						for (int i = 0; i < size; i++) {
							Element ele = (Element) Message.getChildren()
									.get(i);
							if (ele.getName().equals(MMConstants.STATUS)) {
								for (int j = 0; j < ele.getChildren().size(); j++) {
									Element subEle = (Element) ele.getChildren().get(j);
									if (subEle.getName().equals(MMConstants.STATUSCODE))
										res.setStatusCode(Integer.parseInt(subEle.getTextTrim()));
									else if (subEle.getName().equals(MMConstants.STATUSTEXT))
										res.setStatusText(subEle.getTextTrim());
									else if (subEle.getName().equals(MMConstants.STATUSDETAIL))
										res.setStatusDetail(subEle.getTextTrim());
								}
							}
						}
						return res;
					}
				}
			}
		} catch (JDOMException jdome) {
			MM7RSErrorRes error = new MM7RSErrorRes();
			System.err.print(jdome);
			error.setStatusCode(-109);
			error.setStatusText("XML解析错误！原因：" + jdome);
			return error;
		} catch (Exception e) {
			e.printStackTrace();
			MM7RSErrorRes error = new MM7RSErrorRes();
			error.setStatusCode(-100);
			error.setStatusText("系统错误！原因：" + e);
			return error;
		}
	}

	private ByteArrayOutputStream getSendMessage(byte[] bcontent) {
		try {
			/**
			 * 在此加以判断，如果是长连接，则得到以前的连接，否则新建连接
			 * */
			if (pool.getKeepAlive().equals("on")) {
				if (this.TimeOutFlag == true) {
					if (pool.deleteURL(TimeOutWrap)) {
						connWrap = pool.getConnWrap();
						this.TimeOutWrap = connWrap;
						if (connWrap != null) {
							client = connWrap.getSocket();
						} else {
							client = null;
						}
					} else {
						return null;
					}
				} else {
					connWrap = pool.getConnWrap();
					this.TimeOutWrap = connWrap;
					if (connWrap != null) {
						client = connWrap.getSocket();
						if (connWrap.getUserfulFlag() == false || client.isConnected() == false) {
							pool.deleteURL(connWrap);
							connWrap = pool.getConnWrap();
							this.TimeOutWrap = connWrap;
						}
						if (connWrap.getAuthFlag() == true) {
							AuthInfor = connWrap.getDigestInfor();
						}
						// ///end add by hudm
					} else
						client = null;
				}
			} else {
				String MMSCIP = (String) mm7Config.getMMSCIP().get(0);
				int index = MMSCIP.indexOf(":");
				String ip;
				int port;
				if (index == -1) {
					ip = MMSCIP;
					port = 80;
				} else {
					ip = MMSCIP.substring(0, index);
					port = Integer.parseInt(MMSCIP.substring(index + 1));
				}
				client = new Socket(ip, port);
			}
			if (client != null) {
				sender = new BufferedOutputStream(client.getOutputStream());
				receiver = new BufferedInputStream(client.getInputStream());
				client.setSoTimeout(mm7Config.getTimeOut());
				client.setKeepAlive(true);

				sb = new StringBuffer();
				sb.append(beforAuth);
				sb.append(AuthInfor);
				sb.append(afterAuth);
				try {
					sendBaos = new ByteArrayOutputStream();
					sendBaos.write(sb.toString().getBytes());
					sendBaos.write(bcontent);
					return sendBaos;
				} catch (IOException e) {
					System.out.println("IOException!原因："+e);
					return null;
				}
			} else {
				return null;
			}
		} catch (UnknownHostException uhe) {
			System.out.println("UnknownHostExcepion!原因：" + uhe);
			return null;
		} catch (SocketException se) {
			System.out.println("SocketException!原因：" + se);
			return null;
		}
		/**
		 * 超时后，被捕获，认为该次发送失败，重新进行发送，当发送超过一定次数后， 即认为整个发送失败。返回失败信息。
		 * */
		catch (InterruptedIOException iioe) {
			this.TimeOutFlag = true;
			/*
			for (int j = 0; j < mm7Config.getReSendCount(); j++) {
				sendBaos = getSendMessage(bcontent);
				if (sendBaos != null)
					res = this.SendandReceiveMessage(sendBaos);
			}
			*/
			res.setStatusCode(-101);
			res.setStatusText("超时发送失败！原因：" + iioe);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatusCode(-100);
			res.setStatusText("系统错误！原因：" + e);
			return null;
		}
	}
	
	private MM7RSRes SendandReceiveMessage(ByteArrayOutputStream sendbaos) {
		try {
			sender.write(sendbaos.toByteArray());
			sender.flush();
			
			if (this.receiveMessge()) {
				res = parseXML();
				return res;
			} else {
				MM7RSErrorRes error = new MM7RSErrorRes();
				error.setStatusCode(-102);
				error.setStatusText("接收失败！");
				return error;
			}
		} catch (IOException ioe) {
			this.TimeOutFlag = true;
			/*
			sendBaos = getSendMessage(this.TimeOutbCount);
			if (sendBaos != null) {
				res = SendandReceiveMessage(sendBaos);
				if (res != null) {
					this.TimeOutFlag = false;
					return res;
				} else {
					MM7RSErrorRes error = new MM7RSErrorRes();
					error.setStatusCode(-103);
					error.setStatusText("没有返回正确的消息");
					SevereBuffer.append("[Message_Type=MM7RSErrorRes]");
					SevereBuffer.append("[Comments={-103;" + error.getStatusText() + "}]");
					return error;
				}
			} else {
				MM7RSErrorRes error = new MM7RSErrorRes();
				error.setStatusCode(-104);
				error.setStatusText(" " + ioe);
				SevereBuffer.append("[Message_Type=MM7RSErrorRes]");
				SevereBuffer.append("[Comments={-104;" + error.getStatusText() + "}]");
				return error;
			}
			*/
			MM7RSErrorRes error = new MM7RSErrorRes();
			error.setStatusCode(-104);
			error.setStatusText(" " + ioe);
			return error;
		}
	}
	
	public boolean receiveMessge() throws IOException {
		try {
			baos.reset();
			boolean bReceive = false;
			byte[] data = new byte[MMConstants.ARRAYLIMIT];
			//int word = -1;
			int readline = -1;
			int totalline = 0;
			boolean flag = false;
			boolean bHead = false;
			//int readlineOut = 1;
			//int envelopecount = 0;
			while (true) {
				if (flag) break;
				readline = receiver.read(data);
				if (readline != -1) {
					baos.write(data, 0, readline);
					// System.out.println("baos==="+baos.toString());
					totalline = totalline + readline;
					if (baos.toString().indexOf("\r\n\r\n") < 0)
						continue;
					if (bHead == false) {
						int httpindex = baos.toString().indexOf("HTTP/1.1");
						if (httpindex != -1) {
							String httpCode = baos.toString().substring(httpindex + 8, httpindex + 12).trim();
							int httpsepindex = baos.toString().indexOf("\r\n\r\n");
							if (httpCode.equals("401")) {
								if (baos.toString().indexOf("Digest") != -1) {
									if (ResendCount < mm7Config.getReSendCount()) {
										ResendCount = ResendCount + 1;
										pool.setInitNonceCount();
										String clientAuthInfor = "";
										String authInfor = "";
										int authindex = baos.toString().indexOf(MMConstants.AUTHENTICATION);
										if (authindex > 0) {
											int lineend = baos.toString().indexOf("\r", authindex + 1);
											int linebeg = authindex + MMConstants.AUTHENTICATION.length() + 1;
											authInfor = baos.toString().substring(linebeg, lineend);
											clientAuthInfor = setDigestAuth(authInfor);
										}
										
										int connectionindex = baos.toString().toLowerCase().indexOf(MMConstants.CONNECTION);
										int connlength = connectionindex+ MMConstants.CONNECTION.length() + 1;
										int connectionend = baos.toString().indexOf("\r\n",connectionindex);
										String ConnectionFlag = "";
										if (connectionindex != -1 && connectionend != -1)
											ConnectionFlag = baos.toString().substring(connlength,connectionend);
										
										sb = new StringBuffer();
										sb.append(beforAuth);
										sb.append(clientAuthInfor);
										sb.append(afterAuth);
										sb.append(entityBody);
										sender.write(sb.toString().getBytes());
										sender.flush();
										baos = new ByteArrayOutputStream();
										data = new byte[MMConstants.ARRAYLIMIT];
										int resline = -1;
										int totalresline = 0;
										boolean excuteFlag = false;
										int httpseper = -1;
										int contlen3 = -1;
										while (true) {
											resline = receiver.read(data);
											if (resline == -1) break;
											baos.write(data, 0, resline);
											totalresline += resline;
											
											if (baos.toString().indexOf("\r\n\r\n") < 0)
												continue;
											
											if (excuteFlag == false) {
												httpindex = baos.toString().indexOf("HTTP/1.1");
												httpCode = baos.toString().substring(httpindex + 8, httpindex + 12).trim();
												int conlen1 = baos.toString().toLowerCase().indexOf(MMConstants.CONTENT_LENGTH);
												int conlen2 = baos.toString().indexOf("\r", conlen1);
												contlen3 = Integer.parseInt(baos.toString().substring((conlen1+ MMConstants.CONTENT_LENGTH.length() + 1),conlen2).trim());
												httpseper = baos.toString().indexOf("\r\n\r\n");
												if (httpCode.equals("200")) {
													// 还要再加判断客户端是否是长连接
													ResendCount = 0;
													excuteFlag = true;
													if (ConnectionFlag.trim().toLowerCase().equals("keep-alive")) {
														pool.setNonceCount(Integer.toString(Integer.parseInt(pool.getNonceCount(),8) + 1));
														connWrap.setDigestInfor(setDigestAuth(authInfor));
														continue;
													}
												}
											}
											// 开始接收http体
											if (totalresline == httpseper + contlen3 + 4) {
												/*
												 * if(this.TimeOutFlag==true){
												 * SevereBuffer
												 * .append("baos.tostring()=="
												 * +baos.toString()); }
												 */
												if (pool.getKeepAlive().equals("off")) {
													sender.close();
													receiver.close();
													client.close();
												} else {
													connWrap.setUserfulFlag(true);
													connWrap.setFree(true);
												}
												flag = true;
												bReceive = true;
												break;
											}
										}
									} else if (baos.toString().indexOf("Basic") != -1) {
										if (ResendCount < mm7Config.getReSendCount()) {
											ResendCount = ResendCount + 1;
											receiveMessge();
										} else {
											bReceive = false;
											break;
										}
									}
								} else {
									bReceive = false;
									break;
								}
							} else {
								int index1 = baos.toString().toLowerCase().indexOf(MMConstants.CONTENT_LENGTH);
								int index2 = baos.toString().indexOf("\r",index1);
								int contlength = 0;
								if (index1 == -1 || index2 == -1) {
									int encodingindex = baos.toString().toLowerCase().indexOf("transfer-encoding:");
									if (encodingindex >= 0) {
										int encodingend = baos.toString().indexOf("\r\n", encodingindex);
										if (encodingend >= 0) {
											String strenc = baos.toString().substring(encodingindex + "transfer-encoding:".length(), encodingend).trim();
											if (strenc.equalsIgnoreCase("chunked")) {
												// //在这里可以增加chunked的处理
												int endencindex2 = baos.toString().indexOf("\r\n", encodingend + 1);
												if (endencindex2 >= 0) {
													int xmlbeg = baos.toString().indexOf(MMConstants.BEGINXMLFLAG, endencindex2 + 1);
													if (xmlbeg > 0) {
														String strTempContLen = baos.toString().substring(endencindex2, xmlbeg).trim();
														contlength = Integer.parseInt(strTempContLen, 16);
													}
												}
											} else {
												bReceive = false;
												break;
											}
										} else {
											bReceive = false;
											break;
										}
									} else {
										continue;
									}

									if (totalline >= httpsepindex + contlength + 8) {
										if (pool.getKeepAlive().equals("off")) {
											sender.close();
											receiver.close();
											client.close();
										} else {
											connWrap.setUserfulFlag(true);
											connWrap.setFree(true);
										}
										bReceive = true;
										break;
									}

								} else {
									contlength = Integer.parseInt(baos.toString().substring((index1 + MMConstants.CONTENT_LENGTH.length() + 1), index2).trim());
									// 开始接收http体
									if (totalline == httpsepindex + contlength + 4) {
										// end add by hudm
										if (pool.getKeepAlive().equals("off")) {
											sender.close();
											receiver.close();
											client.close();
										} else {
											connWrap.setUserfulFlag(true);
											connWrap.setFree(true);
										}
										bReceive = true;
										break;
									}
								}
							}
						} else {
							bReceive = false;
							break;
						}

					}
				}
			}
			return bReceive;
		} catch (SocketTimeoutException ste) {
			// System.out.println("第1100行。");
			this.TimeOutFlag = true;
			/*
			ReadTimeOutCount++;
			
			if (ReadTimeOutCount < mm7Config.getReSendCount()) {
				sendBaos = getSendMessage(this.TimeOutbCount);
				if (sendBaos != null) {
					res = SendandReceiveMessage(sendBaos);
					if (res != null) {
						this.TimeOutFlag = false;
						return true;
					}
				} else
					return false;
			}*/
			this.TimeOutFlag = false;
			res.setStatusCode(-101);
			res.setStatusText("超时发送失败！");
			//ReadTimeOutCount = 0;
			return false;
		}
	}

	/** 输入string，输出经过MD5编码的String */
	public String calcMD5(String str) {
		try {
			MessageDigest alga = MessageDigest.getInstance("MD5");
			alga.update(str.getBytes());
			byte[] digesta = alga.digest();
			return byte2hex(digesta);
		} catch (NoSuchAlgorithmException ex) {
			System.out.println("出错了！！没有这种算法！");
		}
		return "NULL";
	}

	// byte[]数组转成字符串
	public String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + "";
		}
		return hs;
	}

	private String setDigestAuth(String authinfor) {
		String auth = authinfor + "\r\n";
		int equal = -1;
		int comma = -1;
		StringBuffer authBuffer = new StringBuffer();
		authBuffer.append("Authorization: Digest ");
		String qopValue;
		String MD5A2;
		String algValue;
		String cnonceValue = "";
		String ncValue;
		String responseValue;
		String uri = mm7Config.getMMSCURL();
		if (uri == null)
			uri = "";
		String username = mm7Config.getUserName();
		authBuffer.append("uri=\"" + uri + "\",");
		authBuffer.append("username=\"" + username + "\",");

		String passwd = mm7Config.getPassword();
		// 获得realm的值
		int realmindex = auth.indexOf(MMConstants.REALM);
		equal = auth.indexOf("=", realmindex + 1);
		comma = auth.indexOf("\"", equal + 2);
		String realmValue = auth.substring(equal + 1, comma);
		if (realmValue.startsWith("\""))
			realmValue = realmValue.substring(1, realmValue.length());
		authBuffer.append("realm=\"" + realmValue + "\",");
		String MD5A1;
		// 取得nonce的值
		int nonceindex = auth.indexOf(MMConstants.NONCE);
		equal = auth.indexOf("=", nonceindex + 1);
		comma = auth.indexOf("\"", equal + 2);
		String nonceValue = auth.substring(equal + 1, comma);
		if (nonceValue.startsWith("\""))
			nonceValue = nonceValue.substring(1, nonceValue.length());
		authBuffer.append("nonce=\"" + nonceValue + "\",");
		// 判断有没有opaque，若有，则原封不动地返回给服务器
		int opaqueindex = auth.indexOf(MMConstants.OPAQUE);
		if (opaqueindex > 0) {
			equal = auth.indexOf("=", opaqueindex + 1);
			comma = auth.indexOf("\"", equal + 2);
			authBuffer.append("opaque=" + auth.substring(equal + 1, comma + 1));
		}
		// 取得algorithm的值
		int algindex = auth.indexOf(MMConstants.ALGORITHM);
		if (algindex > 0) {
			equal = auth.indexOf("=", algindex);
			comma = auth.indexOf(",", equal + 2);
			if (comma >= 0) {
				algValue = auth.substring(equal + 1, comma);
				if (algValue.startsWith("\""))
					algValue = algValue.substring(1, algValue.length() - 1);
			} else {
				comma = auth.indexOf("/r", equal);
				algValue = auth.substring(equal + 1, comma);
				if (algValue.startsWith("\""))
					algValue = algValue.substring(1, algValue.length());
			}
		} else {
			algValue = "MD5";
		}
		// 取得qop的值
		int qopindex = auth.indexOf(MMConstants.QOP);
		if (algValue.equals("MD5") || algValue.equals("MD5-sess")) {
			MD5A1 = calcMD5(username + ":" + realmValue + ":" + passwd);
			// 服务器存在qop这个字段
			if (qopindex > 0) {
				equal = auth.indexOf("=", qopindex);
				comma = auth.indexOf("\"", equal + 2);
				qopValue = auth.substring(equal + 1, comma);
				if (qopValue.startsWith("\""))
					qopValue = qopValue.substring(1, qopValue.length());
				/**
				 * 表明服务器给出了两种qop，为auth和auth-int， 这是应该是客户端自己选择采用哪种方式进行鉴权
				 * */
				if (qopValue.indexOf(",") > 0) {
					if (mm7Config.getDigest().equals("auth-int")) {
						MD5A2 = calcMD5("POST" + ":" + uri + ":"
								+ calcMD5(entityBody.toString()));
					} else {
						MD5A2 = calcMD5("POST" + ":" + uri);
					}
					ncValue = String.valueOf(pool.getNonceCount());
					cnonceValue = getBASE64(ncValue);
					responseValue = calcMD5(MD5A1 + ":" + nonceValue + ":"
							+ ncValue + ":" + cnonceValue + ":" + qopValue
							+ ":" + MD5A2);
					authBuffer.append("qop=\"" + mm7Config.getDigest() + "\",");
					authBuffer.append("nc=" + ncValue + ",");
					authBuffer.append("cnonce=\"" + cnonceValue + "\",");
					authBuffer.append("response=\"" + responseValue + "\",");
				}
				// 服务器端只有一种qop方式。
				else {
					if (qopValue.equals("auth-int")) {
						MD5A2 = calcMD5("POST" + ":" + uri + ":" + calcMD5(entityBody.toString()));
					} else
						MD5A2 = calcMD5("POST" + ":" + uri);
					ncValue = String.valueOf(pool.getNonceCount());
					cnonceValue = getBASE64(ncValue);
					responseValue = calcMD5(MD5A1 + ":" + nonceValue + ":" + ncValue + ":" + cnonceValue + ":" + qopValue + ":" + MD5A2);
					authBuffer.append("qop=\"" + qopValue + "\",");
					authBuffer.append("nc=" + ncValue + ",");
					authBuffer.append("cnonce=\"" + cnonceValue + "\",");
					authBuffer.append("response=\"" + responseValue + "\",");
				}
			}
			// 服务器端不存在对qop的方式的选择
			else {
				MD5A2 = calcMD5("POST" + ":" + uri);
				responseValue = calcMD5(MD5A1 + ":" + nonceValue + ":" + MD5A2);
				authBuffer.append("response=\"" + responseValue + "\",");
			}
		}
		// 去掉最后一个逗号
		int lastcommaindex = authBuffer.lastIndexOf(",");
		authBuffer.delete(lastcommaindex, lastcommaindex + 1);
		authBuffer.append("\r\n");
		return authBuffer.toString();
	}

	public boolean isConnected() {
		return pool.isConnected();
	}
}