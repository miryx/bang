package com.bang.util;

import java.util.Properties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
/**
 * @author 尹超
 *  mybatis分页拦截器
 */
@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class})})
public class PaginationInterceptor implements Interceptor {
	private final static Log log = LogFactory
	.getLog(PaginationInterceptor.class);
	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	//private static String DEFAULT_PAGE_SQL_ID = ".*Page$"; // 需要拦截的ID(正则匹配)
	public Object intercept(Invocation arg0) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) arg0.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		MetaObject metaStatementHandler = MetaObject.forObject(
				statementHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
		RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
		if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
			return arg0.proceed();
		}
		RowsBoundsCommon myrowBounds = null;
		if(rowBounds instanceof RowsBoundsCommon){
			myrowBounds = (RowsBoundsCommon) rowBounds;
		}else{
			return arg0.proceed();
		}
		Configuration configuration = (Configuration) metaStatementHandler
		.getValue("delegate.configuration");
		Dialect.SqlType databaseType = null;
		try {
			databaseType = Dialect.SqlType.valueOf(configuration.getVariables()
					.getProperty("dialect").toUpperCase());
		} catch (Exception e) {
			// ignore
		}
		if (databaseType == null) {
			throw new RuntimeException(
					"the value of the dialect property in configuration.xml is not defined : "
					+ configuration.getVariables().getProperty(
					"dialect"));
		}
		Dialect dialect = null;
		switch (databaseType) {
		case ORACLE:
			dialect = new ORACLEDialect();
			break;
		case MYSQL:
			dialect = new MYSQLDialect();
			break;
		default:
			break;
		}

//		String originalSql = (String) metaStatementHandler
//		.getValue("delegate.boundSql.sql");
		String originalSql=boundSql.getSql();
		metaStatementHandler.setValue("delegate.boundSql.sql", dialect
				.getLimitString(myrowBounds.getCol(), originalSql, myrowBounds.getCurrentPage(),
						myrowBounds.getPageCount()));
		metaStatementHandler.setValue("delegate.rowBounds.offset",
				RowBounds.NO_ROW_OFFSET);
		metaStatementHandler.setValue("delegate.rowBounds.limit",
				RowBounds.NO_ROW_LIMIT);
		   MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement"); 

		Connection connection = (Connection) arg0.getArgs()[0];  
		setPageParameter(originalSql,connection,mappedStatement,boundSql,myrowBounds);
		if (log.isDebugEnabled()) {
			log.debug("生成分页SQL : " + boundSql.getSql());
		}
		return arg0.proceed();
	}

	public Object plugin(Object arg0) {
		// TODO Auto-generated method stub
		return Plugin.wrap(arg0, this);
	}

	public void setProperties(Properties arg0) {
		// TODO Auto-generated method stub

	}
	
	
	private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement,  
	        BoundSql boundSql, RowsBoundsCommon page) {  
	    // 记录总记录数  
	    String countSql = "select count(1)  total from (" + sql + ") total";  
	    PreparedStatement countStmt = null;  
	    ResultSet rs = null;  
	    try {  
	        countStmt = connection.prepareStatement(countSql);  
	        BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,  
	                boundSql.getParameterMappings(), boundSql.getParameterObject());  
	        setParameters(countStmt, mappedStatement, countBS, boundSql.getParameterObject());  
	        rs = countStmt.executeQuery();  
	        int totalCount = 0;  
	        if (rs.next()) {  
	            totalCount = rs.getInt(1);  
	        }  
	        page.setTotalCount(totalCount);  
	        int totalPage = totalCount / page.getPageCount() + ((totalCount % page.getPageCount() == 0) ? 0 : 1);  
	        page.setTotalPage(totalPage);  
	    } catch (SQLException e) {  
	    	log.error("Ignore this exception", e);  
	    } finally {  
	        try {
	        	if(rs!=null)
	        		rs.close();  
	        } catch (SQLException e) {  
	        	log.error("Ignore this exception", e);  
	        }  
	        try {  
	            countStmt.close();  
	        } catch (SQLException e) {  
	        	log.error("Ignore this exception", e);  
	        }  
	    }  
	} 

	private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,  
	        Object parameterObject) throws SQLException {  
	    ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);  
	    parameterHandler.setParameters(ps);  
	} 

}
