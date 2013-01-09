package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.service.ISerializer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import org.apache.log4j.Logger;

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
	
	private static final String GET_DS = "SELECT data FROM projects WHERE id IN (?);";
	private static final String INSERT_DS = "INSERT INTO projects (id, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = (?);";
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
		databaseUrl = "jdbc:mysql://" + url + "/" + db;
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
	public Job get(String id) throws SQLException {
		ResultSet dsResults;
		ensureDBConnection();
		PreparedStatement dsStatement = connection.prepareStatement(GET_DS);
		dsStatement.setString(1, id);
		dsResults = dsStatement.executeQuery();
		if (!dsResults.next()) {
			return null;
		}
		String dsJson = dsResults.getString("data");
		dsStatement.close();
		AbstractDawidSkene ads = serializer.parse(dsJson, JSONUtils.dawidSkeneType);
		return new Job(ads, id);
	}

	@Override
	public void add(Job job) throws SQLException{
		ensureDBConnection();
		PreparedStatement dsStatement = connection
			.prepareStatement(INSERT_DS);
		dsStatement.setString(1, job.getId());
		String dsString = serializer.serialize(job.getDs());
		dsStatement.setString(2, dsString);
		dsStatement.setString(3, dsString);

		dsStatement.executeUpdate();
		dsStatement.close();
	}
	
	@Override
	public void remove(String id) throws Exception {
		ensureDBConnection();
		PreparedStatement dsStatement = connection.prepareStatement(DELETE_DS);
		dsStatement.setString(1, id);
		dsStatement.executeUpdate();
		dsStatement.close();
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
		PreparedStatement dsStatement = connection.prepareStatement(INSERT_DS);
		dsStatement.setString(1, jid);
		dsStatement.setString(2, content);
		dsStatement.setString(3, content);
		dsStatement.executeUpdate();
		dsStatement.close();

		dsStatement = connection.prepareStatement(DELETE_DS);
		dsStatement.setString(1, jid);
		dsStatement.executeUpdate();
		dsStatement.close();
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
