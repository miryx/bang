package com.bang;

import com.im.Bussiness;

import net.sf.json.JSONObject;

/**
 * 一个SocketConnect 对应一个 SocketProxy 对应 一个 SocketBussiness
 * 
 * 
 *  */
public class SocketBussiness {
/**
 * socket接受到的消息 对应的请求处理   注解指向 service_name
 * 实现方法必须 返回JSONObject对象  入参也是JSONObject 为接收到的interfacemsg内容  返回值 有返回正常填写  没有则返回 null
 * 
 * eg：{"route":{"sid":"xxx","service_name":"xxxx"},"interfacemsg":{}}
 * */
	@Bussiness(name="xxxx")
	public JSONObject getJson(JSONObject json){
		System.out.println(json+"---------->");
		return json;
	}
}
