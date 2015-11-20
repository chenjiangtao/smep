package com.huawei.insa2.comm;

import java.io.*;

public abstract class PWriter {
	public abstract void write(PMessage message) throws IOException;
}