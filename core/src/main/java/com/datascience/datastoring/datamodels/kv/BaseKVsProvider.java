package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.WorkerContResults;
import com.datascience.core.results.WorkerResult;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Collection;

public abstract class BaseKVsProvider implements IKVsProvider {

	abstract protected <V> ISafeKVStorage<V> getKV(String table, Type expectedType);
	abstract protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, Type expectedType);
	abstract protected <V> ISafeKVStorage<Collection<V>> getMultiItemKVForJob(String id, String table,
																			  Type expectedType);

	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;

	public BaseKVsProvider(){
		jobSettings = getKV("JobSettings", JsonObject.class);
		jobTypes = getKV("JobTypes", String.class);
	}

	@Override
	public ISafeKVStorage<JsonObject> getSettingsKV() {
		return jobSettings;
	}

	@Override
	public ISafeKVStorage<String> getKindsKV() {
		return jobTypes;
	}

	@Override
	public <T> ISafeKVStorage<Collection<AssignedLabel<T>>> getWorkerAssignsKV(String id) {
//		return getKVForJob(id, "WorkerAssigns", )
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ISafeKVStorage<Collection<AssignedLabel<T>>> getObjectAssignsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ISafeKVStorage<Collection<LObject<T>>> getObjectsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ISafeKVStorage<Collection<LObject<T>>> getGoldObjectsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ISafeKVStorage<Collection<LObject<T>>> getEvaluationObjectsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ISafeKVStorage<Collection<Worker<T>>> getWorkersKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ISafeKVStorage<PureNominalData> getNominalJobSettingsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ISafeKVStorage<Collection<DatumContResults>> getDatumContResultsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ISafeKVStorage<Collection<WorkerContResults>> getWorkerContResultsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ISafeKVStorage<Collection<DatumResult>> getDatumResultsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ISafeKVStorage<Collection<WorkerResult>> getWorkerResultsKV(String id) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void clear() throws Exception {
		// TODO XXX
	}

	@Override
	public void rebuild() throws Exception {
		// TODO XXX
	}

	@Override
	public String getID() {
		// TODO XXX
		return "TODO XXX";
	}
}
