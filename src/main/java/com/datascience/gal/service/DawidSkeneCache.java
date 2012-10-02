/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.datascience.gal.DawidSkene;

/**
 * a class for persisting DawidSkene Objects persists dawidskene objects to
 * mysql
 *
 *
 * TODO: cache using memcached TODO: make not shitty TODO: make read a
 * configuration
 *
 * @author josh
 *
 *
 *         create table projects ( id varchar(1000) NOT NULL PRIMARY KEY, data
 *         TEXT NOT NULL); create index idIndex on projects (id);
 */
public class DawidSkeneCache {
	private static Logger logger = Logger.getLogger(DawidSkeneCache.class);

	private static final int VALIDATION_TIMEOUT = 2;

	private Connection connection;
	private Properties connectionProporties;
	private String databaseUrl;

	private static final String GET_DS = "SELECT data FROM projects WHERE id IN (?);";
	private static final String INSERT_DS = "INSERT INTO projects (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = (?);";
	private static final String DELETE_DS = "DELETE FROM projects WHERE id = (?);";
	private static final String CHECK_DS = "SELECT 1 FROM projects WHERE id IN (?);";

	private final static int DEFAULT_CACHE_SIZE = 5;

	private int cachesize;
	private Map<String, DawidSkene> cache;



	public DawidSkeneCache(String user,String password,String db,String url) throws ClassNotFoundException,
		SQLException, IOException {
		this(user,password,db,url,DEFAULT_CACHE_SIZE);
	}

	public DawidSkeneCache(String user,String password,String db,String url,int cachesize) throws ClassNotFoundException,
		SQLException, IOException {

		Class.forName("com.mysql.jdbc.Driver");


		connectionProporties = new Properties();
		connectionProporties.setProperty("user", user);
		if (password != null)
			connectionProporties.setProperty("password", password);
		this.databaseUrl = "jdbc:mysql://" + url + "/" + db;
		this.connectDB();
		this.initializeCache();
	}

	/**
	 * Connects to DB
	 *
	 * @throws SQLException
	 */
	private void connectDB() throws SQLException {
		logger.info("attempting to connect to " + this.databaseUrl);
		connection = DriverManager.getConnection(this.databaseUrl, this.connectionProporties);
		logger.info("connected to " + this.databaseUrl);
	}

	/**
	 * Makes sure that connection to DB is valid. We need to ensure that we wont
	 * mess connection when multiple threads try to reconnect
	 *
	 * @throws SQLException
	 */
	private synchronized void ensureDBConnection() throws SQLException {

		if (!connection.isValid(VALIDATION_TIMEOUT)) {
			this.connectDB();
		}
	}

	/**
	 *
	 * @param id
	 *            identifier of ds task
	 * @return dawid skene object with appropriate id or null.
	 */
	public DawidSkene getDawidSkene(String id) {
		try {
			synchronized(this.cache) {
				if (this.cache.containsKey(id)) {
					return this.cache.get(id);
				}
			}
			ResultSet dsResults;
			synchronized(databaseUrl) {
				this.ensureDBConnection();
				PreparedStatement dsStatement = connection.prepareStatement(GET_DS);
				dsStatement.setString(1, id);
				dsResults = dsStatement.executeQuery();
			}
			if (dsResults.next()) {
				try {
					String dsJson = dsResults.getString("data");
					DawidSkene ds = JSONUtils.gson.fromJson(dsJson,
															JSONUtils.dawidSkeneType);
					synchronized(this.cache) {
						logger.info("returning ds object with id " + id);
						this.cache.put(id, ds);
					}
					return ds;
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
					return null;
				}

			} else {
				logger.info("no ds object with id " + id
							+ " found. returning null");
				return null;
			}

		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage());
			return null;
		}
	}

	public DawidSkene insertDawidSkene(final DawidSkene ds) {
		try {
			synchronized(databaseUrl) {
				ensureDBConnection();
				PreparedStatement dsStatement = connection.prepareStatement(INSERT_DS);
				dsStatement.setString(1, ds.getId());
				String dsString = ds.toString();
				dsStatement.setString(2, dsString);
				dsStatement.setString(3, dsString);

				dsStatement.executeUpdate();
				dsStatement.close();
			}
			logger.info("upserting ds with id " + ds.getId());
			this.cache.put(ds.getId(), ds);
		} catch (SQLException e) {
			logger.error(e.getMessage());

		}
		return ds;
	}

	public boolean hasDawidSkene(String id) {

		try {
			synchronized(this.cache) {
				if (this.cache.containsKey(id))
					return true;
			}
			ResultSet dsResults;
			synchronized(databaseUrl) {
				ensureDBConnection();
				PreparedStatement dsStatement = connection.prepareStatement(CHECK_DS);
				dsStatement.setString(1, id);

				dsResults = dsStatement.executeQuery();
			}
			if (dsResults.next())
				return true;

			return false;
		} catch (Exception e) {
			logger.error("error checking ds object: " + e.getLocalizedMessage());
		}

		return false;
	}

	public void deleteDawidSkene(String id) {

		try {
			synchronized(this.cache) {
				if (this.cache.containsKey(id))
					this.cache.remove(id);
			}
			synchronized(databaseUrl) {
				ensureDBConnection();
				PreparedStatement dsStatement = connection.prepareStatement(DELETE_DS);
				dsStatement.setString(1, id);

				dsStatement.executeUpdate();
				dsStatement.close();
			}
			logger.info("deleted ds with id " + id);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * This method is never called - this is bad ...
	 *
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		logger.info("closing db connections");
		connection.close();
	}

	/**
	 * Added hoping that this will sometime turn out to be usefull
	 */
	protected void finalize() throws SQLException {
		close();
	}

	private void initializeCache() {
		this.cache = new LinkedHashMap<String, DawidSkene>() {

			private static final long serialVersionUID = -3654983366018448082L;

			@Override
			protected boolean removeEldestEntry(
			java.util.Map.Entry<String, DawidSkene> arg0) {
				return size() > cachesize;
			}
		};
	}

	public void setCacheSize(int cachesize) {
		this.cachesize = cachesize;
	}

	private class DSDBInserter implements Runnable {
		final DawidSkene ds;

		public DSDBInserter(DawidSkene ds) {
			this.ds = ds;
		}

		@Override
		public void run() {
		}

	}
}
