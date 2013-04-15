package com.datascience.core.storages;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import com.datascience.core.JobFactory;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.NominalProject;
import com.datascience.serialization.ISerializer;
import com.datascience.galc.ContinuousProject;
import org.apache.log4j.Logger;

import com.datascience.core.Job;
import org.apache.log4j.varia.StringMatchFilter;

/**
 * From old class DawidSkeneCache.
 * This doesn't look good but this is temporary
 * FIXME: possible sql injection!
 * @author konrad
 */
public class DBJobStorage implements IJobStorage {

	private static Logger logger = Logger.getLogger(DBJobStorage.class);

	private int VALIDATION_TIMEOUT = 2;

	private Connection connection;
	private Properties connectionProperties;
	private String databaseUrl;
	
	private static final String GET_DS = "SELECT kind, data, results, initializationData, model FROM Projects WHERE id IN (?);";
	private static final String INSERT_DS = "REPLACE INTO Projects (id, kind, data, results, initializationData, model) VALUES (?, ?, ?, ?, ?, ?);";
	private static final String DELETE_DS = "DELETE FROM Projects WHERE id = (?);";

	private ISerializer serializer;
	private JobFactory jobFactory;

	public DBJobStorage(String user, String password, String db, String url,
			ISerializer serializer) throws ClassNotFoundException, SQLException,
			IOException {

		Class.forName("com.mysql.jdbc.Driver");

		connectionProperties = new Properties();
		connectionProperties.setProperty("user", user);
		if (password != null) {
			connectionProperties.setProperty("password", password);
		}
		databaseUrl = "jdbc:mysql://" + url + "/" + db + "?useUnicode=true&characterEncoding=utf-8";
		connectDB();
		
		this.serializer = serializer;
		jobFactory = new JobFactory(serializer);
	}
	
	private void connectDB() throws SQLException {
		logger.info("Trying to connect with: " + this.databaseUrl);
		connection = DriverManager.getConnection(this.databaseUrl,
					 connectionProperties);
		logger.info("Connected to " + this.databaseUrl);
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
	
	@Override
	public <T extends Project> Job<T>  get(String id) throws SQLException {
		logger.debug("Getting job from DB: " + id);
		ResultSet dsResults = null;
		ensureDBConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = connection.prepareStatement(GET_DS);
			dsStatement.setString(1, id);
			dsResults = dsStatement.executeQuery();
			if (!dsResults.next()) {
				return null;
			}
			String data = dsResults.getString("data");
			String kind = dsResults.getString("kind");
			String results = dsResults.getString("results");
			String initializationData = dsResults.getString("initializationData");
			String model = dsResults.getString("model");
			dsStatement.close();
			logger.debug("Getting job from DB: " + id + " DONE");
			return jobFactory.create(kind, initializationData, data, results, model, id);
		} finally {
			if (dsStatement != null) {
				dsStatement.close();
			}
			if (dsResults != null) {
				dsResults.close();
			}
		}
	}

	@Override
	public void add(Job job) throws SQLException{
		logger.debug("Adding job to DB: " + job.getId());
		ensureDBConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = connection.prepareStatement(INSERT_DS);
			dsStatement.setString(1, job.getId());
			dsStatement.setString(2, job.getProject().getKind());
			dsStatement.setString(3, serializer.serialize(job.getProject().getData()));
			dsStatement.setString(4, serializer.serialize(job.getProject().getResults()));
			dsStatement.setString(5, job.getProject().getInitializationData().toString());
			dsStatement.setString(6, serializer.serialize(job.getProject().getAlgorithm().getModel()));
			dsStatement.executeUpdate();
			logger.debug("Adding job to DB: " + job.getId() + " DONE");
		} finally {
			if (dsStatement != null) {
				dsStatement.close();
			}
		}
	}
	
	@Override
	public void remove(Job job) throws Exception {
		logger.debug("Removing job from DB: " + job.getId());
		ensureDBConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = connection.prepareStatement(DELETE_DS);
			dsStatement.setString(1, job.getId());
			dsStatement.executeUpdate();
			dsStatement.close();
			logger.debug("Removing job from DB: " + job.getId() + " DONE");
		} finally {
			if (dsStatement != null) {
				dsStatement.close();
			}
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
	 * Added hoping that this will sometime turn out to be useful
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	@Override
	public void test() throws Exception{
		ensureDBConnection();
		UUID uuid = UUID.randomUUID();
		String jid = "TEST_CONNECTION_" + uuid.toString();
		String content = "TEST_CONTENT_" + uuid.toString();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = connection.prepareStatement(INSERT_DS);
			dsStatement.setString(1, jid);
			dsStatement.setString(2, "TEST_KIND");
			dsStatement.setString(3, content);
			dsStatement.setString(4, content);
			dsStatement.setString(5, content);
			dsStatement.setString(6, content);
			dsStatement.executeUpdate();
			dsStatement.close();

			dsStatement = connection.prepareStatement(DELETE_DS);
			dsStatement.setString(1, jid);
			dsStatement.executeUpdate();
		} finally {
			if (dsStatement != null) {
				dsStatement.close();
			}
		}
	}

	@Override
	public void stop() throws Exception {
		close();
	}
	
	@Override
	public String toString(){
		return "DataBase";
	}
}
