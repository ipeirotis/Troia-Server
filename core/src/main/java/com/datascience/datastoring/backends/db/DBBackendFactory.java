package com.datascience.datastoring.backends.db;

import com.datascience.datastoring.Constants;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.util.Properties;

/**
 * @Author: konrad
 */
public class DBBackendFactory {

	protected PoolProperties getBasicPoolProperties() {
		PoolProperties p = new PoolProperties();
		p.setInitialSize(10);
		p.setMaxActive(30);
		p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		return p;
	}

	public PoolProperties getInMemoryPoolProperties() {
		PoolProperties p = getBasicPoolProperties();
		p.setDriverClassName("org.h2.Driver");
		p.setUrl("jdbc:h2:mem:" + "test" + ";DB_CLOSE_DELAY=-1");
		return p;
	}

	public PoolProperties getPoolProperties(Properties connectionProperties, Properties properties) {
		PoolProperties p = getBasicPoolProperties();
		p.setDriverClassName(properties.getProperty(Constants.DB_DRIVER_CLASS));
		p.setUsername(connectionProperties.getProperty("user"));
		p.setPassword(connectionProperties.getProperty("password"));
		p.setUrl(properties.getProperty(Constants.DB_URL) + "?useUnicode=true&characterEncoding=utf-8");
		return p;
	}

	public DBBackend getInMemoryBackend(){
		return new DBBackend("", getInMemoryPoolProperties(), new SQLCommandOperatorFactory("MERGE INTO", false));
	}

	public DBBackend getDBBackendOnProperties(Properties connectionProperties, Properties properties) {
		return new DBBackend(properties.getProperty(Constants.DB_NAME),
				getPoolProperties(connectionProperties, properties),
				new SQLCommandOperatorFactory("REPLACE INTO", true));
	}
}
