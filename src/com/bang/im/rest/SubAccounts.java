package com.bang.im.rest;

import net.sf.json.JSONObject;

import com.bang.im.RestService;

/**
 * 获取子账户列表
 * */
public class SubAccounts extends RestService {

	private JSONObject json;
	private int startNo = 0;
	private int offset = 10;

	private String service;
	
	public SubAccounts(){}
	/**
	 * @param pageNum 当前第几页
	 * @param pageSize 显示多少条
	 * */
	public SubAccounts(int pageNum,int pageSize){
		this.startNo = (pageNum-1)*pageSize;
		this.offset = pageSize;
		
		service = "GetSubAccounts";
		json = new JSONObject();
		json.put("startNo", startNo);
		json.put("offset", offset);
	}
	
	@Override
	protected String getService() {
		return service;
	}

	@Override
	protected JSONObject getParam() {
		return json;
	}
	
	/**
	 * 查看下一页
	 * */
	public void nextPage(){
		service = "GetSubAccounts";
		json = new JSONObject();
		json.put("startNo", startNo);
		json.put("offset", offset);
		
		this.startNo += offset;
		deal(this.execute());
	}

	protected void deal(JSONObject js) {
		System.out.println(js);
	}
	
	/**
	 * 创建子账户
	 * */
	public void createSubAccountByName(String name){
		service = "SubAccounts";
		json = new JSONObject();
		json.put("friendlyName", name);
		deal(this.execute());
	}

	/**
	 * 关闭子账户
	 * */
	public void closeSubAccount(String name){
		service = "CloseSubAccount";
		json = new JSONObject();
		json.put("subAccountSid", name);
		deal(this.execute());
	}

	/**
	 * 子账户信息
	 * */
	public void querySubAccountByName(String name){
		service = "QuerySubAccountByName";
		json = new JSONObject();
		json.put("friendlyName", name);
		deal(this.execute());
	}
	
	/**
	 * 主账户信息
	 * */
	public void accountInfo(){
		service = "AccountInfo";
		json = null;
		deal(this.execute());
	}
}
