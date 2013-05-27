package com.datascience.datastoring.adapters.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.WorkerContResults;
import com.datascience.core.results.WorkerResult;
import com.datascience.datastoring.IBackendAdapter;
import com.google.gson.JsonObject;

import java.util.Collection;

/**
 * @Author: konrad
 */
public interface IKVsProvider extends IBackendAdapter{

	ISafeKVStorage<JsonObject> getSettingsKV();
	ISafeKVStorage<String> getKindsKV();

	<T> ISafeKVStorage<Collection<AssignedLabel<T>>>getWorkerAssignsKV(String id);
	<T> ISafeKVStorage<Collection<AssignedLabel<T>>>getObjectAssignsKV(String id);
	<T> ISafeKVStorage<Collection<LObject<T>>>getObjectsKV(String id);
	<T> ISafeKVStorage<Collection<LObject<T>>>getGoldObjectsKV(String id);
	<T> ISafeKVStorage<Collection<LObject<T>>>getEvaluationObjectsKV(String id);
	<T> ISafeKVStorage<Collection<Worker<T>>>getWorkersKV(String id);
	ISafeKVStorage<PureNominalData>getNominalJobSettingsKV(String id);

	ISafeKVStorage<Collection<DatumContResults>>getDatumContResultsKV(String id);
	ISafeKVStorage<Collection<WorkerContResults>>getWorkerContResultsKV(String id);

	ISafeKVStorage<Collection<DatumResult>>getDatumResultsKV(String id);
	ISafeKVStorage<Collection<WorkerResult>>getWorkerResultsKV(String id);

}
