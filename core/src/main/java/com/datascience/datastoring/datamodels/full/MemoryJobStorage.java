package com.datascience.datastoring.datamodels.full;

import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.jobs.Job;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
import com.datascience.datastoring.datamodels.memory.InMemoryNominalData;
import com.datascience.datastoring.datamodels.memory.InMemoryResults;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
import com.datascience.datastoring.jobs.JobFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class MemoryJobStorage implements IJobStorage {

	private Map<String, Job> storage;

	public MemoryJobStorage(){
		initialize();
	}

	@Override
	public void setJobFactory(JobFactory jobFactory){
	}

	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		return storage.get(id);
	}

	@Override
	public <T extends Project> void add(Job<T> job) {
		storage.put(job.getId(), job);
	}

	@Override
	public void remove(Job job) {
		storage.remove(job.getId());
	}

	@Override
	public void flush(Job job) {
	}

	@Override
	public void test() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public void clear(){
		storage.clear();
	}

	@Override
	public void initialize(){
		storage = new HashMap<String, Job>();
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
		return "MEMORY_FULL";
	}
}
