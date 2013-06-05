package com.datascience.datastoring.datamodels.full;

import java.sql.SQLException;
import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.datastoring.adapters.db.DBFullAdapter;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
import com.datascience.datastoring.datamodels.memory.InMemoryNominalData;
import com.datascience.datastoring.datamodels.memory.InMemoryResults;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
import com.datascience.datastoring.jobs.BaseJobStorage;
import com.datascience.serialization.ISerializer;
import com.datascience.datastoring.jobs.Job;

/**
 * @author konrad
 */
public class DBJobStorage extends BaseJobStorage {

	private DBFullAdapter adapter;
	private ISerializer serializer;

	public DBJobStorage(DBFullAdapter adapter, ISerializer serializer) throws SQLException{
		super(adapter, serializer);
		this.adapter = adapter;
		this.serializer = serializer;
	}

	@Override
	public <T extends Project> Job<T>  get(String id) throws SQLException {
		String[] params = adapter.get(id);
		if (params != null)
			return jobFactory.create(params[0], params[1], params[2], params[3], params[4], id);
		return null;
	}

	@Override
	public void add(Job job) throws SQLException{
		adapter.add(new String[]{
				job.getId(),
				job.getProject().getKind(),
				serializer.serialize(job.getProject().getData()),
				serializer.serialize(job.getProject().getResults()),
				job.getProject().getInitializationData().toString(),
				serializer.serialize(job.getProject().getAlgorithm().getModel())
		});
	}

	@Override
	public void remove(Job job) throws Exception {
		adapter.remove(job.getId());
	}

	@Override
	public void update(Job job) throws Exception {
		add(job);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
//		adapter.close();
	}

	@Override
	public void test() throws Exception{
		adapter.test();
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

	@Override
	public String toString(){
		return "DB_FULL";
	}
}
