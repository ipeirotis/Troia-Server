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
	
	public Job get(String id) throws Exception {
		Job job = storage.get(id);
		if (job == null) {
			throw new IllegalArgumentException("No job with id=" + id);
		}
		return job;
	}

	public void add(Job job) throws Exception {
		storage.put(job.getId(), job);
	}

	public void remove(String id) throws Exception {
		storage.remove(id);
	}

	public void test() throws Exception {
	}

	public void stop() throws Exception {
		storage.clear();
	}
}
