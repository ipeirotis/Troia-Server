package com.datascience.core.storages;

/**
 *
 * @author konrad
 */
public class CachedWithRegularDumpJobStorage extends CachedJobStorage{
	
	public CachedWithRegularDumpJobStorage(final IJobStorage cachedJobStorage, int cacheSize){
		super(cachedJobStorage, cacheSize);
		
	}
	
	@Override
	public void stop() throws Exception {
		int i = 0;
		// xxxx
		super.stop();
	}
	
	@Override
	public String toString() {
		return "CachedWithRegularDump" + cachedJobStorage.toString();
	}
}
