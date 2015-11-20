package com.huawei.smproxy;

import com.huawei.insa2.comm.*;
import com.huawei.insa2.comm.cmpp.message.*;
import com.huawei.insa2.comm.cmpp30.CMPP30Connection;
import com.huawei.insa2.comm.cmpp30.CMPP30Transaction;
import com.huawei.insa2.comm.cmpp30.message.CMPP30DeliverMessage;

class CMPP30EventAdapter extends PEventAdapter {

	public CMPP30EventAdapter(SMProxy30 smProxy) {
		this.smProxy = smProxy;
		conn = smProxy.getConn();
	}

	public void childCreated(PLayer child) {
		CMPP30Transaction t = (CMPP30Transaction) child;
		CMPPMessage msg = t.getResponse();
		CMPPMessage resmsg = null;
		if (msg.getCommandId() == 2) {
			resmsg = new CMPPTerminateRepMessage();
			smProxy.onTerminate();
		} else if (msg.getCommandId() == 8)
			resmsg = new CMPPActiveRepMessage(0);
		else if (msg.getCommandId() == 5) {
			CMPP30DeliverMessage tmpmes = (CMPP30DeliverMessage) msg;
			resmsg = smProxy.onDeliver(tmpmes);
		} else {
			t.close();
		}
		
		if (resmsg != null) {
			try {
				t.send(resmsg);
			} catch (PException e) {
				e.printStackTrace();
			}
			t.close();
		}
		
		if (msg.getCommandId() == 2)
			conn.close();
	}

	private SMProxy30 smProxy;
	private CMPP30Connection conn;
}