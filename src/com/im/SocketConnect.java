package com.im;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;


public class SocketConnect implements Runnable {

	private String sid;
	private Socket socket;
	private ConnectionManager connects;
	
	public SocketConnect(ConnectionManager connects,Socket socket) throws IOException {
		this.socket = socket;
		this.connects = connects;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	}


	private static final Logger LOGGER = Logger.getLogger(SocketConnect.class);
	boolean reading = true;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String msg;
	@Override
	public void run() {
		try{
			while (reading) {
				msg = in.readLine();
				if(msg!=null)
					SocketProxy.getProxyHandler(this).service(msg);
				else if(!socket.isConnected())
					throw new IOException("�����ѶϿ�");
			}
		} catch(IOException e) {
			LOGGER.error("��Ϣ����ʧ�ܣ������Ѿ��Ͽ�..."+e.getMessage());
			connects.removeSocket(this);
			close();
		} catch(Exception e) {
			e.printStackTrace();
			LOGGER.error("�����ش����--->"+e.getMessage());
			connects.removeSocket(this);
			close();
			throw new RuntimeException(e);
		}
	}
	/**
	 * ����������Ҫ�л�����
	 * */
	void write(String msg){
		String res =  JSONObject.fromObject(msg).toString();
		res = res.replace("\n", "");
		out.println(res);
		//out.println(Coder.toUnicodeString(res));
	}
	
	void close(){
		try {
			reading = false;
			if(out!=null)
				out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
				try {
					if(in!=null)
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}finally{
					if(socket!=null)
						try {
							socket.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
		}
	}
	public String getSid() {
		return sid;
	}

	void setSid(String sid) {
		this.sid = sid;
	}

	
}
