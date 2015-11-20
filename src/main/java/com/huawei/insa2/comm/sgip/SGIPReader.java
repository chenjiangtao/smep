package com.huawei.insa2.comm.sgip;

import com.huawei.insa2.comm.PMessage;
import com.huawei.insa2.comm.PReader;
import com.huawei.insa2.comm.sgip.message.SGIPBindMessage;
import com.huawei.insa2.comm.sgip.message.SGIPBindRepMessage;
import com.huawei.insa2.comm.sgip.message.SGIPDeliverMessage;
import com.huawei.insa2.comm.sgip.message.SGIPReportMessage;
import com.huawei.insa2.comm.sgip.message.SGIPSubmitRepMessage;
import com.huawei.insa2.comm.sgip.message.SGIPUnbindMessage;
import com.huawei.insa2.comm.sgip.message.SGIPUnbindRepMessage;
import com.huawei.insa2.comm.sgip.message.SGIPUserReportMessage;
import java.io.*;

public class SGIPReader extends PReader {

	public SGIPReader(InputStream is) {
		in = new DataInputStream(is);
	}
	
	@Override
	public PMessage read() throws IOException {	
		int total_Length = in.readInt();
		int command_Id = in.readInt();
		byte buf[] = new byte[total_Length - 8];
		in.readFully(buf);
		
		switch(command_Id) {
		case 0x80000001:
			return new SGIPBindRepMessage(buf);
		
		case 0x80000002:
			return new SGIPUnbindRepMessage(buf);
			
		case 0x80000003:
			return new SGIPSubmitRepMessage(buf);
			
		case 1:
			return new SGIPBindMessage(buf);
			
		case 2:
			return new SGIPUnbindMessage(buf);
		
		case 4:
			return new SGIPDeliverMessage(buf);
			
		case 5:
			return new SGIPReportMessage(buf);
		
		case 17:
			return new SGIPUserReportMessage(buf);
			
		default:
			return null;
		}
	}
		
	protected DataInputStream in;
}
