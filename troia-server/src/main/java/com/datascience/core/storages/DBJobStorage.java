package com.datascience.core.storages;

import java.lang.reflect.Type;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.datascience.gal.DawidSkene;
import com.datascience.galc.ContinuousProject;
import org.apache.log4j.Logger;

import com.datascience.core.Job;
import com.datascience.service.ISerializer;

/**
 * From old class DawidSkeneCache.
 * This doesn't look good but this is temporary
 * FIXME: possible sql injection!
 * @author konrad
 */
public class DBJobStorage implements IJobStorage {

	private static Logger logger = Logger.getLogger(DBJobStorage.class);
	protected static Map<String, Type> typesMap = new HashMap<String, Type>();
	static {
		typesMap.put("CONTINUOUS", JSONUtils.continuousProject);
		typesMap.put("NOMINAL", JSONUtils.dawidSkeneType);
	}

	protected <T> String getKind(T object) {
		if (object instanceof ContinuousProject){
			return "CONTINUOUS";
		}
		if (object instanceof DawidSkene){
			return "NOMINAL";
		}
		throw new IllegalArgumentException("Unknown job kind for class: " + object.getClass());
	}

	private int VALIDATION_TIMEOUT = 2;

	private Connection connection;
	private Properties connectionProperties;
	private String databaseUrl;
	
	private static final String GET_DS = "SELECT kind, data FROM projects WHERE id IN (?);";
	private static final String INSERT_DS = "REPLACE INTO projects (id, kind, data) VALUES (?, ?, ?);";
	private static final String DELETE_DS = "DELETE FROM projects WHERE id = (?);";

	private ISerializer serializer;
	
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
	public <T> Job<T>  get(String id) throws SQLException {
		logger.info("Get job from DB: " + id);
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
			String dsJson = dsResults.getString("data");
			String kind = dsResults.getString("kind");
			dsStatement.close();
			T project = serializer.parse(dsJson, typesMap.get(kind));
			return new Job<T>(project, id);
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
		logger.info("Adding job to DB: " + job.getId());
		ensureDBConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = connection.prepareStatement(INSERT_DS);
			dsStatement.setString(1, job.getId());
			String dsString = serializer.serialize(job.getProject());
			dsStatement.setString(2, getKind(job.getProject()));
			dsStatement.setString(3, dsString);

			dsStatement.executeUpdate();
		} finally {
			if (dsStatement != null) {
				dsStatement.close();
			}
		}
	}
	
	@Override
	public void remove(Job job) throws Exception {
		logger.info("Removing job from DB: " + job.getId());
		ensureDBConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = connection.prepareStatement(DELETE_DS);
			dsStatement.setString(1, job.getId());
			dsStatement.executeUpdate();
			dsStatement.close();
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
	protected void finalize() throws SQLException, Throwable {
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
