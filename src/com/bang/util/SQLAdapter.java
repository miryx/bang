package com.bang.util;

/**
 * @author ����
 *  mybatis ��ͬ��ӳ���ļ�����sql
 */
public class SQLAdapter {
	private String sql; 
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public SQLAdapter(String sql){
		this.sql=sql;
	}
}
