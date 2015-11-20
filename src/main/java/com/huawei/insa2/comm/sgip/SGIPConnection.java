package com.huawei.insa2.comm.sgip;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.comm.sgip.message.SGIPBindMessage;
import com.huawei.insa2.comm.sgip.message.SGIPBindRepMessage;
import com.huawei.insa2.comm.sgip.message.SGIPMessage;
import com.huawei.insa2.comm.sgip.message.SGIPUnbindMessage;
import com.huawei.insa2.comm.sgip.message.SGIPUnbindRepMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Resource;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SGIPConnection extends SGIPSocketConnection {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
	
	public SGIPConnection(Args args, boolean ifasClient, Map<String, SGIPConnection> connmap) {
		
		SGIPConstant.debug = args.get("debug", false);
		login_name = args.get("source-addr", "");
		login_pass = args.get("shared-secret", "");
		src_nodeid = args.get("src-nodeid", 0);
		this.connmap = connmap;
		SGIPConstant.initConstant(getResource());
		asClient = ifasClient;
		if (asClient)
			initSMGConnect(args);
	}

	public synchronized void attach(Args args, Socket socket) {
		if (asClient) {
			throw new UnsupportedOperationException("Client socket can not accept connection");
		} else {
			initSPConnect(args, socket);
			ipaddr = socket.getInetAddress().getHostAddress();
			port = socket.getPort();
		}
	}

	protected void onReadTimeOut() {
		close();
	}

	protected PWriter getWriter(OutputStream out) {
		return new SGIPWriter(out);
	}

	protected PReader getReader(InputStream in) {
		return new SGIPReader(in);
	}

	public int getChildId(PMessage message) {
		SGIPMessage mes = (SGIPMessage) message;
		int sequenceId = mes.getSequenceId();
		if (!asClient);
		if (mes.getCommandId() == 4 || mes.getCommandId() == 5 || mes.getCommandId() == 17 || mes.getCommandId() == 1 || mes.getCommandId() == 2)
			return -1;
		else
			return sequenceId;
	}

	public PLayer createChild() {
		return new SGIPTransaction(this);
	}

	public int getTransactionTimeout() {
		return super.transactionTimeout;
	}

	public Resource getResource() {
		try {
			Resource resource = new Resource(getClass(), "resource");
			return resource;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Resource resource1 = null;
		return resource1;
	}

	public synchronized void waitAvailable() {
		try {
			if (getError() == SGIPSocketConnection.NOT_INIT)
				wait(super.transactionTimeout);
		} catch (InterruptedException interruptedexception) {
		}
	}

	public void close() {
		SGIPTransaction t = (SGIPTransaction) createChild();
		t.setSrcNodeId(src_nodeid);
		Date nowtime = new Date();
		String tmpTime = dateFormat.format(nowtime);
		Integer timestamp = new Integer(tmpTime);
		t.setTimestamp(timestamp.intValue());
		try {
			SGIPUnbindMessage msg = new SGIPUnbindMessage();
			t.send(msg);
			t.waitResponse();
			SGIPUnbindRepMessage rsp = (SGIPUnbindRepMessage) t.getResponse();
			if (null == rsp)
				return;
		} catch (PException ex) {
			ex.printStackTrace();
		} finally {
			t.close();
		}
		
		super.close();	
		
		if (!asClient && connmap != null)
			connmap.remove(ipaddr.concat(String.valueOf(port)));
	}

	protected void heartbeat() throws IOException {}

	protected synchronized void connect() {
		super.connect();
		if (!available())
			return;
		
		SGIPBindMessage request = null;
		SGIPBindRepMessage rsp = null;
		try {
			request = new SGIPBindMessage(1, login_name, login_pass);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			close();
			setError(SGIPConstant.CONNECT_INPUT_ERROR);
		}
		
		SGIPTransaction t = (SGIPTransaction) createChild();
		try {
			t.send(request);
			PMessage m = super.in.read();
			onReceive(m);
		} catch (IOException e) {
			e.printStackTrace();
			close();
			setError(SGIPConstant.LOGIN_ERROR.concat(explain(e)));
		}
		
		rsp = (SGIPBindRepMessage) t.getResponse();
		if (rsp == null) {
			close();
			setError(SGIPConstant.CONNECT_TIMEOUT);
		}
		t.close();
		
		if (rsp != null && rsp.getResult() != 0) {
			close();
			if (rsp.getResult() == 1)
				setError(SGIPConstant.NONLICETSP_LOGNAME);
			else
				setError(SGIPConstant.OTHER_ERROR);
		}
		notifyAll();
	}
	
	private boolean asClient;
	private String login_name;
	private String login_pass;
	private int src_nodeid;
	private String ipaddr;
	private int port;
	private Map<String, SGIPConnection> connmap;
}
