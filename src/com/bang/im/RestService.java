package com.bang.im;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.catalina.util.Base64;

import com.bang.util.MD5;

public abstract class RestService {


	public static final String ACCOUNTSID = "8a48b5514be8f938014bf2db227404de";
	public static final String AUTHTOCKEN = "e67d7cad72a34747828450c4f93db2e6";
	public static final String BASEURL = "https://sandboxapp.cloopen.com:8883/2013-12-26";
	public static final String APPID = "8a48b5514be8f938014bf2db8b1e04e0";
	public static final DateFormat DF = new SimpleDateFormat("yyyyMMddHHmmss");

	
	private Date date = Calendar.getInstance().getTime();
	private String datestr , sig , authorization , url , service;
	
	private static final Map<String, String> REQHEAD = new HashMap<String, String>(){
		private static final long serialVersionUID = -1166424680372672747L;
		{
			put("Host", "sandboxapp.cloopen.com:8883");
			put("Accept", "application/json");
			put("Connection", "Keep-Alive");
			put("Content-Type", "application/json;charset=utf-8");
		}
	};
	
	/**
	 * 执行请求方法 
	 * 返回服务器数据
	 * */
	protected String getRestReturnValue() {
		datestr = DF.format(date);
		sig = MD5.getMd5Str32(ACCOUNTSID + AUTHTOCKEN + datestr).toUpperCase();
		authorization = Base64.encode((ACCOUNTSID + ":" + datestr).getBytes());
		
		REQHEAD.put("Authorization", authorization);
		service = getService();
		if(service==null)
			throw new RestException("请求服务不能为空");
		url = BASEURL+"/Accounts/"+ACCOUNTSID+"/"+service+"?sig="+sig;

		JSONObject json = getParam();
		if(json!=null)
			json.put("appId", APPID);
		
		return HttpClient.send(url,REQHEAD,json==null?"":json.toString());
	}
	
	/**
	 * 获取具体需要请求的业务方法 不能返回null
	 * */
	protected abstract String getService();
	
	/**
	 * 获取封装的Http请求参数集合
	 * */
	protected abstract JSONObject getParam();
	
	public JSONObject execute(){
		JSONObject json = JSONObject.fromObject(getRestReturnValue());
		if(!"000000".equals(json.getString("statusCode")))
			throw new RestException(json.getString("statusMsg"));
		
		return json;
	}
	
	/**
	 * 程序抛
	 * */
	public class RestException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6248097650416849622L;

		public RestException(String msg) {
			super(msg);
		}
		
	}
}
