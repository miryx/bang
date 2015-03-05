package com.bang;

import net.sf.json.JSONObject;

public abstract class BWSService {
	
	public abstract JSONObject execute(JSONObject js);
	
	/**
	 * ͨ��JSONObect ��ȡ��Ӧ�Ķ���  
	 * */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(JSONObject js,Class<T> c){
		Object obj = JSONObject.toBean(js, c);
		return (T) obj;
	}
}
