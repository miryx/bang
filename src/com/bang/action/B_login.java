package com.bang.action;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.bang.BWSService;
import com.bang.bean.User;
import com.bang.db.CommonDao;
import com.bang.db.ProcService;

public class B_login extends BWSService {

	@Override
	public JSONObject execute(JSONObject js) {
		JSONObject json = new JSONObject();
		json.put("xxx", "xxxxxxx");
		return json;
	}

	public static void main(String[] args) {
		JSONObject js = new JSONObject();
		js.put("name", "xxx");
		js.put("sex", "0");
		User user = getObject(js, User.class);
		System.out.println(user.getName());
		CommonDao commonDao = new CommonDao();
		//System.out.println(commonDao.getId(IDtype.CON));
		ProcService pService = new ProcService();
		try {
			pService.setProc("proc_test(arg in varchar,arg2 out varchar,arga out varchar)");
			pService.setProc("proc_test1()");
			pService.setProcValue(new HashMap<String, Object>(){
				private static final long serialVersionUID = 390311903713378782L;
				{
					put("arg", "u_type");
				}
			});
			Map<String, Object> map = commonDao.executeProc(pService.getProcMap());
			map.remove("values");
			System.out.println("exec-->"+JSONObject.fromObject(map));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
