package com.datascience.core.storages;

import com.datascience.core.jobs.IJobStorage;

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
