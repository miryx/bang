package com.bang.util;

/**
 * @author ����
 * mybatis sql��ҳ�Ļ��� ���������Զ����ɷ�ҳsql
 */
public abstract class Dialect {
	public static enum SqlType{
		MYSQL,ORACLE,MSSQL
	}
	 public abstract String getLimitString(String other,String sql, int skipResults, int maxResults);
}
