package com.huawei.insa2.comm.smgp;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.comm.smgp.message.SMGPActiveTestMessage;
import com.huawei.insa2.comm.smgp.message.SMGPActiveTestRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPExitMessage;
import com.huawei.insa2.comm.smgp.message.SMGPExitRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPLoginMessage;
import com.huawei.insa2.comm.smgp.message.SMGPLoginRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Resource;
import java.io.*;
import java.util.Date;

public class SMGPConnection extends PSocketConnection {
	private int degree;
	private int hbnoResponseOut;
	private String clientid;
	private int version, loginmode;
	private String shared_secret;

	public SMGPConnection(Args args) {
		degree = 0;
		hbnoResponseOut = 3;
		clientid = null;

		hbnoResponseOut = args.get("heartbeat-noresponseout", 3);
		clientid = args.get("source-addr", "huawei");
		version = args.get("version", 1);
		loginmode = args.get("loginmode", 1);
		shared_secret = args.get("shared-secret", "");
		SMGPConstant.debug = args.get("debug", false);
		SMGPConstant.initConstant(getResource());
		init(args);
	}

	protected PWriter getWriter(OutputStream out) {
		return new SMGPWriter(out);
	}

	protected PReader getReader(InputStream in) {
		return new SMGPReader(in);
	}

	public int getChildId(PMessage message) {
		SMGPMessage mes = (SMGPMessage) message;
		int sequenceId = mes.getSequenceId();
		if (mes.getRequestId() == 3 || mes.getRequestId() == 4
				|| mes.getRequestId() == 6)
			return -1;
		else
			return sequenceId;
	}

	public PLayer createChild() {
		return new SMGPTransaction(this);
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
			if (getError() == PSocketConnection.NOT_INIT)
				wait(super.transactionTimeout);
		} catch (InterruptedException interruptedexception) {
		}
	}

	public void close() {
		SMGPTransaction t = (SMGPTransaction) createChild();
		SMGPExitMessage msg = new SMGPExitMessage();
		try {
			t.send(msg);
			t.waitResponse();
			SMGPExitRespMessage rsp = (SMGPExitRespMessage) t.getResponse();
			if (null == rsp)
				return;
		} catch (PException ex) {
			ex.printStackTrace();
		} finally {
			t.close();
		}
		
		super.close();
	}

	protected void heartbeat() {
		SMGPTransaction t = (SMGPTransaction) createChild();
		SMGPActiveTestMessage hbmes = new SMGPActiveTestMessage();
		try {
			t.send(hbmes);
			t.waitResponse();
			SMGPActiveTestRespMessage rsp = (SMGPActiveTestRespMessage) t.getResponse();
			if (rsp == null) {
				degree++;
				if (degree == hbnoResponseOut) {
					degree = 0;
					setError(SMGPConstant.HEARTBEAT_ABNORMITY);
				}
			} else {
				degree = 0;
			}
		} catch (PException ex) {
			ex.printStackTrace();
		} finally {
			t.close();
		}
	}

	protected synchronized void connect() {
		super.connect();
		if (!available())
			return;
		SMGPLoginMessage request = null;
		SMGPLoginRespMessage rsp = null;
		try {
			request = new SMGPLoginMessage(clientid, shared_secret, loginmode, new Date(), version);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			close();
			setError(SMGPConstant.CONNECT_INPUT_ERROR);
		}
		SMGPTransaction t = (SMGPTransaction) createChild();
		try {
			t.send(request);
			PMessage m = super.in.read();
			onReceive(m);
		} catch (IOException e) {
			e.printStackTrace();
			close();
			setError(SMGPConstant.LOGIN_ERROR.concat(explain(e)));
		}
		rsp = (SMGPLoginRespMessage) t.getResponse();
		if (rsp == null) {
			close();
			setError(SMGPConstant.CONNECT_TIMEOUT);
		}
		t.close();
		if (rsp != null && rsp.getStatus() != 0) {
			close();
			setError("Fail to login,the status code id ".concat(String.valueOf(rsp.getStatus())));
		}
		notifyAll();
	}
}
