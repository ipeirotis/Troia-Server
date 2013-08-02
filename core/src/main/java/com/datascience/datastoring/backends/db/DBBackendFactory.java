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
		p.setUrl("" + "?useUnicode=true&characterEncoding=utf-8");
//		properties.getProperty(Constants.DB_URL),
//				properties.getProperty(Constants.DB_DRIVER_CLASS),
//				connectionProperties,
//				properties.getProperty(Constants.DB_NAME),
		return p;
	}

	public PoolProperties getInMemoryPoolProperties() {
		PoolProperties p = getBasicPoolProperties();
		p.setDriverClassName("org.h2.Driver");
		p.setUrl("jdbc:h2:mem:" + "test" + ";DB_CLOSE_DELAY=-1");
//		p.setUsername();
//		p.setPassword();
		p.setInitialSize(10);
		p.setMaxActive(20);

		return p;
	}

	public PoolProperties getPoolProperties() {
		PoolProperties p = getBasicPoolProperties();

		return p;
	}

	public DBBackend getInMemoryBackend(){
		return new DBBackend("", getInMemoryPoolProperties(), new SQLCommandOperatorFactory("MERGE INTO", false));
	}

	public DBBackend getDBBackendOnProperties(Properties connectionProperties, Properties properties) {
		return new DBBackend(properties.getProperty(Constants.DB_NAME), getPoolProperties(), new SQLCommandOperatorFactory("REPLACE INTO", true));
		// TODO XXX add dbName
	}
}
