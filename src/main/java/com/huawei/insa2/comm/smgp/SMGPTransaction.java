package com.huawei.insa2.comm.smgp;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.comm.smgp.message.SMGPMessage;

public class SMGPTransaction extends PLayer {

	public SMGPTransaction(PLayer connection) {
		super(connection);
		sequenceId = super.id;
	}

	public synchronized void onReceive(PMessage msg) {
		receive = (SMGPMessage) msg;
		sequenceId = receive.getSequenceId();
		notifyAll();
	}

	public void send(PMessage message) throws PException {
		SMGPMessage mes = (SMGPMessage) message;
		mes.setSequenceId(sequenceId);
		super.parent.send(message);
	}

	public SMGPMessage getResponse() {
		return receive;
	}

	public synchronized void waitResponse() {
		if (receive == null)
			try {
				wait(((SMGPConnection) super.parent).getTransactionTimeout());
			} catch (InterruptedException interruptedexception) {
			}
	}

	private SMGPMessage receive;
	private int sequenceId;
}
