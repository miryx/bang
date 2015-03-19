package com.im;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

	private List<SocketConnect> list = new ArrayList<SocketConnect>();
	private String name;
	public ConnectionManager (String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void putSocket(SocketConnect e) {
		this.list.add(e);
	}
	
	public SocketConnect getSocketConnectBySid(String sid){
		for(SocketConnect conn:list){
			if(sid.equals(conn.getSid()))
				return conn;
		}
		return null;
	}

	public void removeSocket(SocketConnect socketConnect) {
		socketConnect.close();
		list.remove(socketConnect);
	}
}
