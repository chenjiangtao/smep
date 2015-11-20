package com.huawei.insa2.comm.sgip;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.comm.sgip.message.SGIPMessage;

public class SGIPTransaction extends PLayer {

	public SGIPTransaction(PLayer connection) {
		super(connection);
		sequenceId = super.id;
	}

	public void setSrcNodeId(int node_id) {
		src_nodeid = node_id;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public synchronized void onReceive(PMessage msg) {
		receive = (SGIPMessage) msg;
		src_nodeid = receive.getSrcNodeId();
		timestamp = receive.getTimeStamp();
		sequenceId = receive.getSequenceId();
		notifyAll();
	}

	public void send(PMessage message) throws PException {
		SGIPMessage mes = (SGIPMessage) message;
		mes.setSrcNodeId(src_nodeid);
		mes.setTimeStamp(timestamp);
		mes.setSequenceId(sequenceId);
		super.parent.send(message);
	}

	public SGIPMessage getResponse() {
		return receive;
	}
	
	public boolean isChildOf(PLayer connection) {
		return (super.parent == null) ? false : (connection == super.parent);
	}
	
	public PLayer getParent() {
		return super.parent;
	}

	public synchronized void waitResponse() {
		if (receive == null)
			try {
				wait(((SGIPConnection) super.parent).getTransactionTimeout());
			} catch (InterruptedException ex) {}
	}

	private SGIPMessage receive;
	private int src_nodeid;
	private int timestamp;
	private int sequenceId;
}
