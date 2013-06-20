package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.*;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.INominalModel;
import com.datascience.datastoring.datamodels.memory.IncrementalNominalModel;
import com.datascience.datastoring.datamodels.memory.NominalModel;
import com.datascience.core.results.*;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.datascience.datastoring.jobs.*;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @Author: konrad
 */
public class KVJobStorage extends BaseJobStorage{

	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;
	protected KVCleaner cleaner;
	protected IKVsProvider kvsProvider;

	public KVJobStorage(IKVsProvider kvsProvider){
		super(kvsProvider);
		this.kvsProvider = kvsProvider;
		cleaner = new KVCleaner();
		jobSettings = kvsProvider.getSettingsKV();
		jobTypes = kvsProvider.getKindsKV();
	}

	@Override
	public <T extends Project> void add(Job<T> job) throws Exception {
		jobTypes.put(job.getId(), job.getProject().getKind());
		jobSettings.put(job.getId(), job.getProject().getInitializationData());
	}

	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		JsonObject settings = jobSettings.get(id);
		String type = jobTypes.get(id);
		if (type == null || settings == null)
			return null;
		return jobFactory.create(type, settings, id);
	}

	@Override
	public void remove(Job job) throws Exception {
		Project p = job.getProject();
		cleaner.cleanUp((KVResults) p.getResults(), p.getData());
		cleaner.cleanUp((KVData) p.getData()); // order is important ...
		jobTypes.remove(job.getId());
		jobSettings.remove(job.getId());
	}

	@Override
	public void flush(Job job){
	}

	@Override
	public IData<ContValue> getContData(String id) {
		KVData<ContValue> data = new KVData<ContValue>(
				kvsProvider.getContWorkerAssignsKV(id),
				kvsProvider.getContObjectAssignsKV(id),
				kvsProvider.getContObjectsKV(id),
				kvsProvider.getContGoldObjectsKV(id),
				kvsProvider.getContEvaluationObjectsKV(id),
				kvsProvider.getWorkersKV(id)
		);
		return data;
	}

	@Override
	public INominalData getNominalData(String id) {
		INominalData data = new KVNominalData(
				kvsProvider.getNominalWorkerAssignsKV(id),
				kvsProvider.getNominalObjectAssignsKV(id),
				kvsProvider.getNominalObjectsKV(id),
				kvsProvider.getNominalGoldObjectsKV(id),
				kvsProvider.getNominalEvaluationObjectsKV(id),
				kvsProvider.getWorkersKV(id)
		);
		return data;
	}

	@Override
	public IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id) {
		return new KVResults(
				new ResultsFactory.DatumContResultFactory(),
				new ResultsFactory.WorkerContResultFactory(),
				kvsProvider.getDatumContResultsKV(id),
				kvsProvider.getWorkerContResultsKV(id));
	}

	@Override
	public IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories) {
		ResultsFactory.WorkerResultNominalFactory wrnf = new ResultsFactory.WorkerResultNominalFactory();
		ResultsFactory.DatumResultFactory drf = new ResultsFactory.DatumResultFactory();
		wrnf.setCategories(categories);
		return new KVResults(drf, wrnf,
				kvsProvider.getDatumResultsKV(id, drf),
				kvsProvider.getWorkerResultsKV(id, wrnf));
	}

	@Override
	public INominalModel getNominalModel(String id, Type t){
		if (t.equals(new TypeToken<NominalModel>(){}.getType()))
			return new KVNominalModel(kvsProvider.getNominalModel(id));
		else
			return new KVIncrementalNominalModel(kvsProvider.getIncrementalNominalModel(id));
	}

	@Override
	public String toString(){
		return "KV:" + kvsProvider.toString();
	}
}
