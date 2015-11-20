package com.huawei.insa2.comm.cmpp;

import com.huawei.insa2.comm.PMessage;
import com.huawei.insa2.comm.PWriter;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import java.io.IOException;
import java.io.OutputStream;

public class CMPPWriter extends PWriter {

	public CMPPWriter(OutputStream out) {
		this.out = out;
	}

	public void write(PMessage message) throws IOException {
		CMPPMessage msg = (CMPPMessage) message;
		out.write(msg.getBytes());
		out.flush();
	}

	public void writeHeartbeat() throws IOException {}
	
	public void close() throws IOException {
		if (null != out) {
			out.close();
			out = null;
		}
	}
	
	protected OutputStream out;
}