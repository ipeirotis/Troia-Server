package com.datascience.core.storages;

import com.datascience.core.Job;
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
	public Job get(String id) throws Exception {
		return storage.get(id);
	}

	@Override
	public void add(Job job) throws Exception {
		storage.put(job.getId(), job);
	}

	@Override
	public void remove(String id) throws Exception {
		storage.remove(id);
	}

	@Override
	public void test() throws Exception {
	}

	@Override
	public void stop() throws Exception {
		storage.clear();
	}
}
