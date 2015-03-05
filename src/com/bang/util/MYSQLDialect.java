package com.bang.util;

/**
 * @author “¸≥¨
 *MYSQL∑÷“≥¿‡
 */
public class MYSQLDialect extends Dialect {

	@Override
	public String getLimitString(String other, String sql, int skipResults,
			int maxResults) {
		// TODO Auto-generated method stub
		return sql+" LIMIT "+(skipResults-1) +","+maxResults;
	}

}
