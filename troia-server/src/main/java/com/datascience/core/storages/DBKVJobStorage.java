package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.base.Project;
import com.datascience.core.datastoring.kv.KVData;
import com.datascience.utils.DBKVHelper;
import com.datascience.utils.storage.DBKVStorage;
import com.datascience.utils.storage.DefaultSafeKVStorage;
import com.datascience.utils.storage.IKVStorage;
import com.datascience.utils.storage.ISafeKVStorage;

import java.lang.reflect.Type;

/**
 * @Author: konrad
 */
public class DBKVJobStorage implements IJobStorage{

	protected DBKVHelper helper;

	public DBKVJobStorage(DBKVHelper helper){
		this.helper = helper;
	}

	protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, Type expectedType){
		IKVStorage<V> kvstorage = null;
//		kvstorage = new DBKVStorage(table, helper);
		return new DefaultSafeKVStorage<V>(kvstorage, table);
	}

	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
//		KVData
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void add(Job job) throws Exception {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void remove(Job job) throws Exception {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void test() throws Exception {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void stop() throws Exception {
		helper.close();
	}
}
