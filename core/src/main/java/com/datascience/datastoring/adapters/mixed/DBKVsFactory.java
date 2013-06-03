package com.datascience.datastoring.adapters.mixed;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.adapters.kv.*;
import com.datascience.datastoring.datamodels.kv.*;

/**
 * TODO
 * @Author: konrad
 */
public class DBKVsFactory<T> implements IBackendKVFactory<T> {

	@Override
	public IKVStorage<T> getKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void remove(String kvId) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public IBackend getBackend() {
		return null;
	}

	@Override
	public String getID() {
		return "DB";
	}
}
