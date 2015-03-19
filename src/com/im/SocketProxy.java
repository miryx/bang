package com.im;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class SocketProxy {

	private static Map<SocketConnect, SocketProxy> map = new HashMap<SocketConnect, SocketProxy>();
	private SocketConnect socketConnect;
	private static Class<?> serviceClass;
	private Object serviceInstance;
	public static SocketProxy getProxyHandler(SocketConnect socketConnect) {
		if(map.get(socketConnect)==null){
			SocketProxy reciveProxy = new SocketProxy();
			reciveProxy.socketConnect = socketConnect;
			map.put(socketConnect, reciveProxy); 
			try {
				reciveProxy.serviceInstance = serviceClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map.get(socketConnect);
	}
	private JSONObject json,route,interfacemsg,retjson;
	void service(String msg) {
		try{
			json = JSONObject.fromObject(msg);
			if(!json.has("route"))
				throw new JSONException("缺少route信息");
			route = json.getJSONObject("route");
			if(!json.has("interfacemsg"))
				throw new JSONException("缺少interfacemsg信息");
			interfacemsg = json.getJSONObject("interfacemsg");
			if(socketConnect.getSid()!=null){
				if(!socketConnect.getSid().equals(route.getString("sid")))
					throw new JSONException("sid信息不一致");
			} else
				socketConnect.setSid(route.getString("sid"));
			Method [] ms = serviceClass.getMethods();
			for(Method m:ms){
				if(m.getAnnotation(Bussiness.class) != null)
					if(m.getAnnotation(Bussiness.class).name().equals(route.getString("service_name"))){
						try {
							retjson = new JSONObject();
							retjson.put("route", route);
							if(m.getParameterTypes()[0].equals(JSONObject.class)&&m.getReturnType().equals(JSONObject.class)){
								Object res = m.invoke(serviceInstance, interfacemsg);
								retjson.put("interfacemsg", res!=null?res:JSONObject.fromObject("{\"returncode\":\"0000\",\"msg\":\"执行完成！\"}"));
								send(retjson.toString());
							}else{
								throw new JSONException("目标方法执行参数会返回类型有误");
							}
							return;
						} catch (Exception e) {
							e.printStackTrace();
							throw new JSONException(e.getMessage());
						}
					}
			}
			retjson = new JSONObject();
			retjson.put("route", route);
			retjson.put("interfacemsg", JSONObject.fromObject("{\"returncode\":\"404\",\"msg\":\"服务器没有提供该服务！\"}"));
			send(retjson.toString());
		}catch(JSONException e){
			JSONObject json = new JSONObject();
			json.put("route",msg);
			json.put("interfacemsg", "{\"returncode\":\"9999\",\"msg\":\""+e.getMessage()+"\"}");
			send(json.toString());
		}
	}

	static{
		Properties properties = new Properties();
		try {
			properties.load(SocketProxy.class.getClassLoader().getResourceAsStream("im.properties"));
			serviceClass = Class.forName(properties.getProperty("service"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(String msg){
		socketConnect.write(msg);
	}
}
