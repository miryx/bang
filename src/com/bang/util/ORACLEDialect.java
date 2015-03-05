package com.bang.util;

/**
 * @author Òü³¬
 * oracle·ÖÒ³Àà
 */
public class ORACLEDialect extends Dialect {

	@Override
	public String getLimitString(String other, String sql, int offset,
			int limit) {
		  int bengin = (offset-1)*limit+1;
		  int end = offset*limit;
	      sql = sql.trim();    
	      if(other==null||"".equals(other)){
	    	  other="*";
	      }
	      StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);    
	      pagingSelect.append("select "+other+" from ( select row_.*, rownum rownum_ from ( ");    
	      pagingSelect.append(sql);    
	      pagingSelect.append(" ) row_ ) where rownum_ >= ").append(bengin).append(" and rownum_ <= ").append(end);    
	      return pagingSelect.toString();
	}

}
