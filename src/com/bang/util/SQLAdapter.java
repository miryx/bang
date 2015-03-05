package com.bang.util;

/**
 * @author 尹超
 *  mybatis 不同过映射文件配置sql
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
