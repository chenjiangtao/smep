package com.huawei.smproxy;

import java.io.IOException;
import java.util.Map;

import com.huawei.insa2.comm.smgp.SMGPConnection;
import com.huawei.insa2.comm.smgp.SMGPTransaction;
import com.huawei.insa2.comm.smgp.message.SMGPDeliverMessage;
import com.huawei.insa2.comm.smgp.message.SMGPDeliverRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPMessage;
import com.huawei.insa2.util.Args;

public class SMGPSMProxy {
	private SMGPConnection conn;
	private Args args;
	private boolean needReconnect = false;
	
	public SMGPSMProxy(Map<String, Object> args) {
		this(new Args(args));
	}

	public SMGPSMProxy(Args args) {
		this.args = args;
		connect();
	}
	
	protected void connect() {
		conn = new SMGPConnection(args);
		conn.addEventListener(new SMGPEventAdapter(this));
		conn.waitAvailable();
		if (!conn.available())
			throw new IllegalStateException(conn.getError());
		else
			return;
	}
	
	public SMGPMessage send(SMGPMessage message) throws IOException {
		if (message == null)
			return null;
		SMGPTransaction t = (SMGPTransaction) conn.createChild();
		try {
			t.send(message);
			t.waitResponse();
			SMGPMessage rsp = t.getResponse();
			SMGPMessage smgpmessage = rsp;
			return smgpmessage;
		} finally {
			t.close();
		}
	}

	public void onTerminate() {
	}

	public SMGPMessage onDeliver(SMGPDeliverMessage msg) {
		return new SMGPDeliverRespMessage(msg.getMsgId(), 0);
	}

	public void close() {
		conn.close();
		if (needReconnect) {
			connect();
		}
	}

	public SMGPConnection getConn() {
		return conn;
	}

	public String getConnState() {
		return (null != conn) ? conn.getError() : "\u5C1A\u672A\u5EFA\u7ACB\u8FDE\u63A5";
	}
	
	public void setNeedReconnect(boolean needReconnect) {
		this.needReconnect = needReconnect;
	}
}
