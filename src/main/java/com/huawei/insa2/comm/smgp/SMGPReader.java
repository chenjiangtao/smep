package com.huawei.insa2.comm.smgp;

import com.huawei.insa2.comm.PMessage;
import com.huawei.insa2.comm.PReader;
import com.huawei.insa2.comm.smgp.message.SMGPActiveTestMessage;
import com.huawei.insa2.comm.smgp.message.SMGPActiveTestRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPDeliverMessage;
import com.huawei.insa2.comm.smgp.message.SMGPExitMessage;
import com.huawei.insa2.comm.smgp.message.SMGPExitRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPForwardRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPLoginRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPMoRouteUpdateRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPMtRouteUpdateRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPQueryRespMessage;
import com.huawei.insa2.comm.smgp.message.SMGPSubmitRespMessage;
import java.io.*;

public class SMGPReader extends PReader {

	public SMGPReader(InputStream is) {
		in = new DataInputStream(is);
	}
	
	@Override
	public PMessage read() throws IOException {
		int total_Length = in.readInt();
		int command_Id = in.readInt();
		byte buf[] = new byte[total_Length - 8];
		in.readFully(buf);

		switch (command_Id) {
		case 0x80000001:
			return new SMGPLoginRespMessage(buf);

		case 0x80000002:
			return new SMGPSubmitRespMessage(buf);

		case 0x80000004:
			return new SMGPActiveTestRespMessage(buf);

		case 0x80000005:
			return new SMGPForwardRespMessage(buf);

		case 0x80000006:
			return new SMGPExitRespMessage(buf);

		case 0x80000007:
			return new SMGPQueryRespMessage(buf);

		case 0x80000008:
			return new SMGPMtRouteUpdateRespMessage(buf);

		case 0x80000009:
			return new SMGPMoRouteUpdateRespMessage(buf);

		case 3:
			return new SMGPDeliverMessage(buf);

		case 4:
			return new SMGPActiveTestMessage(buf);

		case 6:
			return new SMGPExitMessage(buf);

		default:
			return null;
		}
	}
		
	protected DataInputStream in;
}
