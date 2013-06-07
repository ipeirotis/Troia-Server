package com.datascience.datastoring.datamodels.kv;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.adapters.kv.IKVStorage;

public interface IBackendKVFactory<V> {

	IKVStorage<V> getKV(String id);
	void remove(String kvId) throws Exception;

	IBackend getBackend();
	String getID();
}
