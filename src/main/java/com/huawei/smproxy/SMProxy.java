// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-7-11 18:01:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   SMProxy.java

package com.huawei.smproxy;

import com.huawei.insa2.comm.cmpp.CMPPConnection;
import com.huawei.insa2.comm.cmpp.CMPPTransaction;
import com.huawei.insa2.comm.cmpp.message.*;
import com.huawei.insa2.util.Args;
import java.io.IOException;
import java.util.Map;

public class SMProxy {

	private CMPPConnection conn;
	private Args args;
	private boolean needReconnect = false;
	
	public SMProxy(Map<String, Object> args) {
		this(new Args(args));
	}

	public SMProxy(Args args) {
		this.args = args;
		connect();
	}
	
	protected void connect() {
		conn = new CMPPConnection(args);
		conn.addEventListener(new CMPPEventAdapter(this));
		conn.waitAvailable();
		if (!conn.available())
			throw new IllegalStateException(conn.getError());
		else
			return;
	}
	
	public CMPPMessage send(CMPPMessage message) throws IOException {
		if (message == null)
			return null;
		CMPPTransaction t = (CMPPTransaction) conn.createChild();
		try {
			t.send(message);
			t.waitResponse();
			CMPPMessage rsp = t.getResponse();
			CMPPMessage cmppmessage = rsp;
			return cmppmessage;
		} finally {
			t.close();
		}
	}

	public void onTerminate() {
		
	}

	public CMPPMessage onDeliver(CMPPDeliverMessage msg) {
		return new CMPPDeliverRepMessage(msg.getMsgId(), 0);
	}
	
	public void close() {
		conn.close();
		if (needReconnect) {
			connect();
		}
	}
	
	public CMPPConnection getConn() {
		return conn;
	}

	public String getConnState() {
		return  (null != conn) ? conn.getError() : "\u5C1A\u672A\u5EFA\u7ACB\u8FDE\u63A5";
	}
	
	public void setNeedReconnect(boolean needReconnect) {
		this.needReconnect = needReconnect;
	}
}