package com.bang.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class CommonDao {
	public static enum IDtype{
		CON,FLOW,PARAM,PROP,SHEET,
	}
	private static final String SELECTPROC = "com.bang.bean.SqlHelper.selectProc";
	private static final String GETID = "com.bang.bean.SqlHelper.getID";
	
	private static final Logger LOGGER = Logger.getLogger(CommonDao.class);
	
	private SqlSession sqlSession ;
	
	public int insert(String statement,Object obj){
		sqlSession = DBUtil.getSessionFactory().openSession();
		int i = sqlSession.insert(statement, obj);
		sqlSession.close();
		return i;
	}
	
	public String getId(IDtype iDtype){
		sqlSession = DBUtil.getSessionFactory().openSession();
		String id = sqlSession.selectOne(GETID, iDtype.toString().toLowerCase());
		sqlSession.close();
		return id;
	}
	
	public int update(String statement,Object obj){
		sqlSession = DBUtil.getSessionFactory().openSession();
		int i = sqlSession.update(statement, obj);
		sqlSession.close();
		return i;
	}
	public int delete(String statement,Object obj){
		sqlSession = DBUtil.getSessionFactory().openSession();
		int i = sqlSession.delete(statement, obj);
		sqlSession.close();
		return i;
	}
	
	public Map<String, Object> executeProc(Map<String, Object> callporc){
		sqlSession = DBUtil.getSessionFactory().openSession();
		try{
			List<Map<String, Object>> objList = sqlSession.selectList(SELECTPROC, callporc);
			callporc.put("exec", "ok");
			dealList(callporc,objList);
		}catch(Exception e){
			LOGGER.error("执行存储过程发生异常："+callporc+"\r\n原因："+e);
			callporc.put("exec", "err");
		}
		sqlSession.close();
		return callporc;
	}
	
	@SuppressWarnings("unchecked")
	private static void dealList(Map<String, Object> callporc,List<Map<String, Object>> objList){
		callporc.put("cursor", JSONArray.fromObject(objList));
		if(callporc.get("values")!=null)
			for(Map<String, Object> i:(List<Map<String, Object>>)callporc.get("values")){
				if("OUT".equals(i.get("mode"))){
					if ("CURSOR".equals(i.get("type"))) {
						try {
							callporc.put(i.get("value")+"",getListFromRs((ResultSet)callporc.get(i.get("value"))));
						} catch (SQLException e) {
							callporc.put(i.get("value")+"","SQL Exception:"+e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
	}
	/**
	 * 功能：通过ResultSet获取 List<Map<String,Object>>
	 * @author tonsr
	 * @return 返回resultSet 对于的List集合 list每一项都是Map key 是列名 value 是当前值
	 * @param java.sql.ResultSet
	 * @date 2014-11-7
	 * @exception 可能会抛出sql错误
	 * */
	private static List<Map<String, Object>> getListFromRs(ResultSet rs) throws SQLException {
		if(rs==null)
			return null;
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			Map<String,Object> item=new HashMap<String,Object>();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				item.put(rsmd.getColumnName(i),rs.getObject(i));
			}
			list.add(item);
		}
		return list;
	}

}
