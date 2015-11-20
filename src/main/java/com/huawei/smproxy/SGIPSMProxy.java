package com.huawei.smproxy;

import com.huawei.insa2.comm.sgip.*;
import com.huawei.insa2.comm.sgip.message.*;
import com.huawei.insa2.util.Args;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class SGIPSMProxy implements SSEventListener {
	
	protected final static SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
	
	public SGIPSMProxy(Map<String, Object> args) {
		this(new Args(args));
	}

	public SGIPSMProxy(Args args) {
		this.args = args;
		src_nodeid = args.get("src-nodeid", "3085199999");
	}

	public synchronized boolean connect() {
		boolean result = true;
		conn = new SGIPConnection(args, true, null);
		conn.addEventListener(new SGIPEventAdapter(this, conn));
		conn.waitAvailable();
		if (!conn.available()) {
			result = false;
			throw new IllegalStateException(conn.getError());
		} else {
			return result;
		}
	}

	protected synchronized void startListener() {
		if (listener != null)
			return;

		String lishost = args.get("listener-host", "127.0.0.1");
		int lisport = args.get("listener-host", 8801);

		try {
			listener = new SSListener(lishost, lisport, this);
			listener.beginListen();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized void stopListener() {

		if (listener == null)
			return;
		
		listener.running = false;
		
		listener.stopListen();

		if (serconns != null) {
			SGIPConnection conn;
			for (Iterator<String> iterator = serconns.keySet().iterator(); iterator.hasNext(); conn.close()) {
				String addr = iterator.next();
				conn = (SGIPConnection) serconns.get(addr);
			}
			serconns.clear();
		}		
	}
	
	public synchronized void onConnect(Socket socket) {
		String peerIP = socket.getInetAddress().getHostAddress();
		int port = socket.getPort();

		if (serconns == null)
			serconns = new HashMap<String, SGIPConnection>();

		SGIPConnection conn = new SGIPConnection(args, false, serconns);
		conn.addEventListener(new SGIPEventAdapter(this, conn));
		conn.attach(args, socket);
		serconns.put(peerIP.concat(String.valueOf(port)), conn);
	}
	
	public SGIPMessage send(SGIPMessage message) throws IOException {
		if (message == null)
			return null;
		
		SGIPTransaction t = (SGIPTransaction) conn.createChild();
		//t.setSrcNodeId(src_nodeid);
		t.setSrcNodeId(new BigInteger(src_nodeid).intValue());
		Date nowtime = new Date();
		int timestamp = Integer.parseInt(dateFormat.format(nowtime));
		t.setTimestamp(timestamp);
		
		try {
			t.send(message);
			t.waitResponse();
			SGIPMessage rsp = t.getResponse();
			SGIPMessage sgipmessage = rsp;
			return sgipmessage;
		} finally {
			t.close();
		}
	}

	public void onTerminate() {
	}

	public SGIPMessage onDeliver(SGIPDeliverMessage msg) {
		return new SGIPDeliverRepMessage(0);
	}

	public SGIPMessage onReport(SGIPReportMessage msg) {
		return new SGIPReportRepMessage(0);
	}

	public SGIPMessage onUserReport(SGIPUserReportMessage msg) {
		return new SGIPUserReportRepMessage(0);
	}

	public void close() {
		conn.close();
		if (needReconnect) {
			connect();
		}
	}

	public SGIPConnection getConn() {
		return conn;
	}

	public String getConnState() {
		return (null != conn) ? conn.getError() : "\u5C1A\u672A\u5EFA\u7ACB\u8FDE\u63A5";
	}
	
	public void setNeedReconnect(boolean needReconnect) {
		this.needReconnect = needReconnect;
	}
	
	private SGIPConnection conn;
	private SSListener listener;
	private Args args;
	private Map<String, SGIPConnection> serconns;
	private String src_nodeid;
	private boolean needReconnect = false;

}
