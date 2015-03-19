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
 *  查找 select t.*, t.rowid from tbl_base_r_proc t where t.proc_id='1'; 中的proc_content
 *	proc_gs_testclob(out_p out CLOB,
 *	out_j out CLOB,
 *	out_z out cursor,
 *	in_param in VARCHAR)
 *	页面提交参数IN 对应的参数 in_param ，执行完成后返回 OUT 对应参数的结果集，
 *	注意：
 *	proc_gs_testclob( in_param in VARCHAR,out_j out CLOB,out_p out CLOB, out_p out DATE,out_z out cursor)
 *	存储名(参数名 参数出入说明 参数对应的JdbcType类型,...)
 *	编写 proc_content值的时候需要注意 
 *	1.参数的顺序必须和存储过程保持一致，
 *	2.单个参数属性 空格相隔，
 *	4.多个参数之间逗号分隔
 *	3.参数开始处要用英文小括号"(",参数结束要用英文小括号")"
 *	出参参数名可以随便写：结果集中的key值使用 且大小写敏感，入参参数名取值通过request.getParamter(参数名),页面表单提交需要保持一致
 *	参数出入说明：仅为IN 或 OUT ,IN 标识 入参 OUT 标识 出参
 *	参数对应的JdbcType类型：参考JdbcType这个枚举中的值，类型对应参照Mybatis API
 *  
 *  修改：原基础上增加了 直接使用 定义好的存储过程头部信息  入参出参大小写敏感  参数类型 出入描述不敏感 
 *  注意： 不支持无参存储
 *  	  入参仅支持 varchar类型 出参暂时发现 不支持 BLOB
 * @author tonsr      
 * @version 1.1   
 * @created Nov 21, 2014 11:28:22 AM  
 */
public class ProcService {
	//存储过程格式
	private static final Pattern PATTERN_PROC = Pattern.compile("[\\w^\\S]+\\s*\\(((\\s*[\\w]+)\\s+(IN|in|OUT|out)\\s+([\\w]+\\s*[,]{0,1}))*\\)");
	//出参列表
	private List<String> outter = new ArrayList<String>();
	//封装好的 存储过程
	private JSONObject proc = null;
	//参数集合 调用execute()方法后 集合会增加结果集
	private Map<String,Object> param = null;
	//封装好的入参信息
	private JSONObject values = null;

	/**    
	 * 获取出参集合
	 * @return    出参集合 在param 结果集的key 
	 */
	public List<String> getOutter() {
		return outter;
	}
	/**
	 * 设置调用的存储名称信息
	 * @param procstr 存储过程
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
			throw new RuntimeException(procstr+"存储调用发生错误："+e.getMessage(),e);
		}
	}
	/**
	 * 设置调用存储所需要的参数值
	 * @param request对象自动获取
	 * */
	public void setProcValue(Map<String, Object> params){
		values = new JSONObject();
		for(String key:params.keySet()){
			values.put(key, params.get(key));
		}
	}

	/**
	 * 设置调用存储所需的参数值
	 * @param values 封装参数信息
	 * */
	public void setProcValue(JSONObject values) {
		this.values = values;
	}

	/**
	 * 封装存储调用的参数
	 * @param values 参数值
	 * @param procstr 存储过程
	 * @return 封装好的参数集 可作为 CommonDao.excuteProc(caller) 的参数
	 * @throws Exception 
	 * */
	public Map<String, Object> getProcMap(JSONObject values,String procstr) {
		this.values= values;
		this.setProc(procstr);
		this.exceteProcMap();
		return param;
	}
	/**
	 * 封装存储调用的参数
	 * @param params 包含参数的request 
	 * @param procstr 存储过程
	 * @return 封装好的参数集 可作为 CommonDao.excuteProc(caller)的参数
	 * @throws Exception 
	 * */
	public Map<String, Object> getProcMap(Map<String, Object> params,String procstr) {
		this.setProcValue(params);
		this.setProc(procstr);
		this.exceteProcMap();
		return param;
	}
	/**
	 * 获取封装后的参数集
	 * 如果没有事先设置proc 和values 则会返回null
	 * */
	public Map<String, Object> getProcMap() {
		this.exceteProcMap();
		return param;
	}
	/**
	 * 封装调用参数集 为执行CommonDao.excuteProc(caller) 准备
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
	 * 获取 proc_gs_aas( sas IN VARCHAR) 的JSON数据
	 * @author tonsr
	 * @createdate 2014-11-20
	 * @param procstr proc_gs_aas(asa IN VARCHAR)
	 * @return {"PROC":"PROC_GS_AAS","PARAM":[{"PARAM":"aas","MODE":"IN","TYPE":"VARCHAR"}]}
	 * asa IN VARCHAR 多组数据用逗号分隔
	 * asa 为参数名 IN 入参，OUT 出参，VARCHAR 参考MyBatis JDBCTYPE 
	 * */
	private static JSONObject getProcJson(String procstr) {
		JSONObject json = null;
		JSONArray js = null;
		procstr = procstr.trim();
		//格式匹配
		if(PATTERN_PROC.matcher(procstr).find()){
			if(procstr.split("\\(").length==2&&procstr.endsWith(")")){
					procstr = procstr.substring(0, procstr.length()-1);
					String[] proc = procstr.split("\\(");
					json = new JSONObject();
					if(proc[0].trim().indexOf(" ")==-1)
						json.put("PROC", proc[0].trim());
					else throw new RuntimeException(proc[0]+"存储过程名中不能包含空格字符");
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
									else throw new RuntimeException(item[1]+" MODE 类型只能为 IN 或 OUT");
									paramitem.put("TYPE", getJdbcType(item[2]));
									js.add(paramitem);
								}
							}
							json.put("PARAM", js);
						}else throw new RuntimeException("参数错误");
					}
			}
			else throw new RuntimeException("不支持无参存储过程调用");
		}
		else throw new RuntimeException("存储过程内容标记不符合格式要求");
		return json;
	}
	/**
	 * 通过proc的声明类型获取jdbc转换的类型
	 * */
	private static String getJdbcType(String type) {
		if("blob".equals(type.toLowerCase())){
			throw new RuntimeException(type+"类型是不能被支持的数据类型...");
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
		throw new RuntimeException("程序未考虑到的情况发生了，因为存储过程中存在未能识别的类型"+type+"，请在ProcService下getJdbcType中添加对应的转换类型");
	}

}
