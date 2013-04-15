package com.datascience.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import com.datascience.executor.NotSafeRWLock;

public class JobsManager{

	private Map<String, ReadWriteLock> locks;
	
	public JobsManager(){
		locks = new HashMap<String, ReadWriteLock>();
	}

	public synchronized ReadWriteLock getLock(String id){
		ReadWriteLock lock = locks.get(id);
		if (lock == null){
			lock = new NotSafeRWLock();
			locks.put(id, lock);
		}
		return lock;
	}
}
