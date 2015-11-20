package com.huawei.insa2.comm.sgip;

import java.io.IOException;
import java.net.*;

public class SSListener extends Thread
{
    public SSListener(String ip, int port, SSEventListener lis)
    {
        status = 1;
        this.ip = ip;
        this.port = port;
        listener = lis;
        running = true;
        start();
    }
	
    public void run()
    {
        do
            if(status == 0) {
                try { 
                	Socket incoming = serversocket.accept();
                    if(status == 0)
                        listener.onConnect(incoming);
                } catch(Exception ex) { 
                	if(status != 0); 
                }
            } else {
                synchronized(this) {
                    try { wait(3000L); } catch(Exception exception) { }
                }
            }
        while(running);
    }

    public synchronized void beginListen() throws IOException
    {
        if(status == 0)
            return;

        try
        {
            serversocket = new ServerSocket();
            serversocket.bind(new InetSocketAddress(ip, port));
            status = 0;
            notifyAll();
        }
        catch(IOException ex) { throw ex; }
    }

    public synchronized void stopListen()
    {
        if(status == 0)
            try
            {
                if(serversocket != null)
                {
                    status = 1;
                    serversocket.close();
                    serversocket = null;
                }
            }
            catch(IOException ex) {}
    }

    private ServerSocket serversocket;
    private SSEventListener listener;
    private String ip;
    private int port;
    private int status;
    public boolean running = true;
}
