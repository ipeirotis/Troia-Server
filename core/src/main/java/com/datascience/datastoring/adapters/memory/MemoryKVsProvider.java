package com.datascience.datastoring.adapters.memory;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.datascience.datastoring.backends.memory.MemoryBackend;
import com.datascience.datastoring.datamodels.kv.BaseKVsProvider;

import java.lang.reflect.Type;
import java.util.Collection;


/** TODO XXX FIXME FINISH THIS
 */
public class MemoryKVsProvider extends BaseKVsProvider {

	MemoryBackend memoryBackend = new MemoryBackend();

	@Override
	protected <V> ISafeKVStorage<V> getKV(String table, Type expectedType) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, Type expectedType) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected <V> ISafeKVStorage<Collection<V>> getMultiItemKVForJob(String id, String table, Type expectedType) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IBackend getBackend() {
		return memoryBackend;
	}
}
