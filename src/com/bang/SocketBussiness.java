package com.bang;

import com.im.Bussiness;

import net.sf.json.JSONObject;

/**
 * һ��SocketConnect ��Ӧһ�� SocketProxy ��Ӧ һ�� SocketBussiness
 * 
 * 
 *  */
public class SocketBussiness {
/**
 * socket���ܵ�����Ϣ ��Ӧ��������   ע��ָ�� service_name
 * ʵ�ַ������� ����JSONObject����  ���Ҳ��JSONObject Ϊ���յ���interfacemsg����  ����ֵ �з���������д  û���򷵻� null
 * 
 * eg��{"route":{"sid":"xxx","service_name":"xxxx"},"interfacemsg":{}}
 * */
	@Bussiness(name="xxxx")
	public JSONObject getJson(JSONObject json){
		System.out.println(json+"---------->");
		return json;
	}
}
