package com.datascience.core.storages;

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
