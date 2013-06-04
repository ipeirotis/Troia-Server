package com.datascience.datastoring.adapters.memory;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.adapters.kv.IKVStorage;
import com.datascience.datastoring.adapters.kv.MemoryKVStorage;
import com.datascience.datastoring.datamodels.kv.IBackendKVFactory;

import java.util.HashMap;
import java.util.Map;


public class MemoryKVFactory<V> implements IBackendKVFactory<V>, IBackend{

	protected Map<String, IKVStorage<V>> kvs = new HashMap<String, IKVStorage<V>>();

	@Override
	public IKVStorage<V> getKV(String id) {
		IKVStorage<V> kv = kvs.get(id);
		if (kv == null) {
			kv = new MemoryKVStorage<V>();
			kvs.put(id, kv);
		}
		return kv;
	}

	@Override
	public void remove(String kvId) {
		kvs.remove(kvId);
	}

	@Override
	public IBackend getBackend() {
		return this;
	}

	@Override
	public String getID() {
		return "MEM";
	}

	@Override
	public void test() {}

	@Override
	public void stop() {
		kvs.clear();
	}
}
