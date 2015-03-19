package com.bang.im.rest;

import net.sf.json.JSONObject;

import com.bang.im.RestService;

public class SMS extends RestService {

	private String to;
	private String template;
	private String datas;
	
	public SMS() {}
	public SMS(String to,String template,String datas){
		this.to = to;
		this.template = template;
		this.datas = datas;
	}
	
	@Override
	protected String getService() {
		return "SMS/TemplateSMS";
	}

	@Override
	protected JSONObject getParam() {
		JSONObject json = new JSONObject();
		json.put("to", to);
		json.put("templateId", template);
		json.put("datas", datas);
		return json;
	}

	public void deal(JSONObject js) {
		System.out.println(js);
	}
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getDatas() {
		return datas;
	}
	public void setDatas(String datas) {
		this.datas = datas;
	}

}
