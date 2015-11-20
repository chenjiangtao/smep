package com.huawei.insa2.comm;

import java.io.*;
import java.net.*;
import java.text.*;
import com.huawei.insa2.util.*;

public abstract class PSocketConnection extends PLayer {
	/*
	public PSocketConnection(Args args) {
		super(null);
		errorTime = new Date();
		port = -1;
		localPort = -1;
		init(args);
	}
	*/
	protected PSocketConnection() {
		super(null);
		port = -1;
		localPort = -1;
	}

	protected void init(Args args) {
		resource = getResource();
		initResource();
		error = NOT_INIT;
		setAttributes(args);
		
		if (heartbeatInterval > 0) {
			class HeartbeatThread extends WatchThread {
				public HeartbeatThread() {
					super(name + "-heartbeat");
				}

				@Override
				public void task() {
					try {
						sleep(heartbeatInterval);
					} catch (InterruptedException ex) {}

					if (error == null && out != null) {
						try {
							heartbeat();
						} catch (IOException ex) {
							setError(SEND_ERROR + explain(ex));
						}
					}
				}
			}

			heartbeatThread = new HeartbeatThread();
			heartbeatThread.start();
		}
		

		class ReceiveThread extends WatchThread {
			public ReceiveThread() {
				super(name + "-receive");
			}
			
			@Override
			public void task() {
				try {
					if (error == null) {
						PMessage m = in.read();
						if (m != null)
							onReceive(m);
					} else {
						if (!error.equals(NOT_INIT)) {
							try {
								sleep(reconnectInterval);
							} catch (InterruptedException ex) {}
						}
						connect();
					}
				} catch (IOException ex) {
					setError(RECEIVE_ERROR + explain(ex));
				}
			}
		}

		receiveThread = new ReceiveThread();
		receiveThread.start();
	}
	
	public void setAttributes(Args args) {
		if (name != null && name.equals(host.concat(":").concat(String.valueOf(port))))
			name = null;
		
		String oldHost = host;
		int oldPort = port;
		//String oldLocalHost = localHost;
		//int oldLocalPort = localPort;

		host = args.get("host", null);
		port = args.get("port", -1);
		localHost = args.get("local-host", null);
		localPort = args.get("local-port", -1);
		name = args.get("name", null);
		readTimeout = 1000 * args.get("read-timeout", readTimeout / 1000);
		
		if (name == null)
			name = host.concat(":").concat(String.valueOf(port));
		
		if (socket != null) {
			try {
				socket.setSoTimeout(readTimeout);
			} catch (SocketException ex) {}
		}

		reconnectInterval = 1000 * args.get("reconnect-interval", -1);
		heartbeatInterval = 1000 * args.get("heartbeat-interval", -1);
		transactionTimeout = 1000 * args.get("transaction-timeout", -1);

		if(error == null && host != null && port != -1 && (!host.equals(oldHost) || port != oldPort/*port || !host.equals(oldHost) || port != port*/)) 
		{
			setError(resource.get("comm/need-reconnect"));
			receiveThread.interrupt();
		}
	}

	public void send(PMessage message) throws PException {
		if (error != null) 
			throw new PException(SEND_ERROR.concat(getError()));
		
		try {
			out.write(message);
			fireEvent(new PEvent(PEvent.MESSAGE_SEND_SUCCESS, this, message));
		} catch (PException ex) {
			fireEvent(new PEvent(PEvent.MESSAGE_SEND_FAIL, this, message));
			setError(SEND_ERROR.concat(explain(ex)));
			throw ex;
		} catch (Exception ex) {
			fireEvent(new PEvent(PEvent.MESSAGE_SEND_FAIL, this, message));
			setError(SEND_ERROR.concat(explain(ex)));
		}
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getReconnectInterval() {
		return reconnectInterval / 1000;
	}

	public String toString() {
		return "PSocketConnection:" + name + '(' + host + ':' + port + ')';
	}

	public int getReadTimeout() {
		return readTimeout / 1000;
	}

	public boolean available() {
		return error == null;
	}

	public String getError() {
		return error;
	}
	
	/*
	public Date getErrorTime() {
		return errorTime;
	}
	*/
	
	public synchronized void close() {
		try {
			if (socket != null) {				
				socket.close();
				in = null;
				out = null;
				socket = null;
				
				if (heartbeatThread != null)
					heartbeatThread.kill();
				
				receiveThread.kill();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setError(NOT_INIT);
		}
	}
	
	protected synchronized void connect() {
		if (error == NOT_INIT)
			error = CONNECTING;
		else if (error == null)
			error = RECONNECTING;
		
		//errorTime = new Date();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ex) {}
		}

		try {
			if (port <= 0 || port > 65535) {
				setError(PORT_ERROR + "port:" + port);
				return;
			}

			if (localPort < -1 || localPort > 65535) {
				setError(PORT_ERROR + "local-port:" + localPort);
				return;
			}

			if (localHost != null) {
				boolean isConnected = false;
				InetAddress localAddr = InetAddress.getByName(localHost);
				if (localPort == -1) {
					for (int p = (int) (Math.random() * 64500); p < 64500 * 14; p += 13) {
						try {
							socket = new Socket(host, port, localAddr, 1025 + (p % 64500));
							isConnected = true;
							break;
						} 
						catch (IOException ex) {} 
						catch (SecurityException ex) {}
					}
					
					if (!isConnected)
						throw new SocketException("Can not find an avaliable local port");
					else 
						socket = new Socket(host, port, localAddr, localPort);
				}
			} else {
				socket = new Socket(host, port);
			}

			socket.setSoTimeout(readTimeout);

			out = null;
			out = getWriter(socket.getOutputStream());
			in = null;
			in = getReader(socket.getInputStream());
			setError(null);
		} catch (IOException ex) {
			setError(CONNECT_ERROR + explain(ex));
		}
	}

	protected void setError(String desc) {
		if ((error == null && desc == null) || (desc != null && desc.equals(error)))
			return;
		
		error = desc;
		//errorTime = new Date();
		if (desc == null) {
			desc = CONNECTED;
		}
	}

	protected abstract PWriter getWriter(OutputStream out);

	protected abstract PReader getReader(InputStream in);

	protected abstract Resource getResource();

	protected void heartbeat() throws IOException {}

	public void initResource() {
		NOT_INIT = resource.get("comm/not-init");
		CONNECTING = resource.get("comm/connecting");
		RECONNECTING = resource.get("comm/reconnecting");
		CONNECTED = resource.get("comm/connected");
		HEARTBEATING = resource.get("comm/heartbeating");
		RECEIVEING = resource.get("comm/receiveing");
		CLOSEING = resource.get("comm/closeing");
		CLOSED = resource.get("comm/closed");
		UNKNOWN_HOST = resource.get("comm/unknown-host");
		PORT_ERROR = resource.get("comm/port-error");
		CONNECT_REFUSE = resource.get("comm/connect-refused");
		NO_ROUTE_TO_HOST = resource.get("comm/no-route");
		RECEIVE_TIMEOUT = resource.get("comm/receive-timeout");
		CLOSE_BY_PEER = resource.get("comm/close-by-peer");
		RESET_BY_PEER = resource.get("comm/reset-by-peer");
		CONNECTION_CLOSED = resource.get("comm/connection-closed");
		COMMUNICATION_ERROR = resource.get("comm/communication-error");
		CONNECT_ERROR = resource.get("comm/connect-error");
		SEND_ERROR = resource.get("comm/send-error");
		RECEIVE_ERROR = resource.get("comm/receive-error");
		CLOSE_ERROR = resource.get("comm/close-error");
	}

	protected String explain(Exception ex) {
		String msg = ex.getMessage();
		if (msg == null) {
			msg = "";
		}
		if (ex instanceof PException) {
			return ex.getMessage();
		} else if (ex instanceof EOFException) {
			return CLOSE_BY_PEER;
		} else if (msg.indexOf("Connection reset by peer") != -1) {
			return RESET_BY_PEER;
		} else if (msg.indexOf("SocketTimeoutException") != -1) {
			return RECEIVE_TIMEOUT;
		} else if (ex instanceof NoRouteToHostException) {
			return NO_ROUTE_TO_HOST;
		} else if (ex instanceof ConnectException) {
			return CONNECT_REFUSE;
		} else if (ex instanceof UnknownHostException) {
			return UNKNOWN_HOST;
		} else if (msg.indexOf("errno: 128") != -1) {
			return NO_ROUTE_TO_HOST;
		} else {
			ex.printStackTrace();
			return ex.toString();
		}
	}
	
	protected static String NOT_INIT;
	protected static String CONNECTING;
	protected static String RECONNECTING;
	protected static String CONNECTED;
	protected static String HEARTBEATING;
	protected static String RECEIVEING;
	protected static String CLOSEING;
	protected static String CLOSED;
	protected static String UNKNOWN_HOST;
	protected static String PORT_ERROR;
	protected static String CONNECT_REFUSE;
	protected static String NO_ROUTE_TO_HOST;
	protected static String RECEIVE_TIMEOUT;
	protected static String CLOSE_BY_PEER;
	protected static String RESET_BY_PEER;
	protected static String CONNECTION_CLOSED;
	protected static String COMMUNICATION_ERROR;
	protected static String CONNECT_ERROR;
	protected static String SEND_ERROR;
	protected static String RECEIVE_ERROR;
	protected static String CLOSE_ERROR;
	private String error;
	//protected Date errorTime = new Date();
	protected String name;
	protected String host;
	protected int port = -1;
	protected String localHost;
	protected int localPort = -1;
	protected int heartbeatInterval;
	protected PReader in;
	protected PWriter out;
	protected static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	protected int readTimeout;
	protected int reconnectInterval;
	protected Socket socket;
	protected WatchThread heartbeatThread;
	protected WatchThread receiveThread;
	protected int transactionTimeout;
	protected Resource resource;	
}

