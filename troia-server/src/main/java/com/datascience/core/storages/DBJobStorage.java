package com.datascience.core.storages;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.core.datastoring.memory.InMemoryNominalData;
import com.datascience.core.datastoring.memory.InMemoryResults;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.DBHelper;
import org.apache.log4j.Logger;

import com.datascience.core.jobs.Job;

/**
 * From old class DawidSkeneCache.
 * This doesn't look good but this is temporary
 * FIXME: possible sql injection!
 * @author konrad
 */
public class DBJobStorage extends BaseDBJobStorage<DBHelper>{

	private static Logger logger = Logger.getLogger(DBJobStorage.class);

	private static final String GET_DS = "SELECT kind, data, results, initializationData, model FROM Projects WHERE id IN (?);";
	private static final String INSERT_DS = "REPLACE INTO Projects (id, kind, data, results, initializationData, model) VALUES (?, ?, ?, ?, ?, ?);";
	private static final String DELETE_DS = "DELETE FROM Projects WHERE id = (?);";

	public DBJobStorage(DBHelper helper, ISerializer serializer) throws SQLException{
		super(helper, serializer);
	}

	@Override
	public <T extends Project> Job<T>  get(String id) throws SQLException {
		logger.debug("Getting job from DB: " + id);
		ResultSet dsResults = null;
		helper.ensureConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = helper.getConnection().prepareStatement(GET_DS);
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
		helper.ensureConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = helper.getConnection().prepareStatement(INSERT_DS);
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
		helper.ensureConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = helper.getConnection().prepareStatement(DELETE_DS);
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
		helper.close();
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
		helper.ensureConnection();
		UUID uuid = UUID.randomUUID();
		String jid = "TEST_CONNECTION_" + uuid.toString();
		String content = "TEST_CONTENT_" + uuid.toString();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = helper.getConnection().prepareStatement(INSERT_DS);
			dsStatement.setString(1, jid);
			dsStatement.setString(2, "TEST_KIND");
			dsStatement.setString(3, content);
			dsStatement.setString(4, content);
			dsStatement.setString(5, content);
			dsStatement.setString(6, content);
			dsStatement.executeUpdate();
			dsStatement.close();

			dsStatement = helper.getConnection().prepareStatement(DELETE_DS);
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
	public IData<ContValue> getContData(String id) {
		return new InMemoryData<ContValue>();
	}

	@Override
	public INominalData getNominalData(String id) {
		return new InMemoryNominalData();
	}

	@Override
	public IResults getContResults(String id) {
		return new InMemoryResults<ContValue, DatumContResults, WorkerContResults>(
				new ResultsFactory.DatumContResultFactory(), new ResultsFactory.WorkerContResultFactory());
	}

	@Override
	public IResults getNominalResults(String id, Collection<String> categories){
		ResultsFactory.WorkerResultNominalFactory wrnf = new ResultsFactory.WorkerResultNominalFactory();
		wrnf.setCategories(categories);
		return new InMemoryResults<String, DatumResult, WorkerResult>(new ResultsFactory.DatumResultFactory(), wrnf);
	}
}
