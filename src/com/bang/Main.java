package com.bang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class);

	public static String ReturnJson(String json){
		LOGGER.info("--->"+json);
		JSONObject returnMsg = new JSONObject();
		JSONObject reInput  = null;
		String service_name = null; 
		String className = null;
		try {
			reInput = JSONObject.fromObject(json.replaceAll("\r|\n", ""));
			if(!reInput.has("route"))
				throw new JSONException("���� route �����ڡ�");
			JSONObject route = reInput.getJSONObject("route");
			if(!reInput.has("interfacemsg"))
				throw new JSONException("���� interfacemsg �����ڡ�");
			if(!route.has("service_name"))
				throw new JSONException("route �� service_name �����ڡ�");
			service_name = route.getString("service_name");
			className = "com.bang.action.B_" + service_name;
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getMethod("execute", JSONObject.class);
			returnMsg = (JSONObject) method.invoke(clazz.newInstance(), reInput.getJSONObject("interfacemsg"));
			reInput.put("returnMsg", returnMsg);
			reInput.remove("interfacemsg");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			LOGGER.error(className+"����ʧ�ܣ������಻����");
			returnMsg.put("returnCode", 1);
			returnMsg.put("returnInfo", "û���ṩ�ķ������ͣ�"+service_name);
			reInput.put("returnMsg", returnMsg);
		} catch (JSONException e) {
			e.printStackTrace();
			LOGGER.error(e);
			if(reInput!=null){
				returnMsg.put("returnCode", 1);
				returnMsg.put("returnInfo", e.getMessage());
				reInput.put("returnMsg", returnMsg);
			}else{
				returnMsg.put("returnCode", 1);
				returnMsg.put("returnInfo", "�������ݣ�'"+json+"', ��������"+e.getMessage());
				reInput = returnMsg;
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			LOGGER.error(e.getTargetException().getMessage());
			System.out.println("e.getTargetException().getMessage()��"+e.getMessage());
			returnMsg.put("returnCode", 1);
			returnMsg.put("returnInfo", e.getTargetException().getMessage());
			reInput.put("returnMsg", returnMsg);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			System.out.println("Exception��"+e.getMessage());
			returnMsg.put("returnCode", 1);
			returnMsg.put("returnInfo", e.getMessage());
			reInput.put("returnMsg", returnMsg);
		}
		LOGGER.info("<---"+reInput);
		return reInput.toString();
	}
}
