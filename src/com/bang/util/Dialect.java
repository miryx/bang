package com.bang.util;

/**
 * @author 尹超
 * mybatis sql分页的基类 根据配置自动生成分页sql
 */
public abstract class Dialect {
	public static enum SqlType{
		MYSQL,ORACLE,MSSQL
	}
	 public abstract String getLimitString(String other,String sql, int skipResults, int maxResults);
}
