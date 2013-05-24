package com.datascience.datastoring.storages;

import com.datascience.datastoring.jobs.IJobStorage;

/**
 *
 * @author konrad
 */
public class CachedJobStorageTest extends CachedJobStorageBaseTest{

	@Override
	public IJobStorage getProxy(IJobStorage jobStorage){
		return new CachedJobStorage(jobStorage, 2);
	}
}
