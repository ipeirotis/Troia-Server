package com.datascience.datastoring.datamodels.kv;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.adapters.kv.IKVStorage;

import java.util.List;

public interface IBackendKVFactory<V> {

	IKVStorage<V> getKV(String id);
	void remove(String kvId) throws Exception;
	void rebuild() throws Exception;
	void test(List<String> kvs) throws Exception;

	IBackend getBackend();
	String getID();
}
