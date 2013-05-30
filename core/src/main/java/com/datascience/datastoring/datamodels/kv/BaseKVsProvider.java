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
import com.datascience.utils.ITransformation;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @param <T> backend's data type
 */
public abstract class BaseKVsProvider<T> implements IKVsProvider {

	abstract protected <V> ISafeKVStorage<V> getKV(String table, ITransformation<V, T> transformation);
	abstract protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, ITransformation<V, T> transformation);

	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;

	public BaseKVsProvider(){
		jobSettings = getKV("JobSettings", JsonObject.class);
		jobTypes = getKV("JobTypes", String.class);
	}

	protected <V> ISafeKVStorage<V> getSingleRowKVForJob(String id, String table, ITransformation<V, T> transformation){
		return getKVForJob(id, table, transformation);
	}

	protected <V> ISafeKVStorage<Collection<V>> getMultirowKVForJob(String id, String table, Type expectedType){
		return getKVForJob(id + "_", table, expectedType);
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
	public ISafeKVStorage<JsonObject> getSettingsKV() {
		return jobSettings;
	}

	@Override
	public ISafeKVStorage<String> getKindsKV() {
		return jobTypes;
	}

	@Override
	public <T> ISafeKVStorage<Collection<AssignedLabel<T>>> getWorkerAssignsKV(String id) {
		return getMultirowKVForJob(id, "WorkerAssigns", new TypeToken<Collection<AssignedLabel<T>>>(){}.getType());
	}

	@Override
	public <T> ISafeKVStorage<Collection<AssignedLabel<T>>> getObjectAssignsKV(String id) {
		return getMultirowKVForJob(id, "ObjectAssigns", new TypeToken<Collection<AssignedLabel<T>>>(){}.getType());

	}

	@Override
	public <T> ISafeKVStorage<Collection<LObject<T>>> getObjectsKV(String id) {
		return getSingleRowKVForJob(id, "Objects", new TypeToken<Collection<LObject<T>>>(){}.getType());
	}

	@Override
	public <T> ISafeKVStorage<Collection<LObject<T>>> getGoldObjectsKV(String id) {
		return getSingleRowKVForJob(id, "GoldObjects", new TypeToken<Collection<LObject<T>>>(){}.getType());
	}

	@Override
	public <T> ISafeKVStorage<Collection<LObject<T>>> getEvaluationObjectsKV(String id) {
		return getSingleRowKVForJob(id, "EvaluationObjects", new TypeToken<Collection<LObject<T>>>(){}.getType());
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

}
