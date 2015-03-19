package com.bang;

import java.io.IOException;

import com.im.SocketProxy;
import com.im.SocketServer;

public class SocketManager {
	/**
	 * 测试socket服务   
	 * */
	public static void main(String[] args) {
		SocketServer socketServer = SocketServer.getSocketServer("aaaa",9000);
		try {
			//方法会阻塞程序
			socketServer.startServer();
			//获得指定 connect
			socketServer.getConnects().getSocketConnectBySid("");
			//通过代理类 实现消息发送
			SocketProxy.getProxyHandler(socketServer.getConnects().getSocketConnectBySid("客户对应的sid")).send("内容");;
			socketServer.closeSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
