package com.huawei.insa2.comm.smgp;

import com.huawei.insa2.comm.PMessage;
import com.huawei.insa2.comm.PWriter;
import com.huawei.insa2.comm.smgp.message.SMGPMessage;
import java.io.IOException;
import java.io.OutputStream;

public class SMGPWriter extends PWriter {

	public SMGPWriter(OutputStream out) {
		this.out = out;
	}

	public void write(PMessage message) throws IOException {
		SMGPMessage msg = (SMGPMessage) message;
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
