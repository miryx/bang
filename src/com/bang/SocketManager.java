package com.bang;

import java.io.IOException;

import com.im.SocketProxy;
import com.im.SocketServer;

public class SocketManager {
	/**
	 * ����socket����   
	 * */
	public static void main(String[] args) {
		SocketServer socketServer = SocketServer.getSocketServer("aaaa",9000);
		try {
			//��������������
			socketServer.startServer();
			//���ָ�� connect
			socketServer.getConnects().getSocketConnectBySid("");
			//ͨ�������� ʵ����Ϣ����
			SocketProxy.getProxyHandler(socketServer.getConnects().getSocketConnectBySid("�ͻ���Ӧ��sid")).send("����");;
			socketServer.closeSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
