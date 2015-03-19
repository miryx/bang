package com.im;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class SocketServer {

	private static final Logger LOGGER = Logger.getLogger(SocketServer.class);
	private static Map<String, SocketServer> socketMap = new ConcurrentHashMap<String, SocketServer>();
	private int port;
	private ConnectionManager connects;
	private boolean running = true;
	private ServerSocket s = null;
	private static SocketServer socketServer;

	public static SocketServer getSocketServer(String name) {
		return getServer(name, 8888);
	}
	public static SocketServer getSocketServer(String name, int i) {
		return getServer(name,i);
	}

	private static SocketServer getServer(String name,int i){
		for(String key:socketMap.keySet()){
			if(name.equals(key)){
				return socketMap.get(key);
			}
		}
		socketServer = new SocketServer();
		socketServer.port = i;
		socketServer.connects = new ConnectionManager(name);
		socketMap.put(name, socketServer);
		return socketServer;
	}
	
	public void startServer() throws IOException {
		closeSocket();
		running = true;
		s = new ServerSocket(port);
		LOGGER.info("The Server is start: "+s);
		Socket socket;
		SocketConnect connect;
		Thread thread;
		while (running) {
			socket = s.accept();
			LOGGER.info("Accept the Client:  "+socket);
			connect = new SocketConnect(connects,socket);
			connects.putSocket(connect);
			thread = new Thread(connect);
			thread.setDaemon(true);
			thread.start();
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void closeSocket() throws IOException {
		running = false;
		if(s!=null){
			s.close();
			s = null;
		}
	}
	
	public ConnectionManager getConnects(){
		return connects;
	}
}
