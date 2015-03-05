package com.bang.db;

import java.io.IOException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

class DBUtil {

	private static final Logger LOGGER = Logger.getLogger(DBUtil.class);

	private static SqlSessionFactory sessionFactory = null;  
	static{
		String resource = "mybatis.xml";
		try {
			sessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(resource));  
		} catch (IOException e) {  
			e.printStackTrace(); 
			LOGGER.error(e);
		}
	}
	private DBUtil(){}

	public static SqlSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
}
