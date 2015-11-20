package com.huawei.insa2.comm.cmpp30;

import com.huawei.insa2.comm.PMessage;
import com.huawei.insa2.comm.PReader;
import com.huawei.insa2.comm.cmpp.message.*;
import com.huawei.insa2.comm.cmpp30.message.CMPP30ConnectRepMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30DeliverMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitRepMessage;
import java.io.*;

public class CMPP30Reader extends PReader {

	protected DataInputStream in;

	public CMPP30Reader(InputStream is) {
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
			return new CMPP30ConnectRepMessage(buf);

		case 0x80000002:
			return new CMPPTerminateRepMessage(buf);

		case 0x80000004:
			return new CMPP30SubmitRepMessage(buf);

		case 0x80000006:
			return new CMPPQueryRepMessage(buf);

		case 0x80000007:
			return new CMPPCancelRepMessage(buf);

		case 0x80000008:
			return new CMPPActiveRepMessage(buf);

		case 2:
			return new CMPPTerminateMessage(buf);

		case 5:
			return new CMPP30DeliverMessage(buf);

		case 8:
			return new CMPPActiveMessage(buf);

		default:
			return null;
		}
	}
}
