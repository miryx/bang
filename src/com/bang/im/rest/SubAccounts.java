package com.bang.im.rest;

import net.sf.json.JSONObject;

import com.bang.im.RestService;

/**
 * ��ȡ���˻��б�
 * */
public class SubAccounts extends RestService {

	private JSONObject json;
	private int startNo = 0;
	private int offset = 10;

	private String service;
	
	public SubAccounts(){}
	/**
	 * @param pageNum ��ǰ�ڼ�ҳ
	 * @param pageSize ��ʾ������
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
	 * �鿴��һҳ
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
	 * �������˻�
	 * */
	public void createSubAccountByName(String name){
		service = "SubAccounts";
		json = new JSONObject();
		json.put("friendlyName", name);
		deal(this.execute());
	}

	/**
	 * �ر����˻�
	 * */
	public void closeSubAccount(String name){
		service = "CloseSubAccount";
		json = new JSONObject();
		json.put("subAccountSid", name);
		deal(this.execute());
	}

	/**
	 * ���˻���Ϣ
	 * */
	public void querySubAccountByName(String name){
		service = "QuerySubAccountByName";
		json = new JSONObject();
		json.put("friendlyName", name);
		deal(this.execute());
	}
	
	/**
	 * ���˻���Ϣ
	 * */
	public void accountInfo(){
		service = "AccountInfo";
		json = null;
		deal(this.execute());
	}
}
