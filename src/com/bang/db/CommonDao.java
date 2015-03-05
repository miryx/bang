package com.bang.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

public class CommonDao {

	private static final String SELECTPROC = "com.bang.bean.SqlHelper.selectPrco";
	
	private SqlSession sqlSession ;
	
	public int insert(String statement,Object obj){
		sqlSession = DBUtil.getSessionFactory().openSession();
		int i = sqlSession.insert(statement, obj);
		sqlSession.close();
		return i;
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> executeProc(Map<String, Object> callporc){
		sqlSession = DBUtil.getSessionFactory().openSession();
		sqlSession.selectOne(SELECTPROC, callporc);
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
		sqlSession.close();
		return callporc;
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
