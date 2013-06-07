package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.datastoring.IBackendAdapter;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.google.gson.JsonObject;

import java.util.Collection;

/**
 * @Author: konrad
 */
public interface IKVsProvider extends IBackendAdapter{

	ISafeKVStorage<JsonObject> getSettingsKV();
	ISafeKVStorage<String> getKindsKV();

	ISafeKVStorage<Collection<AssignedLabel<String>>>getNominalWorkerAssignsKV(String id);
	ISafeKVStorage<Collection<AssignedLabel<String>>>getNominalObjectAssignsKV(String id);
	ISafeKVStorage<Collection<LObject<String>>>getNominalObjectsKV(String id);
	ISafeKVStorage<Collection<LObject<String>>>getNominalGoldObjectsKV(String id);
	ISafeKVStorage<Collection<LObject<String>>>getNominalEvaluationObjectsKV(String id);

	ISafeKVStorage<Collection<AssignedLabel<ContValue>>>getContWorkerAssignsKV(String id);
	ISafeKVStorage<Collection<AssignedLabel<ContValue>>>getContObjectAssignsKV(String id);
	ISafeKVStorage<Collection<LObject<ContValue>>>getContObjectsKV(String id);
	ISafeKVStorage<Collection<LObject<ContValue>>>getContGoldObjectsKV(String id);
	ISafeKVStorage<Collection<LObject<ContValue>>>getContEvaluationObjectsKV(String id);

	ISafeKVStorage<Collection<Worker>>getWorkersKV(String id);

	ISafeKVStorage<Collection<DatumContResults>>getDatumContResultsKV(String id);
	ISafeKVStorage<Collection<WorkerContResults>>getWorkerContResultsKV(String id);

	ISafeKVStorage<Collection<DatumResult>>getDatumResultsKV(String id, ResultsFactory.DatumResultFactory resultFactory);
	ISafeKVStorage<Collection<WorkerResult>>getWorkerResultsKV(String id, ResultsFactory.WorkerResultNominalFactory resultFactory);

}
