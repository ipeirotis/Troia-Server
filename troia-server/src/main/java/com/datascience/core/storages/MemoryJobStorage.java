package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.core.datastoring.memory.InMemoryNominalData;
import com.datascience.core.datastoring.memory.InMemoryResults;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class MemoryJobStorage implements IJobStorage{

	private Map<String, Job> storage;
	
	public MemoryJobStorage(){
		storage = new HashMap<String, Job>();
	}
	
	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		return storage.get(id);
	}

	@Override
	public void add(Job job) throws Exception {
		storage.put(job.getId(), job);
	}

	@Override
	public void remove(Job job) throws Exception {
		storage.remove(job.getId());
	}

	@Override
	public void test() throws Exception {
	}

	@Override
	public void stop() throws Exception {
		storage.clear();
	}

	@Override
	public <T> IData<T> getData(String id) {
		return new InMemoryData<T>();
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
		return "InMemory";
	}
}
