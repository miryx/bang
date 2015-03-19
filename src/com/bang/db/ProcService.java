package com.bang.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

import org.apache.ibatis.type.JdbcType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**    
 *  ���� select t.*, t.rowid from tbl_base_r_proc t where t.proc_id='1'; �е�proc_content
 *	proc_gs_testclob(out_p out CLOB,
 *	out_j out CLOB,
 *	out_z out cursor,
 *	in_param in VARCHAR)
 *	ҳ���ύ����IN ��Ӧ�Ĳ��� in_param ��ִ����ɺ󷵻� OUT ��Ӧ�����Ľ������
 *	ע�⣺
 *	proc_gs_testclob( in_param in VARCHAR,out_j out CLOB,out_p out CLOB, out_p out DATE,out_z out cursor)
 *	�洢��(������ ��������˵�� ������Ӧ��JdbcType����,...)
 *	��д proc_contentֵ��ʱ����Ҫע�� 
 *	1.������˳�����ʹ洢���̱���һ�£�
 *	2.������������ �ո������
 *	4.�������֮�䶺�ŷָ�
 *	3.������ʼ��Ҫ��Ӣ��С����"(",��������Ҫ��Ӣ��С����")"
 *	���β������������д��������е�keyֵʹ�� �Ҵ�Сд���У���β�����ȡֵͨ��request.getParamter(������),ҳ����ύ��Ҫ����һ��
 *	��������˵������ΪIN �� OUT ,IN ��ʶ ��� OUT ��ʶ ����
 *	������Ӧ��JdbcType���ͣ��ο�JdbcType���ö���е�ֵ�����Ͷ�Ӧ����Mybatis API
 *  
 *  �޸ģ�ԭ������������ ֱ��ʹ�� ����õĴ洢����ͷ����Ϣ  ��γ��δ�Сд����  �������� �������������� 
 *  ע�⣺ ��֧���޲δ洢
 *  	  ��ν�֧�� varchar���� ������ʱ���� ��֧�� BLOB
 * @author tonsr      
 * @version 1.1   
 * @created Nov 21, 2014 11:28:22 AM  
 */
public class ProcService {
	//�洢���̸�ʽ
	private static final Pattern PATTERN_PROC = Pattern.compile("[\\w^\\S]+\\s*\\(((\\s*[\\w]+)\\s+(IN|in|OUT|out)\\s+([\\w]+\\s*[,]{0,1}))*\\)");
	//�����б�
	private List<String> outter = new ArrayList<String>();
	//��װ�õ� �洢����
	private JSONObject proc = null;
	//�������� ����execute()������ ���ϻ����ӽ����
	private Map<String,Object> param = null;
	//��װ�õ������Ϣ
	private JSONObject values = null;

	/**    
	 * ��ȡ���μ���
	 * @return    ���μ��� ��param �������key 
	 */
	public List<String> getOutter() {
		return outter;
	}
	/**
	 * ���õ��õĴ洢������Ϣ
	 * @param procstr �洢����
	 * eg:
	 * "proc_gs_testclob( in_param in VARCHAR,out_j out CLOB,out_p out CLOB, out_p out DATE,out_z out cursor)";
	 * @throws Exception 
	 * */
	public void setProc(String procstr) {
		if(procstr==null&&"".equals(procstr))
			return ;
		try {
			this.proc = getProcJson(procstr);
		} catch (Exception e) {
			throw new RuntimeException(procstr+"�洢���÷�������"+e.getMessage(),e);
		}
	}
	/**
	 * ���õ��ô洢����Ҫ�Ĳ���ֵ
	 * @param request�����Զ���ȡ
	 * */
	public void setProcValue(Map<String, Object> params){
		values = new JSONObject();
		for(String key:params.keySet()){
			values.put(key, params.get(key));
		}
	}

	/**
	 * ���õ��ô洢����Ĳ���ֵ
	 * @param values ��װ������Ϣ
	 * */
	public void setProcValue(JSONObject values) {
		this.values = values;
	}

	/**
	 * ��װ�洢���õĲ���
	 * @param values ����ֵ
	 * @param procstr �洢����
	 * @return ��װ�õĲ����� ����Ϊ CommonDao.excuteProc(caller) �Ĳ���
	 * @throws Exception 
	 * */
	public Map<String, Object> getProcMap(JSONObject values,String procstr) {
		this.values= values;
		this.setProc(procstr);
		this.exceteProcMap();
		return param;
	}
	/**
	 * ��װ�洢���õĲ���
	 * @param params ����������request 
	 * @param procstr �洢����
	 * @return ��װ�õĲ����� ����Ϊ CommonDao.excuteProc(caller)�Ĳ���
	 * @throws Exception 
	 * */
	public Map<String, Object> getProcMap(Map<String, Object> params,String procstr) {
		this.setProcValue(params);
		this.setProc(procstr);
		this.exceteProcMap();
		return param;
	}
	/**
	 * ��ȡ��װ��Ĳ�����
	 * ���û����������proc ��values ��᷵��null
	 * */
	public Map<String, Object> getProcMap() {
		this.exceteProcMap();
		return param;
	}
	/**
	 * ��װ���ò����� Ϊִ��CommonDao.excuteProc(caller) ׼��
	 */
	private void exceteProcMap(){
		if(values==null||proc==null){
			return ;
		}
		param = new HashMap<String, Object>();
		param.put("proc",proc.getString("PROC"));
		if(proc.has("PARAM")){
			JSONArray js= proc.getJSONArray("PARAM");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(int i=0;i<js.size();i++){
				Map<String, Object> map = new HashMap<String, Object>();
				proc = (JSONObject)js.get(i);
				map.put("mode", proc.get("MODE"));
				if("OUT".equals(proc.get("MODE"))){
					outter.add(proc.get("PARAM")+"");
					map.put("value", proc.get("PARAM"));
				}else{
					for(Object key:values.keySet()){
						if(key.toString().equals(proc.get("PARAM"))){
							map.put("value", values.get(key));
							break;
						}
					}
				}
				map.put("type", proc.get("TYPE"));
				list.add(map);
			}

			param.put("values", list);
		}
	}

	/**
	 * ��ȡ proc_gs_aas( sas IN VARCHAR) ��JSON����
	 * @author tonsr
	 * @createdate 2014-11-20
	 * @param procstr proc_gs_aas(asa IN VARCHAR)
	 * @return {"PROC":"PROC_GS_AAS","PARAM":[{"PARAM":"aas","MODE":"IN","TYPE":"VARCHAR"}]}
	 * asa IN VARCHAR ���������ö��ŷָ�
	 * asa Ϊ������ IN ��Σ�OUT ���Σ�VARCHAR �ο�MyBatis JDBCTYPE 
	 * */
	private static JSONObject getProcJson(String procstr) {
		JSONObject json = null;
		JSONArray js = null;
		procstr = procstr.trim();
		//��ʽƥ��
		if(PATTERN_PROC.matcher(procstr).find()){
			if(procstr.split("\\(").length==2&&procstr.endsWith(")")){
					procstr = procstr.substring(0, procstr.length()-1);
					String[] proc = procstr.split("\\(");
					json = new JSONObject();
					if(proc[0].trim().indexOf(" ")==-1)
						json.put("PROC", proc[0].trim());
					else throw new RuntimeException(proc[0]+"�洢�������в��ܰ����ո��ַ�");
					if(proc.length>1){
						Pattern p=Pattern.compile("\\s+");
						Matcher m=p.matcher(proc[1].trim());
						String [] params = m.replaceAll(" ").split(",");
						String[] item = null;
						if(params.length>0){
							js = new JSONArray();
							for(String param:params){
								if(param.trim().split(" ").length==3){
									item = param.trim().split(" ");
									JSONObject paramitem = new JSONObject();
									paramitem.put("PARAM", item[0]);
									if("IN".equals(item[1].toUpperCase())||"OUT".equals(item[1].toUpperCase()))
										paramitem.put("MODE", item[1].toUpperCase());
									else throw new RuntimeException(item[1]+" MODE ����ֻ��Ϊ IN �� OUT");
									paramitem.put("TYPE", getJdbcType(item[2]));
									js.add(paramitem);
								}
							}
							json.put("PARAM", js);
						}else throw new RuntimeException("��������");
					}
			}
			else throw new RuntimeException("��֧���޲δ洢���̵���");
		}
		else throw new RuntimeException("�洢�������ݱ�ǲ����ϸ�ʽҪ��");
		return json;
	}
	/**
	 * ͨ��proc���������ͻ�ȡjdbcת��������
	 * */
	private static String getJdbcType(String type) {
		if("blob".equals(type.toLowerCase())){
			throw new RuntimeException(type+"�����ǲ��ܱ�֧�ֵ���������...");
		}
		for(JdbcType jdb:JdbcType.values()){
			if(jdb.toString().equals(type.toUpperCase())){
				return jdb.toString().toUpperCase();
			}
		}
		if("sys_refcursor".equals(type.toLowerCase())){
			return "CURSOR";
		}else if ("varchar2".equals(type.toLowerCase())) {
			return "VARCHAR";
		}
		throw new RuntimeException("����δ���ǵ�����������ˣ���Ϊ�洢�����д���δ��ʶ�������"+type+"������ProcService��getJdbcType����Ӷ�Ӧ��ת������");
	}

}
